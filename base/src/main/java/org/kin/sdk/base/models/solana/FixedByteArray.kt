package org.kin.sdk.base.models.solana

import org.kin.sdk.base.tools.toHexString

abstract class FixedByteArray(val byteArray: ByteArray) {
    abstract val size: Int

    init {
        check()
    }

    fun check() {
        require(byteArray.size == size)
    }

    operator fun get(i: Int) = byteArray[i]
    operator fun set(i: Int, b: Byte) {
        byteArray[i] = b
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FixedByteArray
        if (!byteArray.contentEquals(other.byteArray)) return false
        return true
    }

    override fun hashCode(): Int {
        return byteArray.contentHashCode()
    }
}

@JvmName("contentHashCodeNullable")
fun FixedByteArray?.contentHashCode(): Int = java.util.Arrays.hashCode(this?.byteArray)

@JvmName("contentEqualsNullable")
infix fun FixedByteArray?.contentEquals(other: FixedByteArray?): Boolean =
    java.util.Arrays.equals(this?.byteArray, other?.byteArray)

class FixedByteArray32(byteArray: ByteArray = ByteArray(32)) : FixedByteArray(byteArray) {
    override val size: Int
        get() = 32

    override fun toString(): String {
        return "FixedByteArray32(bytes=${byteArray.toHexString()})"
    }
}

class FixedByteArray64(byteArray: ByteArray = ByteArray(64)) : FixedByteArray(byteArray) {
    override val size: Int
        get() = 64

    override fun toString(): String {
        return "FixedByteArray64(bytes=${byteArray.toHexString()})"
    }
}
