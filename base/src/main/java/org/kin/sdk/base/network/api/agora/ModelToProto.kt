package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.common.v3.Model.Invoice
import org.kin.agora.gen.common.v3.Model.InvoiceList
import org.kin.agora.gen.common.v3.Model.StellarAccountId
import org.kin.agora.gen.transaction.v3.TransactionService
import org.kin.agora.gen.transaction.v3.TransactionService.GetHistoryRequest.Direction
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryRequest.Order
import org.kin.stellarfork.codec.Base64

fun KinAccount.Id.toProtoStellarAccountId(): StellarAccountId =
    StellarAccountId.newBuilder()
        .setValue(toKeyPair().accountId)
        .build()

fun KinAccountCreationApi.CreateAccountRequest.toGrpcRequest(): AccountService.CreateAccountRequest =
    AccountService.CreateAccountRequest.newBuilder()
        .setAccountId(
            StellarAccountId.newBuilder()
                .setValue(accountId.toKeyPair().accountId)
        )
        .build()

fun KinAccountApi.GetAccountRequest.toGrpcRequest(): AccountService.GetAccountInfoRequest =
    AccountService.GetAccountInfoRequest.newBuilder()
        .setAccountId(
            StellarAccountId.newBuilder()
                .setValue(accountId.toKeyPair().accountId)
        ).build()

fun KinTransactionApi.GetTransactionHistoryRequest.toGrpcRequest(): TransactionService.GetHistoryRequest =
    TransactionService.GetHistoryRequest.newBuilder()
        .setAccountId(accountId.toProtoStellarAccountId())
        .apply {
            if (pagingToken != null && pagingToken.value.isNotEmpty()) {
                cursor =  TransactionService.Cursor.newBuilder()
                    .setValue(ByteString.copyFrom(Base64.decodeBase64(pagingToken.value)))
                    .build()
            }
            direction = when (order) {
                Order.Ascending -> Direction.ASC
                Order.Descending -> Direction.DESC
            }
        }
        .build()

fun KinTransactionApi.GetTransactionRequest.toGrpcRequest(): TransactionService.GetTransactionRequest =
    TransactionService.GetTransactionRequest.newBuilder()
        .setTransactionHash(transactionHash.toProtoTransactionHash())
        .build()

fun KinTransactionApi.SubmitTransactionRequest.toGrpcRequest(): TransactionService.SubmitTransactionRequest? {
    val builder = TransactionService.SubmitTransactionRequest.newBuilder()
        .setEnvelopeXdr(ByteString.copyFrom(transactionEnvelopeXdr))
    invoiceList?.let {
        builder.setInvoiceList(it.invoices.toProto())
    }
    return builder.build()
}

fun TransactionHash.toProtoTransactionHash(): Model.TransactionHash =
    Model.TransactionHash.newBuilder().setValue(ByteString.copyFrom(rawValue))
        .build()

fun LineItem.toProto() = Invoice.LineItem.newBuilder()
    .setTitle(title)
    .setDescription(description)
    .setAmount(amount.toQuarks().value)
    .apply {
        if (this@toProto.sku != null) {
            sku = ByteString.copyFrom(this@toProto.sku.bytes)
        }
    }
    .build()

fun List<LineItem>.toProto() = map { it.toProto() }

fun org.kin.sdk.base.models.Invoice.toProto() = Invoice.newBuilder()
    .addAllItems(lineItems.toProto())
    .build()

fun List<org.kin.sdk.base.models.Invoice>.toProto() = InvoiceList.newBuilder()
    .addAllInvoices(this.map { it.toProto() })
    .build()

fun org.kin.sdk.base.models.InvoiceList.toProto() = InvoiceList.newBuilder()
    .addAllInvoices(this.invoices.map { it.toProto() })
    .build()
