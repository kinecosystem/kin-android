package org.kin.sdk.base.models

import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.stellarfork.Account
import org.kin.stellarfork.AssetTypeNative
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.Network
import org.kin.stellarfork.Transaction
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.responses.AccountResponse
import org.kin.stellarfork.responses.SubmitTransactionResponse
import org.kin.stellarfork.responses.TransactionResponse
import org.kin.stellarfork.xdr.TransactionEnvelope
import org.kin.stellarfork.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

fun KeyPair.asPrivateKey(): Key.PrivateKey =
    rawSecretSeed?.let { Key.PrivateKey(it) } ?: throw Exception("No Private Key Found")

fun KeyPair.asPublicKey(): Key.PublicKey =
    Key.PublicKey(publicKey)

fun KeyPair.asKinAccountId(): KinAccount.Id =
    KinAccount.Id(asPublicKey().value)

fun KeyPair.asKinAccount(): KinAccount =
    KinAccount(asPrivateKey())

fun Key.asKinAccountId(): KinAccount.Id =
    KinAccount.Id(asPublicKey().value)

fun Key.asPublicKey(): Key.PublicKey =
    if (this is Key.PrivateKey) KeyPair.fromSecretSeed(value).asPublicKey()
    else this as Key.PublicKey

fun KinAccount.Id.toKeyPair(): KeyPair = KeyPair.fromPublicKey(value)

fun KinAccount.toSigningKeyPair(): KeyPair =
    (key as? Key.PrivateKey)?.let { KeyPair.fromSecretSeed(it.value) }
        ?: throw Exception("Cannot get a signing KeyPair")

fun String.toUTF8Bytes(): ByteArray = toByteArray(Charsets.UTF_8)

fun ByteArray.toUTF8String(): String = String(this)

fun AccountResponse.kinBalance(): KinBalance {
    val currentBalanceAmount = balances.filter { it.asset is AssetTypeNative }
        .map { KinAmount(it.balance) }
        .firstOrNull() ?: KinAmount.ZERO
    return KinBalance(currentBalanceAmount)
}

fun AccountResponse.kinAccount(): KinAccount {
    return KinAccount(
        keypair.asPublicKey(),
        balance = kinBalance(),
        status = KinAccount.Status.Registered(sequenceNumber)
    )
}

fun TransactionResponse.envelopeXdrBytes(): ByteArray = Base64().decode(envelopeXdr.toUTF8Bytes())!!

fun TransactionResponse.resultXdrBytes(): ByteArray = Base64().decode(resultXdr.toUTF8Bytes())!!

fun SubmitTransactionResponse.envelopeXdrBytes(): ByteArray =
    Base64().decode(getEnvelopeXdr().toUTF8Bytes())!!

fun SubmitTransactionResponse.resultXdrBytes(): ByteArray =
    Base64().decode(getResultXdr().toUTF8Bytes())!!

fun TransactionResponse.asKinTransaction(networkEnvironment: NetworkEnvironment): KinTransaction =
    KinTransaction(
        envelopeXdrBytes(),
        KinTransaction.RecordType.Historical(
            KinDateFormat(createdAt).timestamp,
            resultXdrBytes(),
            KinTransaction.PagingToken(pagingToken)
        ),
        networkEnvironment
    )

fun KinTransaction.asKinPayments(): List<KinPayment> {
    var offset = 0
    return paymentOperations.map { payment ->
        KinPayment(
            KinPayment.Id(transactionHash, offset++),
            KinPayment.Status.Success,
            payment.source,
            payment.destination,
            payment.amount,
            fee,
            memo,
            recordType.timestamp
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

fun Transaction.toEnvelopeXdrBytes(): ByteArray {
    return try {
        val envelope: TransactionEnvelope = this.toEnvelopeXdr()
        val outputStream = ByteArrayOutputStream()
        val xdrOutputStream = XdrDataOutputStream(outputStream)
        TransactionEnvelope.encode(xdrOutputStream, envelope)
        outputStream.toByteArray()
    } catch (e: IOException) {
        throw AssertionError(e)
    }
}

fun KinAccount.toAccount(): Account =
    (status as? KinAccount.Status.Registered)?.let {
        try {
            val keyPair = toSigningKeyPair()
            if (keyPair.canSign()) Account(keyPair, it.sequence)
            else null
        } catch (e: Exception) {
            null
        }
    } ?: throw Exception("Cannot convert to Stellar Account, signing key invalid!")


fun Transaction.toKinTransaction(networkEnvironment: NetworkEnvironment) = KinTransaction(toEnvelopeXdrBytes(), networkEnvironment = networkEnvironment)

fun NetworkEnvironment.getNetwork() = Network(networkPassphrase)

class KinDateFormat(dateString: String) {
    companion object {
        const val KIN_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX"

        @JvmStatic
        fun timestampToString(timestamp: Long): String {
            return FORMAT.format(Date(timestamp))
        }

        val FORMAT by lazy {
            try {
                SimpleDateFormat(KIN_DATE_FORMAT)
            } catch (e: Exception) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            }
        }
    }

    val timestamp =FORMAT.parse(dateString).time
}

