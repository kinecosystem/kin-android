package org.kin.sdk.base.network.services

import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.NetworkOperationsHandler
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.onErrorResumeNext
import org.kin.sdk.base.tools.queueWork


interface MetaServiceApi {
    val configuredMinApi: Int
    fun getMinApiVersion(): Promise<Int>
}

class MetaServiceApiImpl(
    override var configuredMinApi: Int,
    private val opHandler: NetworkOperationsHandler,
    private val api: KinTransactionApiV4,
    private val storage: Storage
) : MetaServiceApi {

    fun postInit(): Promise<Unit> {
        return storage.getMinApiVersion().doOnResolved {
            it.map {
                if (it >= configuredMinApi) {
                    configuredMinApi = it
                }
            }
        }.map { Unit }
    }

    override fun getMinApiVersion(): Promise<Int> {
        return opHandler
            .queueWork<Int> { respond ->
                api.getMinKinVersion {
                    when (it.result) {
                        KinTransactionApiV4.GetMiniumumKinVersionResponse.Result.Ok ->
                            respond.onSuccess(it.version)
                        is KinTransactionApiV4.GetMiniumumKinVersionResponse.Result.TransientFailure ->
                            respond.onError?.invoke(KinService.FatalError.TransientFailure(it.result.error))
                        is KinTransactionApiV4.GetMiniumumKinVersionResponse.Result.UndefinedError ->
                            respond.onError?.invoke(it.result.error)
                    }
                }
            }
            .flatMap { storage.setMinApiVersion(it) }
            .onErrorResumeNext { Promise.of(configuredMinApi) }
            .doOnResolved { configuredMinApi = it }
    }
}

class KinServiceWrapper(
    private val kinServiceV3: KinService,
    private val kinServiceV4: KinService,
    val metaServiceApi: MetaServiceApi
) : KinService {

    private var configuredService: KinService = kinServiceV3

    override fun createAccount(
        accountId: KinAccount.Id,
        signer: Key.PrivateKey
    ): Promise<KinAccount> =
        checkAndMaybeUpgradeApi {
            configuredService.createAccount(accountId, signer)
        }

    override fun getAccount(accountId: KinAccount.Id): Promise<KinAccount> =
        checkAndMaybeUpgradeApi {
            configuredService.getAccount(accountId)
        }

    override fun resolveTokenAccounts(accountId: KinAccount.Id): Promise<List<Key.PublicKey>> =
        checkAndMaybeUpgradeApi {
            configuredService.resolveTokenAccounts(accountId)
        }

    override fun getLatestTransactions(kinAccountId: KinAccount.Id): Promise<List<KinTransaction>> =
        checkAndMaybeUpgradeApi {
            configuredService.getLatestTransactions(kinAccountId)
        }

    override fun getTransactionPage(
        kinAccountId: KinAccount.Id,
        pagingToken: KinTransaction.PagingToken,
        order: KinService.Order
    ): Promise<List<KinTransaction>> =
        checkAndMaybeUpgradeApi {
            configuredService.getTransactionPage(kinAccountId, pagingToken, order)
        }

    override fun getTransaction(transactionHash: TransactionHash): Promise<KinTransaction> =
        checkAndMaybeUpgradeApi {
            configuredService.getTransaction(transactionHash)
        }

    override fun canWhitelistTransactions(): Promise<Boolean> =
        checkAndMaybeUpgradeApi {
            configuredService.canWhitelistTransactions()
        }

    override fun getMinFee(): Promise<QuarkAmount> =
        checkAndMaybeUpgradeApi {
            configuredService.getMinFee()
        }

    override fun buildAndSignTransaction(
        ownerKey: Key.PrivateKey,
        sourceKey: Key.PublicKey,
        nonce: Long,
        paymentItems: List<KinPaymentItem>,
        memo: KinMemo,
        fee: QuarkAmount
    ): Promise<KinTransaction> =
        checkAndMaybeUpgradeApi {
            configuredService.buildAndSignTransaction(
                ownerKey,
                sourceKey,
                nonce,
                paymentItems,
                memo,
                fee
            )
        }

    override fun submitTransaction(transaction: KinTransaction): Promise<KinTransaction> =
        checkAndMaybeUpgradeApi {
            configuredService.submitTransaction(transaction)
        }

    override fun buildSignAndSubmitTransaction(buildAndSignTransaction: () -> Promise<KinTransaction>): Promise<KinTransaction> =
        checkAndMaybeUpgradeApi {
            configuredService.buildSignAndSubmitTransaction(buildAndSignTransaction)
        }

    // TODO: need to trigger update to streams on apiVersion change
    override fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount> =
        checkAndMaybeUpgradeApiObserver {
            configuredService.streamAccount(kinAccountId)
        }

    // TODO: need to trigger update to streams on apiVersion change
    override fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction> =
        checkAndMaybeUpgradeApiObserver {
            configuredService.streamNewTransactions(kinAccountId)
        }

    override fun invalidateBlockhashCache() {
        configuredService.invalidateBlockhashCache()
    }

    override val testService: KinTestService = object : KinTestService {
        override fun fundAccount(accountId: KinAccount.Id): Promise<KinAccount> =
            checkAndMaybeUpgradeApi {
                configuredService.testService.fundAccount(accountId)
            }
    }

    // Utils

    /**
     * Executes a given KinService function on Kin3 first, and
     * upon receiving SDKUpgradeRequired Exception we check the minApiVersion using the
     * MetaServiceApi and try again on the appropriate Api version. In this fashion we can
     * upgrade our api calls to a different version (typically only higher)
     */

    private fun delegateCheck(version: Int) {
        configuredService = when (version) {
            3 -> kinServiceV3
            else -> kinServiceV4
        }
    }

    private fun <T> checkAndMaybeUpgradeApi(execute: () -> Promise<T>): Promise<T> {
        delegateCheck(metaServiceApi.configuredMinApi)
        return execute().onErrorResumeNext(KinService.FatalError.SDKUpgradeRequired.javaClass) {
            metaServiceApi.getMinApiVersion()
                .flatMap { minVersion ->
                    delegateCheck(minVersion)
                    execute()
                }
        }
    }

    private fun <T> checkAndMaybeUpgradeApiObserver(execute: () -> Observer<T>): Observer<T> {
        delegateCheck(metaServiceApi.configuredMinApi)
        return execute()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> Any.findMethod(methodName: String, vararg paramaters: Any) =
        this::class.java.methods.find { it.name == methodName }
            ?.invoke(this, *paramaters) as T
}
