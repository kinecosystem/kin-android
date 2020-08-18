package org.kin.sdk.base.models

import org.kin.sdk.base.tools.sha224
import org.kin.stellarfork.codec.Base64

/**
 * The SHA-224 hash of an Invoice or InvoiceList.
 * @param encodedValue - UTF-8 String representation of the 29 bytes representing the first 230 bits of a SHA-256
 */
data class SHA224Hash(val encodedValue: String) {
    companion object {
        fun of(bytes: ByteArray): SHA224Hash = SHA224Hash(bytes.sha224())
        fun just(bytes: ByteArray): SHA224Hash = SHA224Hash(bytes)
    }

    private constructor(bytes: ByteArray) : this(Base64.encodeBase64String(bytes)!!)

    fun decode(): ByteArray = Base64.decodeBase64(encodedValue)!!
}
