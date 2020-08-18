package org.kin.sdk.base.tools

import org.kin.sdk.base.tools.sha224.SHA224Digest
import java.nio.ByteBuffer
import java.util.BitSet
import java.util.UUID

fun Int.intToByteArray(): ByteArray =
    byteArrayOf(
        this.toByte(),
        (this ushr 8).toByte(),
        (this ushr 16).toByte(),
        (this ushr 24).toByte()
    )

fun ByteArray.byteArrayToInt(): Int {
    if (size > 4) throw java.lang.RuntimeException("Too big to fit in int")
    return (this[0].toInt() and 0xFF shl 0)
        .or(this[1].toInt() and 0xFF shl 8)
        .or(this[2].toInt() and 0xFF shl 16)
        .or(this[3].toInt() and 0xFF shl 24)
}

internal fun ByteArray.printBits(
    prefix: String = "",
    collapsed: Boolean = false,
    showByteOffset: Boolean = true
) = toBitSet().printBits(prefix, collapsed, showByteOffset, size * 8)

internal fun ByteArray.toBitSet(): BitSet = BitSet.valueOf(ByteBuffer.wrap(this))

internal fun BitSet.printBits(
    prefix: String = "",
    collapsed: Boolean = false,
    showByteOffset: Boolean = true,
    size: Int = size()
) {
    fun mapBit(b: Boolean): String = if (b) "1" else "0"
    println("$prefix:\n  ${bits(size)
        .take(size)
        .reversed()
        .mapIndexed { index, b ->
            if (collapsed) {
                mapBit(b)
            } else {
                when {
                    index % 8 == 0 -> {
                        val prefixSym = (if (index == 0) "|" else "")
                        if (showByteOffset) "$prefixSym${(size - 1 - index) / 8}) "
                        else "$prefixSym${mapBit(b)}"
                    }
                    index % 8 == 4 -> " " + mapBit(b)
                    index % 8 == 7 -> mapBit(b) + "|"
                    else -> mapBit(b)
                }
            }
        }
        .joinToString(separator = "")}"
    )
}

internal fun BitSet.bits(size: Int = size()): List<Boolean> = with(ArrayList<Boolean>(size)) {
    for (i in 0 until size()) add(this@bits.get(i))
    this
}

fun UUID.toByteArray(): ByteArray =
    with(ByteBuffer.wrap(ByteArray(16))) {
        putLong(mostSignificantBits)
        putLong(leastSignificantBits)
        array()
    }

infix fun Byte.shl(i: Int): Byte = (toInt() shl i).toByte()

infix fun Byte.ushr(i: Int): Byte = (toInt() ushr i).toByte()

fun ByteArray.subByteArray(startIndex: Int, length: Int): ByteArray =
    ByteArray(length).apply {
        System.arraycopy(this@subByteArray, startIndex, this@apply, 0, length)
    }

fun ByteArray.sha224(): ByteArray {
    val md = SHA224Digest()
    md.update(this, 0, this.size)
    return ByteArray(29).apply {
        md.doFinal(this, 0)
    }
}
