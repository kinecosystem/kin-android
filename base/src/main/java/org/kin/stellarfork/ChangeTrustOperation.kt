package org.kin.stellarfork

import org.kin.stellarfork.Asset.Companion.fromXdr
import org.kin.stellarfork.xdr.ChangeTrustOp
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType

/**
 * Represents [ChangeTrust](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#change-trust) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class ChangeTrustOperation private constructor(
    /** The asset of the trustline. For example, if a gateway extends a trustline of up to 200 USD to a user, the line is USD. */
    val asset: Asset,
    /** The limit of the trustline. For example, if a gateway extends a trustline of up to 200 USD to a user, the limit is 200. */
    val limit: String
) : Operation() {
    override fun toOperationBody(): OperationBody {
        return OperationBody().apply {
            discriminant = OperationType.CHANGE_TRUST
            changeTrustOp = ChangeTrustOp().apply {
                line = asset.toXdr()
                limit = Int64().apply { int64 = toXdrAmount(this@ChangeTrustOperation.limit) }
            }
        }
    }

    /**
     * Builds ChangeTrust operation.
     *
     * @see ChangeTrustOperation
     */
    class Builder {
        private val asset: Asset
        private val limit: String
        private var mSourceAccount: KeyPair? = null

        internal constructor(op: ChangeTrustOp) {
            asset = fromXdr(op.line!!)
            limit = fromXdrAmount(op.limit!!.int64!!.toLong())
        }

        /**
         * Creates a new ChangeTrust builder.
         *
         * @param asset The asset of the trustline. For example, if a gateway extends a trustline of up to 200 USD to a user, the line is USD.
         * @param limit The limit of the trustline. For example, if a gateway extends a trustline of up to 200 USD to a user, the limit is 200.
         * @throws ArithmeticException when limit has more than 7 decimal places.
         */
        constructor(asset: Asset, limit: String) {
            this.asset = asset
            this.limit = limit
        }

        /**
         * Set source account of this operation
         *
         * @param sourceAccount Source account
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair): Builder {
            mSourceAccount = sourceAccount
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): ChangeTrustOperation {
            return ChangeTrustOperation(asset, limit).apply {
                if (mSourceAccount != null) {
                    sourceAccount = mSourceAccount
                }
            }
        }
    }
}
