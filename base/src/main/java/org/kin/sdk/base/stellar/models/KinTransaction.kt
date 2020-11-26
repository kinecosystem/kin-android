package org.kin.sdk.base.stellar.models

import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.getAgoraMemo
import org.kin.sdk.base.models.getNetwork
import org.kin.sdk.base.models.solana.MemoProgram
import org.kin.sdk.base.models.solana.SystemProgram
import org.kin.sdk.base.models.solana.TokenProgram
import org.kin.sdk.base.models.solana.unmarshal
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.stellar.models.KinTransaction.RecordType
import org.kin.sdk.base.tools.byteArrayToLong
import org.kin.sdk.base.tools.subByteArray
import org.kin.sdk.base.tools.toHexString
import org.kin.stellarfork.AssetTypeCreditAlphaNum4
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.PaymentOperation
import org.kin.stellarfork.Transaction
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.MemoType
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.TransactionEnvelope
import org.kin.stellarfork.xdr.TransactionResult
import org.kin.stellarfork.xdr.TransactionResultCode
import org.kin.stellarfork.xdr.XdrDataInputStream
import java.io.ByteArrayInputStream

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
        val amount = QuarkAmount(it.data.subByteArray(1, it.data.size - 1).byteArrayToLong()).toKin()
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
        ) : RecordType(0) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as RecordType.InFlight

                return this.timestamp == other.timestamp
            }

            override fun hashCode(): Int {
                return timestamp.hashCode()
            }
        }

        data class Acknowledged(
            override val timestamp: Long,
            val resultXdrBytes: ByteArray
        ) : RecordType(1) {
            val resultCode: ResultCode by lazy {
                RecordType.Companion.parseResultCode(
                    resultXdrBytes
                )
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is RecordType.Acknowledged) return false

                if (timestamp != other.timestamp) return false
                if (!resultXdrBytes.contentEquals(other.resultXdrBytes)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = timestamp.hashCode()
                result = 31 * result + resultXdrBytes.contentHashCode()
                return result
            }
        }

        data class Historical(
            override val timestamp: Long,
            val resultXdrBytes: ByteArray,
            val pagingToken: PagingToken
        ) : RecordType(2) {
            val resultCode: ResultCode by lazy {
                RecordType.Companion.parseResultCode(
                    resultXdrBytes
                )
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is RecordType.Historical) return false

                if (timestamp != other.timestamp) return false
                if (!resultXdrBytes.contentEquals(other.resultXdrBytes)) return false
                if (pagingToken != other.pagingToken) return false

                return true
            }

            override fun hashCode(): Int {
                var result = timestamp.hashCode()
                result = 31 * result + resultXdrBytes.contentHashCode()
                result = 31 * result + pagingToken.hashCode()
                return result
            }
        }

        companion object {
            fun parseResultCode(resultXdrBytes: ByteArray): ResultCode {
                val transactionResult =
                    TransactionResult.decode(XdrDataInputStream((ByteArrayInputStream(resultXdrBytes))))
                return when (transactionResult.result?.discriminant) {
                    TransactionResultCode.txSUCCESS -> ResultCode.Success
                    TransactionResultCode.txFAILED -> ResultCode.Failed
                    TransactionResultCode.txTOO_EARLY -> ResultCode.TooEarly
                    TransactionResultCode.txTOO_LATE -> ResultCode.TooLate
                    TransactionResultCode.txMISSING_OPERATION -> ResultCode.MissingOperation
                    TransactionResultCode.txBAD_SEQ -> ResultCode.BadSequenceNumber
                    TransactionResultCode.txBAD_AUTH -> ResultCode.BadAuth
                    TransactionResultCode.txINSUFFICIENT_BALANCE -> ResultCode.InsufficientBalance
                    TransactionResultCode.txNO_ACCOUNT -> ResultCode.NoAccount
                    TransactionResultCode.txINSUFFICIENT_FEE -> ResultCode.InsufficientFee
                    TransactionResultCode.txBAD_AUTH_EXTRA -> ResultCode.BadAuthExtra
                    TransactionResultCode.txINTERNAL_ERROR,
                    null -> ResultCode.InternalError
                }
            }
        }
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
    override val invoiceList: InvoiceList? = null
) : KinTransaction {

    fun isKinNonNativeAsset(): Boolean {
        return transactionEnvelope.tx!!.operations.filter { operation ->
            operation!!.body!!.discriminant == OperationType.PAYMENT
        }.map { PaymentOperation.Builder(it!!.body!!.paymentOp!!).build() }
            .map { (it.asset as? AssetTypeCreditAlphaNum4)?.code == "KIN" }.reduce { acc, b -> acc && b }
    }

    internal val transactionEnvelope: TransactionEnvelope by lazy {
        TransactionEnvelope.decode(XdrDataInputStream(ByteArrayInputStream(bytesValue)))
    }

    override val transactionHash: TransactionHash by lazy {
        TransactionHash(
            Transaction.fromEnvelopeXdr(
                transactionEnvelope,
                networkEnvironment.getNetwork()
            ).hash()
        )
    }

    override val signingSource: KinAccount.Id by lazy {
        KinAccount.Id(
            KeyPair.fromXdrPublicKey(transactionEnvelope.tx!!.sourceAccount!!.accountID!!)
                .asPublicKey().value
        )
    }

    override val fee: QuarkAmount by lazy {
        QuarkAmount(transactionEnvelope.tx!!.fee!!.uint32!!.toLong())
    }

    override val memo: KinMemo by lazy {
        transactionEnvelope.tx!!.memo?.let { memo ->
            if (memo.discriminant == MemoType.MEMO_HASH) {
                KinMemo(memo.hash!!.hash!!)
            } else {
                memo.text?.let { KinMemo(it, Charsets.UTF_8) } ?: KinMemo.NONE
            }
        } ?: KinMemo.NONE
    }

    override val paymentOperations: List<KinOperation.Payment> by lazy {
        transactionEnvelope.tx!!.operations.filter { operation ->
            operation!!.body!!.discriminant == OperationType.PAYMENT
        }.map { PaymentOperation.Builder(it!!.body!!.paymentOp!!).build() }
            .map { payOp: PaymentOperation ->
                KinOperation.Payment(
                    KinAmount(payOp.amount),
                    when {
                        payOp.sourceAccount != null -> KinAccount.Id(payOp.sourceAccount!!.publicKey)
                        else -> signingSource
                    },
                    KinAccount.Id(payOp.destination.publicKey)
                )
            }
    }

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
