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
import org.kin.stellarfork.codec.Base64.Companion.decodeBase64
import org.kin.stellarfork.codec.Base64.Companion.decodeInteger
import org.kin.stellarfork.codec.Base64.Companion.encodeBase64
import org.kin.stellarfork.codec.Base64.Companion.encodeBase64Chunked
import org.kin.stellarfork.codec.Base64.Companion.encodeBase64String
import org.kin.stellarfork.codec.Base64.Companion.encodeBase64URLSafe
import org.kin.stellarfork.codec.Base64.Companion.encodeBase64URLSafeString
import org.kin.stellarfork.codec.Base64.Companion.encodeInteger
import org.kin.stellarfork.codec.Base64.Companion.isBase64
import org.kin.stellarfork.codec.Hex.Companion.decodeHex
import org.kin.stellarfork.codec.StringUtils.getBytesUtf8
import org.kin.stellarfork.codec.StringUtils.newStringUsAscii
import org.kin.stellarfork.codec.StringUtils.newStringUtf8
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.util.Arrays
import java.util.Random

/**
 * Test cases for Base64 class.
 *
 * @see [RFC 2045](http://www.ietf.org/rfc/rfc2045.txt)
 */
class Base64Test {
    /**
     * @return Returns the random.
     */
    val random = Random()

    /**
     * Test the isStringBase64 method.
     */
    @Test
    fun testIsStringBase64() {
        val nullString: String? = null
        val emptyString = ""
        val validString =
            "abc===defg\n\r123456\r789\r\rABC\n\nDEF==GHI\r\nJKL=============="
        val invalidString = validString + 0.toChar() // append null
        // character
        try {
            isBase64(nullString!!)
            Assert.fail("Base64.isStringBase64() should not be null-safe.")
        } catch (npe: NullPointerException) {
            Assert.assertNotNull("Base64.isStringBase64() should not be null-safe.", npe)
        } catch (npe: IllegalArgumentException) {
            Assert.assertNotNull("Base64.isStringBase64() should not be null-safe.", npe)
        }
        Assert.assertTrue(
            "Base64.isStringBase64(empty-string) is true",
            isBase64(emptyString)
        )
        Assert.assertTrue(
            "Base64.isStringBase64(valid-string) is true",
            isBase64(validString)
        )
        Assert.assertFalse(
            "Base64.isStringBase64(invalid-string) is false",
            isBase64(invalidString)
        )
    }

    /**
     * Test the Base64 implementation
     */
    @Test
    fun testBase64() {
        val content = "Hello World"
        var encodedContent: String?
        var encodedBytes = encodeBase64(
            getBytesUtf8(content)
        )
        encodedContent = newStringUtf8(encodedBytes)
        Assert.assertEquals("encoding hello world", "SGVsbG8gV29ybGQ=", encodedContent)
        var b64 =
            Base64(BaseNCodec.MIME_CHUNK_SIZE, null) // null
        // lineSeparator
// same as
// saying
// no-chunking
        encodedBytes = b64.encode(getBytesUtf8(content))
        encodedContent = newStringUtf8(encodedBytes)
        Assert.assertEquals("encoding hello world", "SGVsbG8gV29ybGQ=", encodedContent)
        b64 = Base64(0, null) // null lineSeparator same as saying
        // no-chunking
        encodedBytes = b64.encode(getBytesUtf8(content))
        encodedContent = newStringUtf8(encodedBytes)
        Assert.assertEquals("encoding hello world", "SGVsbG8gV29ybGQ=", encodedContent)
        // bogus characters to decode (to skip actually) {e-acute*6}
        val decode =
            b64.decode("SGVsbG{\u00e9\u00e9\u00e9\u00e9\u00e9\u00e9}8gV29ybGQ=")
        val decodeString =
            newStringUtf8(decode)
        Assert.assertEquals("decode hello world", "Hello World", decodeString)
    }

    /**
     * Test our decode with pad character in the middle. (Our current
     * implementation: halt decode and return what we've got so far).
     *
     *
     * The point of this test is not to say
     * "this is the correct way to decode base64." The point is simply to keep
     * us aware of the current logic since 1.4 so we don't accidentally break it
     * without realizing.
     *
     *
     * Note for historians. The 1.3 logic would decode to:
     * "Hello World\u0000Hello World" -- null in the middle --- and 1.4
     * unwittingly changed it to current logic.
     */
    @Test
    fun testDecodeWithInnerPad() {
        val content = "SGVsbG8gV29ybGQ=SGVsbG8gV29ybGQ="
        val result = decodeBase64(content)
        val shouldBe = getBytesUtf8("Hello World")
        Assert.assertTrue(
            "decode should halt at pad (=)",
            Arrays.equals(result, shouldBe)
        )
    }

    /**
     * Tests Base64.encodeBase64().
     */
    @Test
    fun testChunkedEncodeMultipleOf76() {
        val expectedEncode =
            encodeBase64(Base64TestData.DECODED, true)
        // convert to "\r\n" so we're equal to the old openssl encoding test
// stored
// in Base64TestData.ENCODED_76_CHARS_PER_LINE:
        val actualResult =
            Base64TestData.ENCODED_76_CHARS_PER_LINE.replace("\n".toRegex(), "\r\n")
        val actualEncode =
            getBytesUtf8(actualResult)
        Assert.assertTrue(
            "chunkedEncodeMultipleOf76",
            Arrays.equals(expectedEncode, actualEncode)
        )
    }

    /**
     * CODEC-68: isBase64 throws ArrayIndexOutOfBoundsException on some
     * non-BASE64 bytes
     */
    @Test
    fun testCodec68() {
        val x =
            byteArrayOf('n'.toByte(), 'A'.toByte(), '='.toByte(), '='.toByte(), 0x9c.toByte())
        decodeBase64(x)
    }

    @Test
    fun testCodeInteger1() {
        val encodedInt1 = "li7dzDacuo67Jg7mtqEm2TRuOMU="
        val bigInt1 =
            BigInteger("85739377120809420210425962799" + "0318636601332086981")
        Assert.assertEquals(
            encodedInt1,
            String(encodeInteger(bigInt1)!!)
        )
        Assert.assertEquals(
            bigInt1,
            decodeInteger(encodedInt1.toByteArray(CHARSET_UTF8))
        )
    }

    @Test
    fun testCodeInteger2() {
        val encodedInt2 = "9B5ypLY9pMOmtxCeTDHgwdNFeGs="
        val bigInt2 =
            BigInteger("13936727572861167254666467268" + "91466679477132949611")
        Assert.assertEquals(
            encodedInt2,
            String(encodeInteger(bigInt2)!!)
        )
        Assert.assertEquals(
            bigInt2,
            decodeInteger(encodedInt2.toByteArray(CHARSET_UTF8))
        )
    }

    @Test
    fun testCodeInteger3() {
        val encodedInt3 = ("FKIhdgaG5LGKiEtF1vHy4f3y700zaD6QwDS3IrNVGzNp2"
                + "rY+1LFWTK6D44AyiC1n8uWz1itkYMZF0/aKDK0Yjg==")
        val bigInt3 = BigInteger(
            "10806548154093873461951748545" + "1196989136416448805819079363524309897749044958112417136240557"
                    + "4495062430572478766856090958495998158114332651671116876320938126"
        )
        Assert.assertEquals(
            encodedInt3,
            String(encodeInteger(bigInt3)!!)
        )
        Assert.assertEquals(
            bigInt3,
            decodeInteger(encodedInt3.toByteArray(CHARSET_UTF8))
        )
    }

    @Test
    fun testCodeInteger4() {
        val encodedInt4 = ("ctA8YGxrtngg/zKVvqEOefnwmViFztcnPBYPlJsvh6yKI"
                + "4iDm68fnp4Mi3RrJ6bZAygFrUIQLxLjV+OJtgJAEto0xAs+Mehuq1DkSFEpP3o"
                + "DzCTOsrOiS1DwQe4oIb7zVk/9l7aPtJMHW0LVlMdwZNFNNJoqMcT2ZfCPrfvYv" + "Q0=")
        val bigInt4 = BigInteger(
            "80624726256040348115552042320" + "6968135001872753709424419772586693950232350200555646471175944"
                    + "519297087885987040810778908507262272892702303774422853675597"
                    + "748008534040890923814202286633163248086055216976551456088015"
                    + "338880713818192088877057717530169381044092839402438015097654"
                    + "53542091716518238707344493641683483917"
        )
        Assert.assertEquals(
            encodedInt4,
            String(encodeInteger(bigInt4)!!)
        )
        Assert.assertEquals(
            bigInt4,
            decodeInteger(encodedInt4.toByteArray(CHARSET_UTF8))
        )
    }

    @Test
    fun testCodeIntegerEdgeCases() { // TODO
    }

    @Test
    fun testCodeIntegerNull() {
        try {
            encodeInteger(null)
            Assert.fail("Exception not thrown when passing in null to encodeInteger(BigInteger)")
        } catch (npe: NullPointerException) { // expected
        } catch (e: Exception) {
            Assert.fail("Incorrect Exception caught when passing in null to encodeInteger(BigInteger)")
        }
    }

    @Test
    @Suppress("UNUSED_VALUE")
    fun testConstructors() {
        var base64: Base64
        base64 = Base64()
        base64 = Base64(-1)
        base64 = Base64(-1, byteArrayOf())
        base64 = Base64(64, byteArrayOf())
        try {
            base64 = Base64(
                -1,
                byteArrayOf('A'.toByte())
            ) // TODO do we need to
            // check sep if len
// = -1?
            Assert.fail("Should have rejected attempt to use 'A' as a line separator")
        } catch (ignored: IllegalArgumentException) { // Expected
        }
        try {
            base64 = Base64(64, byteArrayOf('A'.toByte()))
            Assert.fail("Should have rejected attempt to use 'A' as a line separator")
        } catch (ignored: IllegalArgumentException) { // Expected
        }
        try {
            base64 = Base64(64, byteArrayOf('='.toByte()))
            Assert.fail("Should have rejected attempt to use '=' as a line separator")
        } catch (ignored: IllegalArgumentException) { // Expected
        }
        base64 = Base64(64, byteArrayOf('$'.toByte())) // OK
        try {
            base64 = Base64(64, byteArrayOf('A'.toByte(), '$'.toByte()))
            Assert.fail("Should have rejected attempt to use 'A$' as a line separator")
        } catch (ignored: IllegalArgumentException) { // Expected
        }
        base64 = Base64(
            64,
            byteArrayOf(' '.toByte(), '$'.toByte(), '\n'.toByte(), '\r'.toByte(), '\t'.toByte())
        ) // OK
        Assert.assertNotNull(base64)
    }

    @Test
    fun testConstructor_Int_ByteArray_Boolean() {
        val base64 =
            Base64(65, byteArrayOf('\t'.toByte()), false)
        val encoded = base64.encode(Base64TestData.DECODED)
        var expectedResult = Base64TestData.ENCODED_64_CHARS_PER_LINE
        expectedResult = expectedResult.replace('\n', '\t')
        val result = newStringUtf8(encoded)
        Assert.assertEquals("new Base64(65, \\t, false)", expectedResult, result)
    }

    @Test
    fun testConstructor_Int_ByteArray_Boolean_UrlSafe() { // url-safe variation
        val base64 =
            Base64(64, byteArrayOf('\t'.toByte()), true)
        val encoded = base64.encode(Base64TestData.DECODED)
        var expectedResult = Base64TestData.ENCODED_64_CHARS_PER_LINE
        expectedResult = expectedResult.replace("=".toRegex(), "") // url-safe has no
        // == padding.
        expectedResult = expectedResult.replace('\n', '\t')
        expectedResult = expectedResult.replace('+', '-')
        expectedResult = expectedResult.replace('/', '_')
        val result = newStringUtf8(encoded)
        Assert.assertEquals("new Base64(64, \\t, true)", result, expectedResult)
    }

    /**
     * Tests conditional true branch for "marker0" test.
     */
    @Test
    fun testDecodePadMarkerIndex2() {
        Assert.assertEquals(
            "A",
            String(decodeBase64("QQ==".toByteArray(CHARSET_UTF8))!!)
        )
    }

    /**
     * Tests conditional branches for "marker1" test.
     */
    @Test
    fun testDecodePadMarkerIndex3() {
        Assert.assertEquals(
            "AA",
            String(decodeBase64("QUE=".toByteArray(CHARSET_UTF8))!!)
        )
        Assert.assertEquals(
            "AAA",
            String(decodeBase64("QUFB".toByteArray(CHARSET_UTF8))!!)
        )
    }

    @Test
    fun testDecodePadOnly() {
        Assert.assertEquals(
            0,
            decodeBase64("====".toByteArray(CHARSET_UTF8))?.size
        )
        Assert.assertEquals(
            "",
            String(decodeBase64("====".toByteArray(CHARSET_UTF8))!!)
        )
        // Test truncated padding
        Assert.assertEquals(
            0,
            decodeBase64("===".toByteArray(CHARSET_UTF8))?.size
        )
        Assert.assertEquals(
            0,
            decodeBase64("==".toByteArray(CHARSET_UTF8))?.size
        )
        Assert.assertEquals(
            0,
            decodeBase64("=".toByteArray(CHARSET_UTF8))?.size
        )
        Assert.assertEquals(
            0,
            decodeBase64("".toByteArray(CHARSET_UTF8))?.size
        )
    }

    @Test
    fun testDecodePadOnlyChunked() {
        Assert.assertEquals(
            0,
            decodeBase64("====\n".toByteArray(CHARSET_UTF8))?.size
        )
        Assert.assertEquals(
            "",
            String(decodeBase64("====\n".toByteArray(CHARSET_UTF8))!!)
        )
        // Test truncated padding
        Assert.assertEquals(
            0,
            decodeBase64("===\n".toByteArray(CHARSET_UTF8))?.size
        )
        Assert.assertEquals(
            0,
            decodeBase64("==\n".toByteArray(CHARSET_UTF8))?.size
        )
        Assert.assertEquals(
            0,
            decodeBase64("=\n".toByteArray(CHARSET_UTF8))?.size
        )
        Assert.assertEquals(
            0,
            decodeBase64("\n".toByteArray(CHARSET_UTF8))?.size
        )
    }

    @Test
    @Throws(Exception::class)
    fun testDecodeWithWhitespace() {
        val orig = "I am a late night coder."
        val encodedArray =
            encodeBase64(orig.toByteArray(CHARSET_UTF8))
        val intermediate = StringBuilder(String(encodedArray!!))
        intermediate.insert(2, ' ')
        intermediate.insert(5, '\t')
        intermediate.insert(10, '\r')
        intermediate.insert(15, '\n')
        val encodedWithWS =
            intermediate.toString().toByteArray(CHARSET_UTF8)
        val decodedWithWS = decodeBase64(encodedWithWS)
        val dest = String(decodedWithWS!!)
        Assert.assertEquals("Dest string doesn't equal the original", orig, dest)
    }

    /**
     * Test encode and decode of empty byte array.
     */
    @Test
    fun testEmptyBase64() {
        var empty = ByteArray(0)
        var result = encodeBase64(empty)
        Assert.assertEquals("empty base64 encode", 0, result!!.size.toLong())
        Assert.assertEquals(
            "empty base64 encode",
            null,
            encodeBase64(null)
        )
        empty = ByteArray(0)
        result = decodeBase64(empty)
        Assert.assertEquals("empty base64 decode", 0, result!!.size.toLong())
        Assert.assertEquals(
            "empty base64 encode",
            null,
            decodeBase64(null as ByteArray?)
        )
    }

    // encode/decode a large random array
    @Test
    fun testEncodeDecodeRandom() {
        for (i in 1..4) {
            val data = ByteArray(random.nextInt(10000) + 1)
            random.nextBytes(data)
            val enc = encodeBase64(data)
            Assert.assertTrue(isBase64(enc!!))
            val data2 = decodeBase64(enc)
            Assert.assertTrue(Arrays.equals(data, data2))
        }
    }

    // encode/decode random arrays from size 0 to size 11
    @Test
    fun testEncodeDecodeSmall() {
        for (i in 0..11) {
            val data = ByteArray(i)
            random.nextBytes(data)
            val enc = encodeBase64(data)
            Assert.assertTrue(
                "\"" + String(enc!!) + "\" is Base64 data.",
                isBase64(enc)
            )
            val data2 = decodeBase64(enc)
            Assert.assertTrue(
                toString(data) + " equals " + toString(data2),
                Arrays.equals(data, data2)
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testEncodeOverMaxSize() {
        testEncodeOverMaxSize(-1)
        testEncodeOverMaxSize(0)
        testEncodeOverMaxSize(1)
        testEncodeOverMaxSize(2)
    }

    @Test
    fun testCodec112() { // size calculation assumes always chunked
        val `in` = byteArrayOf(0)
        val out = encodeBase64(`in`)
        encodeBase64(`in`, false, false, out!!.size)
    }

    @Throws(Exception::class)
    private fun testEncodeOverMaxSize(maxSize: Int) {
        try {
            encodeBase64(
                Base64TestData.DECODED,
                true,
                false,
                maxSize
            )
            Assert.fail("Expected " + IllegalArgumentException::class.java.name)
        } catch (e: IllegalArgumentException) { // Expected
        }
    }

    @Test
    @Throws(Exception::class)
    fun testIgnoringNonBase64InDecode() {
        Assert.assertEquals(
            "The quick brown fox jumped over the lazy dogs.",
            String(
                decodeBase64(
                    "VGhlIH@$#$@%F1aWN@#@#@@rIGJyb3duIGZve\n\r\t%#%#%#%CBqd##$#\$W1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg=="
                        .toByteArray(CHARSET_UTF8)
                )!!
            )
        )
    }

    @Test
    fun testIsArrayByteBase64() {
        Assert.assertFalse(isBase64(byteArrayOf(Byte.MIN_VALUE)))
        Assert.assertFalse(isBase64(byteArrayOf(-125)))
        Assert.assertFalse(isBase64(byteArrayOf(-10)))
        Assert.assertFalse(isBase64(byteArrayOf(0)))
        Assert.assertFalse(
            isBase64(
                byteArrayOf(
                    64,
                    Byte.MAX_VALUE
                )
            )
        )
        Assert.assertFalse(isBase64(byteArrayOf(Byte.MAX_VALUE)))
        Assert.assertTrue(isBase64(byteArrayOf('A'.toByte())))
        Assert.assertFalse(
            isBase64(
                byteArrayOf(
                    'A'.toByte(),
                    Byte.MIN_VALUE
                )
            )
        )
        Assert.assertTrue(
            isBase64(
                byteArrayOf(
                    'A'.toByte(),
                    'Z'.toByte(),
                    'a'.toByte()
                )
            )
        )
        Assert.assertTrue(
            isBase64(
                byteArrayOf(
                    '/'.toByte(),
                    '='.toByte(),
                    '+'.toByte()
                )
            )
        )
        Assert.assertFalse(isBase64(byteArrayOf('$'.toByte())))
    }

    /**
     * Tests isUrlSafe.
     */
    @Test
    fun testIsUrlSafe() {
        val base64Standard =
            Base64(false)
        val base64URLSafe = Base64(true)
        Assert.assertFalse("Base64.isUrlSafe=false", base64Standard.isUrlSafe)
        Assert.assertTrue("Base64.isUrlSafe=true", base64URLSafe.isUrlSafe)
        val whiteSpace =
            byteArrayOf(' '.toByte(), '\n'.toByte(), '\r'.toByte(), '\t'.toByte())
        Assert.assertTrue(
            "Base64.isBase64(whiteSpace)=true",
            isBase64(whiteSpace)
        )
    }

    @Test
    fun testKnownDecodings() {
        Assert.assertEquals(
            "The quick brown fox jumped over the lazy dogs.", String(
                decodeBase64(
                    "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg==".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "It was the best of times, it was the worst of times.", String(
                decodeBase64(
                    "SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIHRpbWVzLg==".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "http://jakarta.apache.org/commmons", String(
                decodeBase64(
                    "aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw==".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz", String(
                decodeBase64(
                    "QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1h4WXlaeg==".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }",
            String(
                decodeBase64(
                    "eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0=".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "xyzzy!",
            String(decodeBase64("eHl6enkh".toByteArray(CHARSET_UTF8))!!)
        )
    }

    @Test
    fun testKnownEncodings() {
        Assert.assertEquals(
            "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wZWQgb3ZlciB0aGUgbGF6eSBkb2dzLg==", String(
                encodeBase64(
                    "The quick brown fox jumped over the lazy dogs.".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "YmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJs\r\nYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFo\r\nIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBibGFoIGJsYWggYmxhaCBi\r\nbGFoIGJsYWg=\r\n",
            String(
                encodeBase64Chunked(
                    "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah"
                        .toByteArray(CHARSET_UTF8)
                )!!
            )
        )
        Assert.assertEquals(
            "SXQgd2FzIHRoZSBiZXN0IG9mIHRpbWVzLCBpdCB3YXMgdGhlIHdvcnN0IG9mIHRpbWVzLg==", String(
                encodeBase64(
                    "It was the best of times, it was the worst of times.".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "aHR0cDovL2pha2FydGEuYXBhY2hlLm9yZy9jb21tbW9ucw==",
            String(
                encodeBase64(
                    "http://jakarta.apache.org/commmons".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "QWFCYkNjRGRFZUZmR2dIaElpSmpLa0xsTW1Obk9vUHBRcVJyU3NUdFV1VnZXd1h4WXlaeg==", String(
                encodeBase64(
                    "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "eyAwLCAxLCAyLCAzLCA0LCA1LCA2LCA3LCA4LCA5IH0=",
            String(
                encodeBase64(
                    "{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }".toByteArray(
                        CHARSET_UTF8
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "eHl6enkh",
            String(encodeBase64("xyzzy!".toByteArray(CHARSET_UTF8))!!)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testNonBase64Test() {
        val bArray = byteArrayOf('%'.toByte())
        Assert.assertFalse(
            "Invalid Base64 array was incorrectly validated as " + "an array of Base64 encoded data",
            isBase64(bArray)
        )
        try {
            val b64 = Base64()
            val result = b64.decode(bArray)
            Assert.assertEquals(
                "The result should be empty as the test encoded content did "
                        + "not contain any valid base 64 characters", 0, result!!.size.toLong()
            )
        } catch (e: Exception) {
            Assert.fail(
                "Exception was thrown when trying to decode "
                        + "invalid base64 encoded data - RFC 2045 requires that all "
                        + "non base64 character be discarded, an exception should not" + " have been thrown"
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testObjectDecodeWithInvalidParameter() {
        val b64 = Base64()
        try {
            b64.decode(Integer.valueOf(5))
            Assert.fail("decode(Object) didn't throw an exception when passed an Integer object")
        } catch (e: DecoderException) { // ignored
        }
    }

    @Test
    @Throws(Exception::class)
    fun testObjectDecodeWithValidParameter() {
        val original = "Hello World!"
        val o: Any? =
            encodeBase64(original.toByteArray(CHARSET_UTF8))
        val b64 = Base64()
        val oDecoded = b64.decode(o)
        val baDecoded = oDecoded as ByteArray?
        val dest = String(baDecoded!!)
        Assert.assertEquals("dest string does not equal original", original, dest)
    }

    @Test
    @Throws(Exception::class)
    fun testObjectEncodeWithInvalidParameter() {
        val b64 = Base64()
        try {
            b64.encode("Yadayadayada")
            Assert.fail("encode(Object) didn't throw an exception when passed a String object")
        } catch (e: EncoderException) { // Expected
        }
    }

    @Test
    @Throws(Exception::class)
    fun testObjectEncodeWithValidParameter() {
        val original = "Hello World!"
        val origObj: Any = original.toByteArray(CHARSET_UTF8)
        val b64 = Base64()
        val oEncoded = b64.encode(origObj)
        val bArray =
            decodeBase64(oEncoded as ByteArray?)
        val dest = String(bArray!!)
        Assert.assertEquals("dest string does not equal original", original, dest)
    }

    @Test
    @Throws(Exception::class)
    fun testObjectEncode() {
        val b64 = Base64()
        Assert.assertEquals(
            "SGVsbG8gV29ybGQ=",
            String(b64.encode("Hello World".toByteArray(CHARSET_UTF8))!!)
        )
    }

    @Test
    fun testPairs() {
        Assert.assertEquals(
            "AAA=",
            String(encodeBase64(byteArrayOf(0, 0))!!)
        )
        for (i in -128..127) {
            val test = byteArrayOf(i.toByte(), i.toByte())
            Assert.assertTrue(
                Arrays.equals(
                    test,
                    decodeBase64(
                        encodeBase64(test)
                    )
                )
            )
        }
    }

    /**
     * Tests RFC 2045 section 2.1 CRLF definition.
     */
    @Test
    fun testRfc2045Section2Dot1CrLfDefinition() {
        Assert.assertTrue(
            Arrays.equals(
                byteArrayOf(13, 10),
                Base64.CHUNK_SEPARATOR
            )
        )
    }

    /**
     * Tests RFC 2045 section 6.8 chuck size definition.
     */
    @Test
    fun testRfc2045Section6Dot8ChunkSizeDefinition() {
        Assert.assertEquals(76, BaseNCodec.MIME_CHUNK_SIZE)
    }

    /**
     * Tests RFC 1421 section 4.3.2.4 chuck size definition.
     */
    @Test
    fun testRfc1421Section6Dot8ChunkSizeDefinition() {
        Assert.assertEquals(64, BaseNCodec.PEM_CHUNK_SIZE)
    }

    /**
     * Tests RFC 4648 section 10 test vectors.
     *
     *  * BASE64("") = ""
     *  * BASE64("f") = "Zg=="
     *  * BASE64("fo") = "Zm8="
     *  * BASE64("foo") = "Zm9v"
     *  * BASE64("foob") = "Zm9vYg=="
     *  * BASE64("fooba") = "Zm9vYmE="
     *  * BASE64("foobar") = "Zm9vYmFy"
     *
     *
     * @see [http://tools.ietf.org/
     * html/rfc4648](http://tools.ietf.org/html/rfc4648)
     */
    @Test
    fun testRfc4648Section10Decode() {
        Assert.assertEquals(
            "",
            newStringUsAscii(
                decodeBase64("")
            )
        )
        Assert.assertEquals(
            "f",
            newStringUsAscii(
                decodeBase64("Zg==")
            )
        )
        Assert.assertEquals(
            "fo",
            newStringUsAscii(
                decodeBase64("Zm8=")
            )
        )
        Assert.assertEquals(
            "foo",
            newStringUsAscii(
                decodeBase64("Zm9v")
            )
        )
        Assert.assertEquals(
            "foob",
            newStringUsAscii(
                decodeBase64("Zm9vYg==")
            )
        )
        Assert.assertEquals(
            "fooba",
            newStringUsAscii(
                decodeBase64("Zm9vYmE=")
            )
        )
        Assert.assertEquals(
            "foobar",
            newStringUsAscii(
                decodeBase64("Zm9vYmFy")
            )
        )
    }

    /**
     * Tests RFC 4648 section 10 test vectors.
     *
     *  * BASE64("") = ""
     *  * BASE64("f") = "Zg=="
     *  * BASE64("fo") = "Zm8="
     *  * BASE64("foo") = "Zm9v"
     *  * BASE64("foob") = "Zm9vYg=="
     *  * BASE64("fooba") = "Zm9vYmE="
     *  * BASE64("foobar") = "Zm9vYmFy"
     *
     *
     * @see [http://tools.ietf.org/
     * html/rfc4648](http://tools.ietf.org/html/rfc4648)
     */
    @Test
    fun testRfc4648Section10DecodeWithCrLf() {
        val CRLF =
            newStringUsAscii(Base64.CHUNK_SEPARATOR)
        Assert.assertEquals(
            "",
            newStringUsAscii(
                decodeBase64("" + CRLF)
            )
        )
        Assert.assertEquals(
            "f",
            newStringUsAscii(
                decodeBase64("Zg==$CRLF")
            )
        )
        Assert.assertEquals(
            "fo",
            newStringUsAscii(
                decodeBase64("Zm8=$CRLF")
            )
        )
        Assert.assertEquals(
            "foo",
            newStringUsAscii(
                decodeBase64("Zm9v$CRLF")
            )
        )
        Assert.assertEquals(
            "foob",
            newStringUsAscii(
                decodeBase64("Zm9vYg==$CRLF")
            )
        )
        Assert.assertEquals(
            "fooba",
            newStringUsAscii(
                decodeBase64("Zm9vYmE=$CRLF")
            )
        )
        Assert.assertEquals(
            "foobar",
            newStringUsAscii(
                decodeBase64("Zm9vYmFy$CRLF")
            )
        )
    }

    /**
     * Tests RFC 4648 section 10 test vectors.
     *
     *  * BASE64("") = ""
     *  * BASE64("f") = "Zg=="
     *  * BASE64("fo") = "Zm8="
     *  * BASE64("foo") = "Zm9v"
     *  * BASE64("foob") = "Zm9vYg=="
     *  * BASE64("fooba") = "Zm9vYmE="
     *  * BASE64("foobar") = "Zm9vYmFy"
     *
     *
     * @see [http://tools.ietf.org/
     * html/rfc4648](http://tools.ietf.org/html/rfc4648)
     */
    @Test
    fun testRfc4648Section10Encode() {
        Assert.assertEquals(
            "",
            encodeBase64String(
                getBytesUtf8("")
            )
        )
        Assert.assertEquals(
            "Zg==",
            encodeBase64String(
                getBytesUtf8("f")
            )
        )
        Assert.assertEquals(
            "Zm8=",
            encodeBase64String(
                getBytesUtf8("fo")
            )
        )
        Assert.assertEquals(
            "Zm9v",
            encodeBase64String(
                getBytesUtf8("foo")
            )
        )
        Assert.assertEquals(
            "Zm9vYg==",
            encodeBase64String(
                getBytesUtf8("foob")
            )
        )
        Assert.assertEquals(
            "Zm9vYmE=",
            encodeBase64String(
                getBytesUtf8("fooba")
            )
        )
        Assert.assertEquals(
            "Zm9vYmFy",
            encodeBase64String(
                getBytesUtf8("foobar")
            )
        )
    }

    /**
     * Tests RFC 4648 section 10 test vectors.
     *
     *  * BASE64("") = ""
     *  * BASE64("f") = "Zg=="
     *  * BASE64("fo") = "Zm8="
     *  * BASE64("foo") = "Zm9v"
     *  * BASE64("foob") = "Zm9vYg=="
     *  * BASE64("fooba") = "Zm9vYmE="
     *  * BASE64("foobar") = "Zm9vYmFy"
     *
     *
     * @see [http://tools.ietf.org/
     * html/rfc4648](http://tools.ietf.org/html/rfc4648)
     */
    @Test
    fun testRfc4648Section10DecodeEncode() {
        testDecodeEncode("")
        testDecodeEncode("Zg==")
        testDecodeEncode("Zm8=")
        testDecodeEncode("Zm9v")
        testDecodeEncode("Zm9vYg==")
        testDecodeEncode("Zm9vYmE=")
        testDecodeEncode("Zm9vYmFy")
    }

    private fun testDecodeEncode(encodedText: String) {
        val decodedText = newStringUsAscii(
            decodeBase64(encodedText)
        )
        val encodedText2 = encodeBase64String(
            getBytesUtf8(decodedText)
        )
        Assert.assertEquals(encodedText, encodedText2)
    }

    /**
     * Tests RFC 4648 section 10 test vectors.
     *
     *  * BASE64("") = ""
     *  * BASE64("f") = "Zg=="
     *  * BASE64("fo") = "Zm8="
     *  * BASE64("foo") = "Zm9v"
     *  * BASE64("foob") = "Zm9vYg=="
     *  * BASE64("fooba") = "Zm9vYmE="
     *  * BASE64("foobar") = "Zm9vYmFy"
     *
     *
     * @see [http://tools.ietf.org/
     * html/rfc4648](http://tools.ietf.org/html/rfc4648)
     */
    @Test
    fun testRfc4648Section10EncodeDecode() {
        testEncodeDecode("")
        testEncodeDecode("f")
        testEncodeDecode("fo")
        testEncodeDecode("foo")
        testEncodeDecode("foob")
        testEncodeDecode("fooba")
        testEncodeDecode("foobar")
    }

    private fun testEncodeDecode(plainText: String) {
        val encodedText = encodeBase64String(
            getBytesUtf8(plainText)
        )
        val decodedText = newStringUsAscii(
            decodeBase64(encodedText)
        )
        Assert.assertEquals(plainText, decodedText)
    }

    @Test
    fun testSingletons() {
        Assert.assertEquals(
            "AA==",
            String(encodeBase64(byteArrayOf(0.toByte()))!!)
        )
        Assert.assertEquals(
            "AQ==",
            String(encodeBase64(byteArrayOf(1.toByte()))!!)
        )
        Assert.assertEquals(
            "Ag==",
            String(encodeBase64(byteArrayOf(2.toByte()))!!)
        )
        Assert.assertEquals(
            "Aw==",
            String(encodeBase64(byteArrayOf(3.toByte()))!!)
        )
        Assert.assertEquals(
            "BA==",
            String(encodeBase64(byteArrayOf(4.toByte()))!!)
        )
        Assert.assertEquals(
            "BQ==",
            String(encodeBase64(byteArrayOf(5.toByte()))!!)
        )
        Assert.assertEquals(
            "Bg==",
            String(encodeBase64(byteArrayOf(6.toByte()))!!)
        )
        Assert.assertEquals(
            "Bw==",
            String(encodeBase64(byteArrayOf(7.toByte()))!!)
        )
        Assert.assertEquals(
            "CA==",
            String(encodeBase64(byteArrayOf(8.toByte()))!!)
        )
        Assert.assertEquals(
            "CQ==",
            String(encodeBase64(byteArrayOf(9.toByte()))!!)
        )
        Assert.assertEquals(
            "Cg==",
            String(encodeBase64(byteArrayOf(10.toByte()))!!)
        )
        Assert.assertEquals(
            "Cw==",
            String(encodeBase64(byteArrayOf(11.toByte()))!!)
        )
        Assert.assertEquals(
            "DA==",
            String(encodeBase64(byteArrayOf(12.toByte()))!!)
        )
        Assert.assertEquals(
            "DQ==",
            String(encodeBase64(byteArrayOf(13.toByte()))!!)
        )
        Assert.assertEquals(
            "Dg==",
            String(encodeBase64(byteArrayOf(14.toByte()))!!)
        )
        Assert.assertEquals(
            "Dw==",
            String(encodeBase64(byteArrayOf(15.toByte()))!!)
        )
        Assert.assertEquals(
            "EA==",
            String(encodeBase64(byteArrayOf(16.toByte()))!!)
        )
        Assert.assertEquals(
            "EQ==",
            String(encodeBase64(byteArrayOf(17.toByte()))!!)
        )
        Assert.assertEquals(
            "Eg==",
            String(encodeBase64(byteArrayOf(18.toByte()))!!)
        )
        Assert.assertEquals(
            "Ew==",
            String(encodeBase64(byteArrayOf(19.toByte()))!!)
        )
        Assert.assertEquals(
            "FA==",
            String(encodeBase64(byteArrayOf(20.toByte()))!!)
        )
        Assert.assertEquals(
            "FQ==",
            String(encodeBase64(byteArrayOf(21.toByte()))!!)
        )
        Assert.assertEquals(
            "Fg==",
            String(encodeBase64(byteArrayOf(22.toByte()))!!)
        )
        Assert.assertEquals(
            "Fw==",
            String(encodeBase64(byteArrayOf(23.toByte()))!!)
        )
        Assert.assertEquals(
            "GA==",
            String(encodeBase64(byteArrayOf(24.toByte()))!!)
        )
        Assert.assertEquals(
            "GQ==",
            String(encodeBase64(byteArrayOf(25.toByte()))!!)
        )
        Assert.assertEquals(
            "Gg==",
            String(encodeBase64(byteArrayOf(26.toByte()))!!)
        )
        Assert.assertEquals(
            "Gw==",
            String(encodeBase64(byteArrayOf(27.toByte()))!!)
        )
        Assert.assertEquals(
            "HA==",
            String(encodeBase64(byteArrayOf(28.toByte()))!!)
        )
        Assert.assertEquals(
            "HQ==",
            String(encodeBase64(byteArrayOf(29.toByte()))!!)
        )
        Assert.assertEquals(
            "Hg==",
            String(encodeBase64(byteArrayOf(30.toByte()))!!)
        )
        Assert.assertEquals(
            "Hw==",
            String(encodeBase64(byteArrayOf(31.toByte()))!!)
        )
        Assert.assertEquals(
            "IA==",
            String(encodeBase64(byteArrayOf(32.toByte()))!!)
        )
        Assert.assertEquals(
            "IQ==",
            String(encodeBase64(byteArrayOf(33.toByte()))!!)
        )
        Assert.assertEquals(
            "Ig==",
            String(encodeBase64(byteArrayOf(34.toByte()))!!)
        )
        Assert.assertEquals(
            "Iw==",
            String(encodeBase64(byteArrayOf(35.toByte()))!!)
        )
        Assert.assertEquals(
            "JA==",
            String(encodeBase64(byteArrayOf(36.toByte()))!!)
        )
        Assert.assertEquals(
            "JQ==",
            String(encodeBase64(byteArrayOf(37.toByte()))!!)
        )
        Assert.assertEquals(
            "Jg==",
            String(encodeBase64(byteArrayOf(38.toByte()))!!)
        )
        Assert.assertEquals(
            "Jw==",
            String(encodeBase64(byteArrayOf(39.toByte()))!!)
        )
        Assert.assertEquals(
            "KA==",
            String(encodeBase64(byteArrayOf(40.toByte()))!!)
        )
        Assert.assertEquals(
            "KQ==",
            String(encodeBase64(byteArrayOf(41.toByte()))!!)
        )
        Assert.assertEquals(
            "Kg==",
            String(encodeBase64(byteArrayOf(42.toByte()))!!)
        )
        Assert.assertEquals(
            "Kw==",
            String(encodeBase64(byteArrayOf(43.toByte()))!!)
        )
        Assert.assertEquals(
            "LA==",
            String(encodeBase64(byteArrayOf(44.toByte()))!!)
        )
        Assert.assertEquals(
            "LQ==",
            String(encodeBase64(byteArrayOf(45.toByte()))!!)
        )
        Assert.assertEquals(
            "Lg==",
            String(encodeBase64(byteArrayOf(46.toByte()))!!)
        )
        Assert.assertEquals(
            "Lw==",
            String(encodeBase64(byteArrayOf(47.toByte()))!!)
        )
        Assert.assertEquals(
            "MA==",
            String(encodeBase64(byteArrayOf(48.toByte()))!!)
        )
        Assert.assertEquals(
            "MQ==",
            String(encodeBase64(byteArrayOf(49.toByte()))!!)
        )
        Assert.assertEquals(
            "Mg==",
            String(encodeBase64(byteArrayOf(50.toByte()))!!)
        )
        Assert.assertEquals(
            "Mw==",
            String(encodeBase64(byteArrayOf(51.toByte()))!!)
        )
        Assert.assertEquals(
            "NA==",
            String(encodeBase64(byteArrayOf(52.toByte()))!!)
        )
        Assert.assertEquals(
            "NQ==",
            String(encodeBase64(byteArrayOf(53.toByte()))!!)
        )
        Assert.assertEquals(
            "Ng==",
            String(encodeBase64(byteArrayOf(54.toByte()))!!)
        )
        Assert.assertEquals(
            "Nw==",
            String(encodeBase64(byteArrayOf(55.toByte()))!!)
        )
        Assert.assertEquals(
            "OA==",
            String(encodeBase64(byteArrayOf(56.toByte()))!!)
        )
        Assert.assertEquals(
            "OQ==",
            String(encodeBase64(byteArrayOf(57.toByte()))!!)
        )
        Assert.assertEquals(
            "Og==",
            String(encodeBase64(byteArrayOf(58.toByte()))!!)
        )
        Assert.assertEquals(
            "Ow==",
            String(encodeBase64(byteArrayOf(59.toByte()))!!)
        )
        Assert.assertEquals(
            "PA==",
            String(encodeBase64(byteArrayOf(60.toByte()))!!)
        )
        Assert.assertEquals(
            "PQ==",
            String(encodeBase64(byteArrayOf(61.toByte()))!!)
        )
        Assert.assertEquals(
            "Pg==",
            String(encodeBase64(byteArrayOf(62.toByte()))!!)
        )
        Assert.assertEquals(
            "Pw==",
            String(encodeBase64(byteArrayOf(63.toByte()))!!)
        )
        Assert.assertEquals(
            "QA==",
            String(encodeBase64(byteArrayOf(64.toByte()))!!)
        )
        Assert.assertEquals(
            "QQ==",
            String(encodeBase64(byteArrayOf(65.toByte()))!!)
        )
        Assert.assertEquals(
            "Qg==",
            String(encodeBase64(byteArrayOf(66.toByte()))!!)
        )
        Assert.assertEquals(
            "Qw==",
            String(encodeBase64(byteArrayOf(67.toByte()))!!)
        )
        Assert.assertEquals(
            "RA==",
            String(encodeBase64(byteArrayOf(68.toByte()))!!)
        )
        Assert.assertEquals(
            "RQ==",
            String(encodeBase64(byteArrayOf(69.toByte()))!!)
        )
        Assert.assertEquals(
            "Rg==",
            String(encodeBase64(byteArrayOf(70.toByte()))!!)
        )
        Assert.assertEquals(
            "Rw==",
            String(encodeBase64(byteArrayOf(71.toByte()))!!)
        )
        Assert.assertEquals(
            "SA==",
            String(encodeBase64(byteArrayOf(72.toByte()))!!)
        )
        Assert.assertEquals(
            "SQ==",
            String(encodeBase64(byteArrayOf(73.toByte()))!!)
        )
        Assert.assertEquals(
            "Sg==",
            String(encodeBase64(byteArrayOf(74.toByte()))!!)
        )
        Assert.assertEquals(
            "Sw==",
            String(encodeBase64(byteArrayOf(75.toByte()))!!)
        )
        Assert.assertEquals(
            "TA==",
            String(encodeBase64(byteArrayOf(76.toByte()))!!)
        )
        Assert.assertEquals(
            "TQ==",
            String(encodeBase64(byteArrayOf(77.toByte()))!!)
        )
        Assert.assertEquals(
            "Tg==",
            String(encodeBase64(byteArrayOf(78.toByte()))!!)
        )
        Assert.assertEquals(
            "Tw==",
            String(encodeBase64(byteArrayOf(79.toByte()))!!)
        )
        Assert.assertEquals(
            "UA==",
            String(encodeBase64(byteArrayOf(80.toByte()))!!)
        )
        Assert.assertEquals(
            "UQ==",
            String(encodeBase64(byteArrayOf(81.toByte()))!!)
        )
        Assert.assertEquals(
            "Ug==",
            String(encodeBase64(byteArrayOf(82.toByte()))!!)
        )
        Assert.assertEquals(
            "Uw==",
            String(encodeBase64(byteArrayOf(83.toByte()))!!)
        )
        Assert.assertEquals(
            "VA==",
            String(encodeBase64(byteArrayOf(84.toByte()))!!)
        )
        Assert.assertEquals(
            "VQ==",
            String(encodeBase64(byteArrayOf(85.toByte()))!!)
        )
        Assert.assertEquals(
            "Vg==",
            String(encodeBase64(byteArrayOf(86.toByte()))!!)
        )
        Assert.assertEquals(
            "Vw==",
            String(encodeBase64(byteArrayOf(87.toByte()))!!)
        )
        Assert.assertEquals(
            "WA==",
            String(encodeBase64(byteArrayOf(88.toByte()))!!)
        )
        Assert.assertEquals(
            "WQ==",
            String(encodeBase64(byteArrayOf(89.toByte()))!!)
        )
        Assert.assertEquals(
            "Wg==",
            String(encodeBase64(byteArrayOf(90.toByte()))!!)
        )
        Assert.assertEquals(
            "Ww==",
            String(encodeBase64(byteArrayOf(91.toByte()))!!)
        )
        Assert.assertEquals(
            "XA==",
            String(encodeBase64(byteArrayOf(92.toByte()))!!)
        )
        Assert.assertEquals(
            "XQ==",
            String(encodeBase64(byteArrayOf(93.toByte()))!!)
        )
        Assert.assertEquals(
            "Xg==",
            String(encodeBase64(byteArrayOf(94.toByte()))!!)
        )
        Assert.assertEquals(
            "Xw==",
            String(encodeBase64(byteArrayOf(95.toByte()))!!)
        )
        Assert.assertEquals(
            "YA==",
            String(encodeBase64(byteArrayOf(96.toByte()))!!)
        )
        Assert.assertEquals(
            "YQ==",
            String(encodeBase64(byteArrayOf(97.toByte()))!!)
        )
        Assert.assertEquals(
            "Yg==",
            String(encodeBase64(byteArrayOf(98.toByte()))!!)
        )
        Assert.assertEquals(
            "Yw==",
            String(encodeBase64(byteArrayOf(99.toByte()))!!)
        )
        Assert.assertEquals(
            "ZA==",
            String(encodeBase64(byteArrayOf(100.toByte()))!!)
        )
        Assert.assertEquals(
            "ZQ==",
            String(encodeBase64(byteArrayOf(101.toByte()))!!)
        )
        Assert.assertEquals(
            "Zg==",
            String(encodeBase64(byteArrayOf(102.toByte()))!!)
        )
        Assert.assertEquals(
            "Zw==",
            String(encodeBase64(byteArrayOf(103.toByte()))!!)
        )
        Assert.assertEquals(
            "aA==",
            String(encodeBase64(byteArrayOf(104.toByte()))!!)
        )
        for (i in -128..127) {
            val test = byteArrayOf(i.toByte())
            Assert.assertTrue(
                Arrays.equals(
                    test,
                    decodeBase64(
                        encodeBase64(test)
                    )
                )
            )
        }
    }

    @Test
    fun testSingletonsChunked() {
        Assert.assertEquals(
            "AA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(0.toByte()))!!)
        )
        Assert.assertEquals(
            "AQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(1.toByte()))!!)
        )
        Assert.assertEquals(
            "Ag==\r\n",
            String(encodeBase64Chunked(byteArrayOf(2.toByte()))!!)
        )
        Assert.assertEquals(
            "Aw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(3.toByte()))!!)
        )
        Assert.assertEquals(
            "BA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(4.toByte()))!!)
        )
        Assert.assertEquals(
            "BQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(5.toByte()))!!)
        )
        Assert.assertEquals(
            "Bg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(6.toByte()))!!)
        )
        Assert.assertEquals(
            "Bw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(7.toByte()))!!)
        )
        Assert.assertEquals(
            "CA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(8.toByte()))!!)
        )
        Assert.assertEquals(
            "CQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(9.toByte()))!!)
        )
        Assert.assertEquals(
            "Cg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(10.toByte()))!!)
        )
        Assert.assertEquals(
            "Cw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(11.toByte()))!!)
        )
        Assert.assertEquals(
            "DA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(12.toByte()))!!)
        )
        Assert.assertEquals(
            "DQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(13.toByte()))!!)
        )
        Assert.assertEquals(
            "Dg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(14.toByte()))!!)
        )
        Assert.assertEquals(
            "Dw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(15.toByte()))!!)
        )
        Assert.assertEquals(
            "EA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(16.toByte()))!!)
        )
        Assert.assertEquals(
            "EQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(17.toByte()))!!)
        )
        Assert.assertEquals(
            "Eg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(18.toByte()))!!)
        )
        Assert.assertEquals(
            "Ew==\r\n",
            String(encodeBase64Chunked(byteArrayOf(19.toByte()))!!)
        )
        Assert.assertEquals(
            "FA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(20.toByte()))!!)
        )
        Assert.assertEquals(
            "FQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(21.toByte()))!!)
        )
        Assert.assertEquals(
            "Fg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(22.toByte()))!!)
        )
        Assert.assertEquals(
            "Fw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(23.toByte()))!!)
        )
        Assert.assertEquals(
            "GA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(24.toByte()))!!)
        )
        Assert.assertEquals(
            "GQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(25.toByte()))!!)
        )
        Assert.assertEquals(
            "Gg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(26.toByte()))!!)
        )
        Assert.assertEquals(
            "Gw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(27.toByte()))!!)
        )
        Assert.assertEquals(
            "HA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(28.toByte()))!!)
        )
        Assert.assertEquals(
            "HQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(29.toByte()))!!)
        )
        Assert.assertEquals(
            "Hg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(30.toByte()))!!)
        )
        Assert.assertEquals(
            "Hw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(31.toByte()))!!)
        )
        Assert.assertEquals(
            "IA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(32.toByte()))!!)
        )
        Assert.assertEquals(
            "IQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(33.toByte()))!!)
        )
        Assert.assertEquals(
            "Ig==\r\n",
            String(encodeBase64Chunked(byteArrayOf(34.toByte()))!!)
        )
        Assert.assertEquals(
            "Iw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(35.toByte()))!!)
        )
        Assert.assertEquals(
            "JA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(36.toByte()))!!)
        )
        Assert.assertEquals(
            "JQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(37.toByte()))!!)
        )
        Assert.assertEquals(
            "Jg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(38.toByte()))!!)
        )
        Assert.assertEquals(
            "Jw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(39.toByte()))!!)
        )
        Assert.assertEquals(
            "KA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(40.toByte()))!!)
        )
        Assert.assertEquals(
            "KQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(41.toByte()))!!)
        )
        Assert.assertEquals(
            "Kg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(42.toByte()))!!)
        )
        Assert.assertEquals(
            "Kw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(43.toByte()))!!)
        )
        Assert.assertEquals(
            "LA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(44.toByte()))!!)
        )
        Assert.assertEquals(
            "LQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(45.toByte()))!!)
        )
        Assert.assertEquals(
            "Lg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(46.toByte()))!!)
        )
        Assert.assertEquals(
            "Lw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(47.toByte()))!!)
        )
        Assert.assertEquals(
            "MA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(48.toByte()))!!)
        )
        Assert.assertEquals(
            "MQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(49.toByte()))!!)
        )
        Assert.assertEquals(
            "Mg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(50.toByte()))!!)
        )
        Assert.assertEquals(
            "Mw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(51.toByte()))!!)
        )
        Assert.assertEquals(
            "NA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(52.toByte()))!!)
        )
        Assert.assertEquals(
            "NQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(53.toByte()))!!)
        )
        Assert.assertEquals(
            "Ng==\r\n",
            String(encodeBase64Chunked(byteArrayOf(54.toByte()))!!)
        )
        Assert.assertEquals(
            "Nw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(55.toByte()))!!)
        )
        Assert.assertEquals(
            "OA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(56.toByte()))!!)
        )
        Assert.assertEquals(
            "OQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(57.toByte()))!!)
        )
        Assert.assertEquals(
            "Og==\r\n",
            String(encodeBase64Chunked(byteArrayOf(58.toByte()))!!)
        )
        Assert.assertEquals(
            "Ow==\r\n",
            String(encodeBase64Chunked(byteArrayOf(59.toByte()))!!)
        )
        Assert.assertEquals(
            "PA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(60.toByte()))!!)
        )
        Assert.assertEquals(
            "PQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(61.toByte()))!!)
        )
        Assert.assertEquals(
            "Pg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(62.toByte()))!!)
        )
        Assert.assertEquals(
            "Pw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(63.toByte()))!!)
        )
        Assert.assertEquals(
            "QA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(64.toByte()))!!)
        )
        Assert.assertEquals(
            "QQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(65.toByte()))!!)
        )
        Assert.assertEquals(
            "Qg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(66.toByte()))!!)
        )
        Assert.assertEquals(
            "Qw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(67.toByte()))!!)
        )
        Assert.assertEquals(
            "RA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(68.toByte()))!!)
        )
        Assert.assertEquals(
            "RQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(69.toByte()))!!)
        )
        Assert.assertEquals(
            "Rg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(70.toByte()))!!)
        )
        Assert.assertEquals(
            "Rw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(71.toByte()))!!)
        )
        Assert.assertEquals(
            "SA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(72.toByte()))!!)
        )
        Assert.assertEquals(
            "SQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(73.toByte()))!!)
        )
        Assert.assertEquals(
            "Sg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(74.toByte()))!!)
        )
        Assert.assertEquals(
            "Sw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(75.toByte()))!!)
        )
        Assert.assertEquals(
            "TA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(76.toByte()))!!)
        )
        Assert.assertEquals(
            "TQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(77.toByte()))!!)
        )
        Assert.assertEquals(
            "Tg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(78.toByte()))!!)
        )
        Assert.assertEquals(
            "Tw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(79.toByte()))!!)
        )
        Assert.assertEquals(
            "UA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(80.toByte()))!!)
        )
        Assert.assertEquals(
            "UQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(81.toByte()))!!)
        )
        Assert.assertEquals(
            "Ug==\r\n",
            String(encodeBase64Chunked(byteArrayOf(82.toByte()))!!)
        )
        Assert.assertEquals(
            "Uw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(83.toByte()))!!)
        )
        Assert.assertEquals(
            "VA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(84.toByte()))!!)
        )
        Assert.assertEquals(
            "VQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(85.toByte()))!!)
        )
        Assert.assertEquals(
            "Vg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(86.toByte()))!!)
        )
        Assert.assertEquals(
            "Vw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(87.toByte()))!!)
        )
        Assert.assertEquals(
            "WA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(88.toByte()))!!)
        )
        Assert.assertEquals(
            "WQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(89.toByte()))!!)
        )
        Assert.assertEquals(
            "Wg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(90.toByte()))!!)
        )
        Assert.assertEquals(
            "Ww==\r\n",
            String(encodeBase64Chunked(byteArrayOf(91.toByte()))!!)
        )
        Assert.assertEquals(
            "XA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(92.toByte()))!!)
        )
        Assert.assertEquals(
            "XQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(93.toByte()))!!)
        )
        Assert.assertEquals(
            "Xg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(94.toByte()))!!)
        )
        Assert.assertEquals(
            "Xw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(95.toByte()))!!)
        )
        Assert.assertEquals(
            "YA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(96.toByte()))!!)
        )
        Assert.assertEquals(
            "YQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(97.toByte()))!!)
        )
        Assert.assertEquals(
            "Yg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(98.toByte()))!!)
        )
        Assert.assertEquals(
            "Yw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(99.toByte()))!!)
        )
        Assert.assertEquals(
            "ZA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(100.toByte()))!!)
        )
        Assert.assertEquals(
            "ZQ==\r\n",
            String(encodeBase64Chunked(byteArrayOf(101.toByte()))!!)
        )
        Assert.assertEquals(
            "Zg==\r\n",
            String(encodeBase64Chunked(byteArrayOf(102.toByte()))!!)
        )
        Assert.assertEquals(
            "Zw==\r\n",
            String(encodeBase64Chunked(byteArrayOf(103.toByte()))!!)
        )
        Assert.assertEquals(
            "aA==\r\n",
            String(encodeBase64Chunked(byteArrayOf(104.toByte()))!!)
        )
    }

    @Test
    fun testTriplets() {
        Assert.assertEquals(
            "AAAA",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        0.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAB",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        1.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAC",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        2.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAD",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        3.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAE",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        4.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAF",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        5.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAG",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        6.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAH",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        7.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAI",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        8.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAJ",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        9.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAK",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        10.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAL",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        11.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAM",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        12.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAN",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        13.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAO",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        14.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAP",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        15.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAQ",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        16.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAR",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        17.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAS",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        18.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAT",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        19.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAU",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        20.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAV",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        21.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAW",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        22.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAX",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        23.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAY",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        24.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAZ",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        25.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAa",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        26.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAb",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        27.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAc",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        28.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAd",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        29.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAe",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        30.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAf",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        31.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAg",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        32.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAh",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        33.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAi",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        34.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAj",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        35.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAk",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        36.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAl",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        37.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAm",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        38.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAn",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        39.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAo",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        40.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAp",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        41.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAq",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        42.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAr",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        43.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAs",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        44.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAt",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        45.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAu",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        46.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAv",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        47.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAw",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        48.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAx",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        49.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAy",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        50.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAz",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        51.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA0",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        52.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA1",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        53.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA2",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        54.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA3",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        55.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA4",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        56.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA5",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        57.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA6",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        58.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA7",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        59.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA8",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        60.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA9",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        61.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA+",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        62.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA/",
            String(
                encodeBase64(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        63.toByte()
                    )
                )!!
            )
        )
    }

    @Test
    fun testTripletsChunked() {
        Assert.assertEquals(
            "AAAA\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        0.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAB\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        1.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAC\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        2.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAD\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        3.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAE\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        4.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAF\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        5.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAG\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        6.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAH\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        7.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAI\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        8.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAJ\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        9.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAK\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        10.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAL\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        11.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAM\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        12.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAN\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        13.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAO\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        14.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAP\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        15.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAQ\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        16.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAR\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        17.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAS\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        18.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAT\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        19.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAU\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        20.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAV\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        21.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAW\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        22.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAX\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        23.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAY\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        24.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAZ\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        25.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAa\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        26.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAb\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        27.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAc\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        28.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAd\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        29.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAe\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        30.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAf\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        31.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAg\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        32.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAh\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        33.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAi\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        34.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAj\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        35.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAk\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        36.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAl\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        37.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAm\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        38.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAn\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        39.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAo\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        40.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAp\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        41.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAq\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        42.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAr\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        43.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAs\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        44.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAt\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        45.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAu\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        46.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAv\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        47.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAw\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        48.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAx\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        49.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAy\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        50.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAAz\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        51.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA0\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        52.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA1\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        53.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA2\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        54.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA3\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        55.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA4\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        56.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA5\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        57.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA6\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        58.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA7\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        59.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA8\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        60.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA9\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        61.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA+\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        62.toByte()
                    )
                )!!
            )
        )
        Assert.assertEquals(
            "AAA/\r\n",
            String(
                encodeBase64Chunked(
                    byteArrayOf(
                        0.toByte(),
                        0.toByte(),
                        63.toByte()
                    )
                )!!
            )
        )
    }

    /**
     * Tests url-safe Base64 against random data, sizes 0 to 150.
     */
    @Test
    fun testUrlSafe() { // test random data of sizes 0 thru 150
        for (i in 0..150) {
            val randomData = Base64TestData.randomData(i, true)
            val encoded = randomData[1]
            val decoded = randomData[0]
            val result = decodeBase64(encoded)
            Assert.assertTrue("url-safe i=$i", Arrays.equals(decoded, result))
            Assert.assertFalse(
                "url-safe i=$i no '='",
                Base64TestData.bytesContain(encoded, '='.toByte())
            )
            Assert.assertFalse(
                "url-safe i=$i no '\\'",
                Base64TestData.bytesContain(encoded, '\\'.toByte())
            )
            Assert.assertFalse(
                "url-safe i=$i no '+'",
                Base64TestData.bytesContain(encoded, '+'.toByte())
            )
        }
    }

    /**
     * Base64 encoding of UUID's is a common use-case, especially in URL-SAFE
     * mode. This test case ends up being the "URL-SAFE" JUnit's.
     *
     * @throws DecoderException if Hex.decode() fails - a serious problem since Hex comes
     * from our own commons-codec!
     */
    @Test
    @Throws(DecoderException::class)
    fun testUUID() { // The 4 UUID's below contains mixtures of + and / to help us test the
// URL-SAFE encoding mode.
        val ids = arrayOfNulls<ByteArray>(4)
        // ids[0] was chosen so that it encodes with at least one +.
        ids[0] = decodeHex("94ed8d0319e4493399560fb67404d370")
        // ids[1] was chosen so that it encodes with both / and +.
        ids[1] = decodeHex("2bf7cc2701fe4397b49ebeed5acc7090")
        // ids[2] was chosen so that it encodes with at least one /.
        ids[2] = decodeHex("64be154b6ffa40258d1a01288e7c31ca")
        // ids[3] was chosen so that it encodes with both / and +, with /
// right at the beginning.
        ids[3] = decodeHex("ff7f8fc01cdb471a8c8b5a9306183fe8")
        val standard = arrayOfNulls<ByteArray>(4)
        standard[0] =
            getBytesUtf8("lO2NAxnkSTOZVg+2dATTcA==")
        standard[1] =
            getBytesUtf8("K/fMJwH+Q5e0nr7tWsxwkA==")
        standard[2] =
            getBytesUtf8("ZL4VS2/6QCWNGgEojnwxyg==")
        standard[3] =
            getBytesUtf8("/3+PwBzbRxqMi1qTBhg/6A==")
        val urlSafe1 = arrayOfNulls<ByteArray>(4)
        // regular padding (two '==' signs).
        urlSafe1[0] =
            getBytesUtf8("lO2NAxnkSTOZVg-2dATTcA==")
        urlSafe1[1] =
            getBytesUtf8("K_fMJwH-Q5e0nr7tWsxwkA==")
        urlSafe1[2] =
            getBytesUtf8("ZL4VS2_6QCWNGgEojnwxyg==")
        urlSafe1[3] =
            getBytesUtf8("_3-PwBzbRxqMi1qTBhg_6A==")
        val urlSafe2 = arrayOfNulls<ByteArray>(4)
        // single padding (only one '=' sign).
        urlSafe2[0] =
            getBytesUtf8("lO2NAxnkSTOZVg-2dATTcA=")
        urlSafe2[1] =
            getBytesUtf8("K_fMJwH-Q5e0nr7tWsxwkA=")
        urlSafe2[2] =
            getBytesUtf8("ZL4VS2_6QCWNGgEojnwxyg=")
        urlSafe2[3] =
            getBytesUtf8("_3-PwBzbRxqMi1qTBhg_6A=")
        val urlSafe3 = arrayOfNulls<ByteArray>(4)
        // no padding (no '=' signs).
        urlSafe3[0] =
            getBytesUtf8("lO2NAxnkSTOZVg-2dATTcA")
        urlSafe3[1] =
            getBytesUtf8("K_fMJwH-Q5e0nr7tWsxwkA")
        urlSafe3[2] =
            getBytesUtf8("ZL4VS2_6QCWNGgEojnwxyg")
        urlSafe3[3] =
            getBytesUtf8("_3-PwBzbRxqMi1qTBhg_6A")
        for (i in 0..3) {
            val encodedStandard =
                encodeBase64(ids[i])
            val encodedUrlSafe =
                encodeBase64URLSafe(ids[i])
            val decodedStandard =
                decodeBase64(standard[i])
            val decodedUrlSafe1 =
                decodeBase64(urlSafe1[i])
            val decodedUrlSafe2 =
                decodeBase64(urlSafe2[i])
            val decodedUrlSafe3 =
                decodeBase64(urlSafe3[i])
            // Very important debugging output should anyone
// ever need to delve closely into this stuff.
//            {
//                System.out.println("reference: [" + Hex.encodeHexString(ids[i]) + "]");
//                System.out.println("standard:  [" + Hex.encodeHexString(decodedStandard) + "] From: ["
//                        + StringUtils.newStringUtf8(standard[i]) + "]");
//                System.out.println("safe1:     [" + Hex.encodeHexString(decodedUrlSafe1) + "] From: ["
//                        + StringUtils.newStringUtf8(urlSafe1[i]) + "]");
//                System.out.println("safe2:     [" + Hex.encodeHexString(decodedUrlSafe2) + "] From: ["
//                        + StringUtils.newStringUtf8(urlSafe2[i]) + "]");
//                System.out.println("safe3:     [" + Hex.encodeHexString(decodedUrlSafe3) + "] From: ["
//                        + StringUtils.newStringUtf8(urlSafe3[i]) + "]");
//            }
            Assert.assertTrue(
                "standard encode uuid",
                Arrays.equals(encodedStandard, standard[i])
            )
            Assert.assertTrue(
                "url-safe encode uuid",
                Arrays.equals(encodedUrlSafe, urlSafe3[i])
            )
            Assert.assertTrue(
                "standard decode uuid",
                Arrays.equals(decodedStandard, ids[i])
            )
            Assert.assertTrue(
                "url-safe1 decode uuid",
                Arrays.equals(decodedUrlSafe1, ids[i])
            )
            Assert.assertTrue(
                "url-safe2 decode uuid",
                Arrays.equals(decodedUrlSafe2, ids[i])
            )
            Assert.assertTrue(
                "url-safe3 decode uuid",
                Arrays.equals(decodedUrlSafe3, ids[i])
            )
        }
    }

    @Test
    @Throws(DecoderException::class)
    fun testByteToStringVariations() {
        val base64 = Base64(0)
        val b1 = getBytesUtf8("Hello World")
        val b2 = ByteArray(0)
        val b3: ByteArray? = null
        val b4 = decodeHex("2bf7cc2701fe4397b49ebeed5acc7090") // for
        // url-safe
// tests
        Assert.assertEquals(
            "byteToString Hello World",
            "SGVsbG8gV29ybGQ=",
            base64.encodeToString(b1)
        )
        Assert.assertEquals(
            "byteToString static Hello World",
            "SGVsbG8gV29ybGQ=",
            encodeBase64String(b1)
        )
        Assert.assertEquals("byteToString \"\"", "", base64.encodeToString(b2))
        Assert.assertEquals(
            "byteToString static \"\"",
            "",
            encodeBase64String(b2)
        )
        Assert.assertEquals("byteToString null", null, base64.encodeToString(b3))
        Assert.assertEquals(
            "byteToString static null",
            null,
            encodeBase64String(b3)
        )
        Assert.assertEquals(
            "byteToString UUID",
            "K/fMJwH+Q5e0nr7tWsxwkA==",
            base64.encodeToString(b4)
        )
        Assert.assertEquals(
            "byteToString static UUID",
            "K/fMJwH+Q5e0nr7tWsxwkA==",
            encodeBase64String(b4)
        )
        Assert.assertEquals(
            "byteToString static-url-safe UUID", "K_fMJwH-Q5e0nr7tWsxwkA",
            encodeBase64URLSafeString(b4)
        )
    }

    @Test
    @Throws(DecoderException::class)
    fun testStringToByteVariations() {
        val base64 = Base64()
        val s1 = "SGVsbG8gV29ybGQ=\r\n"
        val s2 = ""
        val s3: String? = null
        val s4a = "K/fMJwH+Q5e0nr7tWsxwkA==\r\n"
        val s4b = "K_fMJwH-Q5e0nr7tWsxwkA"
        val b4 = decodeHex("2bf7cc2701fe4397b49ebeed5acc7090") // for
        // url-safe
// tests
        Assert.assertEquals(
            "StringToByte Hello World",
            "Hello World",
            newStringUtf8(base64.decode(s1))
        )
        Assert.assertEquals(
            "StringToByte Hello World", "Hello World",
            newStringUtf8(base64.decode(s1 as Any) as ByteArray?)
        )
        Assert.assertEquals(
            "StringToByte static Hello World", "Hello World",
            newStringUtf8(
                decodeBase64(
                    s1
                )
            )
        )
        Assert.assertEquals(
            "StringToByte \"\"",
            "",
            newStringUtf8(base64.decode(s2))
        )
        Assert.assertEquals(
            "StringToByte static \"\"",
            "",
            newStringUtf8(
                decodeBase64(s2)
            )
        )
        Assert.assertEquals(
            "StringToByte null",
            null,
            newStringUtf8(base64.decode(s3))
        )
        Assert.assertEquals(
            "StringToByte static null",
            null,
            newStringUtf8(
                decodeBase64(s3)
            )
        )
        Assert.assertTrue(
            "StringToByte UUID",
            Arrays.equals(b4, base64.decode(s4b))
        )
        Assert.assertTrue(
            "StringToByte static UUID",
            Arrays.equals(b4, decodeBase64(s4a))
        )
        Assert.assertTrue(
            "StringToByte static-url-safe UUID",
            Arrays.equals(b4, decodeBase64(s4b))
        )
    }

    private fun toString(data: ByteArray?): String {
        val buf = StringBuilder()
        for (i in data!!.indices) {
            buf.append(data[i])
            if (i != data.size - 1) {
                buf.append(",")
            }
        }
        return buf.toString()
    }

    companion object {
        private val CHARSET_UTF8 = StandardCharsets.UTF_8
        private val BASE64_IMPOSSIBLE_CASES = arrayOf(
            "ZE==",
            "ZmC=",
            "Zm9vYE==",
            "Zm9vYmC=",
            "AB"
        )
        /**
         * Copy of the standard base-64 encoding table. Used to test decoding the final
         * character of encoded bytes.
         */
        private val STANDARD_ENCODE_TABLE = byteArrayOf(
            'A'.toByte(),
            'B'.toByte(),
            'C'.toByte(),
            'D'.toByte(),
            'E'.toByte(),
            'F'.toByte(),
            'G'.toByte(),
            'H'.toByte(),
            'I'.toByte(),
            'J'.toByte(),
            'K'.toByte(),
            'L'.toByte(),
            'M'.toByte(),
            'N'.toByte(),
            'O'.toByte(),
            'P'.toByte(),
            'Q'.toByte(),
            'R'.toByte(),
            'S'.toByte(),
            'T'.toByte(),
            'U'.toByte(),
            'V'.toByte(),
            'W'.toByte(),
            'X'.toByte(),
            'Y'.toByte(),
            'Z'.toByte(),
            'a'.toByte(),
            'b'.toByte(),
            'c'.toByte(),
            'd'.toByte(),
            'e'.toByte(),
            'f'.toByte(),
            'g'.toByte(),
            'h'.toByte(),
            'i'.toByte(),
            'j'.toByte(),
            'k'.toByte(),
            'l'.toByte(),
            'm'.toByte(),
            'n'.toByte(),
            'o'.toByte(),
            'p'.toByte(),
            'q'.toByte(),
            'r'.toByte(),
            's'.toByte(),
            't'.toByte(),
            'u'.toByte(),
            'v'.toByte(),
            'w'.toByte(),
            'x'.toByte(),
            'y'.toByte(),
            'z'.toByte(),
            '0'.toByte(),
            '1'.toByte(),
            '2'.toByte(),
            '3'.toByte(),
            '4'.toByte(),
            '5'.toByte(),
            '6'.toByte(),
            '7'.toByte(),
            '8'.toByte(),
            '9'.toByte(),
            '+'.toByte(),
            '/'
                .toByte()
        )

        /**
         * Test base 64 decoding of the final trailing bits. Trailing encoded bytes
         * cannot fit exactly into 6-bit characters so the last character has a limited
         * alphabet where the final bits are zero. This asserts that illegal final
         * characters throw an exception when decoding.
         *
         * @param nbits the number of trailing bits (must be a factor of 6 and `<24`)
         */
        private fun assertBase64DecodingOfTrailingBits(nbits: Int) {
            val codec = Base64()
            // Create the encoded bytes. The first characters must be valid so fill with 'zero'.
            val encoded = ByteArray(nbits / 6)
            Arrays.fill(encoded, STANDARD_ENCODE_TABLE[0])
            // Compute how many bits would be discarded from 8-bit bytes
            val discard = nbits % 8
            val emptyBitsMask = (1 shl discard) - 1
            // Enumerate all 64 possible final characters in the last position
            val last = encoded.size - 1
            for (i in 0..63) {
                encoded[last] = STANDARD_ENCODE_TABLE[i]
                // If the lower bits are set we expect an exception. This is not a valid
// final character.
                if (i and emptyBitsMask != 0) {
                    try {
                        codec.decode(encoded)
                        Assert.fail("Final base-64 digit should not be allowed")
                    } catch (ex: IllegalArgumentException) { // expected
                    }
                } else { // Otherwise this should decode
                    val decoded = codec.decode(encoded)
                    // Compute the bits that were encoded. This should match the final decoded byte.
                    val bitsEncoded = i shr discard
                    Assert.assertEquals(
                        "Invalid decoding of last character",
                        bitsEncoded.toLong(),
                        decoded!![decoded.size - 1]
                    )
                }
            }
        }
    }
}
