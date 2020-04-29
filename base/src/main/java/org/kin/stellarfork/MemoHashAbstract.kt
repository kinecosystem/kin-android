package org.kin.stellarfork

import org.kin.stellarfork.Util.paddedByteArray
import org.kin.stellarfork.codec.Hex.Companion.decodeHex
import org.kin.stellarfork.codec.Hex.Companion.encodeHex
import java.util.Arrays

abstract class MemoHashAbstract(
    /**
     * Returns 32 bytes long array contained in this memo.
     */
    var bytes: ByteArray
) : Memo() {
    constructor(hexString: String) : this(decodeHex(hexString.toCharArray())) {}

    init {
        var bytes = bytes
        if (bytes.size < 32) {
            bytes = paddedByteArray(bytes, 32)
        } else if (bytes.size > 32) {
            throw MemoTooLongException("MEMO_HASH can contain 32 bytes at max.")
        }
        this.bytes = bytes
    }

    /**
     *
     * Returns hex representation of bytes contained in this memo.
     *
     *
     * Example:
     * `
     * MemoHash memo = new MemoHash("4142434445");
     * memo.getHexValue(); // 4142434445000000000000000000000000000000000000000000000000000000
     * memo.getTrimmedHexValue(); // 4142434445
    ` *
     */
    val hexValue: String
        get() = String(encodeHex(bytes))

    /**
     *
     * Returns hex representation of bytes contained in this memo until null byte (0x00) is found.
     *
     *
     * Example:
     * `
     * MemoHash memo = new MemoHash("4142434445");
     * memo.getHexValue(); // 4142434445000000000000000000000000000000000000000000000000000000
     * memo.getTrimmedHexValue(); // 4142434445
    ` *
     */
    val trimmedHexValue: String
        get() = hexValue.split("00").toTypedArray()[0]

    abstract override fun toXdr(): org.kin.stellarfork.xdr.Memo

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as MemoHashAbstract
        return Arrays.equals(bytes, that.bytes)
    }

    override fun hashCode(): Int {
        return 31 + bytes.contentHashCode()
    }
}
