package org.kin.stellarfork

/**
 * Represents an account in Stellar network with it's sequence number.
 * Account object is required to build a [Transaction].
 *
 * @see Transaction.Builder
 */
data class Account
/**
 * Class constructor.
 *
 * @param keypair        KeyPair associated with this Account
 * @param sequenceNumber Current sequence number of the account (can be obtained using java-stellar-sdk or horizon server)
 */
@JvmOverloads
constructor(
    override val keypair: KeyPair,
    override var sequenceNumber: Long,
    override var incrementedSequenceNumber: Long = sequenceNumber + 1
) : TransactionBuilderAccount {
    /**
     * Increments sequence number in this object by one.
     */
    override fun incrementSequenceNumber() {
        sequenceNumber = incrementedSequenceNumber
    }
}
