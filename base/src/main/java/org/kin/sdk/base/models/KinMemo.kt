package org.kin.sdk.base.models

data class KinMemo(val rawValue: ByteArray) {
    companion object {
        val NONE = KinMemo(ByteArray(0))
    }

    /**
     * Text that will be encoded into a UTF8Byte representation
     */
    constructor(textValue: String) : this(textValue.toUTF8Bytes())

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
}
