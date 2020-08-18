package org.kin.sdk.base.tools.sha224

/**
 * Utility methods for converting byte arrays into ints and longs, and back again.
 */
object Pack {
    fun bigEndianToInt(bs: ByteArray, off: Int): Int {
        var offLocal = off
        var n: Int = bs[offLocal].toInt() shl 24
        n = n or (bs[++offLocal].toInt() and 0xff shl 16)
        n = n or (bs[++offLocal].toInt() and 0xff shl 8)
        n = n or (bs[++offLocal].toInt() and 0xff)
        return n
    }

    fun intToBigEndian(n: Int, bs: ByteArray, off: Int) {
        var offLocal = off
        bs[offLocal] = (n ushr 24).toByte()
        bs[++offLocal] = (n ushr 16).toByte()
        bs[++offLocal] = (n ushr 8).toByte()
        bs[++offLocal] = n.toByte()
    }

    fun bigEndianToLong(bs: ByteArray, off: Int): Long {
        val hi = bigEndianToInt(bs, off)
        val lo = bigEndianToInt(bs, off + 4)
        return (hi and 0xffffffffL.toInt()).toLong() shl 32 or (lo and 0xffffffffL.toInt()).toLong()
    }

    fun longToBigEndian(n: Long, bs: ByteArray, off: Int) {
        intToBigEndian((n ushr 32).toInt(), bs, off)
        intToBigEndian((n and 0xffffffffL).toInt(), bs, off + 4)
    }
}
