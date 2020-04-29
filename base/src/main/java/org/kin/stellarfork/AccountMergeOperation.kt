package org.kin.stellarfork

import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType

/**
 * Represents [AccountMerge](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#account-merge) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class AccountMergeOperation private constructor(
    /**
     * The account that receives the remaining XLM balance of the source account.
     */
    val destination: KeyPair
) : Operation() {

    override fun toOperationBody(): OperationBody {
        return OperationBody().apply {
            destination = AccountID().apply {
                accountID = this@AccountMergeOperation.destination.xdrPublicKey
            }
            discriminant = OperationType.ACCOUNT_MERGE
        }
    }

    /**
     * Builds AccountMerge operation.
     *
     * @see AccountMergeOperation
     */
    class Builder {
        private val destination: KeyPair
        private var mSourceAccount: KeyPair? = null

        internal constructor(op: OperationBody) {
            destination = KeyPair.fromXdrPublicKey(op.destination!!.accountID!!)
        }

        /**
         * Creates a new AccountMerge builder.
         *
         * @param destination The account that receives the remaining XLM balance of the source account.
         */
        constructor(destination: KeyPair) {
            this.destination = destination
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
        fun build(): AccountMergeOperation {
            return AccountMergeOperation(destination)
                .apply {
                    mSourceAccount?.let { sourceAccount = it }
                }
        }
    }
}
