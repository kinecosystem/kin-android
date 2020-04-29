package org.kin.sdk.base.models

import org.kin.stellarfork.codec.Hex

data class TransactionHash(val rawValue: ByteArray) {

    constructor(transactionHashString: String) : this(Hex.decodeHex(transactionHashString))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionHash

        if (!rawValue.contentEquals(other.rawValue)) return false

        return true
    }

    override fun hashCode(): Int {
        return rawValue.contentHashCode()
    }

    override fun toString(): String {
        return Hex.encodeHexString(rawValue)
    }
}
