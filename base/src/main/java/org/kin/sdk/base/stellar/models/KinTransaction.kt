package org.kin.sdk.base.stellar.models

import org.kin.agora.gen.transaction.v4.TransactionService
import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.getAgoraMemo
import org.kin.sdk.base.models.solana.MemoProgram
import org.kin.sdk.base.models.solana.SystemProgram
import org.kin.sdk.base.models.solana.TokenProgram
import org.kin.sdk.base.models.solana.unmarshal
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.network.api.agora.toPublicKey
import org.kin.sdk.base.stellar.models.KinTransaction.RecordType
import org.kin.sdk.base.tools.byteArrayToLong
import org.kin.sdk.base.tools.subByteArray
import org.kin.sdk.base.tools.toHexString
import org.kin.stellarfork.codec.Base64

val org.kin.sdk.base.models.solana.Transaction.totalAmount: KinAmount
    get() = paymentOperations.map { it.amount }.reduce { acc, kinAmount -> acc + kinAmount }
val org.kin.sdk.base.models.solana.Transaction.transactionHash: TransactionHash
    get() = TransactionHash(signatures.first().value.byteArray)
val org.kin.sdk.base.models.solana.Transaction.signingSource: KinAccount.Id
    get() = message.accounts[1].asKinAccountId()
val org.kin.sdk.base.models.solana.Transaction.fee: QuarkAmount
    get() = QuarkAmount(0)
val org.kin.sdk.base.models.solana.Transaction.memo: KinMemo
    get() = message.instructions.find { message.accounts[it.programIndex.toInt()] == MemoProgram.PROGRAM_KEY }
        ?.let {
            val base64Decoded = Base64.decodeBase64(it.data)
            if (base64Decoded != null) {
                val memo = KinMemo(base64Decoded)
                if (memo.getAgoraMemo() != null) {
                    memo
                } else {
                    KinMemo(it.data, KinMemo.Type.CharsetEncoded(Charsets.UTF_8))
                }
            } else {
                KinMemo(String(it.data, Charsets.UTF_8), Charsets.UTF_8)
            }
        } ?: KinMemo.NONE
val org.kin.sdk.base.models.solana.Transaction.paymentOperations: List<KinOperation.Payment>
    get() = message.instructions.filter {
        val programKey = message.accounts[it.programIndex.toInt()]
        programKey != MemoProgram.PROGRAM_KEY
                && programKey != SystemProgram.PROGRAM_KEY
                && it.data.first().toInt() == TokenProgram.Command.Transfer.value
    }.map {
        val amount =
            QuarkAmount(it.data.subByteArray(1, it.data.size - 1).byteArrayToLong()).toKin()
        val source = message.accounts[it.accounts[0].toInt()].asKinAccountId()
        val destination = message.accounts[it.accounts[1].toInt()].asKinAccountId()
        KinOperation.Payment(amount, source, destination)
    }


data class SolanaKinTransaction @JvmOverloads constructor(
    override val bytesValue: ByteArray,
    override val recordType: RecordType = RecordType.InFlight(System.currentTimeMillis()),
    override val networkEnvironment: NetworkEnvironment,
    override val invoiceList: InvoiceList? = null
) : KinTransaction {
    private val transaction = org.kin.sdk.base.models.solana.Transaction.unmarshal(bytesValue)

    override val transactionHash: TransactionHash
        get() = transaction.transactionHash
    override val signingSource: KinAccount.Id
        get() = transaction.signingSource
    override val fee: QuarkAmount
        get() = transaction.fee
    override val memo: KinMemo
        get() = transaction.memo
    override val paymentOperations: List<KinOperation.Payment>
        get() = transaction.paymentOperations

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SolanaKinTransaction) return false

        if (!bytesValue.contentEquals(other.bytesValue)) return false
        if (recordType != other.recordType) return false
        if (networkEnvironment != other.networkEnvironment) return false
        if (invoiceList != other.invoiceList) return false
        if (transaction != other.transaction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytesValue.contentHashCode()
        result = 31 * result + recordType.hashCode()
        result = 31 * result + networkEnvironment.hashCode()
        result = 31 * result + (invoiceList?.hashCode() ?: 0)
        result = 31 * result + transaction.hashCode()
        return result
    }

    override fun toString(): String {
        return "SolanaKinTransaction(bytesValue=${bytesValue.toHexString()}, recordType=$recordType, networkEnvironment=$networkEnvironment, invoiceList=$invoiceList, transaction=$transaction)"
    }
}

interface KinTransaction {
    val bytesValue: ByteArray
    val recordType: RecordType
    val networkEnvironment: NetworkEnvironment
    val invoiceList: InvoiceList?
    val transactionHash: TransactionHash
    val signingSource: KinAccount.Id
    val fee: QuarkAmount
    val memo: KinMemo
    val paymentOperations: List<KinOperation.Payment>

    sealed class RecordType(val value: Int) {
        abstract val timestamp: Long

        data class InFlight(
            override val timestamp: Long
        ) : RecordType(0)

        data class Acknowledged(
            override val timestamp: Long,
            val resultCode: ResultCode,
        ) : RecordType(1)

        data class Historical(
            override val timestamp: Long,
            val resultCode: ResultCode,
            val pagingToken: PagingToken
        ) : RecordType(2)
    }

    sealed class ResultCode(val value: Int) {
        object Success : ResultCode(0)
        object Failed : ResultCode(-1)
        object TooEarly : ResultCode(-2)
        object TooLate : ResultCode(-3)
        object MissingOperation : ResultCode(-4)
        object BadSequenceNumber : ResultCode(-5)
        object BadAuth : ResultCode(-6)
        object InsufficientBalance : ResultCode(-7)
        object NoAccount : ResultCode(-8)
        object InsufficientFee : ResultCode(-9)
        object BadAuthExtra : ResultCode(-10)
        object InternalError : ResultCode(-11)
    }

    data class PagingToken(val value: String) : Comparable<PagingToken> {
        override fun compareTo(other: PagingToken): Int = value.compareTo(other.value)
    }
}

data class StellarKinTransaction @JvmOverloads constructor(
    override val bytesValue: ByteArray,
    override val recordType: RecordType = RecordType.InFlight(System.currentTimeMillis()),
    override val networkEnvironment: NetworkEnvironment,
    override val invoiceList: InvoiceList? = null,
    internal val historyItem: TransactionService.HistoryItem
) : KinTransaction {
    override val transactionHash: TransactionHash
        get() = TransactionHash(historyItem.transactionId.value.toByteArray())
    override val signingSource: KinAccount.Id
        get() = historyItem.paymentsList.first().source.toPublicKey().asKinAccountId()
    override val fee: QuarkAmount
        get() = QuarkAmount(0)
    override val memo: KinMemo
        get() = KinMemo.NONE // We don't have the memo for stellar based transactions parsed in HistoryItems
    override val paymentOperations: List<KinOperation.Payment>
        get() = historyItem.paymentsList.map {
            KinOperation.Payment(
                QuarkAmount(it.amount).toKin(),
                it.source.toPublicKey().asKinAccountId(),
                it.destination.toPublicKey().asKinAccountId()
            )
        }

//    fun isKinNonNativeAsset(): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    internal val transactionEnvelope: TransactionEnvelope by lazy {
//        TODO("Not yet implemented")
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KinTransaction) return false

        if (!bytesValue.contentEquals(other.bytesValue)) return false
        if (recordType != other.recordType) return false
        if (networkEnvironment != other.networkEnvironment) return false
        if (invoiceList != other.invoiceList) return false

        return true
    }

    override fun hashCode(): Int {
        return transactionHash.hashCode()
    }

    override fun toString(): String {
        return "StellarKinTransaction(bytesValue=${bytesValue.toHexString()}, recordType=$recordType, networkEnvironment=$networkEnvironment, invoiceList=$invoiceList)"
    }
}

//data class StellarKinTransaction @JvmOverloads constructor(
//    override val bytesValue: ByteArray,
//    override val recordType: RecordType = RecordType.InFlight(System.currentTimeMillis()),
//    override val networkEnvironment: NetworkEnvironment,
//    override val invoiceList: InvoiceList? = null
//) : KinTransaction {
//
//    fun isKinNonNativeAsset(): Boolean {
//        return transactionEnvelope.tx!!.operations.filter { operation ->
//            operation!!.body!!.discriminant == OperationType.PAYMENT
//        }.map { PaymentOperation.Builder(it!!.body!!.paymentOp!!).build() }
//            .map { (it.asset as? AssetTypeCreditAlphaNum4)?.code == "KIN" }
//            .reduce { acc, b -> acc && b }
//    }
//
//    internal val transactionEnvelope: TransactionEnvelope by lazy {
//        TransactionEnvelope.decode(XdrDataInputStream(ByteArrayInputStream(bytesValue)))
//    }
//
//    override val transactionHash: TransactionHash by lazy {
//        TransactionHash(
//            Transaction.fromEnvelopeXdr(
//                transactionEnvelope,
//                networkEnvironment.getNetwork()
//            ).hash()
//        )
//    }
//
//    override val signingSource: KinAccount.Id by lazy {
//        KinAccount.Id(
//            KeyPair.fromXdrPublicKey(transactionEnvelope.tx!!.sourceAccount!!.accountID!!)
//                .asPublicKey().value
//        )
//    }
//
//    override val fee: QuarkAmount by lazy {
//        QuarkAmount(transactionEnvelope.tx!!.fee!!.uint32!!.toLong())
//    }
//
//    override val memo: KinMemo by lazy {
//        transactionEnvelope.tx!!.memo?.let { memo ->
//            if (memo.discriminant == MemoType.MEMO_HASH) {
//                KinMemo(memo.hash!!.hash!!)
//            } else {
//                memo.text?.let { KinMemo(it, Charsets.UTF_8) } ?: KinMemo.NONE
//            }
//        } ?: KinMemo.NONE
//    }
//
//    override val paymentOperations: List<KinOperation.Payment> by lazy {
//        transactionEnvelope.tx!!.operations.filter { operation ->
//            operation!!.body!!.discriminant == OperationType.PAYMENT
//        }.map { PaymentOperation.Builder(it!!.body!!.paymentOp!!).build() }
//            .map { payOp: PaymentOperation ->
//                KinOperation.Payment(
//                    KinAmount(payOp.amount),
//                    when {
//                        payOp.sourceAccount != null -> KinAccount.Id(payOp.sourceAccount!!.publicKey)
//                        else -> signingSource
//                    },
//                    KinAccount.Id(payOp.destination.publicKey)
//                )
//            }
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is KinTransaction) return false
//
//        if (!bytesValue.contentEquals(other.bytesValue)) return false
//        if (recordType != other.recordType) return false
//        if (networkEnvironment != other.networkEnvironment) return false
//        if (invoiceList != other.invoiceList) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return transactionHash.hashCode()
//    }
//
//    override fun toString(): String {
//        return "StellarKinTransaction(bytesValue=${bytesValue.toHexString()}, recordType=$recordType, networkEnvironment=$networkEnvironment, invoiceList=$invoiceList)"
//    }
//}
