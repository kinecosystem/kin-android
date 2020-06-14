package org.kin.sdk.base

import okhttp3.OkHttpClient
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.network.api.FriendBotApi
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.rest.DefaultHorizonKinAccountCreationApi
import org.kin.sdk.base.network.api.rest.DefaultHorizonKinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.rest.HorizonKinApi
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.network.services.KinServiceImpl
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.Callback
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.NetworkOperationsHandler
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.callback
import org.slf4j.ILoggerFactory
import org.slf4j.LoggerFactory

sealed class KinEnvironment {
    abstract val networkEnvironment: NetworkEnvironment
    abstract val logger: ILoggerFactory
    abstract val service: KinService
    internal abstract val storage: Storage
    internal abstract val executors: ExecutorServices
    internal abstract val networkHandler: NetworkOperationsHandler

    class Horizon private constructor(
        internal val okHttpClient: OkHttpClient,
        override val networkEnvironment: NetworkEnvironment,
        override val logger: ILoggerFactory,
        override val storage: Storage,
        override val executors: ExecutorServices,
        override val networkHandler: NetworkOperationsHandler,
        override val service: KinService
    ) : KinEnvironment() {
        class Builder(private val networkEnvironment: NetworkEnvironment) {
            private var accountCreationApi: KinAccountCreationApi? = null
            private var transactionWhitelistingApi: KinTransactionWhitelistingApi? = null
            private var okHttpClient: OkHttpClient? = null
            private var executors: ExecutorServices? = null
            private var logger: ILoggerFactory? = null
            private var networkHandler: NetworkOperationsHandler? = null
            private var service: KinService? = null

            private lateinit var storage: Storage
            private var storageBuilder: KinFileStorage.Builder? = null

            inner class CompletedBuilder internal constructor() {
                private fun NetworkEnvironment.horizonApiConfig() = when (this) {
                    NetworkEnvironment.KinStellarTestNet -> ApiConfig.TestNetHorizon
                    NetworkEnvironment.KinStellarMainNet -> ApiConfig.MainNetHorizon
                }

                fun build(): KinEnvironment {
                    val okHttpClient = okHttpClient ?: OkHttpClient.Builder().build()
                    val logger = logger ?: LoggerFactory.getILoggerFactory()
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
                        accountCreationApi ?: DefaultHorizonKinAccountCreationApi(
                            networkEnvironment.horizonApiConfig(),
                            FriendBotApi(okHttpClient)
                        ),
                        transactionWhitelistingApi ?: DefaultHorizonKinTransactionWhitelistingApi()
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

            internal fun setOkHttpClient(okHttpClient: OkHttpClient): Builder {
                this.okHttpClient = okHttpClient
                return this
            }

            internal fun setExecutorServices(executors: ExecutorServices): Builder {
                this.executors = executors
                return this
            }

            internal fun setNetworkOperationsHandler(networkHandler: NetworkOperationsHandler): Builder {
                this.networkHandler = networkHandler
                return this
            }

            fun setLogger(logger: ILoggerFactory): Builder {
                this.logger = logger
                return this
            }

            fun setKinService(kinService: KinService): Builder {
                this.service = kinService
                return this
            }

            fun setKinAccountCreationApi(accountCreationApi: KinAccountCreationApi): Builder {
                this.accountCreationApi = accountCreationApi
                return this
            }

            fun setKinTransactionWhitelistingApi(transactionWhitelistingApi: KinTransactionWhitelistingApi): Builder {
                this.transactionWhitelistingApi = transactionWhitelistingApi
                return this
            }

            fun setStorage(storage: Storage): CompletedBuilder {
                this.storage = storage
                return CompletedBuilder()
            }

            fun setStorage(fileStorageBuilder: KinFileStorage.Builder): CompletedBuilder {
                this.storageBuilder = fileStorageBuilder
                return CompletedBuilder()
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
}


