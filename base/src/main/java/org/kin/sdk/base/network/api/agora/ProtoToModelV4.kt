package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.kin.agora.gen.account.v4.AccountService
import org.kin.agora.gen.account.v4.AccountService.AccountInfo
import org.kin.agora.gen.common.v3.Model.InvoiceError
import org.kin.agora.gen.common.v4.Model
import org.kin.agora.gen.transaction.v4.TransactionService
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.solana.FixedByteArray32
import org.kin.sdk.base.models.solana.FixedByteArray64
import org.kin.sdk.base.models.solana.Hash
import org.kin.sdk.base.models.solana.Signature
import org.kin.sdk.base.models.solana.marshal
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.network.api.KinAccountApiV4
import org.kin.sdk.base.network.api.KinAccountApiV4.GetAccountResponse
import org.kin.sdk.base.network.api.KinAccountCreationApiV4.CreateAccountResponse
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.network.api.KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse
import org.kin.sdk.base.network.api.KinTransactionApiV4.GetMiniumumKinVersionResponse
import org.kin.sdk.base.network.api.KinTransactionApiV4.GetRecentBlockHashResponse
import org.kin.sdk.base.network.api.KinTransactionApiV4.GetServiceConfigResponse
import org.kin.sdk.base.network.api.KinTransactionApiV4.GetTransactionHistoryResponse
import org.kin.sdk.base.network.api.KinTransactionApiV4.GetTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApiV4.SubmitTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionApiV4.SubmitTransactionResponse
import org.kin.sdk.base.network.api.agora.GrpcApi.Companion.canRetry
import org.kin.sdk.base.network.api.agora.GrpcApi.Companion.isForcedUpgrade
import org.kin.sdk.base.network.api.agora.GrpcApi.UnrecognizedProtoResponse
import org.kin.sdk.base.network.api.agora.GrpcApi.UnrecognizedResultException
import org.kin.sdk.base.network.api.toSubmitTransactionResultV4
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.SolanaKinTransaction
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.sdk.base.tools.PromisedCallback
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.TransactionResult
import org.kin.stellarfork.xdr.TransactionResultCode
import org.kin.stellarfork.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream

internal fun AccountInfo.toKinAccount(): KinAccount =
    KinAccount(
        accountId.toPublicKey(),
        balance = KinBalance(QuarkAmount(balance).toKin()),
        status = KinAccount.Status.Registered(0)
    )

internal fun Model.SolanaAccountId.toPublicKey(): Key.PublicKey =
    KeyPair.fromPublicKey(value.toByteArray()).asPublicKey()

internal fun ((CreateAccountResponse) -> Unit).createAccountResponse() =
    PromisedCallback<AccountService.CreateAccountResponse>({ response ->
        var account: KinAccount? = null
        val result = when (response.result) {
            AccountService.CreateAccountResponse.Result.OK -> {
                account = response.accountInfo.toKinAccount()
                CreateAccountResponse.Result.Ok
            }
            AccountService.CreateAccountResponse.Result.EXISTS -> CreateAccountResponse.Result.Exists
            else -> CreateAccountResponse.Result.UndefinedError(UnrecognizedResultException(UnrecognizedProtoResponse))
        }
        this(CreateAccountResponse(result, account))
    }, {
        val result = when {
            it.canRetry() -> CreateAccountResponse.Result.TransientFailure(it)
            it.isForcedUpgrade() -> CreateAccountResponse.Result.UpgradeRequiredError
            else -> CreateAccountResponse.Result.UndefinedError(UnrecognizedResultException(it))
        }
        this(CreateAccountResponse(result))
    })

internal fun ((GetServiceConfigResponse) -> Unit).createServiceConfigResponse(): PromisedCallback<TransactionService.GetServiceConfigResponse> {
    return PromisedCallback<TransactionService.GetServiceConfigResponse>({ response ->
        val subsidizer = response.subsidizerAccount.toPublicKey().asKinAccountId()
        val tokenProgram = response.tokenProgram.toPublicKey().asKinAccountId()
        val token = response.token.toPublicKey().asKinAccountId()

        this(
            GetServiceConfigResponse(
                GetServiceConfigResponse.Result.Ok,
                subsidizer,
                tokenProgram,
                token
            )
        )
    }, {
        val result = when {
            it.canRetry() -> GetServiceConfigResponse.Result.TransientFailure(it)
            it.isForcedUpgrade() -> GetServiceConfigResponse.Result.UpgradeRequiredError
            else -> GetServiceConfigResponse.Result.UndefinedError(UnrecognizedResultException(it))
        }
        this(GetServiceConfigResponse(result, null, null, null))
    })
}

internal fun ((GetMiniumumKinVersionResponse) -> Unit).getMinKinVersionResponse(): PromisedCallback<TransactionService.GetMinimumKinVersionResponse> {
    return PromisedCallback<TransactionService.GetMinimumKinVersionResponse>({ response ->
        this(
            GetMiniumumKinVersionResponse(
                GetMiniumumKinVersionResponse.Result.Ok,
                response.version
            )
        )
    }, {
        val result = when {
            it.canRetry() -> GetMiniumumKinVersionResponse.Result.TransientFailure(it)
            else -> GetMiniumumKinVersionResponse.Result.UndefinedError(UnrecognizedResultException(it))
        }
        this(GetMiniumumKinVersionResponse(result, -1))
    })
}

internal fun ((GetAccountResponse) -> Unit).getAccountInfoResponse(): PromisedCallback<AccountService.GetAccountInfoResponse> {
    return PromisedCallback<AccountService.GetAccountInfoResponse>({ response ->
        var account: KinAccount? = null
        val result = when (response.result) {
            AccountService.GetAccountInfoResponse.Result.OK -> {
                account = response.accountInfo.toKinAccount()
                GetAccountResponse.Result.Ok
            }
            AccountService.GetAccountInfoResponse.Result.NOT_FOUND -> GetAccountResponse.Result.NotFound
            else -> GetAccountResponse.Result.UndefinedError(UnrecognizedResultException(UnrecognizedProtoResponse))
        }
        this(GetAccountResponse(result, account))
        if (response.result == AccountService.GetAccountInfoResponse.Result.OK) {
            this(
                GetAccountResponse(
                    GetAccountResponse.Result.Ok,
                    response.accountInfo.toKinAccount()
                )
            )
        }
    }, {
        val result = when {
            it.canRetry() -> GetAccountResponse.Result.TransientFailure(
                it
            )
            it.isForcedUpgrade() -> GetAccountResponse.Result.UpgradeRequiredError
            else -> GetAccountResponse.Result.UndefinedError(UnrecognizedResultException(it))
        }
        this(GetAccountResponse(result, null))
    })
}


internal fun ((GetRecentBlockHashResponse) -> Unit).getRecentBlockHashResponse(): PromisedCallback<TransactionService.GetRecentBlockhashResponse> {
    return PromisedCallback<TransactionService.GetRecentBlockhashResponse>({ response ->
        this(
            GetRecentBlockHashResponse(
                GetRecentBlockHashResponse.Result.Ok,
                response.blockhash.toModel()
            )
        )
    }, {
        val result = when {
            it.canRetry() -> GetRecentBlockHashResponse.Result.TransientFailure(
                it
            )
            it.isForcedUpgrade() -> GetRecentBlockHashResponse.Result.UpgradeRequiredError
            else -> GetRecentBlockHashResponse.Result.UndefinedError(UnrecognizedResultException(it))
        }
        this(GetRecentBlockHashResponse(result, null))
    })
}

internal fun ((GetMinimumBalanceForRentExemptionResponse) -> Unit).getMinimumBalanceForRentExemptionResponse(): PromisedCallback<TransactionService.GetMinimumBalanceForRentExemptionResponse> {
    return PromisedCallback<TransactionService.GetMinimumBalanceForRentExemptionResponse>({ response ->
        this(
            GetMinimumBalanceForRentExemptionResponse(
                GetMinimumBalanceForRentExemptionResponse.Result.Ok,
                response.lamports
            )
        )
    }, {
        val result = when {
            it.canRetry() -> GetMinimumBalanceForRentExemptionResponse.Result.TransientFailure(
                it
            )
            it.isForcedUpgrade() -> GetMinimumBalanceForRentExemptionResponse.Result.UpgradeRequiredError
            else -> GetMinimumBalanceForRentExemptionResponse.Result.UndefinedError(
                UnrecognizedResultException(it)
            )
        }
        this(GetMinimumBalanceForRentExemptionResponse(result, null))
    })
}

internal fun ((GetTransactionResponse) -> Unit).getTransactionResponse(networkEnvironment: NetworkEnvironment): PromisedCallback<TransactionService.GetTransactionResponse> {
    return PromisedCallback<TransactionService.GetTransactionResponse>({
        var transaction: KinTransaction? = null
        val state = when (it.state) {
            TransactionService.GetTransactionResponse.State.SUCCESS -> {
                transaction = it.item.toHistoricalKinTransaction(networkEnvironment)
                GetTransactionResponse.Result.Ok
            }
            TransactionService.GetTransactionResponse.State.UNKNOWN,
            TransactionService.GetTransactionResponse.State.UNRECOGNIZED,
            null -> GetTransactionResponse.Result.NotFound
            TransactionService.GetTransactionResponse.State.FAILED ->
                GetTransactionResponse.Result.UndefinedError(Exception("Result.Failed"))
            TransactionService.GetTransactionResponse.State.PENDING -> {
                transaction = it.item.toAcknowledgedKinTransaction(networkEnvironment)
                GetTransactionResponse.Result.Ok
            }
        }
        this(GetTransactionResponse(state, transaction))
    }, {
        val result = when {
            it.canRetry() -> GetTransactionResponse.Result.TransientFailure(
                it
            )
            it.isForcedUpgrade() -> GetTransactionResponse.Result.UpgradeRequiredError
            else -> GetTransactionResponse.Result.UndefinedError(
                UnrecognizedResultException(it)
            )
        }
        this(GetTransactionResponse(result, null))
    })
}

internal fun ((SubmitTransactionResponse) -> Unit).submitTransactionResponse(
    request: SubmitTransactionRequest,
    networkEnvironment: NetworkEnvironment,
): PromisedCallback<TransactionService.SubmitTransactionResponse> {
    return PromisedCallback<TransactionService.SubmitTransactionResponse>({ response ->

        var transaction: KinTransaction? = null

        val result = when (response.result) {
            TransactionService.SubmitTransactionResponse.Result.OK,
            TransactionService.SubmitTransactionResponse.Result.ALREADY_SUBMITTED -> { // TODO: ALREADY_SUBMITTED could be bubbled up the chain to be more accurate/descriptive...
                transaction = TransactionService.HistoryItem.newBuilder()
                    .setSolanaTransaction(
                        Model.Transaction.newBuilder()
                            .setValue(
                                ByteString.copyFrom(
                                    request.transaction
                                        .copy(
                                            signatures = request.transaction.signatures.toMutableList()
                                                .apply {
                                                    set(0, response.signature.toModel())
                                                }
                                        )
                                        .marshal()
                                )
                            )
                    )
                    .apply {
                        request.invoiceList?.let { invoiceList = it.toProto() }
                    }
                    .build()
                    .toAcknowledgedKinTransaction(networkEnvironment)

                SubmitTransactionResponse.Result.Ok
            }
            TransactionService.SubmitTransactionResponse.Result.FAILED -> {
                val result = KinTransaction.RecordType.parseResultCode(response.transactionError.toResultXdr())
                    .toSubmitTransactionResultV4()

                if (result == SubmitTransactionResponse.Result.Ok) {
                    SubmitTransactionResponse.Result.UndefinedError(Exception("no transaction_error provided"))
                } else result
            }
            TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR -> {
                SubmitTransactionResponse.Result.InvoiceErrors(response.invoiceErrorsList.map {
                    when (it.reason) {
                        InvoiceError.Reason.ALREADY_PAID -> SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.ALREADY_PAID
                        InvoiceError.Reason.WRONG_DESTINATION -> SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.WRONG_DESTINATION
                        InvoiceError.Reason.SKU_NOT_FOUND -> SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.SKU_NOT_FOUND
                        InvoiceError.Reason.UNRECOGNIZED,
                        InvoiceError.Reason.UNKNOWN,
                        null -> SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.UNKNOWN
                    }
                })
            }
            TransactionService.SubmitTransactionResponse.Result.REJECTED -> SubmitTransactionResponse.Result.WebhookRejected
            TransactionService.SubmitTransactionResponse.Result.PAYER_REQUIRED ->
                SubmitTransactionResponse.Result.UndefinedError(Exception("subsidizer required: not yet supported in this sdk outside of community agora instance"))
            TransactionService.SubmitTransactionResponse.Result.UNRECOGNIZED,
            null -> SubmitTransactionResponse.Result.UndefinedError(Exception("Internal Error"))
        }
        this(SubmitTransactionResponse(result, transaction))
    }, {
        val result = when {
            it.canRetry() -> SubmitTransactionResponse.Result.TransientFailure(it)
            it.isForcedUpgrade() -> SubmitTransactionResponse.Result.UpgradeRequiredError
            else -> SubmitTransactionResponse.Result.UndefinedError(UnrecognizedResultException(it))
        }
        this(SubmitTransactionResponse(result, null))
    })
}

internal fun ((GetTransactionHistoryResponse) -> Unit).getTransactionHistoryResponse(
    networkEnvironment: NetworkEnvironment
): PromisedCallback<TransactionService.GetHistoryResponse> {
    return PromisedCallback<TransactionService.GetHistoryResponse>({
        var transactions: List<KinTransaction>? = null
        val result = when (it.result) {
            TransactionService.GetHistoryResponse.Result.OK -> {
                transactions = it.itemsList.map { historyItem ->
                    historyItem.toHistoricalKinTransaction(networkEnvironment)
                }.requireNoNulls()
                GetTransactionHistoryResponse.Result.Ok
            }
            TransactionService.GetHistoryResponse.Result.NOT_FOUND -> GetTransactionHistoryResponse.Result.NotFound
            TransactionService.GetHistoryResponse.Result.UNRECOGNIZED,
            null -> GetTransactionHistoryResponse.Result.UndefinedError(UnrecognizedResultException(UnrecognizedProtoResponse))
        }
        this(GetTransactionHistoryResponse(result, transactions))
    }, {
        val result = when {
            it.canRetry() -> GetTransactionHistoryResponse.Result.TransientFailure(it)
            it.isForcedUpgrade() -> GetTransactionHistoryResponse.Result.UpgradeRequiredError
            (it as? StatusRuntimeException)?.status?.code == Status.Code.NOT_FOUND -> GetTransactionHistoryResponse.Result.NotFound
            else -> GetTransactionHistoryResponse.Result.UndefinedError(UnrecognizedResultException(it))
        }
        this(GetTransactionHistoryResponse(result))
    })
}

internal fun TransactionResultCode.toResultXdr(): ByteArray {
    val transactionResult = TransactionResult().apply {
        result = TransactionResult.TransactionResultResult().apply {
            discriminant = this@toResultXdr
        }
        feeCharged = Int64().apply {
            int64 = 0
        }
        ext = TransactionResult.TransactionResultExt().apply {
            discriminant = 0
        }
    }
    return with(ByteArrayOutputStream()) {
        TransactionResult.encode(XdrDataOutputStream(this), transactionResult)
        toByteArray()
    }
}

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
internal fun Model.TransactionError.toResultXdr(): ByteArray {
    return when (reason) {
        Model.TransactionError.Reason.NONE -> TransactionResultCode.txSUCCESS
        Model.TransactionError.Reason.UNAUTHORIZED -> TransactionResultCode.txBAD_AUTH
        Model.TransactionError.Reason.BAD_NONCE -> TransactionResultCode.txBAD_SEQ
        Model.TransactionError.Reason.INSUFFICIENT_FUNDS -> TransactionResultCode.txINSUFFICIENT_BALANCE
        Model.TransactionError.Reason.INVALID_ACCOUNT -> TransactionResultCode.txNO_ACCOUNT
        Model.TransactionError.Reason.UNKNOWN -> TransactionResultCode.txFAILED
        Model.TransactionError.Reason.UNRECOGNIZED -> TransactionResultCode.txINTERNAL_ERROR
    }.toResultXdr()

}

internal fun TransactionService.HistoryItem.toAcknowledgedKinTransaction(networkEnvironment: NetworkEnvironment): KinTransaction {
    return if (hasSolanaTransaction()) {
        SolanaKinTransaction(
            solanaTransaction.value.toByteArray(),
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                transactionError.toResultXdr()
            ),
            networkEnvironment,
            if (hasInvoiceList()) invoiceList.toInvoiceList() else null
        )
    } else {
        StellarKinTransaction(
            stellarTransaction.envelopeXdr.toByteArray(),
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                stellarTransaction.resultXdr.toByteArray()
            ),
            networkEnvironment,
            if (hasInvoiceList()) invoiceList.toInvoiceList() else null
        )
    }
}

internal fun TransactionService.HistoryItem.toHistoricalKinTransaction(networkEnvironment: NetworkEnvironment): KinTransaction {
    return if (hasSolanaTransaction()) {
        SolanaKinTransaction(
            solanaTransaction.value.toByteArray(),
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                transactionError.toResultXdr(),
                KinTransaction.PagingToken(Base64.encodeBase64String(cursor.value.toByteArray())!!)
            ),
            networkEnvironment,
            if (hasInvoiceList()) invoiceList.toInvoiceList() else null
        )
    } else {
        StellarKinTransaction(
            stellarTransaction.envelopeXdr.toByteArray(),
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                stellarTransaction.resultXdr.toByteArray(),
                KinTransaction.PagingToken(Base64.encodeBase64String(cursor.value.toByteArray())!!)
            ),
            networkEnvironment,
            if (hasInvoiceList()) invoiceList.toInvoiceList() else null
        )
    }
}

internal fun ((KinAccountApiV4.ResolveTokenAccountsResponse) -> Unit).resolveTokenAccountsResponse(): PromisedCallback<AccountService.ResolveTokenAccountsResponse> {
    return PromisedCallback<AccountService.ResolveTokenAccountsResponse>({ response ->
        val tokenAccounts = response.tokenAccountsList.map { it.toPublicKey() }

        this(
            KinAccountApiV4.ResolveTokenAccountsResponse(
                KinAccountApiV4.ResolveTokenAccountsResponse.Result.Ok,
                tokenAccounts
            )
        )
    }, {
        val result = when {
            it.canRetry() -> KinAccountApiV4.ResolveTokenAccountsResponse.Result.TransientFailure(it)
            it.isForcedUpgrade() -> KinAccountApiV4.ResolveTokenAccountsResponse.Result.UpgradeRequiredError
            else -> KinAccountApiV4.ResolveTokenAccountsResponse.Result.UndefinedError(
                UnrecognizedResultException(it)
            )
        }
        this(KinAccountApiV4.ResolveTokenAccountsResponse(result, emptyList()))
    })
}

internal fun Model.TransactionSignature.toModel(): Signature =
    Signature(FixedByteArray64(value.toByteArray()))

internal fun Model.Blockhash.toModel(): Hash = Hash(FixedByteArray32(value.toByteArray()))
