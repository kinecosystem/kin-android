package org.kin.stellarfork

import org.kin.stellarfork.Asset.Companion.fromXdr
import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.PaymentOp

/**
 * Represents [Payment](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#payment) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
data class PaymentOperation private constructor(
    /** Account that receives the payment. */
    val destination: KeyPair,
    /** Asset to send to the destination account. */
    val asset: Asset,
    /** Amount of the asset to send. */
    val amount: String
) : Operation() {
    override fun toOperationBody(): OperationBody {
        val thiz = this@PaymentOperation
        return OperationBody().apply {
            discriminant = OperationType.PAYMENT
            paymentOp = PaymentOp().apply {
                destination = AccountID().apply { accountID = thiz.destination.xdrPublicKey }
                asset = thiz.asset.toXdr()
                amount = Int64().apply { int64 = toXdrAmount(thiz.amount) }
            }
        }
    }

    /**
     * Builds Payment operation.
     *
     * @see PathPaymentOperation
     */
    class Builder {
        private val destination: KeyPair
        private val asset: Asset
        private val amount: String
        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new PaymentOperation builder from a PaymentOp XDR.
         *
         * @param op [PaymentOp]
         */
        constructor(op: PaymentOp) {
            destination = KeyPair.fromXdrPublicKey(op.destination!!.accountID!!)
            asset = fromXdr(op.asset!!)
            amount = fromXdrAmount(op.amount!!.int64!!.toLong())
        }

        /**
         * Creates a new PaymentOperation builder.
         *
         * @param destination The destination keypair (uses only the public key).
         * @param asset       The asset to send.
         * @param amount      The amount to send in lumens.
         * @throws ArithmeticException when amount has more than 7 decimal places.
         */
        constructor(
            destination: KeyPair,
            asset: Asset,
            amount: String
        ) {
            this.destination = destination
            this.asset = asset
            this.amount = amount
        }

        /**
         * Sets the source account for this operation.
         *
         * @param account The operation's source account.
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(account: KeyPair?): Builder {
            mSourceAccount = account
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): PaymentOperation {
            val operation = PaymentOperation(destination, asset, amount)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
