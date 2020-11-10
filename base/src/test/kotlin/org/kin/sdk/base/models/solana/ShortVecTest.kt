package org.kin.sdk.base.models.solana

import org.kin.sdk.base.tools.byteArrayToInt
import org.kin.sdk.base.tools.intToByteArray
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalUnsignedTypes
class ShortVecTest {

    @Test
    fun TestShortVec_Valid() {
        for (i in 0..UShort.MAX_VALUE.toInt()) {
            val output = ByteArrayOutputStream()
            ShortVec.encodeLen(output, i)
            val input = ByteArrayInputStream(output.toByteArray())
            val actual = ShortVec.decodeLen(input)
            assertEquals(i, actual)
        }
    }

    @Test
    fun TestShortVec_Valid_1() {
        val numbers = (0 until UShort.MAX_VALUE.toInt()).toList()
        val output = ByteArrayOutputStream()
        ShortVec.encodeShortVecOf(output, numbers, Int::intToByteArray)

        val input = ByteArrayInputStream(output.toByteArray())
        val actual = ShortVec.decodeShortVecOf<Int>(input, Int.SIZE_BYTES, ByteArray::byteArrayToInt)

        numbers.forEachIndexed { index, i ->
            assertEquals(i, actual[index])
        }
    }

    @Test
    fun TestShortVec_CrossImpl() {

        data class test(val value: Int, val encoded: ByteArray)

        listOf(
            test(0x0, byteArrayOf(0x0)),
            test(0x7f, byteArrayOf(0x7f.toByte())),
            test(0x80, byteArrayOf(0x80.toByte(), 0x01.toByte())),
            test(0xff, byteArrayOf(0xff.toByte(), 0x01.toByte())),
            test(0x100, byteArrayOf(0x80.toByte(), 0x02.toByte())),
            test(0x7fff, byteArrayOf(0xff.toByte(), 0xff.toByte(), 0x01.toByte())),
            test(0xffff, byteArrayOf(0xff.toByte(), 0xff.toByte(), 0x03.toByte())),
        ).forEach {
            val output = ByteArrayOutputStream()
            val written = ShortVec.encodeLen(output, it.value)
            assertEquals(it.encoded.size, written)
            assertTrue { it.encoded.contentEquals(output.toByteArray()) }
        }
    }

    @Test(expected = RuntimeException::class)
    fun TestShortVec_Invalid() {
        val output = ByteArrayOutputStream()
        ShortVec.encodeLen(output, UShort.MAX_VALUE.toInt() + 1)
    }
}
