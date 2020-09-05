package org.kin.sdk.base.network.services

import okhttp3.OkHttpClient
import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.getNetwork
import org.kin.sdk.base.models.toAccount
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toKinTransaction
import org.kin.sdk.base.models.toSigningKeyPair
import org.kin.sdk.base.network.api.FriendBotApi
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountRequest
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountResponse
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountRequest
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse
import org.kin.sdk.base.network.api.KinStreamingApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionApi.GetMinFeeForTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryRequest
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.agora.sha224Hash
import org.kin.sdk.base.network.api.agora.toProto
import org.kin.sdk.base.network.api.horizon.KinFriendBotApi
import org.kin.sdk.base.network.services.KinService.FatalError.BadSequenceNumberInRequest
import org.kin.sdk.base.network.services.KinService.FatalError.IllegalRequest
import org.kin.sdk.base.network.services.KinService.FatalError.IllegalResponse
import org.kin.sdk.base.network.services.KinService.FatalError.InsufficientBalanceForSourceAccountInRequest
import org.kin.sdk.base.network.services.KinService.FatalError.InsufficientFeeInRequest
import org.kin.sdk.base.network.services.KinService.FatalError.InvoiceErrorsInRequest
import org.kin.sdk.base.network.services.KinService.FatalError.ItemNotFound
import org.kin.sdk.base.network.services.KinService.FatalError.PermanentlyUnavailable
import org.kin.sdk.base.network.services.KinService.FatalError.SDKUpgradeRequired
import org.kin.sdk.base.network.services.KinService.FatalError.TransientFailure
import org.kin.sdk.base.network.services.KinService.FatalError.UnexpectedServiceError
import org.kin.sdk.base.network.services.KinService.FatalError.UnknownAccountInRequest
import org.kin.sdk.base.network.services.KinService.FatalError.WebhookRejectedTransaction
import org.kin.sdk.base.network.services.KinService.Order
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.KinLoggerFactory
import org.kin.sdk.base.tools.NetworkOperationsHandler
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.queueWork
import org.kin.stellarfork.AssetTypeNative
import org.kin.stellarfork.Memo
import org.kin.stellarfork.PaymentOperation
import org.kin.stellarfork.Transaction
import org.kin.stellarfork.codec.Base64
import java.io.IOException

interface KinService {

    /**
     * Creates a [KinAccount] and activates it on the network.
     */
    fun createAccount(accountId: KinAccount.Id): Promise<KinAccount>

    fun getAccount(accountId: KinAccount.Id): Promise<KinAccount>

    fun getLatestTransactions(kinAccountId: KinAccount.Id): Promise<List<KinTransaction>>

    fun getTransactionPage(
        kinAccountId: KinAccount.Id,
        pagingToken: KinTransaction.PagingToken,
        order: Order = Order.Descending
    ): Promise<List<KinTransaction>>

    fun getTransaction(transactionHash: TransactionHash): Promise<KinTransaction>

    fun canWhitelistTransactions(): Promise<Boolean>

    fun getMinFee(): Promise<QuarkAmount>

    fun buildAndSignTransaction(
        sourceKinAccount: KinAccount,
        paymentItems: List<KinPaymentItem>,
        memo: KinMemo,
        fee: QuarkAmount
    ): Promise<KinTransaction>

    fun submitTransaction(transaction: KinTransaction): Promise<KinTransaction>

    fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount>

    fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction>

    sealed class Order(val value: Int) {
        object Ascending : Order(0)
        object Descending : Order(1)
    }

    sealed class FatalError(reason: Throwable) : RuntimeException(reason) {

        open class TransientFailure(reason: Throwable? = null) :
            FatalError(IOException("The request was retried until limit was exceeded", reason))

        open class UnexpectedServiceError(reason: Throwable? = null) :
            FatalError(IOException("There was an unexpected service error", reason))

        open class Denied(reason: Throwable? = null) :
            FatalError(reason ?: Exception("This action was not allowed"))

        open class IllegalRequest(reason: Throwable? = null) :
            FatalError(IllegalStateException("Malformed request", reason))

        object IllegalResponse :
            FatalError(IllegalStateException("Malformed response from server"))

        object ItemNotFound :
            FatalError(NoSuchElementException("The requested item was not found"))

        object PermanentlyUnavailable :
            FatalError(Exception("This operation is not supported under this configuration"))

        object UnknownAccountInRequest :
            FatalError(IllegalStateException("Unknown Account"))

        object BadSequenceNumberInRequest :
            FatalError(IllegalArgumentException("Bad Sequence Number"))

        object InsufficientFeeInRequest :
            FatalError(IllegalArgumentException("Insufficient Fee"))

        object InsufficientBalanceForSourceAccountInRequest :
            FatalError(IllegalStateException("Insufficient Balance"))

        object WebhookRejectedTransaction :
            FatalError(Exception("This transaction was rejected by the configured webhook without a reason"))

        data class InvoiceErrorsInRequest(val invoiceErrors: List<SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError>) :
            FatalError(IllegalArgumentException("Invoice Errors"))

        /**
         * It is expected that this error is handled gracefully by notifying users
         * to upgrade to a newer version of the software that should contain a more
         * recent version of this SDK.
         */
        object SDKUpgradeRequired :
            FatalError(Exception("Please upgrade to a newer version of the SDK"))
    }

    /**
     * WARNING: This *ONLY* works in test environments.
     */
    val testService: KinTestService
}

/**
 * WARNING: This *ONLY* works in test environments.
 */
interface KinTestService {
    /**
     * Funds an account with a set amount of test Kin.
     */
    fun fundAccount(accountId: KinAccount.Id): Promise<KinAccount>
}

class KinTestServiceImpl(
    private val networkOperationsHandler: NetworkOperationsHandler,
    private val friendBotApi: KinFriendBotApi
) : KinTestService {
    override fun fundAccount(accountId: KinAccount.Id): Promise<KinAccount> {
        return networkOperationsHandler.queueWork { respond ->
            friendBotApi.fundAccount(CreateAccountRequest(accountId)) { response ->
                val error: Exception? = when (response.result) {
                    CreateAccountResponse.Result.Ok,
                    CreateAccountResponse.Result.Exists -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else IllegalResponse
                    }
                    is CreateAccountResponse.Result.TransientFailure -> TransientFailure(
                        response.result.error
                    )
                    is CreateAccountResponse.Result.UndefinedError -> UnexpectedServiceError(
                        response.result.error
                    )
                    CreateAccountResponse.Result.Unavailable -> PermanentlyUnavailable
                    CreateAccountResponse.Result.UpgradeRequiredError -> SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }
}

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
        log.debug("[Request]:${this}")
        return this
    }

    private fun <T> T.responsePrint(): T {
        log.debug("[Response]:${this}")
        return this
    }

    override fun createAccount(accountId: KinAccount.Id): Promise<KinAccount> {
        return networkOperationsHandler.queueWork { respond ->
            accountCreationApi.createAccount(CreateAccountRequest(accountId).requestPrint()) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    CreateAccountResponse.Result.Ok -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else IllegalResponse
                    }
                    CreateAccountResponse.Result.Exists -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else {
                            getAccount(accountId).then {
                                respond(it)
                            }
                            null
                        }
                    }
                    is CreateAccountResponse.Result.TransientFailure -> TransientFailure(
                        response.result.error
                    )
                    is CreateAccountResponse.Result.UndefinedError -> UnexpectedServiceError(
                        response.result.error
                    )
                    CreateAccountResponse.Result.Unavailable -> PermanentlyUnavailable
                    CreateAccountResponse.Result.UpgradeRequiredError -> SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun getAccount(accountId: KinAccount.Id): Promise<KinAccount> {
        return networkOperationsHandler.queueWork { respond ->
            accountApi.getAccount(GetAccountRequest(accountId).requestPrint()) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    GetAccountResponse.Result.Ok -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else IllegalResponse
                    }
                    GetAccountResponse.Result.NotFound -> ItemNotFound
                    is GetAccountResponse.Result.UndefinedError -> UnexpectedServiceError(
                        response.result.error
                    )
                    is GetAccountResponse.Result.TransientFailure -> TransientFailure(response.result.error)
                    GetAccountResponse.Result.UpgradeRequiredError -> SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun getLatestTransactions(kinAccountId: KinAccount.Id): Promise<List<KinTransaction>> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransactionHistory(GetTransactionHistoryRequest(kinAccountId).requestPrint()) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    GetTransactionHistoryResponse.Result.Ok -> {
                        if (response.transactions != null) {
                            respond(response.transactions); null
                        } else IllegalResponse
                    }
                    GetTransactionHistoryResponse.Result.NotFound -> ItemNotFound
                    is GetTransactionHistoryResponse.Result.UndefinedError -> UnexpectedServiceError(
                        response.result.error
                    )
                    is GetTransactionHistoryResponse.Result.TransientFailure -> TransientFailure(
                        response.result.error
                    )
                    GetTransactionHistoryResponse.Result.UpgradeRequiredError -> SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun getTransactionPage(
        kinAccountId: KinAccount.Id,
        pagingToken: KinTransaction.PagingToken,
        order: Order
    ): Promise<List<KinTransaction>> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransactionHistory(
                GetTransactionHistoryRequest(
                    kinAccountId,
                    pagingToken,
                    when (order) {
                        Order.Ascending -> GetTransactionHistoryRequest.Order.Ascending
                        Order.Descending -> GetTransactionHistoryRequest.Order.Descending
                    }
                ).requestPrint()
            ) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    GetTransactionHistoryResponse.Result.Ok -> {
                        if (response.transactions != null) {
                            respond(response.transactions); null
                        } else IllegalResponse
                    }
                    GetTransactionHistoryResponse.Result.NotFound -> ItemNotFound
                    is GetTransactionHistoryResponse.Result.UndefinedError ->
                        UnexpectedServiceError(response.result.error)
                    is GetTransactionHistoryResponse.Result.TransientFailure ->
                        TransientFailure(response.result.error)
                    GetTransactionHistoryResponse.Result.UpgradeRequiredError -> SDKUpgradeRequired
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
                    GetTransactionResponse.Result.Ok -> {
                        if (response.transaction != null) {
                            respond(response.transaction); null
                        } else IllegalResponse
                    }
                    GetTransactionResponse.Result.NotFound -> ItemNotFound
                    is GetTransactionResponse.Result.UndefinedError ->
                        UnexpectedServiceError(response.result.error)
                    is GetTransactionResponse.Result.TransientFailure ->
                        TransientFailure(response.result.error)
                    GetTransactionResponse.Result.UpgradeRequiredError -> SDKUpgradeRequired
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
                    is GetMinFeeForTransactionResponse.Result.Ok -> {
                        if (response.minFee != null) {
                            respond(response.minFee); null
                        } else IllegalResponse
                    }
                    is GetMinFeeForTransactionResponse.Result.UndefinedError,
                    is GetMinFeeForTransactionResponse.Result.TransientFailure ->
                        IllegalRequest()
                    GetMinFeeForTransactionResponse.Result.UpgradeRequiredError -> SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun buildAndSignTransaction(
        sourceKinAccount: KinAccount,
        paymentItems: List<KinPaymentItem>,
        memo: KinMemo,
        fee: QuarkAmount
    ): Promise<KinTransaction> {
        return networkOperationsHandler.queueWork<KinTransaction> { respond ->
            val account = try {
                sourceKinAccount.toAccount()
            } catch (e: Exception) {
                null
            }
            if (account != null) {
                val transaction =
                    Transaction.Builder(account, networkEnvironment.getNetwork())
                        .apply {
                            paymentItems.forEach {
                                addOperation(
                                    PaymentOperation
                                        .Builder(
                                            it.destinationAccount.toKeyPair(),
                                            AssetTypeNative,
                                            it.amount.toString()
                                        )
                                        .setSourceAccount(sourceKinAccount.toAccount().keypair)
                                        .build()
                                )
                            }
                            addFee(fee.value.toInt())
                            if (memo != KinMemo.NONE) {
                                val stellarMemo = when (memo.type) {
                                    KinMemo.Type.NoEncoding -> Memo.hash(memo.rawValue)
                                    is KinMemo.Type.CharsetEncoded -> Memo.text(memo.toString())
                                }
                                addMemo(stellarMemo)
                            }
                        }
                        .build()
                        .apply { sign(sourceKinAccount.toSigningKeyPair()) }
                        .toKinTransaction(networkEnvironment, paymentItems.toInvoiceList())
                respond(transaction);
            } else {
                respond(IllegalRequest(IllegalAccessException("Account is null")))
            }
        }
    }

    override fun submitTransaction(transaction: KinTransaction): Promise<KinTransaction> {
        return whitelistIfNeccessary(transaction)
            .flatMap { transactionToSend ->
                networkOperationsHandler.queueWork<KinTransaction> { respond ->
                    transactionApi.submitTransaction(
                        SubmitTransactionRequest(
                            transactionToSend.envelopeXdrBytes,
                            transaction.invoiceList
                        ).requestPrint()
                    ) { response ->
                        response.responsePrint()
                        val error: Exception? = when (response.result) {
                            SubmitTransactionResponse.Result.Ok -> response.transaction?.let {
                                respond(it); null
                            }
                            SubmitTransactionResponse.Result.InsufficientFee ->
                                InsufficientFeeInRequest
                            SubmitTransactionResponse.Result.BadSequenceNumber ->
                                BadSequenceNumberInRequest
                            SubmitTransactionResponse.Result.NoAccount ->
                                UnknownAccountInRequest
                            SubmitTransactionResponse.Result.InsufficientBalance ->
                                InsufficientBalanceForSourceAccountInRequest
                            is SubmitTransactionResponse.Result.InvoiceErrors ->
                                InvoiceErrorsInRequest(response.result.errors)
                            SubmitTransactionResponse.Result.WebhookRejected ->
                                WebhookRejectedTransaction
                            is SubmitTransactionResponse.Result.UndefinedError ->
                                UnexpectedServiceError(response.result.error)
                            is SubmitTransactionResponse.Result.TransientFailure ->
                                TransientFailure(response.result.error)
                            SubmitTransactionResponse.Result.UpgradeRequiredError ->
                                SDKUpgradeRequired
                        }
                        error?.let { respond(it) }
                    }
                }
            }
    }

    override fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount> {
        return streamingApi.streamAccount(kinAccountId)
            .add {
                log.debug("streamAccount::Update $it")
            }
    }

    override fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction> {
        return streamingApi.streamNewTransactions(kinAccountId)
            .add {
                log.debug("streamNewTransactions::Update $it")
            }
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
                                Base64().encodeAsString(kinTransaction.envelopeXdrBytes)
                            )
                        transactionWhitelistingApi.whitelistTransaction(request) { response ->
                            when (response.result) {
                                KinTransactionWhitelistingApi.WhitelistTransactionResponse.Result.Ok -> {
                                    val txnBytes =
                                        Base64().decode(response.base64EncodedWhitelistedTransactionEnvelopeBytes)!!
                                    val whitelistedTransaction = KinTransaction(
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
