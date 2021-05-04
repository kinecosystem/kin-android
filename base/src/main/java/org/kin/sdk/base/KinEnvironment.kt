package org.kin.sdk.base

import io.grpc.ManagedChannel
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.network.api.agora.AgoraKinAccountApiV4
import org.kin.sdk.base.network.api.agora.AgoraKinAccountCreationApiV4
import org.kin.sdk.base.network.api.agora.AgoraKinTransactionsApiV4
import org.kin.sdk.base.network.api.agora.AppUserAuthInterceptor
import org.kin.sdk.base.network.api.agora.KinVersionInterceptor
import org.kin.sdk.base.network.api.agora.LoggingInterceptor
import org.kin.sdk.base.network.api.agora.OkHttpChannelBuilderForcedTls12
import org.kin.sdk.base.network.api.agora.UserAgentInterceptor
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.network.services.KinServiceImplV4
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
            private var enableLogging: Boolean = networkEnvironment == NetworkEnvironment.TestNet
            private var logger: KinLoggerFactory? = null
            private var networkHandler: NetworkOperationsHandler? = null
            private var appInfoProvider: AppInfoProvider? = null
            private var service: KinService? = null
            private var minApiVersion: Int = 4

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
                    val managedChannel =
                        managedChannel ?: networkEnvironment.agoraApiConfig()
                            .asManagedChannel(logger, minApiVersion)


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

                    val service = buildV4ApiService()

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
                    NetworkEnvironment.TestNet -> ApiConfig.TestNetAgora
                    NetworkEnvironment.MainNet -> ApiConfig.MainNetAgora
                }

                private fun ApiConfig.asManagedChannel(logger: KinLoggerFactory, blockchainVersion: Int) =
                    OkHttpChannelBuilderForcedTls12.forAddress(networkEndpoint, tlsPort)
                        .intercept(
                            *listOfNotNull(
                                AppUserAuthInterceptor(appInfoProvider!!),
                                UserAgentInterceptor(storage),
                                LoggingInterceptor(logger),
                                KinVersionInterceptor(blockchainVersion)
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




