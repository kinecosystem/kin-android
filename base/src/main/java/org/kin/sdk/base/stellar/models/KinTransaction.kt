package org.kin.sdk.base.stellar.models

import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.getNetwork
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.PaymentOperation
import org.kin.stellarfork.Transaction
import org.kin.stellarfork.xdr.MemoType
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.TransactionEnvelope
import org.kin.stellarfork.xdr.TransactionResult
import org.kin.stellarfork.xdr.TransactionResultCode
import org.kin.stellarfork.xdr.XdrDataInputStream
import java.io.ByteArrayInputStream

data class KinTransaction @JvmOverloads constructor(
    val envelopeXdrBytes: ByteArray,
    val recordType: RecordType = RecordType.InFlight(System.currentTimeMillis()),
    val networkEnvironment: NetworkEnvironment,
    val invoiceList: InvoiceList? = null
) {
    sealed class RecordType(val value: Int) {
        abstract val timestamp: Long

        data class InFlight(
            override val timestamp: Long
        ) : RecordType(0) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as InFlight

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
            val resultCode: ResultCode = parseResultCode(resultXdrBytes)

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Acknowledged

                return this.timestamp == other.timestamp &&
                        this.resultXdrBytes.contentEquals(other.resultXdrBytes)
            }

            override fun hashCode(): Int {
                var result = timestamp.hashCode()
                result = 31 * result + resultXdrBytes.contentHashCode()
                result = 31 * result + resultCode.hashCode()
                return result
            }
        }

        data class Historical(
            override val timestamp: Long,
            val resultXdrBytes: ByteArray,
            val pagingToken: PagingToken
        ) : RecordType(2) {
            val resultCode: ResultCode = parseResultCode(resultXdrBytes)

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Historical

                return this.timestamp == other.timestamp &&
                        this.resultXdrBytes.contentEquals(other.resultXdrBytes) &&
                        this.pagingToken.equals(other.pagingToken)
            }

            override fun hashCode(): Int {
                var result = timestamp.hashCode()
                result = 31 * result + resultXdrBytes.contentHashCode()
                result = 31 * result + pagingToken.hashCode()
                result = 31 * result + resultCode.hashCode()
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

    private val transactionEnvelope: TransactionEnvelope by lazy {
        TransactionEnvelope.decode(XdrDataInputStream(ByteArrayInputStream(envelopeXdrBytes)))
    }

    val transactionHash: TransactionHash by lazy {
        TransactionHash(
            Transaction.fromEnvelopeXdr(
                transactionEnvelope,
                networkEnvironment.getNetwork()
            ).hash()
        )
    }

    val signingSource: KinAccount.Id by lazy {
        KinAccount.Id(
            KeyPair.fromXdrPublicKey(transactionEnvelope.tx!!.sourceAccount!!.accountID!!)
                .asPublicKey().value
        )
    }

    val signingSequenceNumber: Long by lazy { transactionEnvelope.tx!!.seqNum!!.sequenceNumber!!.uint64!! }

    val fee: QuarkAmount by lazy {
        QuarkAmount(transactionEnvelope.tx!!.fee!!.uint32!!.toLong())
    }

    val memo: KinMemo by lazy {
        transactionEnvelope.tx!!.memo?.let { memo ->
            if (memo.discriminant == MemoType.MEMO_HASH) {
                KinMemo(memo.hash!!.hash!!)
            } else {
                memo.text?.let { KinMemo(it, Charsets.UTF_8) } ?: KinMemo.NONE
            }
        } ?: KinMemo.NONE
    }

    val paymentOperations: List<KinOperation.Payment> by lazy {
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

        if (!envelopeXdrBytes.contentEquals(other.envelopeXdrBytes)) return false
        if (recordType != other.recordType) return false
        if (networkEnvironment != other.networkEnvironment) return false
        if (invoiceList != other.invoiceList) return false

        return true
    }

    override fun hashCode(): Int {
        return transactionHash.hashCode()
    }
}
