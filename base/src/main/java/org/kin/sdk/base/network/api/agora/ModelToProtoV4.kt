package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import org.kin.agora.gen.account.v4.AccountService
import org.kin.agora.gen.common.v4.Model
import org.kin.agora.gen.transaction.v4.TransactionService
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.solana.marshal
import org.kin.sdk.base.network.api.KinAccountApiV4
import org.kin.sdk.base.network.api.KinAccountCreationApiV4
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.stellar.models.paymentOperations
import org.kin.sdk.base.stellar.models.totalAmount
import org.kin.stellarfork.codec.Base64
import java.math.BigDecimal

internal fun KinAccount.Id.toProtoSolanaAccountId(): Model.SolanaAccountId =
    Model.SolanaAccountId.newBuilder()
        .setValue(ByteString.copyFrom(value))
        .build()

internal fun KinAccountCreationApiV4.CreateAccountRequest.toGrpcRequest(): AccountService.CreateAccountRequest =
    AccountService.CreateAccountRequest.newBuilder()
        .setCommitment(Model.Commitment.SINGLE)
        .setTransaction(
            Model.Transaction.newBuilder()
                .setValue(ByteString.copyFrom(transaction.marshal()))
        )
        .build()


internal fun KinAccountApiV4.GetAccountRequest.toGrpcRequest(): AccountService.GetAccountInfoRequest =
    AccountService.GetAccountInfoRequest.newBuilder()
        .setAccountId(accountId.toProtoSolanaAccountId())
        .setCommitment(Model.Commitment.SINGLE)
        .build()

internal fun KinAccountApiV4.ResolveTokenAccountsRequest.toGrpcRequest(): AccountService.ResolveTokenAccountsRequest? =
    AccountService.ResolveTokenAccountsRequest.newBuilder()
        .setIncludeAccountInfo(true)
        .setAccountId(accountId.toProtoSolanaAccountId())
        .build()

internal fun KinTransactionApiV4.GetServiceConfigRequest.toGrpcRequest() =
    TransactionService.GetServiceConfigRequest.getDefaultInstance()

internal fun KinTransactionApiV4.GetMiniumumKinVersionRequest.toGrpcRequest(): TransactionService.GetMinimumKinVersionRequest? =
    TransactionService.GetMinimumKinVersionRequest.getDefaultInstance()

internal fun KinTransactionApiV4.GetRecentBlockHashRequest.toGrpcRequest() =
    TransactionService.GetRecentBlockhashRequest.getDefaultInstance()

internal fun KinTransactionApiV4.GetMinimumBalanceForRentExemptionRequest.toGrpcRequest() =
    TransactionService.GetMinimumBalanceForRentExemptionRequest.newBuilder()
        .setSize(size)
        .build()

internal fun KinTransactionApiV4.GetTransactionRequest.toGrpcRequest(): TransactionService.GetTransactionRequest =
    TransactionService.GetTransactionRequest.newBuilder()
        .setCommitment(Model.Commitment.SINGLE)
        .setTransactionId(
            Model.TransactionId.newBuilder()
                .setValue(ByteString.copyFrom(transactionHash.rawValue))
        )
        .build()

internal fun KinTransactionApiV4.SignTransactionRequest.toGrpcRequest(): TransactionService.SignTransactionRequest {
    return TransactionService.SignTransactionRequest.newBuilder()
        .setTransaction(
            Model.Transaction.newBuilder()
                .setValue(ByteString.copyFrom(transaction.marshal()))
        )
        .apply {
            this@toGrpcRequest.invoiceList?.let { invoiceList = it.toProto() }
        }
        .build()
}

internal fun KinTransactionApiV4.SubmitTransactionRequest.toGrpcRequest(): TransactionService.SubmitTransactionRequest? {

    val amount = try {
        transaction.totalAmount
    } catch (t: Throwable) {
        KinAmount.ONE
    }
    val commitment = when {
        amount.value < BigDecimal(50000) -> {
            Model.Commitment.RECENT
        }
        amount.value < BigDecimal(500000) -> {
            Model.Commitment.SINGLE
        }
        else -> {
            Model.Commitment.MAX
        }
    }

    return TransactionService.SubmitTransactionRequest.newBuilder()
        .setTransaction(
            Model.Transaction.newBuilder()
                .setValue(ByteString.copyFrom(transaction.marshal()))
        )
        .apply {
            this@toGrpcRequest.invoiceList?.let { invoiceList = it.toProto() }
        }
        .setCommitment(commitment)
        .build()
}

internal fun KinTransactionApiV4.GetTransactionHistoryRequest.toGrpcRequest(): TransactionService.GetHistoryRequest =
    TransactionService.GetHistoryRequest.newBuilder()
        .setAccountId(accountId.toProtoSolanaAccountId())
        .apply {
            if (pagingToken != null && pagingToken.value.isNotEmpty()) {
                cursor = TransactionService.Cursor.newBuilder()
                    .setValue(ByteString.copyFrom(Base64.decodeBase64(pagingToken.value)))
                    .build()
            }
            direction = when (order) {
                KinTransactionApiV4.GetTransactionHistoryRequest.Order.Ascending -> TransactionService.GetHistoryRequest.Direction.ASC
                KinTransactionApiV4.GetTransactionHistoryRequest.Order.Descending -> TransactionService.GetHistoryRequest.Direction.DESC
            }
        }
        .build()
