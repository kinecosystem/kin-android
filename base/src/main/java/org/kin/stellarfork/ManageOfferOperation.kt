package org.kin.stellarfork

import org.kin.stellarfork.Asset.Companion.fromXdr
import org.kin.stellarfork.Price.Companion.fromString
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.ManageOfferOp
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.Uint64
import java.math.BigDecimal

/**
 * Represents [ManageOffer](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#manage-offer) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class ManageOfferOperation private constructor(
    /** The asset being sold in this operation */
    val selling: Asset,
    /** The asset being bought in this operation */
    val buying: Asset,
    /** Amount of selling being sold. */
    val amount: String,
    /** Price of 1 unit of selling in terms of buying. */
    val price: String,
    /** The ID of the offer.*/
    val offerId: Long?
) : Operation() {
    override fun toOperationBody(): OperationBody {
        val thiz = this@ManageOfferOperation
        return OperationBody().apply {
            discriminant = OperationType.MANAGE_OFFER
            manageOfferOp = ManageOfferOp().apply {
                selling = thiz.selling.toXdr()
                buying = thiz.buying.toXdr()
                amount = Int64().apply { int64 = toXdrAmount(thiz.amount) }
                price = fromString(thiz.price).toXdr()
                offerID = Uint64().apply {
                    uint64 = thiz.offerId?.let { java.lang.Long.valueOf(it) }
                }
            }
        }
    }

    /**
     * Builds ManageOffer operation. If you want to update existing offer use
     * [ManageOfferOperation.Builder.setOfferId].
     *
     * @see ManageOfferOperation
     */
    class Builder {
        private val selling: Asset
        private val buying: Asset
        private val amount: String
        private val price: String
        private var offerId: Long = 0
        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new CreateAccount builder from a CreateAccountOp XDR.
         *
         * @param op [CreateAccountOp]
         */
        internal constructor(op: ManageOfferOp) {
            selling = fromXdr(op.selling!!)
            buying = fromXdr(op.buying!!)
            amount = fromXdrAmount(op.amount!!.int64!!.toLong())
            val n = op.price!!.n!!.int32!!.toInt()
            val d = op.price!!.d!!.int32!!.toInt()
            price = BigDecimal(n).divide(BigDecimal(d)).toString()
            offerId = op.offerID!!.uint64!!.toLong()
        }

        /**
         * Creates a new ManageOffer builder. If you want to update existing offer use
         * [ManageOfferOperation.Builder.setOfferId].
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
         * Sets offer ID. `0` creates a new offer. Set to existing offer ID to change it.
         *
         * @param offerId
         */
        fun setOfferId(offerId: Long): Builder {
            this.offerId = offerId
            return this
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
        fun build(): ManageOfferOperation {
            return ManageOfferOperation(selling, buying, amount, price, offerId).apply {
                if (mSourceAccount != null) {
                    sourceAccount = mSourceAccount
                }
            }
        }
    }
}
