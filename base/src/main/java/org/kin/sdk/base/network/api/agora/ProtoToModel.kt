package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.account.v3.AccountService.AccountInfo
import org.kin.agora.gen.account.v3.AccountService.GetAccountInfoResponse
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.common.v3.Model.InvoiceError
import org.kin.agora.gen.transaction.v3.TransactionService
import org.kin.agora.gen.transaction.v3.TransactionService.GetHistoryResponse
import org.kin.agora.gen.transaction.v3.TransactionService.GetTransactionResponse.State
import org.kin.agora.gen.transaction.v3.TransactionService.HistoryItem
import org.kin.agora.gen.transaction.v3.TransactionService.SubmitTransactionResponse.*
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.SHA224Hash
import org.kin.sdk.base.models.SKU
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountResponse
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors
import org.kin.sdk.base.network.api.agora.GrpcApi.*
import org.kin.sdk.base.network.api.agora.GrpcApi.Companion.canRetry
import org.kin.sdk.base.network.api.agora.GrpcApi.Companion.isForcedUpgrade
import org.kin.sdk.base.network.api.toSubmitTransactionResult
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransaction.ResultCode
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.sdk.base.tools.PromisedCallback
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Base64
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountResponse.Result as GetAccountResult
import org.kin.sdk.base.network.api.KinAccountCreationApi.CreateAccountResponse.Result as CreateAccountResult
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryResponse.Result as GetHistoryResult
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionResponse.Result as GetTransactionResult
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse.Result as SubmitTransactionResult

internal fun AccountInfo.toKinAccount(): KinAccount =
    KinAccount(
        accountId.toPublicKey(),
        balance = KinBalance(QuarkAmount(balance).toKin()),
        status = KinAccount.Status.Registered(sequenceNumber)
    )

internal fun Model.StellarAccountId.toPublicKey(): Key.PublicKey =
    KeyPair.fromAccountId(value).asPublicKey()

internal fun HistoryItem.toAcknowledgedKinTransaction(networkEnvironment: NetworkEnvironment) =
    resultXdr?.let {
        val recordType = KinTransaction.RecordType.Acknowledged(
            System.currentTimeMillis(),
            it.toByteArray()
        )
        StellarKinTransaction(
            envelopeXdr.toByteArray(),
            recordType,
            networkEnvironment,
            if (hasInvoiceList()) invoiceList.toInvoiceList() else null
        )
    }

internal fun HistoryItem.toHistoricalKinTransaction(networkEnvironment: NetworkEnvironment) =
    resultXdr?.let {
        val recordType = KinTransaction.RecordType.Historical(
            System.currentTimeMillis(),
            it.toByteArray(),
            KinTransaction.PagingToken(Base64.encodeBase64String(cursor.value.toByteArray())!!)
        )
        StellarKinTransaction(
            envelopeXdr.toByteArray(),
            recordType,
            networkEnvironment,
            if (hasInvoiceList()) invoiceList.toInvoiceList() else null
        )
    }

internal fun Model.InvoiceList.toInvoiceList(): InvoiceList = InvoiceList(
    InvoiceList.Id(sha224Hash()),
    invoicesList.map { it.toInvoice() }
)

internal fun Model.Invoice.toInvoice(): Invoice =
    Invoice(Invoice.Id(sha224Hash()), itemsList.map { it.toLineItem() })

internal fun Model.Invoice.LineItem.toLineItem(): LineItem =
    LineItem(
        title,
        description,
        QuarkAmount(amount).toKin(),
        if (sku.isEmpty) null else SKU(sku.toByteArray())
    )

internal fun ((CreateAccountResponse) -> Unit).createAccountResponse() =
    PromisedCallback<AccountService.CreateAccountResponse>({ response ->
        var account: KinAccount? = null
        val result = when (response.result) {
            AccountService.CreateAccountResponse.Result.OK -> {
                account = response.accountInfo.toKinAccount()
                CreateAccountResult.Ok
            }
            AccountService.CreateAccountResponse.Result.EXISTS -> CreateAccountResult.Exists
            else -> CreateAccountResult.UndefinedError(UnrecognizedResultException(UnrecognizedProtoResponse))
        }
        this(CreateAccountResponse(result, account))
    }, {
        val result = when {
            it.canRetry() -> CreateAccountResult.TransientFailure(it)
            it.isForcedUpgrade() -> CreateAccountResult.UpgradeRequiredError
            else -> CreateAccountResult.UndefinedError(UnrecognizedResultException(it))
        }
        this(CreateAccountResponse(result))
    })

internal fun ((GetAccountResponse) -> Unit).getAccountResponse() =
    PromisedCallback<GetAccountInfoResponse>({ response ->
        var account: KinAccount? = null
        val result = when (response.result) {
            GetAccountInfoResponse.Result.OK -> {
                account = response.accountInfo.toKinAccount()
                GetAccountResult.Ok
            }
            GetAccountInfoResponse.Result.NOT_FOUND -> GetAccountResult.NotFound
            else -> GetAccountResult.UndefinedError(UnrecognizedResultException(UnrecognizedProtoResponse))
        }
        this(GetAccountResponse(result, account))
    }, {
        val result = when {
            it.canRetry() -> GetAccountResult.TransientFailure(it)
            it.isForcedUpgrade() -> GetAccountResult.UpgradeRequiredError
            else -> GetAccountResult.UndefinedError(UnrecognizedResultException(it))
        }
        this(GetAccountResponse(result))
    })

internal fun ((GetTransactionHistoryResponse) -> Unit).getTransactionHistoryResponse(
    networkEnvironment: NetworkEnvironment
) =
    PromisedCallback<GetHistoryResponse>({
        var transactions: List<KinTransaction>? = null
        val result = when (it.result) {
            GetHistoryResponse.Result.OK -> {
                transactions = it.itemsList.map { historyItem ->
                    historyItem.toHistoricalKinTransaction(networkEnvironment)
                }.requireNoNulls()
                GetHistoryResult.Ok
            }
            GetHistoryResponse.Result.NOT_FOUND -> GetHistoryResult.NotFound
            GetHistoryResponse.Result.UNRECOGNIZED,
            null -> GetHistoryResult.UndefinedError(UnrecognizedResultException(UnrecognizedProtoResponse))
        }
        this(GetTransactionHistoryResponse(result, transactions))
    }, {
        val result = when {
            it.canRetry() -> GetHistoryResult.TransientFailure(it)
            it.isForcedUpgrade() -> GetHistoryResult.UpgradeRequiredError
            (it as? StatusRuntimeException)?.status?.code == Status.Code.NOT_FOUND -> GetHistoryResult.NotFound
            else -> GetHistoryResult.UndefinedError(UnrecognizedResultException(it))
        }
        this(GetTransactionHistoryResponse(result))
    })

internal fun ((GetTransactionResponse) -> Unit).getTransactionResponse(networkEnvironment: NetworkEnvironment) =
    PromisedCallback<TransactionService.GetTransactionResponse>({
        var transaction: KinTransaction? = null
        val state = when (it.state) {
            State.SUCCESS -> {
                transaction = it.item.toHistoricalKinTransaction(networkEnvironment)
                GetTransactionResult.Ok
            }
            State.UNKNOWN,
            State.UNRECOGNIZED,
            null -> GetTransactionResult.NotFound
        }
        this(GetTransactionResponse(state, transaction))
    }, {
        val result = when {
            it.canRetry() -> GetTransactionResult.TransientFailure(it)
            it.isForcedUpgrade() -> GetTransactionResult.UpgradeRequiredError
            else -> GetTransactionResult.UndefinedError(UnrecognizedResultException(it))
        }
        this(GetTransactionResponse(result))
    })

internal fun ((SubmitTransactionResponse) -> Unit).submitTransactionResponse(
    request: SubmitTransactionRequest,
    networkEnvironment: NetworkEnvironment
) =
    PromisedCallback<TransactionService.SubmitTransactionResponse>({
        var transaction: KinTransaction? = null

        val result = when (it.result) {
            Result.OK,
            Result.FAILED -> {
                val recordType = it.resultXdr?.let {
                    KinTransaction.RecordType.Acknowledged(
                        System.currentTimeMillis(),
                        it.toByteArray()
                    )
                }
                if (it.result == Result.OK && recordType?.resultCode == ResultCode.Success) {
                    transaction = HistoryItem.newBuilder()
                        .setResultXdr(it.resultXdr)
                        .setEnvelopeXdr(ByteString.copyFrom(request.transactionEnvelopeXdr))
                        .apply {
                            request.invoiceList?.let { invoiceList = it.toProto()  }
                        }
                        .build()
                        .toAcknowledgedKinTransaction(networkEnvironment)
                }
                recordType?.resultCode?.toSubmitTransactionResult()
                    ?: SubmitTransactionResult.UndefinedError(Exception("Internal Error"))
            }
            Result.INVOICE_ERROR -> {
                InvoiceErrors(it.invoiceErrorsList.map {
                    when (it.reason) {
                        InvoiceError.Reason.ALREADY_PAID -> InvoiceErrors.InvoiceError.ALREADY_PAID
                        InvoiceError.Reason.WRONG_DESTINATION -> InvoiceErrors.InvoiceError.WRONG_DESTINATION
                        InvoiceError.Reason.SKU_NOT_FOUND -> InvoiceErrors.InvoiceError.SKU_NOT_FOUND
                        InvoiceError.Reason.UNRECOGNIZED,
                        InvoiceError.Reason.UNKNOWN,
                        null -> InvoiceErrors.InvoiceError.UNKNOWN
                    }
                })
            }
            Result.REJECTED -> SubmitTransactionResponse.Result.WebhookRejected
            Result.UNRECOGNIZED,
            null -> SubmitTransactionResult.UndefinedError(Exception("Internal Error"))
        }
        this(SubmitTransactionResponse(result, transaction))
    }, {
        val result = when {
            it.canRetry() -> SubmitTransactionResult.TransientFailure(it)
            it.isForcedUpgrade() -> SubmitTransactionResult.UpgradeRequiredError
            else -> SubmitTransactionResult.UndefinedError(UnrecognizedResultException(it))
        }
        this(SubmitTransactionResponse(result))
    })


// The SHA-224 hash of the Invoice.
fun Model.Invoice.sha224Hash(): SHA224Hash = SHA224Hash.of(toByteArray())

// The SHA-224 hash of the InvoiceList
fun Model.InvoiceList.sha224Hash(): SHA224Hash = SHA224Hash.of(toByteArray())
