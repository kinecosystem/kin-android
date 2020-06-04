package org.kin.sdk.base.storage

import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinAmount.Companion.max
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.merge
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransactions
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.stellarfork.codec.Hex
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import org.kin.gen.storage.v1.Storage.KinAccount as StorageKinAccount
import org.kin.gen.storage.v1.Storage.KinBalance as StorageKinBalance
import org.kin.gen.storage.v1.Storage.KinConfig as StorageKinConfig
import org.kin.gen.storage.v1.Storage.KinTransaction as StorageKinTransaction
import org.kin.gen.storage.v1.Storage.KinTransactions as StorageKinTransactions
import org.kin.gen.storage.v1.Storage.PrivateKey as StoragePrivateKey
import org.kin.gen.storage.v1.Storage.PublicKey as StoragePublicKey

class KinFileStorage @JvmOverloads internal constructor(
    private val filesDir: String,
    private val networkEnvironment: NetworkEnvironment,
    private val executors: ExecutorServices = ExecutorServices()
) : Storage {
    class Builder(private val filesDir: String) {
        private var networkEnvironment: NetworkEnvironment? = null
        private var executors: ExecutorServices? = null

        fun setNetworkEnvironment(networkEnvironment: NetworkEnvironment): Builder {
            this.networkEnvironment = networkEnvironment
            return this
        }

        fun setExecutors(executors: ExecutorServices): Builder {
            this.executors = executors
            return this
        }

        fun build(): KinFileStorage {
            if (networkEnvironment == null) {
                throw RuntimeException("Require a valid NetworkEnvironment")
            }
            if (executors == null) {
                executors = ExecutorServices()
            }
            return KinFileStorage(filesDir, networkEnvironment!!, executors!!)
        }
    }

    companion object {
        const val fileNameForAccountInfo = "account_info"
        const val directoryNameForAllAccounts = "kin_accounts"
        const val fileNameForConfig = "config"
    }

    override fun addAccount(account: KinAccount): Boolean {
        return writeToFile(
            directoryForAccount(account.id),
            fileNameForAccountInfo,
            account.toStorageKinAccount().toByteArray()
        )
    }

    override fun updateAccount(account: KinAccount): Boolean {
        // If the account is already stored with private key, when updating this account with a
        // public key, keep the private key.
        val existingAccount = getAccount(account.id)
        val mergedAccount = if (existingAccount != null && existingAccount.key is Key.PrivateKey) {
            KinAccount(existingAccount.key, balance = account.balance, status = account.status)
        } else {
            account
        }

        return writeToFile(
            directoryForAccount(account.id),
            fileNameForAccountInfo,
            mergedAccount.toStorageKinAccount().toByteArray()
        )
    }

    override fun removeAccount(accountId: KinAccount.Id): Boolean {
        return removeFile(directoryForAccount(accountId), fileNameForAccountInfo)
    }

    override fun getAccount(accountId: KinAccount.Id): KinAccount? {
        return getAccountFromAccountDirectory(directoryForAccount(accountId))
    }

    override fun advanceSequence(id: KinAccount.Id): KinAccount? {
        val storedAccount = getAccount(id) ?: return null
        val storedStatus = storedAccount.status as? KinAccount.Status.Registered ?: return null

        val newStatus = KinAccount.Status.Registered(storedStatus.sequence + 1)
        val updatedAccount =
            KinAccount(key = storedAccount.key, balance = storedAccount.balance, status = newStatus)

        updateAccount(updatedAccount)

        return updatedAccount
    }

    override fun getAllAccountIds(): List<KinAccount.Id> {
        val accountDirectories = subdirectories(directoryForAllAccounts())
            .map { name -> directoryForAllAccounts() + name }

        val accountIds = accountDirectories
            .map { directory -> getAccountFromAccountDirectory(directory) }
            .filterNotNull()
            .map { account -> account.id }

        return accountIds
    }

    override fun putTransactions(key: KinAccount.Id, transactions: KinTransactions) {
        writeToFile(
            directoryForTransactions(key),
            fileNameForTransactions(key),
            transactions.toKinTransactions().toByteArray()
        )
    }

    override fun removeAllTransactions(key: KinAccount.Id): Boolean {
        val transactionsDir = directoryForTransactions(key)
        val transactionFileNames = subdirectories(transactionsDir)

        transactionFileNames.forEach { fileName ->
            val removed = removeFile(transactionsDir, fileName)
            if (!removed) return false
        }

        return true
    }

    override fun getTransactions(key: KinAccount.Id): KinTransactions? {
        val transactionsDir = directoryForTransactions(key)
        val transactionsFile = fileNameForTransactions(key)

        val transactions = getTransactionsFromFile(transactionsDir, transactionsFile)

        return transactions
    }

    override fun getStoredTransactions(accountId: KinAccount.Id): Promise<KinTransactions?> {
        return Promise.create<KinTransactions?> { resolve, _ ->
            val transactions = getTransactions(accountId)
            resolve(transactions)
        }.workOn(executors.sequentialIO)
    }

    override fun storeTransactions(
        accountId: KinAccount.Id,
        transactions: List<KinTransaction>
    ): Promise<List<KinTransaction>> {
        if (transactions.isEmpty()) {
            return Promise.of(transactions)
        }
        return Promise.create<List<KinTransaction>> { resolve, _ ->
            putTransactions(
                accountId,
                KinTransactions(
                    transactions,
                    transactions.findHeadHistoricalTransaction(),
                    transactions.findTailHistoricalTransaction()
                )
            )
            resolve(transactions)
        }.workOn(executors.sequentialIO)
    }

    override fun upsertNewTransactionsInStorage(
        accountId: KinAccount.Id,
        newTransactions: List<KinTransaction>
    ): Promise<List<KinTransaction>> {
        return getStoredTransactions(accountId)
            .map { storedTransactions ->
                newTransactions + (storedTransactions?.items ?: emptyList()).filter { storedTxn ->
                    newTransactions.find { newTxn ->
                        storedTxn.transactionHash == newTxn.transactionHash
                    } == null
                }
            }
            .flatMap { storeTransactions(accountId, it) }
    }

    override fun upsertOldTransactionsInStorage(
        accountId: KinAccount.Id,
        oldTransactions: List<KinTransaction>
    ): Promise<List<KinTransaction>> {
        return getStoredTransactions(accountId)
            .map { storedTransactions ->
                (storedTransactions?.items ?: emptyList()).filter { storedTxn ->
                    oldTransactions.find { newTxn ->
                        storedTxn.transactionHash == newTxn.transactionHash
                    } == null
                } + oldTransactions
            }
            .flatMap { storeTransactions(accountId, it) }
    }

    override fun insertNewTransactionInStorage(
        accountId: KinAccount.Id,
        newTransaction: KinTransaction
    ): Promise<List<KinTransaction>> {
        return getStoredTransactions(accountId)
            .map { it?.items ?: emptyList() }
            .map { it.toMutableList().apply { add(0, newTransaction) } }
            .flatMap { storeTransactions(accountId, it) }
    }

    override fun getStoredAccount(accountId: KinAccount.Id): Promise<Optional<KinAccount>> {
        return Promise.create<Optional<KinAccount>> { resolve, _ ->
            resolve(Optional.ofNullable(getAccount(accountId)))
        }.workOn(executors.sequentialIO)
    }

    override fun updateAccountInStorage(account: KinAccount): Promise<KinAccount> {
        return getStoredAccount(account.id)
            .flatMap { accountInStorage ->
                Promise.create<KinAccount> { resolve, reject ->
                    val accountToStore = accountInStorage.map { it.merge(account) }
                    if (accountToStore.isPresent && updateAccount(accountToStore.get()!!)) {
                        resolve(accountToStore.get()!!)
                    } else {
                        reject(Exception("Failed to update Account in storage"))
                    }
                }
            }
    }

    override fun deductFromAccountBalance(
        accountId: KinAccount.Id,
        amount: KinAmount
    ): Promise<Optional<KinAccount>> {
        return getStoredAccount(accountId)
            .map { storedAccount ->
                storedAccount.map { account ->
                    val newAmount = max(KinAmount.ZERO, account.balance.amount - amount)
                    KinAccount(
                        account.key,
                        balance = KinBalance(newAmount, newAmount),
                        status = account.status
                    ).also { updateAccount(it) }
                }
            }
    }

    override fun setMinFee(minFee: QuarkAmount): Promise<Optional<QuarkAmount>> {
        return Promise.create<Optional<QuarkAmount>> { resolve, reject ->
            resolve(
                if (writeToFile(
                        directoryForConfig(),
                        fileNameForConfig,
                        StorageKinConfig.newBuilder()
                            .setMinFee(minFee.value.toLong())
                            .build()
                            .toByteArray()
                    )
                ) Optional.of(minFee)
                else Optional.empty<QuarkAmount>()
            )
        }.workOn(executors.sequentialIO)
    }

    override fun getMinFee(): Promise<Optional<QuarkAmount>> {
        return Promise.create<Optional<QuarkAmount>> { resolve, reject ->
            val bytes = readFile(
                directoryForConfig(),
                fileNameForConfig
            )

            if (bytes.isEmpty()) {
                resolve(Optional.empty())
            } else {
                resolve(
                    Optional.ofNullable(
                        try {
                            QuarkAmount(StorageKinConfig.parseFrom(bytes).minFee)
                        } catch (e: InvalidProtocolBufferException) {
                            e.printStackTrace()
                            null
                        }
                    )
                )
            }
        }.workOn(executors.sequentialIO)
    }

    override fun deleteAllStorage(accountId: KinAccount.Id): Promise<Boolean> {
        return Promise.create<Boolean> { resolve, _ ->
            resolve(removeAccount(accountId) and removeAllTransactions(accountId))
        }.workOn(executors.sequentialIO)
    }

    private fun getAccountFromAccountDirectory(directory: String): KinAccount? {
        val bytes = readFile(directory, fileNameForAccountInfo)
        if (bytes.isEmpty()) return null

        try {
            val storageKinAccount = StorageKinAccount.parseFrom(bytes)
            return storageKinAccount.toKinAccount()
        } catch (e: InvalidProtocolBufferException) {
            e.printStackTrace()
            return null
        }
    }

    private fun getTransactionsFromFile(directory: String, fileName: String): KinTransactions? {
        val bytes = readFile(directory, fileName)
        if (bytes.isEmpty()) return null

        try {
            val storageTransaction = StorageKinTransactions.parseFrom(bytes)
            return storageTransaction.toKinTransactions()
        } catch (e: InvalidProtocolBufferException) {
            e.printStackTrace()
            return null
        }
    }

    private fun List<KinTransaction>.findHeadHistoricalTransaction(): KinTransaction.PagingToken? =
        (find { it.recordType is KinTransaction.RecordType.Historical }?.recordType as? KinTransaction.RecordType.Historical)?.pagingToken

    private fun List<KinTransaction>.findTailHistoricalTransaction(): KinTransaction.PagingToken? =
        (reversed().find { it.recordType is KinTransaction.RecordType.Historical }?.recordType as? KinTransaction.RecordType.Historical)?.pagingToken


    // region File Access
    // ------------------------------------------------------------------------

    /**
     * File Structure
     * Environment Directory: <context.getFilesDir>/env/<env passcode in hex>/
     * Config: <Environment Directory>/config
     * Account Directory: <Environment Directory>/kin_accounts/<public key>
     * KinAccount object: <Account Directory>/account_info
     * KinTransaction: <Account Directory>/<account_id>_transactions
     */

    private fun envDirectory(): String =
        "$filesDir/env/${Hex.encodeHexString(networkEnvironment.networkPassphrase.toByteArray())}"

    private fun directoryForConfig(): String =
        "${envDirectory()}/"

    private fun directoryForAllAccounts(): String =
        "${envDirectory()}/$directoryNameForAllAccounts/"

    private fun directoryForAccount(accountId: KinAccount.Id): String {
        return directoryForAllAccounts() + accountId.hashCode() + "/"
    }

    private fun directoryForTransactions(accountId: KinAccount.Id): String {
        return "${directoryForAccount(accountId)}_transactions"
    }

    private fun fileNameForTransactions(accountId: KinAccount.Id): String =
        "${accountId.hashCode()}_transactions"

    private fun writeToFile(directory: String, fileName: String, body: ByteArray): Boolean {
        var outputStream: FileOutputStream? = null
        return try {
            val file = File(directory, fileName)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }

            if (!file.exists()) {
                file.createNewFile()
            }

            outputStream = FileOutputStream(file)
            outputStream.write(body)

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            outputStream?.close()
        }
    }

    private fun removeFile(directory: String, fileName: String): Boolean {
        val file = File(directory, fileName)

        if (!file.exists()) return true

        return try {
            file.delete()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun readFile(directory: String, fileName: String): ByteArray {
        var inputStream: FileInputStream? = null

        val file = File(directory, fileName)
        if (!file.exists()) {
            return ByteArray(0)
        }

        return try {
            inputStream = FileInputStream(file)
            inputStream.readBytes()

        } catch (e: IOException) {
            e.printStackTrace()
            return ByteArray(0)

        } finally {
            inputStream?.close()
        }
    }

    private fun subdirectories(directory: String): List<String> {
        val file = File(directory)
        if (!file.exists() || !file.isDirectory) {
            return emptyList()
        }

        return file.list().asList()
    }

    // ------------------------------------------------------------------------
    // endregion

    // region ModelToProto Extensions
    // ------------------------------------------------------------------------

    private fun KinAccount.toStorageKinAccount(): StorageKinAccount =
        StorageKinAccount.newBuilder()
            .setBalance(balance.toStorageKinBalance())
            .apply {
                when (this@toStorageKinAccount.status) {
                    is KinAccount.Status.Unregistered ->
                        this.setStatus(StorageKinAccount.Status.UNREGISTERED)
                    is KinAccount.Status.Registered -> {
                        this.setStatus(StorageKinAccount.Status.REGISTERED)
                        this.setSequenceNumber(this@toStorageKinAccount.status.sequence)
                    }
                }

                when (this@toStorageKinAccount.key) {
                    is Key.PublicKey -> {
                        val publicKey = StoragePublicKey.newBuilder()
                            .setValue(ByteString.copyFrom(this@toStorageKinAccount.key.value))
                            .build()
                        this.setPublicKey(publicKey)
                    }
                    is Key.PrivateKey -> {
                        val privateKey = StoragePrivateKey.newBuilder()
                            .setValue(ByteString.copyFrom(this@toStorageKinAccount.key.value))
                            .build()
                        this.setPrivateKey(privateKey)
                    }
                }
            }
            .build()

    private fun KinBalance.toStorageKinBalance(): StorageKinBalance =
        StorageKinBalance.newBuilder()
            // ask about quark conversion
            .setQuarkAmount(amount.toQuarks().value.toLong())
            .setPendingQuarkAmount(pendingAmount.toQuarks().value.toLong())
            .build()

    private fun KinTransaction.toStorageKinTransaction(): StorageKinTransaction =
        StorageKinTransaction.newBuilder()
            .setEnvelopeXdr(ByteString.copyFrom(this.envelopeXdrBytes))
            .apply {
                when (this@toStorageKinTransaction.recordType) {
                    is KinTransaction.RecordType.InFlight -> {
                        this.setStatus(StorageKinTransaction.Status.INFLIGHT)
                        this.setTimestamp(this@toStorageKinTransaction.recordType.timestamp)
                    }
                    is KinTransaction.RecordType.Acknowledged -> {
                        this.setStatus(StorageKinTransaction.Status.ACKNOWLEDGED)
                        this.setTimestamp(this@toStorageKinTransaction.recordType.timestamp)
                        this.setResultXdr(ByteString.copyFrom(this@toStorageKinTransaction.recordType.resultXdrBytes))
                    }
                    is KinTransaction.RecordType.Historical -> {
                        this.setStatus(StorageKinTransaction.Status.HISTORICAL)
                        this.setTimestamp(this@toStorageKinTransaction.recordType.timestamp)
                        this.setResultXdr(ByteString.copyFrom(this@toStorageKinTransaction.recordType.resultXdrBytes))
                        this.setPagingToken(this@toStorageKinTransaction.recordType.pagingToken.value)
                    }
                }
            }
            .build()

    private fun KinTransactions.toKinTransactions(): StorageKinTransactions {
        return StorageKinTransactions.newBuilder()
            .addAllItems(items.map { it.toStorageKinTransaction() })
            .also { builder ->
                headPagingToken?.let { builder.setHeadPagingToken(it.value) }
                tailPagingToken?.let { builder.setTailPagingToken(it.value) }
            }
            .build()
    }


    // ------------------------------------------------------------------------
    // endregion

    // region ProtoToModel Extensions
    // ------------------------------------------------------------------------

    @Throws(InvalidProtocolBufferException::class)
    private fun StorageKinAccount.toKinAccount(): KinAccount {
        val key: Key
        if (this.hasPrivateKey()) {
            key = this.privateKey.toPrivateKey()
        } else if (this.hasPublicKey()) {
            key = this.publicKey.toPublicKey()
        } else {
            throw InvalidProtocolBufferException("account is missing key")
        }

        val balance = this.balance.toKinBalance()
        val sequence = this.sequenceNumber
        val status = when (this.status) {
            StorageKinAccount.Status.REGISTERED -> KinAccount.Status.Registered(sequence)
            StorageKinAccount.Status.UNREGISTERED -> KinAccount.Status.Unregistered
            StorageKinAccount.Status.UNRECOGNIZED -> throw InvalidProtocolBufferException("Unrecognized account status.")
        }
        return KinAccount(key = key, balance = balance, status = status)
    }

    private fun StoragePublicKey.toPublicKey(): Key =
        Key.PublicKey(this.value.toByteArray())

    private fun StoragePrivateKey.toPrivateKey(): Key =
        Key.PrivateKey(this.value.toByteArray())

    private fun StorageKinBalance.toKinBalance(): KinBalance {
        val amount = QuarkAmount(this.quarkAmount).toKin()
        val pendingAmount = QuarkAmount(this.pendingQuarkAmount).toKin()
        return KinBalance(amount, pendingAmount)
    }

    @Throws(InvalidProtocolBufferException::class)
    private fun StorageKinTransaction.toKinTransaction(): KinTransaction {
        val recordType = when (this.status) {
            StorageKinTransaction.Status.INFLIGHT -> KinTransaction.RecordType.InFlight(
                this.timestamp
            )
            StorageKinTransaction.Status.ACKNOWLEDGED -> KinTransaction.RecordType.Acknowledged(
                this.timestamp,
                this.resultXdr.toByteArray()
            )
            StorageKinTransaction.Status.HISTORICAL -> KinTransaction.RecordType.Historical(
                this.timestamp,
                this.resultXdr.toByteArray(),
                KinTransaction.PagingToken(this.pagingToken)
            )
            else -> throw InvalidProtocolBufferException("Unrecognized record type.")
        }

        return KinTransaction(this.envelopeXdr.toByteArray(), recordType, networkEnvironment)
    }

    @Throws(InvalidProtocolBufferException::class)
    private fun StorageKinTransactions.toKinTransactions(): KinTransactions {
        return KinTransactions(
            itemsList.map { it.toKinTransaction() },
            KinTransaction.PagingToken(headPagingToken),
            KinTransaction.PagingToken(tailPagingToken)
        )
    }

    // ------------------------------------------------------------------------
    // endregion
}
