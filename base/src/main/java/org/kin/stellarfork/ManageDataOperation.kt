package org.kin.stellarfork

import org.kin.stellarfork.xdr.DataValue
import org.kin.stellarfork.xdr.ManageDataOp
import org.kin.stellarfork.xdr.Operation.OperationBody
import org.kin.stellarfork.xdr.OperationType
import org.kin.stellarfork.xdr.String64

/**
 * Represents [ManageData](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#manage-data) operation.
 *
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class ManageDataOperation private constructor(
    /**
     * The name of the data value
     */
    val name: String,
    /**
     * Data value
     */
    val value: ByteArray?
) : Operation() {
    override fun toOperationBody(): OperationBody {
        return OperationBody().apply {
            discriminant = OperationType.MANAGE_DATA
            manageDataOp = ManageDataOp().apply {
                dataName = String64().apply {
                    string64 = this@ManageDataOperation.name
                }
                value?.let {
                    dataValue = DataValue().apply { dataValue = value }
                }
            }
        }
    }

    class Builder {
        private val name: String
        private val value: ByteArray?
        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new ManageOffer builder from a ManageDataOp XDR.
         *
         * @param op [ManageDataOp]
         */
        internal constructor(op: ManageDataOp) {
            name = op.dataName!!.string64!!
            value = if (op.dataValue != null) {
                op.dataValue!!.dataValue
            } else {
                null
            }
        }

        /**
         * Creates a new ManageData builder. If you want to clearStorage data entry pass null as a `value` param.
         *
         * @param name  The name of data entry
         * @param value The value of data entry. `null`null will clearStorage data entry.
         */
        constructor(name: String, value: ByteArray?) {
            this.name = name
            this.value = value
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
        fun build(): ManageDataOperation {
            val operation = ManageDataOperation(name, value)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
