package org.kin.sdk.base.network.api.agora

import org.kin.agora.gen.account.v3.AccountService.AccountInfo
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.transaction.v3.TransactionService.HistoryItem
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
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Base64

internal fun AccountInfo.toKinAccount(): KinAccount =
    KinAccount(
        accountId.toPublicKey(),
        balance = KinBalance(QuarkAmount(balance).toKin()),
        status = KinAccount.Status.Registered(sequenceNumber)
    )

internal fun Model.StellarAccountId.toPublicKey(): Key.PublicKey =
    KeyPair.fromAccountId(value).asPublicKey()

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

// The SHA-224 hash of the Invoice.
fun Model.Invoice.sha224Hash(): SHA224Hash = SHA224Hash.of(toByteArray())

// The SHA-224 hash of the InvoiceList
fun Model.InvoiceList.sha224Hash(): SHA224Hash = SHA224Hash.of(toByteArray())
