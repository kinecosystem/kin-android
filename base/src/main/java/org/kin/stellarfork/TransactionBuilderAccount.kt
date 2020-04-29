package org.kin.stellarfork

/**
 * Specifies interface for Account object used in [Transaction.Builder]
 */
interface TransactionBuilderAccount {
    /**
     * Returns keypair associated with this Account
     */
    val keypair: KeyPair

    /**
     * Returns current sequence number ot this Account.
     */
    var sequenceNumber: Long

    /**
     * Returns sequence number incremented by one, but does not increment internal counter.
     */
    var incrementedSequenceNumber: Long

    /**
     * Increments sequence number in this object by one.
     */
    fun incrementSequenceNumber()
}
