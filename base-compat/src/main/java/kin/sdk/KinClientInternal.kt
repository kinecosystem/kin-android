package kin.sdk

import android.content.Context
import kin.sdk.exception.CorruptedDataException
import kin.sdk.exception.CreateAccountException
import kin.sdk.exception.CryptoException
import kin.sdk.exception.DeleteAccountException
import kin.sdk.exception.LoadAccountException
import kin.sdk.exception.OperationFailedException
import kin.sdk.internal.BackupRestoreImpl
import kin.sdk.internal.KeyStoreImpl
import kin.sdk.internal.KinAccountImpl
import kin.sdk.internal.SharedPrefStore
import kin.sdk.internal.sync
import kin.utils.Request
import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.KinEnvironment.Horizon
import org.kin.sdk.base.models.AppId
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.Promise
import org.kin.stellarfork.KeyPair
import org.slf4j.Logger
import java.util.ArrayList
import java.util.HashMap

/**
 * An account manager for a [KinAccount].
 */
internal class KinClientInternal {
    companion object {
        private const val STORE_NAME_PREFIX = "KinKeyStore_"

        private fun initKeyStore(
            context: Context,
            id: String,
            backupRestore: BackupRestore
        ): KeyStore {
            val store = SharedPrefStore(
                context.getSharedPreferences(STORE_NAME_PREFIX + id, Context.MODE_PRIVATE)
            )
            return KeyStoreImpl(store, backupRestore)
        }

        private fun enirvonmentToKinEnvironment(
            environment: Environment,
            storage: Storage
        ): KinEnvironment {
            return Horizon.Builder(environmentToNetworkEnvironment(environment))
                .setStorage(storage)
                .build()
        }

        private fun environmentToNetworkEnvironment(environment: Environment?): NetworkEnvironment {
            return when {
                environment != null && environment.isMainNet -> NetworkEnvironment.KinStellarMainNet
                else -> NetworkEnvironment.KinStellarTestNet
            }
        }
    }

    val environment: Environment
    val appId: String
    val storeKey: String

    private val context: Context
    private val backupRestore: BackupRestore
    private val keyStore: KeyStore
    private val storage: Storage

    private val kinEnvironment: KinEnvironment

    private var kinAccounts: MutableList<KinAccountImpl>
    private val log: Logger

    /**
     * Build KinClient object.
     *
     * @param context     android context
     * @param environment the blockchain network details.
     * @param appId       a 4 character string which represent the application id which will be added to each transaction.
     * <br></br>**Note:** appId must contain only upper and/or lower case letters and/or digits and that the total string length is between 3 to 4.
     * For example 1234 or 2ab3 or bcda, etc.
     * @param storeKey    an optional param which is the key for storing this KinClient data, different keys will store a different accounts.
     */
    constructor(
        context: Context,
        environment: Environment,
        appId: String,
        storeKey: String
    ) : this(context, environment, appId, storeKey, BackupRestoreImpl())

    /**
     * For more details please look at [KinClientInternal]
     */
    constructor(
        context: Context,
        environment: Environment,
        appId: String
    ) : this(context, environment, appId, "")

    @JvmOverloads
    constructor(
        context: Context,
        environment: Environment,
        appId: String,
        storeKey: String,
        backupRestore: BackupRestore,
        keyStore: KeyStore,
        storage: Storage,
        kinEnvironment: KinEnvironment = enirvonmentToKinEnvironment(environment, storage)
    ) {
        this.context = context
        this.environment = environment
        this.appId = appId
        this.storeKey = storeKey
        this.backupRestore = backupRestore
        this.keyStore = keyStore
        this.storage = storage
        this.kinEnvironment = kinEnvironment
        this.kinAccounts = ArrayList(1)
        this.log = kinEnvironment.logger.getLogger(javaClass.simpleName)
        validateAppId(appId)
        loadAccounts()
    }

    private constructor(
        context: Context,
        environment: Environment,
        appId: String,
        storeKey: String,
        backupRestore: BackupRestore
    ) : this(
        context,
        environment,
        appId,
        storeKey,
        initKeyStore(
            context.applicationContext,
            storeKey,
            backupRestore
        ),
        KinFileStorage.Builder(context.filesDir.absolutePath + "kin.sdk_" + storeKey)
            .setNetworkEnvironment(environmentToNetworkEnvironment(environment))
            .build()
    )

    private constructor(
        context: Context,
        environment: Environment,
        appId: String,
        storeKey: String,
        keyStore: KeyStore,
        storage: Storage
    ) : this(
        context,
        environment,
        appId,
        storeKey,
        BackupRestoreImpl(),
        keyStore,
        storage,
        enirvonmentToKinEnvironment(environment, storage)
    )

    private fun loadAccounts() {
        var accounts: List<KeyPair>? = null
        try {
            accounts = keyStore.loadAccounts()
        } catch (e: LoadAccountException) {
            e.printStackTrace()
        }
        if (accounts != null && !accounts.isEmpty()) {
            updateKinAccounts(accounts)
        }
    }

    private fun updateKinAccounts(storageAccounts: List<KeyPair>) {
        val accountsMap: MutableMap<String?, KinAccountImpl> =
            HashMap()
        for (kinAccountImpl in kinAccounts) {
            accountsMap[kinAccountImpl.publicAddress] = kinAccountImpl
        }
        val newKinAccountsList: MutableList<KinAccountImpl> =
            ArrayList()
        for (account in storageAccounts) {
            val inMemoryKinAccount = accountsMap[account.accountId]
            if (inMemoryKinAccount != null) {
                newKinAccountsList.add(inMemoryKinAccount)
            } else {
                newKinAccountsList.add(createNewKinAccount(account))
            }
        }
        kinAccounts = newKinAccountsList
    }

    private fun validateAppId(appId: String) {
        if (appId == "") {
            log.info(
                "KinClient",
                "WARNING: KinClient instance was created without a proper application ID. Is this what you intended to do?"
            )
        } else require(appId.matches(Regex("[a-zA-Z0-9]{3,4}"))) {
            """
                appId must contain only upper and/or lower case letters and/or digits and that the total string length is between 3 to 4.
                for example 1234 or 2ab3 or cd2 or fqa, etc.
                """.trimIndent()
        }
    }

    /**
     * Creates and adds an account.
     *
     * Once created, the account information will be stored securely on the device and can
     * be accessed again via the [.getAccount] method.
     *
     * @return [KinAccount] the account created store the kekinAccountsy.
     */
    @Throws(CreateAccountException::class)
    fun addAccount(): KinAccount {
        val account = keyStore.newAccount()
        return addKeyPair(account)
    }

    /**
     * Import an account from a JSON-formatted string.
     *
     * @param exportedJson The exported JSON-formatted string.
     * @param passphrase   The passphrase to decrypt the secret key.
     * @return The imported account
     */
    @Throws(
        CryptoException::class,
        CreateAccountException::class,
        CorruptedDataException::class
    )
    fun importAccount(exportedJson: String, passphrase: String): KinAccount {
        val account = keyStore.importAccount(exportedJson, passphrase)
        val kinAccount = getAccountByPublicAddress(account.accountId)
        return kinAccount ?: addKeyPair(account)
    }

    fun getAccountByPublicAddress(accountId: String): KinAccount? {
        loadAccounts()
        return kinAccounts.indices
            .asSequence()
            .map { kinAccounts[it] }
            .lastOrNull { accountId == it.publicAddress }
    }

    private fun addKeyPair(account: KeyPair): KinAccount {
        val newAccount = createNewKinAccount(account)
        kinAccounts.add(newAccount)
        return newAccount
    }

    /**
     * Returns an account at input index.
     *
     * @return the account at the input index or null if there is no such account
     */
    fun getAccount(index: Int): KinAccount? {
        loadAccounts()
        return if (index >= 0 && kinAccounts.size > index) {
            kinAccounts[index]
        } else null
    }

    /**
     * @return true if there is an existing account
     */
    fun hasAccount(): Boolean {
        return accountCount != 0
    }

    /**
     * Returns the number of existing accounts
     */
    val accountCount: Int
        get() {
            loadAccounts()
            return kinAccounts.size
        }

    /**
     * Deletes the account at input index (if it exists)
     *
     * @return true if the delete was successful or false otherwise
     * @throws DeleteAccountException in case of a delete account exception while trying to delete the account
     */
    @Throws(DeleteAccountException::class)
    fun deleteAccount(index: Int): Boolean {
        var deleteSuccess = false
        if (index in 0 until accountCount) {
            val accountToDelete = kinAccounts[index].publicAddress
            keyStore.deleteAccount(accountToDelete!!)
            val removedAccount = kinAccounts.removeAt(index)
            removedAccount.markAsDeleted()
            deleteSuccess = true
        }
        return deleteSuccess
    }

    /**
     * Deletes all accounts.
     */
    fun clearAllAccounts() {
        keyStore.clearAllAccounts()
        kinAccounts.forEach { kinAccount ->
            kinAccount.markAsDeleted()
        }
        kinAccounts.clear()
    }

    private fun createNewKinAccount(account: KeyPair): KinAccountImpl {
        val accountContext: KinAccountContext =
            KinAccountContext.Builder(kinEnvironment)
                .importExistingPrivateKey(account.asPrivateKey()).build()

        return KinAccountImpl(
            account,
            backupRestore,
            accountContext,
            kinEnvironment.service,
            kinEnvironment.networkEnvironment,
            AppId(appId)
        )
    }

    /**
     * Get the current minimum fee that the network charges per operation.
     * This value is expressed in stroops.
     *
     * @return `Request<Integer>` - the minimum fee.
     */
    val minimumFee: Request<Long>
        get() = Request(
            getMinFeeInternal(),
            this::exceptionCorrectionIfNecessary
        )

    /**
     * Get the current minimum fee that the network charges per operation.
     * This value is expressed in stroops.
     *
     * **Note:** This method may accesses the network, and should not be called on the android main thread.
     *
     * @return the minimum fee.
     */
    @get:Throws(OperationFailedException::class, KinService.SDKUpgradeRequired::class)
    val minimumFeeSync: Long
        get() {
            return try {
                getMinFeeInternal().sync()
            } catch (e: Exception) {
                throw exceptionCorrectionIfNecessary(e)
            }
        }

    private fun getMinFeeInternal(): Promise<Long> {
        return storage.getMinFee()
            .flatMap {
                it.map { Promise.of(it) }
                    .orElse {
                        kinEnvironment.service.getMinFee()
                            .doOnResolved { storage.setMinFee(it).resolve() }
                    }
            }
            .map(QuarkAmount::value)
    }

    private fun exceptionCorrectionIfNecessary(e: Exception): Exception {
        return if (e is KinService.SDKUpgradeRequired) e
        else OperationFailedException(e)
    }
}
