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

    fun postInit() : Promise<Unit> {
        return storage.getMinApiVersion().doOnResolved {
            it.map {
                if(it >= configuredMinApi) {
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

    override fun createAccount(
        accountId: KinAccount.Id,
        signer: Key.PrivateKey
    ): Promise<KinAccount> =
        checkChainUpgrade(::createAccount.name, accountId, signer)

    override fun getAccount(accountId: KinAccount.Id): Promise<KinAccount> =
        checkChainUpgrade(::getAccount.name, accountId)

    override fun resolveTokenAccounts(accountId: KinAccount.Id): Promise<List<Key.PublicKey>> =
        checkChainUpgrade(::resolveTokenAccounts.name, accountId)

    override fun getLatestTransactions(kinAccountId: KinAccount.Id): Promise<List<KinTransaction>> =
        checkChainUpgrade(::getLatestTransactions.name, kinAccountId)

    override fun getTransactionPage(
        kinAccountId: KinAccount.Id,
        pagingToken: KinTransaction.PagingToken,
        order: KinService.Order
    ): Promise<List<KinTransaction>> =
        checkChainUpgrade(::getTransactionPage.name, kinAccountId, pagingToken, order)

    override fun getTransaction(transactionHash: TransactionHash): Promise<KinTransaction> =
        checkChainUpgrade(::getTransaction.name, transactionHash)

    override fun canWhitelistTransactions(): Promise<Boolean> =
        checkChainUpgrade(::canWhitelistTransactions.name)

    override fun getMinFee(): Promise<QuarkAmount> =
        checkChainUpgrade(::getMinFee.name)

    override fun buildAndSignTransaction(
        ownerKey: Key.PrivateKey,
        sourceKey: Key.PublicKey,
        nonce: Long,
        paymentItems: List<KinPaymentItem>,
        memo: KinMemo,
        fee: QuarkAmount
    ): Promise<KinTransaction> =
        checkChainUpgrade(
            ::buildAndSignTransaction.name,
            ownerKey,
            sourceKey,
            nonce,
            paymentItems,
            memo,
            fee
        )

    override fun submitTransaction(transaction: KinTransaction): Promise<KinTransaction> =
        checkChainUpgrade(::submitTransaction.name, transaction)

    override fun buildSignAndSubmitTransaction(buildAndSignTransaction: () -> Promise<KinTransaction>): Promise<KinTransaction> =
        checkChainUpgrade(::buildSignAndSubmitTransaction.name, buildAndSignTransaction)

    // TODO: need to trigger update to streams on apiVersion change
    override fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount> =
        checkChainVerObserver(::streamAccount.name, kinAccountId)

    // TODO: need to trigger update to streams on apiVersion change
    override fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction> =
        checkChainVerObserver(::streamNewTransactions.name, kinAccountId)

    override fun invalidateBlockhashCache() {
        kinServiceV3.invalidateBlockhashCache()
        kinServiceV4.invalidateBlockhashCache()
    }

    override val testService: KinTestService = object : KinTestService {
        override fun fundAccount(accountId: KinAccount.Id): Promise<KinAccount> =
            checkChainUpgrade(
                v3 = kinServiceV3.testService,
                v4 = kinServiceV4.testService,
                methodName = "fundAccount",
                parameters = arrayOf(accountId)
            )
    }

    // Utils

    /**
     * Executes a given KinService function on Kin3 first, and
     * upon receiving SDKUpgradeRequired Exception we check the minApiVersion using the
     * MetaServiceApi and try again on the appropriate Api version. In this fashion we can
     * upgrade our api calls to a different version (typically only higher)
     */
    private fun <T> checkChainUpgrade(
        v3: Any,
        v4: Any,
        methodName: String,
        vararg parameters: Any
    ): Promise<T> {
        fun v3ApiCall(): Promise<T> = v3.findMethod(methodName, *parameters)
        fun v4ApiCall(): Promise<T> = v4.findMethod(methodName, *parameters)

        val configuredVersionCall = when (metaServiceApi.configuredMinApi) {
            3 -> v3ApiCall()
            else -> v4ApiCall()
        }

        return configuredVersionCall.onErrorResumeNext(KinService.FatalError.SDKUpgradeRequired.javaClass) {
            metaServiceApi.getMinApiVersion()
                .flatMap { minVersion ->
                    when (minVersion) {
                        3 -> v3ApiCall()
                        else -> v4ApiCall()
                    }
                }
        }
    }

    private fun <T> checkChainUpgrade(methodName: String, vararg parameters: Any): Promise<T> =
        checkChainUpgrade(
            v3 = kinServiceV3,
            v4 = kinServiceV4,
            methodName = methodName,
            parameters = parameters
        )

    private fun <T> checkChainVerObserver(methodName: String, vararg parameters: Any): Observer<T> {
        return when (metaServiceApi.configuredMinApi) {
            3 -> kinServiceV3.findMethod(methodName, *parameters) as Observer<T>
            else -> kinServiceV4.findMethod(methodName, *parameters) as Observer<T>
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> Any.findMethod(methodName: String, vararg paramaters: Any) =
        this::class.java.methods.find { it.name == methodName }
            ?.invoke(this, *paramaters) as T
}
