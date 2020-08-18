package org.kin.sdk.base.models

import java.nio.charset.Charset

data class KinMemo @JvmOverloads constructor(
    val rawValue: ByteArray,
    val type: Type = Type.NoEncoding
) {
    sealed class Type(val value: Int) {
        object NoEncoding : Type(0)
        data class CharsetEncoded(val charset: Charset) : Type(1)
    }

    companion object {
        val NONE = KinMemo(ByteArray(0), Type.NoEncoding)
    }

    /**
     * Text that will be encoded into charset representation, defaults to UTF8 encoding
     */
    @JvmOverloads
    constructor(textValue: String, charset: Charset = Charsets.UTF_8) : this(
        textValue.toUTF8Bytes(),
        Type.CharsetEncoded(charset)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KinMemo

        if (!rawValue.contentEquals(other.rawValue)) return false

        return true
    }

    override fun hashCode(): Int {
        return rawValue.contentHashCode()
    }

    override fun toString(): String {
        return when (type) {
            Type.NoEncoding -> rawValue.contentToString()
            is Type.CharsetEncoded -> String(rawValue, type.charset)
        }
    }
}

fun KinMemo.getAgoraMemo(): KinBinaryMemo? {
    return try { KinBinaryMemo.decode(rawValue) } catch (e: Exception) { null }
}
