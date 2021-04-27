package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.common.v3.Model.Invoice
import org.kin.agora.gen.common.v3.Model.InvoiceList
import org.kin.agora.gen.common.v3.Model.StellarAccountId
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toQuarks

fun KinAccount.Id.toProtoStellarAccountId(): StellarAccountId =
    StellarAccountId.newBuilder()
        .setValue(toKeyPair().accountId)
        .build()


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
