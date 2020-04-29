/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kin.stellarfork.codec

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.codec.Hex.Companion.decodeHex
import org.kin.stellarfork.codec.Hex.Companion.encodeHex
import org.kin.stellarfork.codec.Hex.Companion.encodeHexString
import org.kin.stellarfork.codec.StringUtils.getBytesUtf8
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.Random

/**
 * Tests [Hex].
 */
open class HexTest {
    /**
     * Allocate a ByteBuffer.
     *
     *
     * The default implementation uses [ByteBuffer.allocate].
     * The method is overridden in AllocateDirectHexTest to use
     * [ByteBuffer.allocateDirect]
     *
     * @param capacity the capacity
     * @return the byte buffer
     */
    protected open fun allocate(capacity: Int): ByteBuffer {
        return ByteBuffer.allocate(capacity)
    }

    /**
     * Encodes the given string into a byte buffer using the UTF-8 charset.
     *
     *
     * The buffer is allocated using [.allocate].
     *
     * @param string the String to encode
     * @return the byte buffer
     */
    private fun getByteBufferUtf8(string: String): ByteBuffer {
        val bytes = string.toByteArray(StandardCharsets.UTF_8)
        val bb = allocate(bytes.size)
        bb.put(bytes)
        bb.flip()
        return bb
    }

    private fun charsetSanityCheck(name: String): Boolean {
        val source = "the quick brown dog jumped over the lazy fox"
        return try {
            val bytes = source.toByteArray(charset(name))
            val str = String(bytes, Charset.forName(name))
            val equals = source == str
            if (equals == false) { // Here with:
//
// Java Sun 1.4.2_19 x86 32-bits on Windows XP
// JIS_X0212-1990
// x-JIS0208
//
// Java Sun 1.5.0_17 x86 32-bits on Windows XP
// JIS_X0212-1990
// x-IBM834
// x-JIS0208
// x-MacDingbat
// x-MacSymbol
//
// Java Sun 1.6.0_14 x86 32-bits
// JIS_X0212-1990
// x-IBM834
// x-JIS0208
// x-MacDingbat
// x-MacSymbol
//
                log("FAILED charsetSanityCheck=Interesting Java charset oddity: Roundtrip failed for $name")
            }
            equals
        } catch (e: UnsupportedEncodingException) { // Should NEVER happen since we are getting the name from the Charset class.
            if (LOG) {
                log("FAILED charsetSanityCheck=$name, e=$e")
                log(e)
            }
            false
        } catch (e: UnsupportedOperationException) { // Caught here with:
// x-JISAutoDetect on Windows XP and Java Sun 1.4.2_19 x86 32-bits
// x-JISAutoDetect on Windows XP and Java Sun 1.5.0_17 x86 32-bits
// x-JISAutoDetect on Windows XP and Java Sun 1.6.0_14 x86 32-bits
            if (LOG) {
                log("FAILED charsetSanityCheck=$name, e=$e")
                log(e)
            }
            false
        }
    }

    private fun checkDecodeHexCharArrayOddCharacters(data: CharArray) {
        try {
            decodeHex(data)
            Assert.fail("An exception wasn't thrown when trying to decode an odd number of characters")
        } catch (e: DecoderException) { // Expected exception
        }
    }

    private fun checkDecodeHexByteBufferOddCharacters(data: ByteBuffer) {
        try {
            Hex().decode(data)
            Assert.fail("An exception wasn't thrown when trying to decode an odd number of characters")
        } catch (e: DecoderException) { // Expected exception
        }
    }

    private fun checkDecodeHexCharArrayOddCharacters(data: String) {
        try {
            decodeHex(data)
            Assert.fail("An exception wasn't thrown when trying to decode an odd number of characters")
        } catch (e: DecoderException) { // Expected exception
        }
    }

    private fun log(s: String) {
        if (LOG) {
            println(s)
            System.out.flush()
        }
    }

    private fun log(t: Throwable) {
        if (LOG) {
            t.printStackTrace(System.out)
            System.out.flush()
        }
    }

    @Test
    @Throws(
        UnsupportedEncodingException::class,
        DecoderException::class
    )
    fun testCustomCharset() {
        for (name in Charset.availableCharsets().keys) {
            testCustomCharset(name, "testCustomCharset")
        }
    }

    /**
     * @param name
     * @param parent
     * @throws UnsupportedEncodingException
     * @throws DecoderException
     */
    @Throws(
        UnsupportedEncodingException::class,
        DecoderException::class
    )
    private fun testCustomCharset(name: String, parent: String) {
        if (charsetSanityCheck(name) == false) {
            return
        }
        log("$parent=$name")
        val customCodec = Hex(name)
        // source data
        val sourceString = "Hello World"
        val sourceBytes = sourceString.toByteArray(charset(name))
        // test 1
// encode source to hex string to bytes with charset
        val actualEncodedBytes = customCodec.encode(sourceBytes)
        // encode source to hex string...
        var expectedHexString = encodeHexString(sourceBytes)
        // ... and get the bytes in the expected charset
        val expectedHexStringBytes = expectedHexString.toByteArray(charset(name))
        Assert.assertTrue(
            Arrays.equals(
                expectedHexStringBytes,
                actualEncodedBytes
            )
        )
        // test 2
        var actualStringFromBytes = String(actualEncodedBytes!!, Charset.forName(name))
        Assert.assertEquals(
            name + ", expectedHexString=" + expectedHexString + ", actualStringFromBytes=" +
                    actualStringFromBytes, expectedHexString, actualStringFromBytes
        )
        // second test:
        val utf8Codec = Hex()
        expectedHexString = "48656c6c6f20576f726c64"
        actualStringFromBytes = String(utf8Codec.decode(expectedHexString)!!, utf8Codec.charset)
        // sanity check:
        Assert.assertEquals(name, sourceString, actualStringFromBytes)
        // actual check:
        val decodedCustomBytes = customCodec.decode(actualEncodedBytes)
        actualStringFromBytes = String(decodedCustomBytes!!, Charset.forName(name))
        Assert.assertEquals(name, sourceString, actualStringFromBytes)
    }

    @Test
    fun testCustomCharsetToString() {
        Assert.assertTrue(Hex().toString().indexOf(Hex.DEFAULT_CHARSET_NAME) >= 0)
    }

    @Test
    fun testDecodeBadCharacterPos0() {
        try {
            Hex().decode("q0")
            Assert.fail("An exception wasn't thrown when trying to decode an illegal character")
        } catch (e: DecoderException) { // Expected exception
        }
    }

    @Test
    fun testDecodeBadCharacterPos1() {
        try {
            Hex().decode("0q")
            Assert.fail("An exception wasn't thrown when trying to decode an illegal character")
        } catch (e: DecoderException) { // Expected exception
        }
    }

    @Test
    @Throws(DecoderException::class)
    fun testDecodeByteArrayEmpty() {
        Assert.assertTrue(
            Arrays.equals(
                ByteArray(0),
                Hex().decode(ByteArray(0))
            )
        )
    }

    @Test
    fun testDecodeByteArrayOddCharacters() {
        try {
            Hex().decode(byteArrayOf(65))
            Assert.fail("An exception wasn't thrown when trying to decode an odd number of characters")
        } catch (e: DecoderException) { // Expected exception
        }
    }

    @Test
    fun testDecodeByteBufferOddCharacters() {
        val bb = allocate(1)
        bb.put(65.toByte())
        bb.flip()
        checkDecodeHexByteBufferOddCharacters(bb)
    }

    @Test
    fun testDecodeByteBufferWithLimitOddCharacters() {
        val bb = allocate(10)
        bb.put(1, 65.toByte())
        bb.position(1)
        bb.limit(2)
        checkDecodeHexByteBufferOddCharacters(bb)
    }

    @Test
    @Throws(DecoderException::class)
    fun testDecodeHexCharArrayEmpty() {
        Assert.assertTrue(
            Arrays.equals(
                ByteArray(0),
                decodeHex(CharArray(0))
            )
        )
    }

    @Test
    @Throws(DecoderException::class)
    fun testDecodeHexStringEmpty() {
        Assert.assertTrue(Arrays.equals(ByteArray(0), decodeHex("")))
    }

    @Test
    fun testDecodeClassCastException() {
        try {
            Hex().decode(intArrayOf(65))
            Assert.fail("An exception wasn't thrown when trying to decode.")
        } catch (e: DecoderException) { // Expected exception
        }
    }

    @Test
    fun testDecodeHexCharArrayOddCharacters1() {
        checkDecodeHexCharArrayOddCharacters(charArrayOf('A'))
    }

    @Test
    fun testDecodeHexStringOddCharacters1() {
        checkDecodeHexCharArrayOddCharacters("A")
    }

    @Test
    fun testDecodeHexCharArrayOddCharacters3() {
        checkDecodeHexCharArrayOddCharacters(charArrayOf('A', 'B', 'C'))
    }

    @Test
    fun testDecodeHexCharArrayOddCharacters5() {
        checkDecodeHexCharArrayOddCharacters(charArrayOf('A', 'B', 'C', 'D', 'E'))
    }

    @Test
    fun testDecodeHexStringOddCharacters() {
        try {
            Hex().decode("6")
            Assert.fail("An exception wasn't thrown when trying to decode an odd number of characters")
        } catch (e: DecoderException) { // Expected exception
        }
    }

    @Test
    @Throws(DecoderException::class)
    fun testDecodeStringEmpty() {
        Assert.assertTrue(
            Arrays.equals(
                ByteArray(0),
                Hex().decode("")
            )
        )
    }

    @Test
    fun testEncodeByteArrayEmpty() {
        Assert.assertTrue(
            Arrays.equals(
                ByteArray(0),
                Hex().encode(ByteArray(0))
            )
        )
    }

    @Test
    @Throws(EncoderException::class)
    fun testEncodeByteArrayObjectEmpty() {
        Assert.assertTrue(
            Arrays.equals(
                CharArray(0),
                Hex().encode(ByteArray(0) as Any)
            )
        )
    }

    @Test
    fun testEncodeClassCastException() {
        try {
            Hex().encode(intArrayOf(65))
            Assert.fail("An exception wasn't thrown when trying to encode.")
        } catch (e: EncoderException) { // Expected exception
        }
    }

    @Test
    @Throws(
        DecoderException::class,
        EncoderException::class
    )
    fun testEncodeDecodeHexCharArrayRandom() {
        val random = Random()
        val hex = Hex()
        for (i in 5 downTo 1) {
            val data = ByteArray(random.nextInt(10000) + 1)
            random.nextBytes(data)
            // static API
            val encodedChars = encodeHex(data)
            var decodedBytes: ByteArray? = decodeHex(encodedChars)
            Assert.assertTrue(Arrays.equals(data, decodedBytes))
            // instance API with array parameter
            val encodedStringBytes = hex.encode(data)
            decodedBytes = hex.decode(encodedStringBytes)
            Assert.assertTrue(Arrays.equals(data, decodedBytes))
            // instance API with char[] (Object) parameter
            var dataString = String(encodedChars)
            var encodedStringChars = hex.encode(dataString)
            decodedBytes = hex.decode(encodedStringChars)
            Assert.assertTrue(
                Arrays.equals(
                    getBytesUtf8(
                        dataString
                    ), decodedBytes
                )
            )
            // instance API with String (Object) parameter
            dataString = String(encodedChars)
            encodedStringChars = hex.encode(dataString)
            decodedBytes = hex.decode(String(encodedStringChars!!))
            Assert.assertTrue(
                Arrays.equals(
                    getBytesUtf8(
                        dataString
                    ), decodedBytes
                )
            )
        }
    }

    @Test
    fun testEncodeHexByteArrayEmpty() {
        Assert.assertTrue(
            Arrays.equals(
                CharArray(0),
                encodeHex(ByteArray(0))
            )
        )
        Assert.assertTrue(
            Arrays.equals(
                ByteArray(0),
                Hex().encode(ByteArray(0))
            )
        )
    }

    @Test
    fun testEncodeHexByteArrayHelloWorldLowerCaseHex() {
        val b = getBytesUtf8("Hello World")
        val expected = "48656c6c6f20576f726c64"
        var actual: CharArray
        actual = encodeHex(b!!)
        Assert.assertEquals(expected, String(actual))
        actual = encodeHex(b, true)
        Assert.assertEquals(expected, String(actual))
        actual = encodeHex(b, false)
        Assert.assertFalse(expected == String(actual))
    }

    @Test
    fun testEncodeHexByteArrayHelloWorldUpperCaseHex() {
        val b = getBytesUtf8("Hello World")
        val expected = "48656C6C6F20576F726C64"
        var actual: CharArray
        actual = encodeHex(b!!)
        Assert.assertFalse(expected == String(actual))
        actual = encodeHex(b, true)
        Assert.assertFalse(expected == String(actual))
        actual = encodeHex(b, false)
        Assert.assertTrue(expected == String(actual))
    }

    @Test
    fun testEncodeHexByteArrayZeroes() {
        val c = encodeHex(ByteArray(36))
        Assert.assertEquals(
            "000000000000000000000000000000000000000000000000000000000000000000000000",
            String(c)
        )
    }

    @Test
    fun testEncodeHexByteString_ByteArrayOfZeroes() {
        val c = encodeHexString(ByteArray(36))
        Assert.assertEquals(
            "000000000000000000000000000000000000000000000000000000000000000000000000",
            c
        )
    }

    @Test
    fun testEncodeHexByteString_ByteArrayBoolean_ToLowerCase() {
        Assert.assertEquals("0a", encodeHexString(byteArrayOf(10)))
    }

    @Test
    @Throws(EncoderException::class)
    fun testEncodeStringEmpty() {
        Assert.assertTrue(
            Arrays.equals(
                CharArray(0),
                Hex().encode("")
            )
        )
    }

    @Test
    fun testGetCharset() {
        Assert.assertEquals(
            StandardCharsets.UTF_8,
            Hex(StandardCharsets.UTF_8.name()).charset
        )
    }

    @Test
    fun testGetCharsetName() {
        Assert.assertEquals(
            StandardCharsets.UTF_8.name(),
            Hex(StandardCharsets.UTF_8.name()).charsetName
        )
    }

    @Test
    @Throws(
        UnsupportedEncodingException::class,
        DecoderException::class
    )
    fun testRequiredCharset() {
        testCustomCharset("UTF-8", "testRequiredCharset")
        testCustomCharset("UTF-16", "testRequiredCharset")
        testCustomCharset("UTF-16BE", "testRequiredCharset")
        testCustomCharset("UTF-16LE", "testRequiredCharset")
        testCustomCharset("US-ASCII", "testRequiredCharset")
        testCustomCharset("ISO8859_1", "testRequiredCharset")
    }

    companion object {
        private const val BAD_ENCODING_NAME = "UNKNOWN"
        private const val LOG = false
    }
}
