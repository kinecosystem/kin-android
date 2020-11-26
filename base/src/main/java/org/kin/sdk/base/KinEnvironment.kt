package org.kin.sdk.base

import io.grpc.ManagedChannel
import okhttp3.OkHttpClient
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.isKin2
import org.kin.sdk.base.network.api.FriendBotApi
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinStreamingApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.agora.AgoraKinAccountApiV4
import org.kin.sdk.base.network.api.agora.AgoraKinAccountCreationApiV4
import org.kin.sdk.base.network.api.agora.AgoraKinAccountsApi
import org.kin.sdk.base.network.api.agora.AgoraKinTransactionsApi
import org.kin.sdk.base.network.api.agora.AgoraKinTransactionsApiV4
import org.kin.sdk.base.network.api.agora.AppUserAuthInterceptor
import org.kin.sdk.base.network.api.agora.KinVersionInterceptor
import org.kin.sdk.base.network.api.agora.LoggingInterceptor
import org.kin.sdk.base.network.api.agora.OkHttpChannelBuilderForcedTls12
import org.kin.sdk.base.network.api.agora.UpgradeApiV4Interceptor
import org.kin.sdk.base.network.api.agora.UserAgentInterceptor
import org.kin.sdk.base.network.api.horizon.DefaultHorizonKinAccountCreationApi
import org.kin.sdk.base.network.api.horizon.DefaultHorizonKinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.horizon.HorizonKinApi
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.network.services.KinServiceImpl
import org.kin.sdk.base.network.services.KinServiceImplV4
import org.kin.sdk.base.network.services.KinServiceWrapper
import org.kin.sdk.base.network.services.MetaServiceApiImpl
import org.kin.sdk.base.repository.AppInfoRepository
import org.kin.sdk.base.repository.InMemoryAppInfoRepositoryImpl
import org.kin.sdk.base.repository.InMemoryInvoiceRepositoryImpl
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.Callback
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.KinLoggerFactory
import org.kin.sdk.base.tools.KinLoggerFactoryImpl
import org.kin.sdk.base.tools.NetworkOperationsHandler
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.callback

sealed class KinEnvironment {
    abstract val networkEnvironment: NetworkEnvironment
    abstract val logger: KinLoggerFactory
    abstract val service: KinService
    internal abstract val storage: Storage
    internal abstract val executors: ExecutorServices
    internal abstract val networkHandler: NetworkOperationsHandler

    @Deprecated("Please use [KinEnvironment.Agora] instead. Horizon may dissapear in a future blockchain migration.")
    class Horizon private constructor(
        internal val okHttpClient: OkHttpClient,
        override val networkEnvironment: NetworkEnvironment,
        override val logger: KinLoggerFactory,
        override val storage: Storage,
        override val executors: ExecutorServices,
        override val networkHandler: NetworkOperationsHandler,
        override val service: KinService,
    ) : KinEnvironment() {
        class Builder(private val networkEnvironment: NetworkEnvironment) {
            private var accountCreationApi: KinAccountCreationApi? = null
            private var transactionWhitelistingApi: KinTransactionWhitelistingApi? = null
            private var enableLogging: Boolean = false
            private var okHttpClient: OkHttpClient? = null
            private var executors: ExecutorServices? = null
            private var logger: KinLoggerFactory? = null
            private var networkHandler: NetworkOperationsHandler? = null
            private var service: KinService? = null

            private lateinit var storage: Storage
            private var storageBuilder: KinFileStorage.Builder? = null

            inner class CompletedBuilder internal constructor() {
                private fun NetworkEnvironment.horizonApiConfig() = when (this) {
                    NetworkEnvironment.KinStellarTestNetKin3 -> ApiConfig.TestNetHorizon
                    NetworkEnvironment.KinStellarMainNetKin3 -> ApiConfig.MainNetHorizon
                    NetworkEnvironment.KinStellarTestNetKin2 -> throw NotImplementedError("Unsupported: please upgrade to Agora")
                    NetworkEnvironment.KinStellarMainNetKin2 -> throw NotImplementedError("Unsupported: please upgrade to Agora")
                }

                fun build(): KinEnvironment {
                    val okHttpClient = okHttpClient ?: OkHttpClient.Builder().build()
                    val logger = logger ?: KinLoggerFactoryImpl(enableLogging)
                    val executors = executors ?: ExecutorServices()
                    val networkHandler = networkHandler ?: NetworkOperationsHandlerImpl(
                        executors.sequentialScheduled,
                        executors.parallelIO,
                        logger,
                        shouldRetryError = { it is KinService.FatalError.TransientFailure }
                    )
                    val api = HorizonKinApi(
                        networkEnvironment.horizonApiConfig(),
                        okHttpClient
                    )
                    val service = service ?: KinServiceImpl(
                        networkEnvironment,
                        networkHandler,
                        api as KinAccountApi,
                        api as KinTransactionApi,
                        api as KinStreamingApi,
                        accountCreationApi ?: DefaultHorizonKinAccountCreationApi(
                            networkEnvironment.horizonApiConfig(),
                            FriendBotApi(okHttpClient)
                        ),
                        transactionWhitelistingApi ?: DefaultHorizonKinTransactionWhitelistingApi(),
                        logger
                    )

                    val storageBuilder = storageBuilder
                    if (!this@Builder::storage.isInitialized && storageBuilder != null) {
                        storage = storageBuilder.setNetworkEnvironment(networkEnvironment).build()
                    }

                    return Horizon(
                        okHttpClient = okHttpClient,
                        networkEnvironment = networkEnvironment,
                        logger = logger,
                        storage = storage,
                        executors = executors,
                        networkHandler = networkHandler,
                        service = service
                    )
                }
            }

            internal fun setOkHttpClient(okHttpClient: OkHttpClient): Builder = apply {
                this.okHttpClient = okHttpClient
            }

            internal fun setExecutorServices(executors: ExecutorServices): Builder = apply {
                this.executors = executors
            }

            internal fun setNetworkOperationsHandler(networkHandler: NetworkOperationsHandler): Builder =
                apply {
                    this.networkHandler = networkHandler
                }

            fun setLogger(logger: KinLoggerFactory): Builder = apply {
                this.logger = logger
            }

            fun setKinService(kinService: KinService): Builder = apply {
                this.service = kinService
            }

            fun setKinAccountCreationApi(accountCreationApi: KinAccountCreationApi): Builder =
                apply {
                    this.accountCreationApi = accountCreationApi
                }

            fun setKinTransactionWhitelistingApi(transactionWhitelistingApi: KinTransactionWhitelistingApi): Builder =
                apply {
                    this.transactionWhitelistingApi = transactionWhitelistingApi
                }

            fun setEnableLogging(): Builder = apply { this.enableLogging = true }

            fun setStorage(storage: Storage): CompletedBuilder {
                this.storage = storage
                return CompletedBuilder()
            }

            fun setStorage(fileStorageBuilder: KinFileStorage.Builder): CompletedBuilder =
                with(this) {
                    this.storageBuilder = fileStorageBuilder
                    CompletedBuilder()
                }
        }
    }

    class Agora private constructor(
        private val managedChannel: ManagedChannel,
        override val networkEnvironment: NetworkEnvironment,
        override val logger: KinLoggerFactory,
        override val storage: Storage,
        override val executors: ExecutorServices,
        override val networkHandler: NetworkOperationsHandler,
        override val service: KinService,
        val appInfoRepository: AppInfoRepository = InMemoryAppInfoRepositoryImpl(),
        val invoiceRepository: InvoiceRepository = InMemoryInvoiceRepositoryImpl(),
        val appInfoProvider: AppInfoProvider,
    ) : KinEnvironment() {
        class Builder(private val networkEnvironment: NetworkEnvironment) {
            private var managedChannel: ManagedChannel? = null
            private var executors: ExecutorServices? = null
            private var enableLogging: Boolean = networkEnvironment == NetworkEnvironment.KinStellarTestNetKin3
            private var logger: KinLoggerFactory? = null
            private var networkHandler: NetworkOperationsHandler? = null
            private var appInfoProvider: AppInfoProvider? = null
            private var service: KinService? = null
            private var minApiVersion: Int = 3
            private var testMigration = false

            private lateinit var storage: Storage
            private var storageBuilder: KinFileStorage.Builder? = null

            inner class CompletedBuilder internal constructor() {
                fun build(): Agora {
                    val logger = logger ?: KinLoggerFactoryImpl(enableLogging)
                    val executors = executors ?: ExecutorServices()
                    val networkHandler = networkHandler ?: NetworkOperationsHandlerImpl(
                        executors.sequentialScheduled,
                        executors.parallelIO,
                        logger,
                        shouldRetryError = { it is KinService.FatalError.TransientFailure }
                    )
                    val appInfoProvider = appInfoProvider
                        ?: throw KinEnvironmentBuilderException("Must provide an ApplicationDelegate!")
                    val storageBuilder = storageBuilder
                    if (!this@Builder::storage.isInitialized && storageBuilder != null) {
                        storage = storageBuilder.setNetworkEnvironment(networkEnvironment).build()
                    }
                    val blockchainVersion = if (networkEnvironment.isKin2()) 2 else minApiVersion
                    val managedChannel =
                        managedChannel ?: networkEnvironment.agoraApiConfig()
                            .asManagedChannel(logger, blockchainVersion)


                    fun buildV3ApiService(): KinService {
                        val accountsApi = AgoraKinAccountsApi(managedChannel, networkEnvironment)
                        val transactionsApi =
                            AgoraKinTransactionsApi(
                                managedChannel,
                                networkEnvironment
                            )
                        return service ?: KinServiceImpl(
                            networkEnvironment,
                            networkHandler,
                            accountsApi,
                            transactionsApi,
                            accountsApi,
                            accountsApi,
                            transactionsApi,
                            logger
                        )
                    }

                    fun buildV4ApiService(): KinService {
                        val accountsApi = AgoraKinAccountApiV4(managedChannel, networkEnvironment)
                        val accountCreationApi = AgoraKinAccountCreationApiV4(managedChannel)
                        val transactionsApi =
                            AgoraKinTransactionsApiV4(
                                managedChannel,
                                networkEnvironment
                            )
                        return service ?: KinServiceImplV4(
                            networkEnvironment,
                            networkHandler,
                            accountsApi,
                            transactionsApi,
                            accountsApi,
                            accountCreationApi,
                            logger
                        )
                    }

                    val metaServiceApi = MetaServiceApiImpl(minApiVersion, networkHandler, AgoraKinTransactionsApiV4(managedChannel, networkEnvironment), storage)
                    metaServiceApi.postInit()
                    val service = KinServiceWrapper(
                        buildV3ApiService(),
                        buildV4ApiService(),
                        metaServiceApi
                    )

                    return Agora(
                        managedChannel,
                        networkEnvironment = networkEnvironment,
                        logger = logger,
                        storage = storage,
                        executors = executors,
                        networkHandler = networkHandler,
                        service = service,
                        appInfoProvider = appInfoProvider
                    ).apply {
                        appInfoRepository.addAppInfo(appInfoProvider.appInfo)

                        with(storage) {
                            getAllAccountIds().forEach {
                                getInvoiceListsMapForAccountId(it)
                                    .flatMap {
                                        invoiceRepository.addAllInvoices(it.values.map { it.invoices }
                                            .reduce { acc, list -> acc + list })
                                    }.resolve()
                            }
                        }
                    }
                }

                private fun NetworkEnvironment.agoraApiConfig() = when (this) {
                    NetworkEnvironment.KinStellarTestNetKin3,
                    NetworkEnvironment.KinStellarTestNetKin2-> ApiConfig.TestNetAgora
                    NetworkEnvironment.KinStellarMainNetKin3,
                    NetworkEnvironment.KinStellarMainNetKin2-> ApiConfig.MainNetAgora
                }

                private fun ApiConfig.asManagedChannel(logger: KinLoggerFactory, blockchainVersion: Int) =
                    OkHttpChannelBuilderForcedTls12.forAddress(networkEndpoint, tlsPort)
                        .intercept(
                            *listOfNotNull(
                                AppUserAuthInterceptor(appInfoProvider!!),
                                UserAgentInterceptor(storage),
                                LoggingInterceptor(logger),
                                if (blockchainVersion == 2) KinVersionInterceptor(blockchainVersion) else null,
                                if (testMigration) UpgradeApiV4Interceptor() else null
                            ).toTypedArray()
                        )
                        .build()
            }

            internal fun setManagedChannel(managedChannel: ManagedChannel): Builder = apply {
                this.managedChannel = managedChannel
            }

            internal fun setExecutorServices(executors: ExecutorServices): Builder = apply {
                this.executors = executors
            }

            internal fun setNetworkOperationsHandler(networkHandler: NetworkOperationsHandler): Builder =
                apply {
                    this.networkHandler = networkHandler
                }

            /**
             * This option allows developers to force which api version the KinService should use.
             * v3 - stellar (Kin 2 or Kin 3 blockchains)
             * v4 - solana
             * It is *not* required to set this as we default to v3 until migration day to solana.
             */
            fun setMinApiVersion(minApiVersion: Int): Builder = apply {
                if (minApiVersion < 3 || minApiVersion > 4) {
                    throw IllegalArgumentException("$minApiVersion is not supported, must be 3 or 4")
                }
                this.minApiVersion = minApiVersion
            }

            /**
             * This option allows developers to force an on-demand migration from the Stellar based
             * Kin Blockchain to Solana on TestNet only.
             */
            fun testMigration(): Builder = apply {
                this.testMigration = true
            }

            fun setLogger(logger: KinLoggerFactory): Builder = apply {
                this.logger = logger
            }

            fun setAppInfoProvider(appInfoProvider: AppInfoProvider) = apply {
                this.appInfoProvider = appInfoProvider
            }

            fun setKinService(kinService: KinService): Builder = apply {
                this.service = kinService
            }

            fun setEnableLogging(): Builder = apply { this.enableLogging = true }

            fun setStorage(storage: Storage): CompletedBuilder = with(this) {
                this.storage = storage
                CompletedBuilder()
            }

            fun setStorage(fileStorageBuilder: KinFileStorage.Builder): Builder.CompletedBuilder =
                with(this) {
                    this.storageBuilder = fileStorageBuilder
                    CompletedBuilder()
                }
        }
    }

    fun importPrivateKey(privateKey: Key.PrivateKey): Promise<Boolean> {
        return Promise.create<Boolean> { resolve, reject ->
            if (storage.getAccount(privateKey.asKinAccountId()) == null) {
                service.getAccount(privateKey.asKinAccountId())
                    .then({
                        try {
                            resolve(storage.addAccount(it.copy(key = privateKey)))
                        } catch (t: Throwable) {
                            reject(t)
                        }
                    }, {
                        try {
                            resolve(storage.addAccount(KinAccount(privateKey)))
                        } catch (t: Throwable) {
                            reject(t)
                        }
                    })
            } else resolve(true)
        }
    }

    fun importPrivateKey(privateKey: Key.PrivateKey, callback: Callback<Boolean>) {
        return importPrivateKey(privateKey).callback(callback)
    }

    fun allAccountIds(): Promise<List<KinAccount.Id>> {
        return Promise.create { resolve, reject ->
            try {
                resolve(storage.getAllAccountIds())
            } catch (t: Throwable) {
                reject(t)
            }
        }
    }

    fun setEnableLogging(enableLogging: Boolean) {
        logger.isLoggingEnabled = enableLogging
    }

    class KinEnvironmentBuilderException(s: String) : IllegalStateException(s)
}




