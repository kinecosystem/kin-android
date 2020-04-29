package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.Memo.Companion.hash
import org.kin.stellarfork.Memo.Companion.id
import org.kin.stellarfork.Memo.Companion.none
import org.kin.stellarfork.Memo.Companion.returnHash
import org.kin.stellarfork.Memo.Companion.text
import org.kin.stellarfork.Util.paddedByteArrayToString
import org.kin.stellarfork.codec.DecoderException
import org.kin.stellarfork.xdr.MemoType
import java.util.Arrays

class MemoTest {
    @Test
    fun testMemoNone() {
        val memo = none()
        Assert.assertEquals(MemoType.MEMO_NONE, memo.toXdr()!!.discriminant)
    }

    @Test
    fun testMemoTextSuccess() {
        val memo = text("test")
        Assert.assertEquals(MemoType.MEMO_TEXT, memo.toXdr()!!.discriminant)
        Assert.assertEquals("test", memo.text)
    }

    @Test
    fun testMemoTextUtf8() {
        val memo = text("三")
        Assert.assertEquals(MemoType.MEMO_TEXT, memo.toXdr()!!.discriminant)
        Assert.assertEquals("三", memo.text)
    }

    @Test
    fun testMemoTextTooLong() {
        try {
            text("12345678901234567890123456789")
            Assert.fail()
        } catch (exception: RuntimeException) {
            Assert.assertTrue(exception.message!!.contains("text must be <= 28 bytes."))
        }
    }

    @Test
    fun testMemoTextTooLongUtf8() {
        try {
            text("价值交易的开源协议!!")
            Assert.fail()
        } catch (exception: RuntimeException) {
            Assert.assertTrue(exception.message!!.contains("text must be <= 28 bytes."))
        }
    }

    @Test
    fun testMemoId() {
        val memo = id(9223372036854775807L)
        Assert.assertEquals(9223372036854775807L, memo.id)
        Assert.assertEquals(MemoType.MEMO_ID, memo.toXdr()!!.discriminant)
        Assert.assertEquals(9223372036854775807L, memo.toXdr()!!.id!!.uint64)
    }

    @Test
    @Throws(DecoderException::class)
    fun testMemoHashSuccess() {
        val memo = hash("4142434445464748494a4b4c")
        Assert.assertEquals(MemoType.MEMO_HASH, memo.toXdr().discriminant)
        val test = "ABCDEFGHIJKL"
        Assert.assertEquals(
            test,
            paddedByteArrayToString(memo.bytes)
        )
        Assert.assertEquals("4142434445464748494a4b4c", memo.trimmedHexValue)
    }

    @Test
    fun testMemoHashBytesSuccess() {
        val bytes = ByteArray(10)
        Arrays.fill(bytes, 'A'.toByte())
        val memo = hash(bytes)
        Assert.assertEquals(MemoType.MEMO_HASH, memo.toXdr().discriminant)
        Assert.assertEquals(
            "AAAAAAAAAA",
            paddedByteArrayToString(memo.bytes)
        )
        Assert.assertEquals(
            "4141414141414141414100000000000000000000000000000000000000000000",
            memo.hexValue
        )
        Assert.assertEquals("41414141414141414141", memo.trimmedHexValue)
    }

    @Test
    fun testMemoHashTooLong() {
        val longer = ByteArray(33)
        Arrays.fill(longer, 0.toByte())
        try {
            hash(longer)
            Assert.fail()
        } catch (exception: MemoTooLongException) {
            Assert.assertTrue(exception.message!!.contains("MEMO_HASH can contain 32 bytes at max."))
        }
    }

    @Test
    fun testMemoHashInvalidHex() {
        try {
            hash("test")
            Assert.fail()
        } catch (e: DecoderException) {
        }
    }

    @Test
    @Throws(DecoderException::class)
    fun testMemoReturnHashSuccess() {
        val memo = returnHash("4142434445464748494a4b4c")
        Assert.assertEquals(MemoType.MEMO_RETURN, memo.toXdr().discriminant)
        Assert.assertEquals("4142434445464748494a4b4c", memo.trimmedHexValue)
    }
}
