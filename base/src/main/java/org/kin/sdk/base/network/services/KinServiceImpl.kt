package org.kin.sdk.base.network.services

import okhttp3.OkHttpClient
import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.createStellarSigningAccount
import org.kin.sdk.base.models.getNetwork
import org.kin.sdk.base.models.isKin2
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toKinTransaction
import org.kin.sdk.base.models.toSigningKeyPair
import org.kin.sdk.base.network.api.FriendBotApi
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinStreamingApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.agora.sha224Hash
import org.kin.sdk.base.network.api.agora.toProto
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.sdk.base.tools.KinLoggerFactory
import org.kin.sdk.base.tools.NetworkOperationsHandler
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.queueWork
import org.kin.stellarfork.Asset
import org.kin.stellarfork.AssetTypeNative
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.Memo
import org.kin.stellarfork.PaymentOperation
import org.kin.stellarfork.Transaction
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.AssetType
import java.math.BigDecimal

class KinServiceImpl(
    private val networkEnvironment: NetworkEnvironment,
    private val networkOperationsHandler: NetworkOperationsHandler,
    private val accountApi: KinAccountApi,
    private val transactionApi: KinTransactionApi,
    private val streamingApi: KinStreamingApi,
    private val accountCreationApi: KinAccountCreationApi,
    private val transactionWhitelistingApi: KinTransactionWhitelistingApi,
    private val logger: KinLoggerFactory
) : KinService {

    private val log = logger.getLogger(javaClass.simpleName)

    private fun <T> T.requestPrint(): T {
        log.log { "[Request][V3] ===============" }
        log.log { "${this}" }
        log.log { "=============== [V3][Request]" }
        return this
    }

    private fun <T> T.responsePrint(): T {
        log.log { "[Response][V3] ===============" }
        log.log { "${this}" }
        log.log { "=============== [V3][Response]" }
        return this
    }

    override fun createAccount(
        accountId: KinAccount.Id,
        signer: Key.PrivateKey
    ): Promise<KinAccount> {
        return networkOperationsHandler.queueWork { respond ->
            accountCreationApi.createAccount(
                KinAccountCreationApi.CreateAccountRequest(accountId).requestPrint()) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinAccountCreationApi.CreateAccountResponse.Result.Ok -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinAccountCreationApi.CreateAccountResponse.Result.Exists -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else {
                            getAccount(accountId).then {
                                respond(it)
                            }
                            null
                        }
                    }
                    is KinAccountCreationApi.CreateAccountResponse.Result.TransientFailure -> KinService.FatalError.TransientFailure(
                        response.result.error
                    )
                    is KinAccountCreationApi.CreateAccountResponse.Result.UndefinedError -> KinService.FatalError.UnexpectedServiceError(
                        response.result.error
                    )
                    KinAccountCreationApi.CreateAccountResponse.Result.Unavailable -> KinService.FatalError.PermanentlyUnavailable
                    KinAccountCreationApi.CreateAccountResponse.Result.UpgradeRequiredError -> KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun getAccount(accountId: KinAccount.Id): Promise<KinAccount> {
        return networkOperationsHandler.queueWork { respond ->
            accountApi.getAccount(
                KinAccountApi.GetAccountRequest(accountId).requestPrint()) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinAccountApi.GetAccountResponse.Result.Ok -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinAccountApi.GetAccountResponse.Result.NotFound -> KinService.FatalError.ItemNotFound
                    is KinAccountApi.GetAccountResponse.Result.UndefinedError -> KinService.FatalError.UnexpectedServiceError(
                        response.result.error
                    )
                    is KinAccountApi.GetAccountResponse.Result.TransientFailure -> KinService.FatalError.TransientFailure(
                        response.result.error
                    )
                    KinAccountApi.GetAccountResponse.Result.UpgradeRequiredError -> KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun resolveTokenAccounts(accountId: KinAccount.Id): Promise<List<Key.PublicKey>> {
        return Promise.of(emptyList())
    }

    override fun getLatestTransactions(kinAccountId: KinAccount.Id): Promise<List<KinTransaction>> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransactionHistory(
                KinTransactionApi.GetTransactionHistoryRequest(
                    kinAccountId
                ).requestPrint()) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinTransactionApi.GetTransactionHistoryResponse.Result.Ok -> {
                        if (response.transactions != null) {
                            respond(response.transactions); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinTransactionApi.GetTransactionHistoryResponse.Result.NotFound -> KinService.FatalError.ItemNotFound
                    is KinTransactionApi.GetTransactionHistoryResponse.Result.UndefinedError -> KinService.FatalError.UnexpectedServiceError(
                        response.result.error
                    )
                    is KinTransactionApi.GetTransactionHistoryResponse.Result.TransientFailure -> KinService.FatalError.TransientFailure(
                        response.result.error
                    )
                    KinTransactionApi.GetTransactionHistoryResponse.Result.UpgradeRequiredError -> KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun getTransactionPage(
        kinAccountId: KinAccount.Id,
        pagingToken: KinTransaction.PagingToken,
        order: KinService.Order
    ): Promise<List<KinTransaction>> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransactionHistory(
                KinTransactionApi.GetTransactionHistoryRequest(
                    kinAccountId,
                    pagingToken,
                    when (order) {
                        KinService.Order.Ascending -> KinTransactionApi.GetTransactionHistoryRequest.Order.Ascending
                        KinService.Order.Descending -> KinTransactionApi.GetTransactionHistoryRequest.Order.Descending
                    }
                ).requestPrint()
            ) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinTransactionApi.GetTransactionHistoryResponse.Result.Ok -> {
                        if (response.transactions != null) {
                            respond(response.transactions); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinTransactionApi.GetTransactionHistoryResponse.Result.NotFound -> KinService.FatalError.ItemNotFound
                    is KinTransactionApi.GetTransactionHistoryResponse.Result.UndefinedError ->
                        KinService.FatalError.UnexpectedServiceError(response.result.error)
                    is KinTransactionApi.GetTransactionHistoryResponse.Result.TransientFailure ->
                        KinService.FatalError.TransientFailure(response.result.error)
                    KinTransactionApi.GetTransactionHistoryResponse.Result.UpgradeRequiredError -> KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond.invoke(it) }
            }
        }
    }

    override fun getTransaction(transactionHash: TransactionHash): Promise<KinTransaction> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransaction(
                KinTransactionApi.GetTransactionRequest(transactionHash).requestPrint()
            ) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinTransactionApi.GetTransactionResponse.Result.Ok -> {
                        if (response.transaction != null) {
                            respond(response.transaction); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinTransactionApi.GetTransactionResponse.Result.NotFound -> KinService.FatalError.ItemNotFound
                    is KinTransactionApi.GetTransactionResponse.Result.UndefinedError ->
                        KinService.FatalError.UnexpectedServiceError(response.result.error)
                    is KinTransactionApi.GetTransactionResponse.Result.TransientFailure ->
                        KinService.FatalError.TransientFailure(response.result.error)
                    KinTransactionApi.GetTransactionResponse.Result.UpgradeRequiredError -> KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond.invoke(it) }
            }
        }
    }

    override fun canWhitelistTransactions(): Promise<Boolean> {
        return Promise.create { resolve, _ ->
            resolve(transactionWhitelistingApi.isWhitelistingAvailable)
        }
    }

    override fun getMinFee(): Promise<QuarkAmount> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransactionMinFee { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    is KinTransactionApi.GetMinFeeForTransactionResponse.Result.Ok -> {
                        if (response.minFee != null) {
                            respond(response.minFee); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    is KinTransactionApi.GetMinFeeForTransactionResponse.Result.UndefinedError,
                    is KinTransactionApi.GetMinFeeForTransactionResponse.Result.TransientFailure ->
                        KinService.FatalError.IllegalRequest()
                    KinTransactionApi.GetMinFeeForTransactionResponse.Result.UpgradeRequiredError -> KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun buildAndSignTransaction(
        ownerKey: Key.PrivateKey,
        sourceKey: Key.PublicKey,
        nonce: Long,
        paymentItems: List<KinPaymentItem>,
        memo: KinMemo,
        fee: QuarkAmount
    ): Promise<KinTransaction> {
        return networkOperationsHandler.queueWork<KinTransaction> { respond ->
            val account = try {
                createStellarSigningAccount(ownerKey, nonce)
            } catch (e: Exception) {
                null
            }
            if (account != null) {
                val transaction =
                    Transaction.Builder(account, networkEnvironment.getNetwork())
                        .apply {
                            paymentItems.forEach {
                                addOperation(
                                    if (networkEnvironment.isKin2()) {
                                        PaymentOperation.Builder(
                                            it.destinationAccount.toKeyPair(),
                                            Asset.createNonNativeAsset("KIN", networkEnvironment.issuer),
                                            it.amount.multiply(KinAmount(100)).toString()
                                        )
                                    } else {
                                        PaymentOperation.Builder(
                                            it.destinationAccount.toKeyPair(),
                                            AssetTypeNative,
                                            it.amount.toString()
                                        )
                                    }.setSourceAccount(account.keypair)
                                        .build()
                                )
                            }
                            addFee( fee.value.toInt().let { if (networkEnvironment.isKin2()) 100 * 100 * paymentItems.size else it } ) // Hack: Kin 2 will always pay fee of 100 quarks, inflated by 100 because of decimal scaling, times the number of payment operations and in the base currency: XLM
                            if (memo != KinMemo.NONE) {
                                val stellarMemo = when (memo.type) {
                                    KinMemo.Type.NoEncoding -> Memo.hash(memo.rawValue)
                                    is KinMemo.Type.CharsetEncoded -> Memo.text(memo.toString())
                                }
                                addMemo(stellarMemo)
                            }
                        }
                        .build()
                        .apply { sign(ownerKey.toSigningKeyPair()) }
                        .toKinTransaction(networkEnvironment, paymentItems.toInvoiceList())
                respond(transaction);
            } else {
                respond(KinService.FatalError.IllegalRequest(IllegalAccessException("Account is null")))
            }
        }
    }

    override fun submitTransaction(transaction: KinTransaction): Promise<KinTransaction> {
        return whitelistIfNeccessary(transaction)
            .flatMap { transactionToSend ->
                networkOperationsHandler.queueWork<KinTransaction> { respond ->
                    transactionApi.submitTransaction(
                        KinTransactionApi.SubmitTransactionRequest(
                            transactionToSend.bytesValue,
                            transaction.invoiceList
                        ).requestPrint()
                    ) { response ->
                        response.responsePrint()
                        val error: Exception? = when (response.result) {
                            KinTransactionApi.SubmitTransactionResponse.Result.Ok -> response.transaction?.let {
                                respond(it); null
                            }
                            KinTransactionApi.SubmitTransactionResponse.Result.InsufficientFee ->
                                KinService.FatalError.InsufficientFeeInRequest
                            KinTransactionApi.SubmitTransactionResponse.Result.BadSequenceNumber ->
                                KinService.FatalError.BadSequenceNumberInRequest
                            KinTransactionApi.SubmitTransactionResponse.Result.NoAccount ->
                                KinService.FatalError.UnknownAccountInRequest
                            KinTransactionApi.SubmitTransactionResponse.Result.InsufficientBalance ->
                                KinService.FatalError.InsufficientBalanceForSourceAccountInRequest
                            is KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors ->
                                KinService.FatalError.InvoiceErrorsInRequest(response.result.errors)
                            KinTransactionApi.SubmitTransactionResponse.Result.WebhookRejected ->
                                KinService.FatalError.WebhookRejectedTransaction
                            is KinTransactionApi.SubmitTransactionResponse.Result.UndefinedError ->
                                KinService.FatalError.UnexpectedServiceError(response.result.error)
                            is KinTransactionApi.SubmitTransactionResponse.Result.TransientFailure ->
                                KinService.FatalError.TransientFailure(response.result.error)
                            KinTransactionApi.SubmitTransactionResponse.Result.UpgradeRequiredError ->
                                KinService.FatalError.SDKUpgradeRequired
                        }
                        error?.let { respond(it) }
                    }
                }
            }
    }

    override fun buildSignAndSubmitTransaction(buildAndSignTransaction: () -> Promise<KinTransaction>): Promise<KinTransaction> {
        return  buildAndSignTransaction()
            .flatMap { submitTransaction(it) }
    }

    override fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount> {
        return streamingApi.streamAccount(kinAccountId)
            .add {
                log.log { "streamAccount::Update $it" }
            }
    }

    override fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction> {
        return streamingApi.streamNewTransactions(kinAccountId)
            .add {
                log.log { "streamNewTransactions::Update $it" }
            }
    }

    override fun invalidateBlockhashCache() {
        // no-op
    }

    override val testService: KinTestService by lazy {
        KinTestServiceImpl(
            networkOperationsHandler,
            FriendBotApi(
                OkHttpClient.Builder().build()
            )
        )
    }

    private fun whitelistIfNeccessary(kinTransaction: KinTransaction): Promise<KinTransaction> {
        return canWhitelistTransactions()
            .flatMap {
                if (it) {
                    networkOperationsHandler.queueWork { respond ->
                        val request =
                            KinTransactionWhitelistingApi.WhitelistTransactionRequest(
                                Base64().encodeAsString(kinTransaction.bytesValue)
                            )
                        transactionWhitelistingApi.whitelistTransaction(request) { response ->
                            when (response.result) {
                                KinTransactionWhitelistingApi.WhitelistTransactionResponse.Result.Ok -> {
                                    val txnBytes =
                                        Base64().decode(response.base64EncodedWhitelistedTransactionEnvelopeBytes)!!
                                    val whitelistedTransaction = StellarKinTransaction(
                                        txnBytes,
                                        networkEnvironment = networkEnvironment
                                    )
                                    respond(whitelistedTransaction)
                                }
                                else -> respond(kinTransaction)
                            }
                        }
                    }
                } else Promise.of(kinTransaction)
            }
    }

    private fun List<KinPaymentItem>.toInvoiceList(): InvoiceList? =
        with(mapNotNull { it.invoice }.map { it.get() }.filterNotNull()) {
            if (isNotEmpty()) {
                InvoiceList(InvoiceList.Id(toProto().sha224Hash()), this)
            } else null
        }
}
