package org.kin.sdk.base.network.api.horizon

import okhttp3.OkHttpClient
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asKinTransaction
import org.kin.sdk.base.models.bytesValue
import org.kin.sdk.base.models.kinAccount
import org.kin.sdk.base.models.resultXdrBytes
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountRequest
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountResponse
import org.kin.sdk.base.network.api.KinStreamingApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionApi.GetMinFeeForTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryRequest
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryRequest.Order
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse
import org.kin.sdk.base.network.api.toSubmitTransactionResult
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransaction.RecordType
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.sdk.base.tools.ManagedServerSentEventStream
import org.kin.sdk.base.tools.NetworkOperationsHandlerException.OperationTimeoutException
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.ValueSubject
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.requests.EventListener
import org.kin.stellarfork.requests.RequestBuilder
import org.kin.stellarfork.requests.TooManyRequestsException
import org.kin.stellarfork.responses.AccountResponse
import org.kin.stellarfork.responses.HttpResponseException
import org.kin.stellarfork.responses.ServerGoneException
import org.kin.stellarfork.responses.TransactionResponse
import java.net.SocketTimeoutException
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountResponse.Result as GetAccountResult
import org.kin.sdk.base.network.api.KinTransactionApi.GetMinFeeForTransactionResponse.Result as GetMinFeeResult
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryResponse.Result as GetHistoryResult
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionResponse.Result as GetTransactionResult
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse.Result as SubmitTransactionResult

class HorizonKinApi(
    environment: ApiConfig,
    okHttpClient: OkHttpClient
) : KinJsonApi(environment, okHttpClient),
    KinAccountApi, KinTransactionApi, KinStreamingApi {

    companion object {
        private const val CURSOR_FUTURE_ONLY = "now"
    }

    private val accountStreams =
        mutableMapOf<KinAccount.Id, ManagedServerSentEventStream<AccountResponse>?>()

    private val transactionStreams =
        mutableMapOf<KinAccount.Id, ManagedServerSentEventStream<TransactionResponse>?>()

    override fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount> {
        val accountStream = accountStreams[kinAccountId]
            ?: ManagedServerSentEventStream<AccountResponse>(
                server.accounts().forAccount(kinAccountId.toKeyPair())
            )
        accountStreams[kinAccountId] = accountStream
        var listener: EventListener<AccountResponse>? = null
        return ValueSubject<KinAccount>()
            .also { subject ->
                listener = object : EventListener<AccountResponse> {
                    override fun onEvent(data: AccountResponse) {
                        subject.onNext(data.kinAccount())
                    }
                }
                accountStream.addListener(listener!!)
            }.doOnDisposed {
                accountStream.removeListener(listener!!)
                accountStreams[kinAccountId] = null
            }
    }

    override fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction> {
        val transactionStream = transactionStreams[kinAccountId]
            ?: ManagedServerSentEventStream<TransactionResponse>(
                server.transactions().forAccount(kinAccountId.toKeyPair())
                    .cursor(CURSOR_FUTURE_ONLY)
            )
        transactionStreams[kinAccountId] = transactionStream
        var listener: EventListener<TransactionResponse>? = null
        return ValueSubject<KinTransaction>()
            .also { subject ->
                listener = object : EventListener<TransactionResponse> {
                    override fun onEvent(data: TransactionResponse) {
                        subject.onNext(data.asKinTransaction(environment.networkEnv))
                    }
                }
                transactionStream.addListener(listener!!)
            }.doOnDisposed {
                transactionStream.removeListener(listener!!)
                accountStreams[kinAccountId] = null
            }
    }

    override fun getAccount(
        request: GetAccountRequest,
        onCompleted: (GetAccountResponse) -> Unit
    ) {
        var account: KinAccount? = null
        val result = try {
            server.accounts()
                .account(request.accountId.toKeyPair())
                ?.let {
                    account = it.kinAccount()
                    GetAccountResult.Ok
                } ?: GetAccountResult.TransientFailure(MalformedBodyException)
        } catch (e: Throwable) {
            when (e) {
                is HttpResponseException -> when {
                    e.statusCode == 404 -> GetAccountResult.NotFound
                    e.statusCode >= 500 -> GetAccountResult.TransientFailure(e)
                    else -> GetAccountResult.UndefinedError(e)
                }
                is SocketTimeoutException,
                is OperationTimeoutException -> GetAccountResult.TransientFailure(TimeoutException)
                is TooManyRequestsException -> GetAccountResult.TransientFailure(e)
                is ServerGoneException -> GetAccountResult.UpgradeRequiredError
                else -> GetAccountResult.UndefinedError(e)
            }
        }
        onCompleted(GetAccountResponse(result, account))
    }

    override fun getTransactionHistory(
        request: GetTransactionHistoryRequest,
        onCompleted: (GetTransactionHistoryResponse) -> Unit
    ) {
        var transactions: List<KinTransaction>? = null
        val result = try {
            server.transactions()
                .apply {
                    forAccount(request.accountId.toKeyPair())
                    request.pagingToken?.let { cursor(it.value) }
                    order(
                        when (request.order) {
                            Order.Ascending -> RequestBuilder.Order.ASC
                            Order.Descending -> RequestBuilder.Order.DESC
                        }
                    )
                }
                .execute()
                ?.let { transactionsPage ->
                    transactions = transactionsPage.records
                        .map { it.asKinTransaction(environment.networkEnv) }
                    GetHistoryResult.Ok
                } ?: GetHistoryResult.TransientFailure(MalformedBodyException)
        } catch (e: Throwable) {
            when (e) {
                is HttpResponseException -> when {
                    e.statusCode == 404 -> GetHistoryResult.NotFound
                    e.statusCode >= 500 -> GetHistoryResult.TransientFailure(e)
                    else -> GetHistoryResult.UndefinedError(e)
                }
                is SocketTimeoutException,
                is OperationTimeoutException -> GetHistoryResult.TransientFailure(TimeoutException)
                is TooManyRequestsException -> GetHistoryResult.TransientFailure(e)
                is ServerGoneException -> GetHistoryResult.UpgradeRequiredError
                else -> GetHistoryResult.UndefinedError(e)
            }
        }
        onCompleted(GetTransactionHistoryResponse(result, transactions))
    }

    override fun getTransaction(
        request: GetTransactionRequest,
        onCompleted: (GetTransactionResponse) -> Unit
    ) {
        var transaction: KinTransaction? = null
        val result = try {
            server.transactions()
                .transaction(request.transactionHash.toString())
                ?.let { response ->
                    transaction = response.asKinTransaction(environment.networkEnv)
                    GetTransactionResult.Ok
                } ?: GetTransactionResult.TransientFailure(MalformedBodyException)
        } catch (e: Throwable) {
            when (e) {
                is HttpResponseException -> when {
                    e.statusCode == 404 -> GetTransactionResult.NotFound
                    e.statusCode >= 500 -> GetTransactionResult.TransientFailure(e)
                    else -> GetTransactionResult.UndefinedError(e)
                }
                is SocketTimeoutException,
                is OperationTimeoutException ->
                    GetTransactionResult.TransientFailure(TimeoutException)
                is TooManyRequestsException -> GetTransactionResult.TransientFailure(e)
                is ServerGoneException -> GetTransactionResult.UpgradeRequiredError
                else -> GetTransactionResult.UndefinedError(e)
            }
        }
        onCompleted(GetTransactionResponse(result, transaction))
    }

    override fun getTransactionMinFee(onCompleted: (GetMinFeeForTransactionResponse) -> Unit) {
        var fee: QuarkAmount? = null
        val result = try {
            val records = server.ledgers()
                .order(RequestBuilder.Order.DESC)
                .limit(1)
                .execute()
                ?.records
            if (records.isNullOrEmpty()) {
                GetMinFeeResult.TransientFailure(MalformedBodyException)
            } else {
                fee = QuarkAmount(records.first().baseFee)
                GetMinFeeResult.Ok
            }
        } catch (e: Throwable) {
            when (e) {
                is HttpResponseException -> when {
                    e.statusCode >= 500 -> GetMinFeeResult.TransientFailure(e)
                    else -> GetMinFeeResult.UndefinedError(e)
                }
                is SocketTimeoutException,
                is OperationTimeoutException -> GetMinFeeResult.TransientFailure(TimeoutException)
                is TooManyRequestsException -> GetMinFeeResult.TransientFailure(e)
                is ServerGoneException -> GetMinFeeResult.UpgradeRequiredError
                else -> GetMinFeeResult.UndefinedError(e)
            }
        }
        onCompleted(GetMinFeeForTransactionResponse(result, fee))
    }

    override fun submitTransaction(
        request: SubmitTransactionRequest,
        onCompleted: (SubmitTransactionResponse) -> Unit
    ) {

        /**
         * TODO: if (request.invoiceList !=null)
         *           Then log Invoice attempted to be sent with transaction.
         *           This is not supported by Horizon, are you sure this is what you wanted?
         */

        var transaction: KinTransaction? = null
        val result = try {
            val recordType =
                server.transactions()
                    .submitTransaction(
                        Base64().encodeAsString(request.transactionEnvelopeXdr)
                    )?.let { response ->
                        RecordType.Acknowledged(
                            System.currentTimeMillis(),
                            response.resultXdrBytes()
                        ).also { type ->
                            transaction =
                                StellarKinTransaction(
                                    response.bytesValue(),
                                    type,
                                    environment.networkEnv
                                )
                        }
                    }
            recordType?.resultCode.toSubmitTransactionResult()
        } catch (e: Throwable) {
            when (e) {
                is HttpResponseException -> when {
                    e.statusCode >= 500 -> SubmitTransactionResult.TransientFailure(e)
                    else -> SubmitTransactionResult.UndefinedError(e)
                }
                is SocketTimeoutException,
                is OperationTimeoutException ->
                    SubmitTransactionResult.TransientFailure(TimeoutException)
                is TooManyRequestsException -> SubmitTransactionResult.TransientFailure(e)
                is ServerGoneException -> SubmitTransactionResult.UpgradeRequiredError
                else -> SubmitTransactionResult.UndefinedError(e)
            }
        }
        onCompleted(SubmitTransactionResponse(result, transaction))
    }
}
