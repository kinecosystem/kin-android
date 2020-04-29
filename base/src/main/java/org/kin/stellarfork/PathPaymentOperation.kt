package org.kin.stellarfork

import org.kin.stellarfork.Asset.Companion.fromXdr
import org.kin.stellarfork.Util.checkArgument
import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.PathPaymentOp

/**
 * Represents [PathPayment](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#path-payment) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class PathPaymentOperation private constructor(
    /** The asset deducted from the sender's account. */
    val sendAsset: Asset,
    /** The maximum amount of send asset to deduct (excluding fees) */
    val sendMax: String,
    /** Account that receives the payment. */
    val destination: KeyPair,
    /** The asset the destination account receives. */
    val destAsset: Asset,
    /** The amount of destination asset the destination account receives. */
    val destAmount: String,
    /** The assets (other than send asset and destination asset) involved in the offers the path takes. For example, if you can only find a path from USD to EUR through XLM and BTC, the path would be USD - XLM - BTC - EUR and the path would contain XLM and BTC. */
    val path: Array<Asset?> = emptyArray()
) : Operation() {
    init {
        checkArgument(path.size <= 5, "The maximum number of assets in the path is 5")
    }

    override fun toOperationBody(): OperationBody {
        val thiz = this@PathPaymentOperation

        val p = arrayOfNulls<org.kin.stellarfork.xdr.Asset>(path.size)
        for (i in thiz.path.indices) {
            p[i] = thiz.path[i]?.toXdr()
        }

        return OperationBody().apply {
            discriminant = OperationType.PATH_PAYMENT
            pathPaymentOp = PathPaymentOp().apply {
                sendAsset = thiz.sendAsset.toXdr()
                sendMax = Int64().apply { int64 = toXdrAmount(thiz.sendMax) }
                destination = AccountID().apply { accountID = thiz.destination.xdrPublicKey }
                destAsset = thiz.destAsset.toXdr()
                destAmount = Int64().apply { int64 = toXdrAmount(thiz.destAmount) }
                path = p
            }
        }
    }

    /**
     * Builds PathPayment operation.
     *
     * @see PathPaymentOperation
     */
    class Builder {
        private val sendAsset: Asset
        private val sendMax: String
        private val destination: KeyPair
        private val destAsset: Asset
        private val destAmount: String
        private var path: Array<Asset?>? = null

        private var mSourceAccount: KeyPair? = null

        internal constructor(op: PathPaymentOp) {
            sendAsset = fromXdr(op.sendAsset!!)
            sendMax = fromXdrAmount(op.sendMax!!.int64!!.toLong())
            destination = KeyPair.fromXdrPublicKey(op.destination!!.accountID!!)
            destAsset = fromXdr(op.destAsset!!)
            destAmount = fromXdrAmount(op.destAmount!!.int64!!.toLong())
            path = arrayOfNulls(op.path.size)
            for (i in op.path.indices) {
                path!![i] = fromXdr(op.path[i]!!)
            }
        }

        /**
         * Creates a new PathPaymentOperation builder.
         *
         * @param sendAsset   The asset deducted from the sender's account.
         * @param sendMax     The asset deducted from the sender's account.
         * @param destination Payment destination
         * @param destAsset   The asset the destination account receives.
         * @param destAmount  The amount of destination asset the destination account receives.
         * @throws ArithmeticException when sendMax or destAmount has more than 7 decimal places.
         */
        constructor(
            sendAsset: Asset,
            sendMax: String,
            destination: KeyPair,
            destAsset: Asset,
            destAmount: String
        ) {
            this.sendAsset = sendAsset
            this.sendMax = sendMax
            this.destination = destination
            this.destAsset = destAsset
            this.destAmount = destAmount
        }

        /**
         * Sets path for this operation
         *
         * @param path The assets (other than send asset and destination asset) involved in the offers the path takes. For example, if you can only find a path from USD to EUR through XLM and BTC, the path would be USD - XLM - BTC - EUR and the path field would contain XLM and BTC.
         * @return Builder object so you can chain methods.
         */
        fun setPath(path: Array<Asset?>): Builder {
            checkArgument(path.size <= 5, "The maximum number of assets in the path is 5")
            this.path = path
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
        fun build(): PathPaymentOperation {
            val operation = path?.let {
                PathPaymentOperation(
                    sendAsset, sendMax, destination,
                    destAsset, destAmount, it
                )
            } ?: PathPaymentOperation(
                sendAsset, sendMax, destination,
                destAsset, destAmount
            )
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }

    }
}
