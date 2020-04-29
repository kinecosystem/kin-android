/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.kin.stellarfork.codec

import org.junit.Assert
import org.junit.Test
import java.nio.charset.StandardCharsets

class Base32Test {
    companion object {
        private val CHARSET_UTF8 = StandardCharsets.UTF_8
        private val BASE32_TEST_CASES =
            arrayOf(
                arrayOf("", ""),
                arrayOf("f", "MY======"),
                arrayOf("fo", "MZXQ===="),
                arrayOf("foo", "MZXW6==="),
                arrayOf("foob", "MZXW6YQ="),
                arrayOf("fooba", "MZXW6YTB"),
                arrayOf("foobar", "MZXW6YTBOI======")
            )
        private val BASE32_IMPOSSIBLE_CASES = arrayOf(
            "MC======",
            "MZXE====",
            "MZXWB===",
            "MZXW6YB=",
            "MZXW6YTBOC======",
            "AB======"
        )
        private val BASE32_IMPOSSIBLE_CASES_CHUNKED =
            arrayOf(
                "M2======\r\n",
                "MZX0====\r\n",
                "MZXW0===\r\n",
                "MZXW6Y2=\r\n",
                "MZXW6YTBO2======\r\n"
            )
        private val BASE32HEX_IMPOSSIBLE_CASES =
            arrayOf(
                "C2======",
                "CPN4====",
                "CPNM1===",
                "CPNMUO1=",
                "CPNMUOJ1E2======"
            )
        /**
         * Copy of the standard base-32 encoding table. Used to test decoding the final
         * character of encoded bytes.
         */
        private val ENCODE_TABLE = byteArrayOf(
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
            '2'.toByte(),
            '3'.toByte(),
            '4'.toByte(),
            '5'.toByte(),
            '6'.toByte(),
            '7'.toByte()
        )
        private val BASE32_BINARY_TEST_CASES: Array<Array<Any>>
        private val BASE32HEX_TEST_CASES =
            arrayOf(
                arrayOf("", ""),
                arrayOf("f", "CO======"),
                arrayOf("fo", "CPNG===="),
                arrayOf("foo", "CPNMU==="),
                arrayOf("foob", "CPNMUOG="),
                arrayOf("fooba", "CPNMUOJ1"),
                arrayOf("foobar", "CPNMUOJ1E8======")
            )
        private val BASE32_TEST_CASES_CHUNKED =
            arrayOf(
                arrayOf("", ""),
                arrayOf("f", "MY======\r\n"),
                arrayOf("fo", "MZXQ====\r\n"),
                arrayOf("foo", "MZXW6===\r\n"),
                arrayOf("foob", "MZXW6YQ=\r\n"),
                arrayOf("fooba", "MZXW6YTB\r\n"),
                arrayOf("foobar", "MZXW6YTBOI======\r\n")
            )
        private val BASE32_PAD_TEST_CASES =
            arrayOf(
                arrayOf("", ""),
                arrayOf("f", "MY%%%%%%"),
                arrayOf("fo", "MZXQ%%%%"),
                arrayOf("foo", "MZXW6%%%"),
                arrayOf("foob", "MZXW6YQ%"),
                arrayOf("fooba", "MZXW6YTB"),
                arrayOf("foobar", "MZXW6YTBOI%%%%%%")
            )

        //            { null, "O0o0O0o0" }
//            BASE32_BINARY_TEST_CASES[2][0] = new Hex().decode("739ce739ce");
        init {
            val hex = Hex()
            BASE32_BINARY_TEST_CASES = try {
                arrayOf(
                    arrayOf<Any>(
                        hex.decode("623a01735836e9a126e12fbf95e013ee6892997c")!!,
                        "MI5AC42YG3U2CJXBF67ZLYAT5ZUJFGL4"
                    ), arrayOf<Any>(
                        hex.decode("623a01735836e9a126e12fbf95e013ee6892997c")!!,
                        "mi5ac42yg3u2cjxbf67zlyat5zujfgl4"
                    ), arrayOf<Any>(
                        hex.decode("739ce42108")!!,
                        "OOOOIIII"
                    )
                )
            } catch (de: DecoderException) {
                throw Error(":(", de)
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testBase32Chunked() {
        val codec = Base32(20)
        for (element in BASE32_TEST_CASES_CHUNKED) {
            Assert.assertEquals(
                element[1],
                codec.encodeAsString(element[0].toByteArray(CHARSET_UTF8))
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testBase32HexSamples() {
        val codec = Base32(true)
        for (element in BASE32HEX_TEST_CASES) {
            Assert.assertEquals(
                element[1],
                codec.encodeAsString(element[0].toByteArray(CHARSET_UTF8))
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testBase32HexSamplesReverse() {
        val codec = Base32(true)
        for (element in BASE32HEX_TEST_CASES) {
            Assert.assertEquals(
                element[0],
                String(codec.decode(element[1])!!, CHARSET_UTF8)
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testBase32Samples() {
        val codec = Base32()
        for (element in BASE32_TEST_CASES) {
            Assert.assertEquals(
                element[1],
                codec.encodeAsString(element[0].toByteArray(CHARSET_UTF8))
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun testBase32BinarySamples() {
        val codec = Base32()
        for (element in BASE32_BINARY_TEST_CASES) {
            var expected: String
            expected = if (element.size > 2) {
                element[2] as String
            } else {
                element[1] as String
            }
            Assert.assertEquals(
                expected.toUpperCase(),
                codec.encodeAsString((element[0] as ByteArray))
            )
        }
    }

    @Test
    fun testRandomBytes() {
        for (i in 0..19) {
            val codec = Base32()
            val b = Base32TestData.randomData(codec, i)
            Assert.assertEquals(
                "" + i + " " + codec.lineLength,
                b[1]!!.size.toLong(),
                codec.getEncodedLength(b[0]!!)
            )
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }

    @Test
    fun testRandomBytesChunked() {
        for (i in 0..19) {
            val codec = Base32(10)
            val b = Base32TestData.randomData(codec, i)
            Assert.assertEquals(
                "" + i + " " + codec.lineLength,
                b[1]!!.size.toLong(),
                codec.getEncodedLength(b[0]!!)
            )
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }

    @Test
    fun testRandomBytesHex() {
        for (i in 0..19) {
            val codec = Base32(true)
            val b = Base32TestData.randomData(codec, i)
            Assert.assertEquals(
                "" + i + " " + codec.lineLength,
                b[1]!!.size.toLong(),
                codec.getEncodedLength(b[0]!!)
            )
            //assertEquals(b[0],codec.decode(b[1]));
        }
    }
}
