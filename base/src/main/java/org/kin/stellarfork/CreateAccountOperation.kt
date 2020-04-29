package org.kin.stellarfork

import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.CreateAccountOp
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType

/**
 * Represents [CreateAccount](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#create-account) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class CreateAccountOperation private constructor(
    /** Account that is created and funded */
    val destination: KeyPair,
    /** Amount of XLM to send to the newly created account. */
    val startingBalance: String
) : Operation() {
    override fun toOperationBody(): OperationBody {
        val thiz = this@CreateAccountOperation
        return OperationBody().apply {
            discriminant = OperationType.CREATE_ACCOUNT
            createAccountOp = CreateAccountOp().apply {
                destination = AccountID().apply {
                    accountID = thiz.destination.xdrPublicKey
                }
                startingBalance = Int64().apply {
                    int64 = toXdrAmount(thiz.startingBalance)
                }
            }
        }
    }

    /**
     * Builds CreateAccount operation.
     *
     * @see CreateAccountOperation
     */
    class Builder {
        private val destination: KeyPair
        private val startingBalance: String
        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new CreateAccount builder from a CreateAccountOp XDR.
         *
         * @param op [CreateAccountOp]
         */
        internal constructor(op: CreateAccountOp) {
            destination = KeyPair.fromXdrPublicKey(op.destination!!.accountID!!)
            startingBalance = fromXdrAmount(op.startingBalance!!.int64!!.toLong())
        }

        /**
         * Creates a new CreateAccount builder.
         *
         * @param destination     The destination keypair (uses only the public key).
         * @param startingBalance The initial balance to start with in lumens.
         * @throws ArithmeticException when startingBalance has more than 7 decimal places.
         */
        constructor(destination: KeyPair, startingBalance: String) {
            this.destination = destination
            this.startingBalance = startingBalance
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
        fun build(): CreateAccountOperation {
            val operation =
                CreateAccountOperation(destination, startingBalance)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
