package kin.sdk

import java.math.BigDecimal

/**
 * Represents payment issued on the blockchain.
 */
interface PaymentInfo {
    /**
     * Transaction creation time.
     */
    fun createdAt(): String

    /**
     * Destination account public id.
     */
    fun destinationPublicKey(): String

    /**
     * Source account public id.
     */
    fun sourcePublicKey(): String

    /**
     * Payment amount in kin.
     */
    fun amount(): BigDecimal

    /**
     * Transaction id (hash).
     */
    fun hash(): TransactionId

    /**
     * An optional string, up-to 28 characters, included on the transaction record.
     */
    fun memo(): String

    /**
     * Amount of fee(in stroops) for this payment.
     */
    fun fee(): Long
}
