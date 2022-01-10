package org.kin.sdk.base.models

import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.stellarfork.KeyPair
import java.text.SimpleDateFormat
import java.util.Date

fun KeyPair.asPrivateKey(): Key.PrivateKey =
    rawSecretSeed?.let { Key.PrivateKey(it) } ?: privateKey?.let { Key.PrivateKey(it) } ?: throw Exception("No Private Key Found")

fun KeyPair.asPublicKey(): Key.PublicKey =
    Key.PublicKey(publicKey)

fun KeyPair.asKinAccountId(): KinAccount.Id =
    KinAccount.Id(asPublicKey().value)

fun KeyPair.asKinAccount(): KinAccount =
    KinAccount(asPrivateKey())

fun Key.asKinAccountId(): KinAccount.Id =
    KinAccount.Id(asPublicKey().value)

fun Key.asPublicKey(): Key.PublicKey =
    if (this is Key.PrivateKey && value.size == 32) KeyPair.fromSecretSeed(value).asPublicKey()
    else if (this is Key.PrivateKey) KeyPair.fromPrivateKey(value).asPublicKey()
    else this as Key.PublicKey

fun KinAccount.Id.toKeyPair(): KeyPair = KeyPair.fromPublicKey(value)

fun KinAccount.toSigningKeyPair(): KeyPair =
    (key as? Key.PrivateKey)?.let {
        if (it.value.size == 32) KeyPair.fromSecretSeed(it.value)
        else KeyPair.fromPrivateKey(it.value)
    } ?: throw Exception("Cannot get a signing KeyPair")

fun Key.PrivateKey.toSigningKeyPair(): KeyPair =
    if (value.size == 32) KeyPair.fromSecretSeed(value)
    else KeyPair.fromPrivateKey(value)

fun String.toUTF8Bytes(): ByteArray = toByteArray(Charsets.UTF_8)

fun ByteArray.toUTF8String(): String = String(this, Charsets.UTF_8)

fun KinTransaction.asKinPayments(): List<KinPayment> {
    var offset = 0
    return paymentOperations.mapIndexed { index, payment ->
        KinPayment(
            KinPayment.Id(transactionHash, offset++),
            KinPayment.Status.Success,
            payment.source,
            payment.destination,
            payment.amount,
            fee,
            memo,
            recordType.timestamp,
            invoiceList?.invoices?.get(index)
        )
    }
}

fun List<KinTransaction>.asKinPayments(reversed: Boolean = false): List<KinPayment> {
    return flatMap {
        val payments = it.asKinPayments()
        if (reversed) payments.reversed()
        else payments
    }
}

class KinDateFormat(dateString: String) {
    companion object {
        const val KIN_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX"

        @JvmStatic
        fun timestampToString(timestamp: Long): String {
            return SimpleDateFormat(KIN_DATE_FORMAT).format(Date(timestamp))
        }
    }

    val timestamp = SimpleDateFormat(KIN_DATE_FORMAT).parse(dateString).time
}

