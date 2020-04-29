package org.kin.stellarfork

import org.kin.stellarfork.Asset.Companion.fromXdr
import org.kin.stellarfork.Price.Companion.fromString
import org.kin.stellarfork.xdr.CreatePassiveOfferOp
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType
import java.math.BigDecimal

/**
 * Represents [CreatePassiveOffer](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#create-passive-offer) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class CreatePassiveOfferOperation private constructor(
    /** The asset being sold in this operation */
    val selling: Asset,
    /** The asset being bought in this operation */
    val buying: Asset,
    /** Amount of selling being sold. */
    val amount: String,
    /** Price of 1 unit of selling in terms of buying. */
    val price: String
) : Operation() {
    override fun toOperationBody(): OperationBody {
        val thiz = this@CreatePassiveOfferOperation
        return OperationBody().apply {
            discriminant = OperationType.CREATE_PASSIVE_OFFER
            createPassiveOfferOp = CreatePassiveOfferOp().apply {
                selling = thiz.selling.toXdr()
                buying = thiz.buying.toXdr()
                amount = Int64().apply {
                    int64 = toXdrAmount(thiz.amount)
                }
                price = fromString(thiz.price).toXdr()
            }
        }
    }

    /**
     * Builds CreatePassiveOffer operation.
     *
     * @see CreatePassiveOfferOperation
     */
    class Builder {
        private val selling: Asset
        private val buying: Asset
        private val amount: String
        private val price: String
        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new CreatePassiveOffer builder from a CreatePassiveOfferOp XDR.
         *
         * @param op
         */
        internal constructor(op: CreatePassiveOfferOp) {
            selling = fromXdr(op.selling!!)
            buying = fromXdr(op.buying!!)
            amount = fromXdrAmount(op.amount!!.int64!!.toLong())
            val n = op.price!!.n!!.int32!!.toInt()
            val d = op.price!!.d!!.int32!!.toInt()
            price = BigDecimal(n).divide(BigDecimal(d)).toString()
        }

        /**
         * Creates a new CreatePassiveOffer builder.
         *
         * @param selling The asset being sold in this operation
         * @param buying  The asset being bought in this operation
         * @param amount  Amount of selling being sold.
         * @param price   Price of 1 unit of selling in terms of buying.
         * @throws ArithmeticException when amount has more than 7 decimal places.
         */
        constructor(
            selling: Asset,
            buying: Asset,
            amount: String,
            price: String
        ) {
            this.selling = selling
            this.buying = buying
            this.amount = amount
            this.price = price
        }

        /**
         * Sets the source account for this operation.
         *
         * @param sourceAccount The operation's source account.
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair): Builder {
            mSourceAccount = sourceAccount
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): CreatePassiveOfferOperation {
            val operation =
                CreatePassiveOfferOperation(selling, buying, amount, price)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
