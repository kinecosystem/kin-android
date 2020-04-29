package kin.sdk

/**
 * Identifier of the transaction, useful for finding information about the transaction.
 */
interface TransactionId {
    /**
     * @return the transaction id
     */
    fun id(): String
}
