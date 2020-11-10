package org.kin.sdk.base.storage

import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import org.kin.agora.gen.common.v3.Model
import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.SHA224Hash
import org.kin.sdk.base.models.getAgoraMemo
import org.kin.sdk.base.models.merge
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.network.api.agora.toInvoiceList
import org.kin.sdk.base.network.api.agora.toProto
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransactions
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.SolanaKinTransaction
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.stellarfork.codec.Hex
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
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
            existingAccount.copy(
                tokenAccounts = account.tokenAccounts,
                balance = account.balance,
                status = account.status
            )
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
        val updatedAccount = storedAccount.copy(
            key = storedAccount.key,
            balance = storedAccount.balance,
            status = newStatus
        )

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

    override fun removeAllInvoices(account: KinAccount.Id): Boolean {
        val invoicesDir = directoryForInvoices(account)
        val invoicesFileNames = subdirectories(invoicesDir)

        invoicesFileNames.forEach { fileName ->
            val removed = removeFile(invoicesDir, fileName)
            if (!removed) return false
        }

        return true
    }

    override fun removeServiceConfig(): Boolean {
        return removeFile(directoryForConfig(), fileNameForConfig)
    }

    override fun getTransactions(key: KinAccount.Id): KinTransactions? {
        val transactionsDir = directoryForTransactions(key)
        val transactionsFile = fileNameForTransactions(key)

        val transactions = getTransactionsFromFile(transactionsDir, transactionsFile)

        return transactions
    }

    private fun setKinConfig(kinConfig: StorageKinConfig): Boolean {
        return writeToFile(
            directoryForConfig(),
            fileNameForConfig,
            kinConfig.toByteArray()
        )
    }

    private fun getKinConfig(): Optional<StorageKinConfig> {
        val bytes = readFile(
            directoryForConfig(),
            fileNameForConfig
        )

        return if (bytes.isEmpty()) {
            Optional.empty()
        } else {
            Optional.ofNullable(
                try {
                    StorageKinConfig.parseFrom(bytes)
                } catch (e: InvalidProtocolBufferException) {
                    e.printStackTrace()
                    null
                }
            )
        }
    }

    override fun getOrCreateCID(): String {
        val exisingkinConfig = getKinConfig()
        return if (exisingkinConfig.isPresent) {
            exisingkinConfig.get()!!.cid
        } else {
            val newCid = UUID.randomUUID().toString()
            val newKinConfig = StorageKinConfig.newBuilder()
                .setCid(newCid)
                .build()

            setKinConfig(newKinConfig)
            newCid
        }
    }

    override fun getStoredTransactions(accountId: KinAccount.Id): Promise<KinTransactions?> {
        return getInvoiceListsMapForAccountId(accountId).flatMap { invoiceListMap ->
            Promise.create<KinTransactions?> { resolve, _ ->
                val transactions = getTransactions(accountId)
                val transactionsWithInvoices = transactions?.copy(
                    items = transactions.items.map { transaction ->
                        transaction.memo.getAgoraMemo()?.let {
                            when (transaction) {
                                is SolanaKinTransaction -> {
                                    transaction.copy(
                                        invoiceList = invoiceListMap[InvoiceList.Id(
                                            SHA224Hash.just(
                                                it.foreignKeyBytes
                                            )
                                        )]
                                    )
                                }
                                is StellarKinTransaction -> {
                                    transaction.copy(
                                        invoiceList = invoiceListMap[InvoiceList.Id(
                                            SHA224Hash.just(
                                                it.foreignKeyBytes
                                            )
                                        )]
                                    )
                                }
                                else -> transaction
                            }
                        } ?: transaction
                    }
                )
                resolve(transactionsWithInvoices)
            }
        }.workOn(executors.sequentialIO)
    }

    override fun storeTransactions(
        accountId: KinAccount.Id,
        transactions: List<KinTransaction>
    ): Promise<List<KinTransaction>> {
        if (transactions.isEmpty()) {
            return Promise.of(transactions)
        }
        return addInvoiceLists(accountId, transactions.mapNotNull { it.invoiceList }).flatMap {
            Promise.create<List<KinTransaction>> { resolve, _ ->
                putTransactions(
                    accountId,
                    KinTransactions(
                        transactions,
                        transactions.findHeadHistoricalTransaction(),
                        transactions.findTailHistoricalTransaction()
                    )
                )
                resolve(transactions)
            }
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
            .map { listOf(newTransaction) + it }
            .flatMap { storeTransactions(accountId, it) }
    }

    override fun getInvoiceListsMapForAccountId(account: KinAccount.Id): Promise<Map<InvoiceList.Id, InvoiceList>> {
        return Promise.create { resolve, reject ->
            try {
                val bytes = readFile(
                    directoryForInvoices(account),
                    fileNameForInvoices(account)
                )

                val map = if (bytes.isEmpty()) {
                    emptyMap()
                } else {
                    val invoices: org.kin.gen.storage.v1.Storage.Invoices =
                        org.kin.gen.storage.v1.Storage.Invoices.parseFrom(bytes)

                    invoices.invoiceListsMap
                        .mapKeys {
                            InvoiceList.Id(SHA224Hash(it.key))
                        }
                        .mapValues {
                            Model.InvoiceList.parseFrom(it.value.networkInvoiceList.toByteArray())
                                .toInvoiceList()
                        }
                }
                resolve(map)

            } catch (t: Throwable) {
                reject(t)
            }
        }
    }

    override fun addInvoiceLists(
        accountId: KinAccount.Id,
        invoiceLists: List<InvoiceList>
    ): Promise<List<InvoiceList>> {

        fun putInvoiceListsForAccountId(
            account: KinAccount.Id,
            invoiceLists: Map<InvoiceList.Id, InvoiceList>
        ): Boolean = writeToFile(
            directoryForInvoices(account),
            fileNameForInvoices(account),
            invoiceLists.toInvoices().toByteArray()
        )

        if (invoiceLists.isEmpty()) {
            return Promise.of(emptyList())
        }

        return getInvoiceListsMapForAccountId(accountId)
            .map {
                val updatedInvoiceLists = it.toMutableMap().apply {
                    invoiceLists.forEach { invoiceList ->
                        put(invoiceList.id, invoiceList)
                    }
                }
                putInvoiceListsForAccountId(accountId, updatedInvoiceLists)
                updatedInvoiceLists.values.toList()
            }
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

    override fun updateAccountBalance(
        accountId: KinAccount.Id,
        balance: KinBalance
    ): Promise<Optional<KinAccount>> {
        return getStoredAccount(accountId)
            .map { storedAccount ->
                storedAccount.map { account ->
                    account.copy(
                        balance = balance
                    ).also { updateAccount(it) }
                }
            }
    }

    override fun setMinFee(minFee: QuarkAmount): Promise<Optional<QuarkAmount>> {
        return Promise.create<Optional<QuarkAmount>> { resolve, _ ->
            val updatedKinConfig = getKinConfig()
                .map {
                    it.toBuilder()
                }
                .orElse {
                    StorageKinConfig.newBuilder()
                }
                .setMinFee(minFee.value.toLong())
                .build()

            resolve(
                if (setKinConfig(updatedKinConfig)) Optional.of(minFee)
                else Optional.empty<QuarkAmount>()
            )
        }.workOn(executors.sequentialIO)
    }

    override fun getMinFee(): Promise<Optional<QuarkAmount>> {
        return Promise.create<Optional<QuarkAmount>> { resolve, _ ->
            resolve(getKinConfig().map { QuarkAmount(it.minFee) })
        }.workOn(executors.sequentialIO)
    }

    override fun deleteAllStorage(accountId: KinAccount.Id): Promise<Boolean> {
        return Promise.create<Boolean> { resolve, _ ->
            resolve(
                removeAllInvoices(accountId)
                        and removeAllTransactions(accountId)
                        and removeAccount(accountId)
                        and removeServiceConfig()
            )
        }.workOn(executors.sequentialIO)
    }

    override fun deleteAllStorage(): Promise<Boolean> {
        return Promise.create<Boolean> { resolve, _ ->
            val accountDeletes = getAllAccountIds().map {
                removeAllInvoices(it) and removeAllTransactions(it) and removeAccount(it)
            }.reduce { acc, b -> acc and b }
            resolve(accountDeletes and removeServiceConfig())
        }.workOn(executors.sequentialIO)
    }

    override fun setMinApiVersion(apiVersion: Int): Promise<Int> {
        return Promise.create<Int> { resolve, reject ->
            val updatedKinConfig = getKinConfig()
                .map {
                    it.toBuilder()
                }
                .orElse {
                    StorageKinConfig.newBuilder()
                }
                .setMinApiVersion(apiVersion.toLong())
                .build()

            if (setKinConfig(updatedKinConfig)) {
                resolve(apiVersion)
            } else {
                reject(Exception("Failed to set minApiVersion"))
            }
        }.workOn(executors.sequentialIO)
    }

    override fun getMinApiVersion(): Promise<Optional<Int>> {
        return Promise.create<Optional<Int>> { resolve, _ ->
            resolve(getKinConfig().map { it.minApiVersion.toInt() })
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
     * KinTransactions: <Account Directory>/<account_id>_transactions/<account_id>_transactions
     * Invoices: <Account Directory>/<account_id>_invoices/<account_id>_invoices
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

    private fun directoryForInvoices(accountId: KinAccount.Id): String {
        return "${directoryForAccount(accountId)}_invoices"
    }

    private fun fileNameForTransactions(accountId: KinAccount.Id): String =
        "${accountId.hashCode()}_transactions"

    private fun fileNameForInvoices(accountId: KinAccount.Id): String =
        "${accountId.hashCode()}_invoices"

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
            .addAllAccounts(
                tokenAccounts.map {
                    StoragePublicKey.newBuilder()
                        .setValue(ByteString.copyFrom(it.value))
                        .build()
                }
            )
            .build()

    private fun KinBalance.toStorageKinBalance(): StorageKinBalance =
        StorageKinBalance.newBuilder()
            // ask about quark conversion
            .setQuarkAmount(amount.toQuarks().value.toLong())
            .setPendingQuarkAmount(pendingAmount.toQuarks().value.toLong())
            .build()

    private fun KinTransaction.toStorageKinTransaction(): StorageKinTransaction =
        StorageKinTransaction.newBuilder()
            .setEnvelopeXdr(ByteString.copyFrom(this.bytesValue))
            .apply {
                when (this@toStorageKinTransaction.recordType) {
                    is KinTransaction.RecordType.InFlight -> {
                        this.setStatus(StorageKinTransaction.Status.INFLIGHT)
                        this.setTimestamp(this@toStorageKinTransaction.recordType.timestamp)
                    }
                    is KinTransaction.RecordType.Acknowledged -> {
                        this.setStatus(StorageKinTransaction.Status.ACKNOWLEDGED)
                        this.setTimestamp(this@toStorageKinTransaction.recordType.timestamp)
                        this.setResultXdr(ByteString.copyFrom((this@toStorageKinTransaction.recordType as KinTransaction.RecordType.Acknowledged).resultXdrBytes))
                    }
                    is KinTransaction.RecordType.Historical -> {
                        this.setStatus(StorageKinTransaction.Status.HISTORICAL)
                        this.setTimestamp(this@toStorageKinTransaction.recordType.timestamp)
                        this.setResultXdr(ByteString.copyFrom((this@toStorageKinTransaction.recordType as KinTransaction.RecordType.Historical).resultXdrBytes))
                        this.setPagingToken((this@toStorageKinTransaction.recordType as KinTransaction.RecordType.Historical).pagingToken.value)
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

    fun Map<InvoiceList.Id, InvoiceList>.toInvoices(): org.kin.gen.storage.v1.Storage.Invoices {
        return org.kin.gen.storage.v1.Storage.Invoices.newBuilder()
            .apply {
                this@toInvoices.entries.forEach {
                    this@apply.putInvoiceLists(
                        it.key.invoiceHash.encodedValue,
                        org.kin.gen.storage.v1.Storage.InvoiceListBlob.newBuilder()
                            .setNetworkInvoiceList(it.value.toProto().toByteString())
                            .build()
                    )
                }
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
            StorageKinAccount.Status.UNRECOGNIZED,
            null -> throw InvalidProtocolBufferException("Unrecognized account status.")
        }
        val accounts = mutableListOf<Key.PublicKey>()
        if (accountsList.isNotEmpty()) {
            accounts.addAll(accountsList.map { it.toPublicKey() })
        }

        return KinAccount(key = key, tokenAccounts = accounts, balance = balance, status = status)
    }

    private fun StoragePublicKey.toPublicKey(): Key.PublicKey =
        Key.PublicKey(this.value.toByteArray())

    private fun StoragePrivateKey.toPrivateKey(): Key.PrivateKey =
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
        var transaction: KinTransaction
        with(
            StellarKinTransaction(
                this.envelopeXdr.toByteArray(),
                recordType,
                networkEnvironment
            )
        ) {
            try {
                transactionEnvelope // Will explode if not a StellarKinTransaction TODO find a better test
                transaction = this
            } catch (t: Throwable) {
                transaction = SolanaKinTransaction(
                    this@toKinTransaction.envelopeXdr.toByteArray(),
                    recordType,
                    networkEnvironment
                )
            }
        }
        return transaction
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
