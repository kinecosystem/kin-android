package org.kin.stellarfork

import org.kin.stellarfork.KeyPair.Companion.fromXdrPublicKey
import org.kin.stellarfork.Operation.Companion.fromXdr
import org.kin.stellarfork.TimeBounds.Companion.fromXdr
import org.kin.stellarfork.Util.checkArgument
import org.kin.stellarfork.Util.createXdrDataInputStream
import org.kin.stellarfork.Util.hash
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.DecoratedSignature
import org.kin.stellarfork.xdr.EnvelopeType
import org.kin.stellarfork.xdr.SequenceNumber
import org.kin.stellarfork.xdr.Signature
import org.kin.stellarfork.xdr.SignatureHint
import org.kin.stellarfork.xdr.Transaction.TransactionExt
import org.kin.stellarfork.xdr.TransactionEnvelope
import org.kin.stellarfork.xdr.Uint32
import org.kin.stellarfork.xdr.Uint64
import org.kin.stellarfork.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.ArrayList

/**
 * Represents [Transaction](https://www.stellar.org/developers/learn/concepts/transactions.html) in Stellar network.
 */
data class Transaction internal constructor(
    val sourceAccount: KeyPair,
    /**
     * Returns fee paid for transaction in stroops (1 quark = 0.0000001 KIN).
     */
    var fee: Int,
    val sequenceNumber: Long,
    val operations: Array<Operation>,
    var memo: Memo = Memo.none(),
    /**
     * @return TimeBounds, or null (representing no time restrictions)
     */
    var timeBounds: TimeBounds?,
    /**
     * Sets the current Network context for this transaction
     * @param mCurrentNetwork - Network object
     */
    val currentNetwork: Network
) {
    init {
        checkArgument(operations.isNotEmpty(), "At least one operation required")
        this.fee = operations.size * fee
    }

    private val mSignatures: ArrayList<DecoratedSignature> = ArrayList()

    /**
     * Adds a new signature ed25519PublicKey to this transaction.
     *
     * @param signer [KeyPair] object representing a signer
     */
    fun sign(signer: KeyPair) = mSignatures.add(signer.signDecorated(hash()))

    /**
     * Adds a new sha256Hash signature to this transaction by revealing preimage.
     *
     * @param preimage the sha256 hash of preimage should be equal to signer hash
     */
    fun sign(preimage: ByteArray) {
        val decoratedSignature = DecoratedSignature()
            .apply {
                val hash = hash(preimage)
                hint = SignatureHint().apply {
                    signatureHint = hash.copyOfRange(hash.size - 4, hash.size)
                }
                signature = Signature().apply { signature = preimage }
            }
        mSignatures.add(decoratedSignature)
    }

    /**
     * Returns transaction hash.
     */
    fun hash(): ByteArray = hash(signatureBase())

    /**
     * Returns signature base.
     */
    fun signatureBase(): ByteArray? {
        return try {
            val outputStream = ByteArrayOutputStream()
            // Hashed NetworkID
            outputStream.write(currentNetwork.networkId)
            // Envelope Type - 4 bytes
            outputStream.write(
                ByteBuffer.allocate(4).putInt(EnvelopeType.ENVELOPE_TYPE_TX.value).array()
            )
            // Transaction XDR bytes
            val txOutputStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(txOutputStream)
            org.kin.stellarfork.xdr.Transaction.encode(xdrOutputStream, toXdr())
            outputStream.write(txOutputStream.toByteArray())
            outputStream.toByteArray()
        } catch (exception: IOException) {
            null
        }
    }
    var signatures: List<DecoratedSignature>
        get() = mSignatures
        set(value) {
            mSignatures.clear()
            mSignatures.addAll(value)
        }

    /**
     * Generates Transaction XDR object.
     */
    fun toXdr(): org.kin.stellarfork.xdr.Transaction { // fee
        val fee = Uint32()
        fee.uint32 = this.fee
        // sequenceNumber
        val sequenceNumberUint = Uint64()
        sequenceNumberUint.uint64 = sequenceNumber
        val sequenceNumber = SequenceNumber()
        sequenceNumber.sequenceNumber = sequenceNumberUint
        // sourceAccount
        val sourceAccount = AccountID()
        sourceAccount.accountID = this.sourceAccount.xdrPublicKey
        // operations
        val operations = arrayOfNulls<org.kin.stellarfork.xdr.Operation>(operations.size)
        for (i in operations.indices) {
            operations[i] = this.operations[i].toXdr()
        }
        // ext
        val ext = TransactionExt()
        ext.discriminant = 0
        val transaction = org.kin.stellarfork.xdr.Transaction()
        transaction.fee = fee
        transaction.seqNum = sequenceNumber
        transaction.sourceAccount = sourceAccount
        transaction.operations = operations
        transaction.memo = memo.toXdr()
        transaction.timeBounds = timeBounds?.toXdr()
        transaction.ext = ext
        return transaction
    }

    /**
     * Generates TransactionEnvelope XDR object. Transaction need to have at least one signature.
     */
    fun toEnvelopeXdr(): TransactionEnvelope {
        if (mSignatures.size == 0) {
            throw NotEnoughSignaturesException("Transaction must be signed by at least one signer. Use transaction.sign().")
        }
        val xdr = TransactionEnvelope()
        val transaction = toXdr()
        xdr.tx = transaction
        var signatures: Array<DecoratedSignature?>? = arrayOfNulls(mSignatures.size)
        signatures = mSignatures.toArray(signatures)
        xdr.signatures = signatures
        return xdr
    }

    /**
     * Returns base64-encoded TransactionEnvelope XDR object. Transaction need to have at least one signature.
     */
    fun toEnvelopeXdrBase64(): String {
        return try {
            val envelope = toEnvelopeXdr()
            val outputStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(outputStream)
            TransactionEnvelope.encode(xdrOutputStream, envelope)
            val base64Codec = Base64()
            base64Codec.encodeAsString(outputStream.toByteArray())
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Transaction) return false

        return hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = sourceAccount.hashCode()
        result = 31 * result + fee
        result = 31 * result + sequenceNumber.hashCode()
        result = 31 * result + operations.contentHashCode()
        result = 31 * result + memo.hashCode()
        result = 31 * result + (timeBounds?.hashCode() ?: 0)
        result = 31 * result + currentNetwork.hashCode()
        mSignatures.forEach { e ->
            result = 31 * result + (e.signature?.signature?.contentHashCode() ?: 0)
        }
        return result
    }

    /**
     * Builds a new Transaction object.
     */
    class Builder(
        private val sourceAccount: TransactionBuilderAccount,
        private val currentNetwork: Network
    ) {
        private var fee = 0
        private var mMemo: Memo? = null
        private var mTimeBounds: TimeBounds? = null
        var mOperations: ArrayList<Operation> = ArrayList()

        val operationsCount: Int
            get() = mOperations.size

        /**
         * Adds a new [operation](https://www.stellar.org/developers/learn/concepts/list-of-operations.html) to this transaction.
         *
         * @param operation
         * @return Builder object so you can chain methods.
         * @see Operation
         */
        fun addOperation(operation: Operation): Builder {
            mOperations.add(operation)
            return this
        }

        /**
         * @param fee this transaction fee
         * @return Builder object so you can chain methods.
         */
        fun addFee(fee: Int): Builder {
            this.fee = fee
            return this
        }

        /**
         * Adds a [memo](https://www.stellar.org/developers/learn/concepts/transactions.html) to this transaction.
         *
         * @param memo
         * @return Builder object so you can chain methods.
         * @see Memo
         */
        fun addMemo(memo: Memo): Builder {
            if (mMemo != null) {
                throw RuntimeException("Memo has been already added.")
            }
            mMemo = memo
            return this
        }

        /**
         * Adds a [time-bounds](https://www.stellar.org/developers/learn/concepts/transactions.html) to this transaction.
         *
         * @param timeBounds
         * @return Builder object so you can chain methods.
         * @see TimeBounds
         */
        fun addTimeBounds(timeBounds: TimeBounds): Builder {
            if (mTimeBounds != null) {
                throw RuntimeException("TimeBounds has been already added.")
            }
            mTimeBounds = timeBounds
            return this
        }

        /**
         * Builds a transaction. It will increment sequence number of the source account.
         */
        fun build(): Transaction {
            if (mMemo == null) {
                mMemo = Memo.none()
            }
            var operations = arrayOfNulls<Operation>(mOperations.size)
            operations = mOperations.toArray(operations)
            val transaction = Transaction(
                sourceAccount.keypair,
                fee,
                sourceAccount.incrementedSequenceNumber,
                operations.requireNoNulls(),
                mMemo!!,
                mTimeBounds,
                currentNetwork
            )
            // Increment sequence number when there were no exceptions when creating a transaction
            sourceAccount.incrementSequenceNumber()
            return transaction
        }

    }

    companion object {

        /**
         * Creates a `Transaction` instance from previously build `TransactionEnvelope`
         *
         * @param envelope Base-64 encoded `TransactionEnvelope`
         * @return
         * @throws IOException
         */
        @Throws(IOException::class)
        @JvmStatic
        fun fromEnvelopeXdr(
            envelope: String?,
            currentNetwork: Network
        ): Transaction {
            val transactionEnvelope = TransactionEnvelope.decode(
                createXdrDataInputStream(envelope!!)
            )
            return fromEnvelopeXdr(transactionEnvelope, currentNetwork)
        }

        /**
         * Creates a `Transaction` instance from previously build `TransactionEnvelope`
         *
         * @param envelope Base-64 encoded `TransactionEnvelope`
         * @return
         */
        @JvmStatic
        fun fromEnvelopeXdr(
            envelope: TransactionEnvelope,
            currentNetwork: Network
        ): Transaction {
            val tx = envelope.tx
            val mFee = tx!!.fee!!.uint32
            val mSourceAccount = fromXdrPublicKey(tx.sourceAccount!!.accountID!!)
            val mSequenceNumber = tx.seqNum!!.sequenceNumber!!.uint64
            val mMemo = Memo.fromXdr(tx.memo!!)
            val mTimeBounds = fromXdr(tx.timeBounds)
            val mOperations = arrayOfNulls<Operation>(tx.operations.size)
            tx.operations.indices.forEach { i ->
                mOperations[i] = fromXdr(tx.operations[i]!!)
            }
            val transaction = Transaction(
                mSourceAccount,
                mFee!!,
                mSequenceNumber!!,
                mOperations.requireNoNulls(),
                mMemo,
                mTimeBounds,
                currentNetwork
            )
            envelope.signatures.forEach { signature ->
                transaction.mSignatures.add(signature!!)
            }
            return transaction
        }
    }
}
