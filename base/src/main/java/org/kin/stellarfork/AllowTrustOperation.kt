package org.kin.stellarfork

import org.kin.stellarfork.Util.paddedByteArray
import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.AllowTrustOp
import org.kin.stellarfork.xdr.AllowTrustOp.AllowTrustOpAsset
import org.kin.stellarfork.xdr.AssetType
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType

/**
 * Represents [AllowTrust](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#allow-trust) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class AllowTrustOperation private constructor(
    /** The account of the recipient of the trustline. */
    val trustor: KeyPair,
    /** The asset of the trustline the source account is authorizing. For example,
     * if a gateway wants to allow another account to hold its USD credit, the type is USD. */
    val assetCode: String,
    /** Flag indicating whether the trustline is authorized. */
    val authorize: Boolean
) : Operation() {

    override fun toOperationBody(): OperationBody {
        return OperationBody().apply {
            discriminant = OperationType.ALLOW_TRUST
            allowTrustOp = AllowTrustOp().apply {
                trustor = AccountID().apply {
                    accountID = this@AllowTrustOperation.trustor.xdrPublicKey
                }
                asset = AllowTrustOpAsset().apply {
                    if (assetCode.length <= 4) {
                        discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM4
                        assetCode4 = paddedByteArray(assetCode, 4)
                    } else {
                        discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM12
                        assetCode12 = paddedByteArray(assetCode, 12)
                    }
                }
                authorize = this@AllowTrustOperation.authorize
            }
        }
    }

    /**
     * Builds AllowTrust operation.
     *
     * @see AllowTrustOperation
     */
    class Builder {
        private val trustor: KeyPair
        private val assetCode: String
        private val authorize: Boolean
        private var mSourceAccount: KeyPair? = null

        internal constructor(op: AllowTrustOp) {
            trustor = KeyPair.fromXdrPublicKey(op.trustor!!.accountID!!)
            assetCode = when (op.asset!!.discriminant) {
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM4 -> String(op.asset!!.assetCode4!!).trim { it <= ' ' }
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM12 -> String(op.asset!!.assetCode12!!).trim { it <= ' ' }
                else -> throw RuntimeException("Unknown asset code")
            }
            authorize = op.authorize!!
        }

        /**
         * Creates a new AllowTrust builder.
         *
         * @param trustor   The account of the recipient of the trustline.
         * @param assetCode The asset of the trustline the source account is authorizing. For example, if a gateway wants to allow another account to hold its USD credit, the type is USD.
         * @param authorize Flag indicating whether the trustline is authorized.
         */
        constructor(
            trustor: KeyPair,
            assetCode: String,
            authorize: Boolean
        ) {
            this.trustor = trustor
            this.assetCode = assetCode
            this.authorize = authorize
        }

        /**
         * Set source account of this operation
         *
         * @param sourceAccount Source account
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair?): Builder {
            mSourceAccount = sourceAccount
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): AllowTrustOperation {
            val operation = AllowTrustOperation(trustor, assetCode, authorize)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
