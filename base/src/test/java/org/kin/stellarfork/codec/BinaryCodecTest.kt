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

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kin.stellarfork.codec.BinaryCodec.Companion.fromAscii
import org.kin.stellarfork.codec.BinaryCodec.Companion.toAsciiBytes
import org.kin.stellarfork.codec.BinaryCodec.Companion.toAsciiChars
import org.kin.stellarfork.codec.BinaryCodec.Companion.toAsciiString
import java.nio.charset.StandardCharsets

/**
 * TestCase for BinaryCodec class.
 */
class BinaryCodecTest {
    /**
     * An instance of the binary codec.
     */
    var instance: BinaryCodec? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        this.instance = BinaryCodec()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        this.instance = null
    }
    // ------------------------------------------------------------------------
//
// Test decode(Object)
//
// ------------------------------------------------------------------------
    /**
     * Tests for Object decode(Object)
     */
    @Test
    fun testDecodeObjectException() {
        try {
            this.instance!!.decode(Any())
        } catch (e: DecoderException) { // all is well.
            return
        }
        Assert.fail("Expected DecoderException")
    }

    /**
     * Tests for Object decode(Object)
     */
    @Test
    @Throws(Exception::class)
    fun testDecodeObject() {
        var bits: ByteArray
        // With a single raw binary
        bits = ByteArray(1)
        assertDecodeObject(bits, "00000000")
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        assertDecodeObject(bits, "00000001")
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        assertDecodeObject(bits, "00000011")
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        assertDecodeObject(bits, "00000111")
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        assertDecodeObject(bits, "00001111")
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        assertDecodeObject(bits, "00011111")
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        assertDecodeObject(bits, "00111111")
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        assertDecodeObject(bits, "01111111")
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "11111111")
        // With a two raw binaries
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "0000000011111111")
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "0000000111111111")
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "0000001111111111")
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "0000011111111111")
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "0000111111111111")
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "0001111111111111")
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "0011111111111111")
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "0111111111111111")
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        assertDecodeObject(bits, "1111111111111111")
        assertDecodeObject(ByteArray(0), null)
    }
    // ------------------------------------------------------------------------
//
// Test decode(byte[])
//
// ------------------------------------------------------------------------
    /**
     * Utility used to assert the encoded and decoded values.
     *
     * @param bits     the pre-encoded data
     * @param encodeMe data to encode and compare
     */
    @Throws(DecoderException::class)
    fun assertDecodeObject(bits: ByteArray?, encodeMe: String?) {
        var decoded: ByteArray?
        decoded = instance!!.decode(encodeMe) as ByteArray?
        Assert.assertEquals(String(bits!!), String(decoded!!))
        decoded = if (encodeMe == null) {
            instance!!.decode(null as ByteArray?)
        } else {
            instance!!.decode(encodeMe.toByteArray(CHARSET_UTF8) as Any) as ByteArray?
        }
        Assert.assertEquals(String(bits), String(decoded!!))
        decoded = if (encodeMe == null) {
            instance!!.decode(null as CharArray?) as ByteArray?
        } else {
            instance!!.decode(encodeMe.toCharArray()) as ByteArray?
        }
        Assert.assertEquals(String(bits), String(decoded!!))
    }

    /*
     * Tests for byte[] decode(byte[])
     */
    @Test
    fun testDecodeByteArray() { // With a single raw binary
        var bits = ByteArray(1)
        var decoded =
            instance!!.decode("00000000".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        decoded = instance!!.decode("00000001".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        decoded = instance!!.decode("00000011".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        decoded = instance!!.decode("00000111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        decoded = instance!!.decode("00001111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        decoded = instance!!.decode("00011111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        decoded = instance!!.decode("00111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        decoded = instance!!.decode("01111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.decode("11111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        // With a two raw binaries
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("0000000011111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("0000000111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("0000001111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("0000011111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("0000111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("0001111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("0011111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("0111111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            instance!!.decode("1111111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
    }

    // ------------------------------------------------------------------------
//
// Test toByteArray(String)
//
// ------------------------------------------------------------------------
/*
     * Tests for byte[] toByteArray(String)
     */
    @Test
    fun testToByteArrayFromString() { // With a single raw binary
        var bits = ByteArray(1)
        var decoded = instance!!.toByteArray("00000000")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        decoded = instance!!.toByteArray("00000001")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        decoded = instance!!.toByteArray("00000011")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        decoded = instance!!.toByteArray("00000111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        decoded = instance!!.toByteArray("00001111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        decoded = instance!!.toByteArray("00011111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        decoded = instance!!.toByteArray("00111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        decoded = instance!!.toByteArray("01111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("11111111")
        Assert.assertEquals(String(bits), String(decoded))
        // With a two raw binaries
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("0000000011111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("0000000111111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("0000001111111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("0000011111111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("0000111111111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("0001111111111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("0011111111111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("0111111111111111")
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = instance!!.toByteArray("1111111111111111")
        Assert.assertEquals(String(bits), String(decoded))
        Assert.assertEquals(
            0,
            instance!!.toByteArray(null as String?).size.toLong()
        )
    }

    // ------------------------------------------------------------------------
//
// Test fromAscii(char[])
//
// ------------------------------------------------------------------------
/*
     * Tests for byte[] fromAscii(char[])
     */
    @Test
    fun testFromAsciiCharArray() {
        Assert.assertEquals(0, fromAscii(null as CharArray?).size.toLong())
        Assert.assertEquals(0, fromAscii(CharArray(0)).size.toLong())
        // With a single raw binary
        var bits = ByteArray(1)
        var decoded = fromAscii("00000000".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        decoded = fromAscii("00000001".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        decoded = fromAscii("00000011".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        decoded = fromAscii("00000111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        decoded = fromAscii("00001111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        decoded = fromAscii("00011111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        decoded = fromAscii("00111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        decoded = fromAscii("01111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("11111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        // With a two raw binaries
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("0000000011111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("0000000111111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("0000001111111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("0000011111111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("0000111111111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("0001111111111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("0011111111111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("0111111111111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded = fromAscii("1111111111111111".toCharArray())
        Assert.assertEquals(String(bits), String(decoded))
        Assert.assertEquals(0, fromAscii(null as CharArray?).size.toLong())
    }

    // ------------------------------------------------------------------------
//
// Test fromAscii(byte[])
//
// ------------------------------------------------------------------------
/*
     * Tests for byte[] fromAscii(byte[])
     */
    @Test
    fun testFromAsciiByteArray() {
        Assert.assertEquals(0, fromAscii(null as ByteArray?)!!.size.toLong())
        Assert.assertEquals(0, fromAscii(ByteArray(0))!!.size.toLong())
        // With a single raw binary
        var bits = ByteArray(1)
        var decoded =
            fromAscii("00000000".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        decoded =
            fromAscii("00000001".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        decoded =
            fromAscii("00000011".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        decoded =
            fromAscii("00000111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        decoded =
            fromAscii("00001111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        decoded =
            fromAscii("00011111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        decoded =
            fromAscii("00111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        decoded =
            fromAscii("01111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("11111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        // With a two raw binaries
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("0000000011111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("0000000111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("0000001111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("0000011111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("0000111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("0001111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("0011111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("0111111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        decoded =
            fromAscii("1111111111111111".toByteArray(CHARSET_UTF8))
        Assert.assertEquals(String(bits), String(decoded!!))
        Assert.assertEquals(0, fromAscii(null as ByteArray?)!!.size.toLong())
    }

    // ------------------------------------------------------------------------
//
// Test encode(byte[])
//
// ------------------------------------------------------------------------
/*
     * Tests for byte[] encode(byte[])
     */
    @Test
    fun testEncodeByteArray() { // With a single raw binary
        var bits = ByteArray(1)
        var l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("00000000", l_encoded)
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("00000001", l_encoded)
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("00000011", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("00000111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("00001111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("00011111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("00111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("01111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("11111111", l_encoded)
        // With a two raw binaries
        bits = ByteArray(2)
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000000000000", l_encoded)
        bits = ByteArray(2)
        bits[0] = BIT_0.toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000000000001", l_encoded)
        bits = ByteArray(2)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000000000011", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000000000111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000000001111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000000011111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000000111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000001111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000011111111", l_encoded)
        // work on the other byte now
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000000111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000001111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000011111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0000111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0001111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0011111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("0111111111111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(instance!!.encode(bits)!!)
        Assert.assertEquals("1111111111111111", l_encoded)
        Assert.assertEquals(0, instance!!.encode(null as ByteArray?)!!.size.toLong())
    }

    // ------------------------------------------------------------------------
//
// Test toAsciiBytes
//
// ------------------------------------------------------------------------
    @Test
    fun testToAsciiBytes() { // With a single raw binary
        var bits = ByteArray(1)
        var l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("00000000", l_encoded)
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("00000001", l_encoded)
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("00000011", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("00000111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("00001111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("00011111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("00111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("01111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("11111111", l_encoded)
        // With a two raw binaries
        bits = ByteArray(2)
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000000000000", l_encoded)
        bits = ByteArray(2)
        bits[0] = BIT_0.toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000000000001", l_encoded)
        bits = ByteArray(2)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000000000011", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000000000111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000000001111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000000011111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000000111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000001111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000011111111", l_encoded)
        // work on the other byte now
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000000111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000001111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000011111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0000111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0001111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0011111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("0111111111111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiBytes(bits)!!)
        Assert.assertEquals("1111111111111111", l_encoded)
        Assert.assertEquals(
            0,
            toAsciiBytes(null as ByteArray?)!!.size.toLong()
        )
    }

    // ------------------------------------------------------------------------
//
// Test toAsciiChars
//
// ------------------------------------------------------------------------
    @Test
    fun testToAsciiChars() { // With a single raw binary
        var bits = ByteArray(1)
        var l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("00000000", l_encoded)
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("00000001", l_encoded)
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("00000011", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("00000111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("00001111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("00011111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("00111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("01111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("11111111", l_encoded)
        // With a two raw binaries
        bits = ByteArray(2)
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000000000000", l_encoded)
        bits = ByteArray(2)
        bits[0] = BIT_0.toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000000000001", l_encoded)
        bits = ByteArray(2)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000000000011", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000000000111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000000001111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000000011111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000000111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000001111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000011111111", l_encoded)
        // work on the other byte now
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000000111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000001111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000011111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0000111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0001111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0011111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("0111111111111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String(toAsciiChars(bits)!!)
        Assert.assertEquals("1111111111111111", l_encoded)
        Assert.assertEquals(
            0,
            toAsciiChars(null as ByteArray?)!!.size.toLong()
        )
    }
    // ------------------------------------------------------------------------
//
// Test toAsciiString
//
// ------------------------------------------------------------------------
    /**
     * Tests the toAsciiString(byte[]) method
     */
    @Test
    fun testToAsciiString() { // With a single raw binary
        var bits = ByteArray(1)
        var l_encoded = toAsciiString(bits)
        Assert.assertEquals("00000000", l_encoded)
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("00000001", l_encoded)
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("00000011", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("00000111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("00001111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("00011111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("00111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("01111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("11111111", l_encoded)
        // With a two raw binaries
        bits = ByteArray(2)
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000000000000", l_encoded)
        bits = ByteArray(2)
        bits[0] = BIT_0.toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000000000001", l_encoded)
        bits = ByteArray(2)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000000000011", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000000000111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000000001111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000000011111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000000111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000001111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000011111111", l_encoded)
        // work on the other byte now
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000000111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000001111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000011111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0000111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0001111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0011111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("0111111111111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = toAsciiString(bits)
        Assert.assertEquals("1111111111111111", l_encoded)
    }

    // ------------------------------------------------------------------------
//
// Test encode(Object)
//
// ------------------------------------------------------------------------
/*
     * Tests for Object encode(Object)
     */
    @Test
    @Throws(Exception::class)
    fun testEncodeObjectNull() {
        val obj: Any = ByteArray(0)
        Assert.assertEquals(0, (instance!!.encode(obj) as CharArray?)!!.size.toLong())
    }

    /*
     * Tests for Object encode(Object)
     */
    @Test
    fun testEncodeObjectException() {
        try {
            instance!!.encode("")
        } catch (e: EncoderException) { // all is well.
            return
        }
        Assert.fail("Expected EncoderException")
    }

    /*
     * Tests for Object encode(Object)
     */
    @Test
    @Throws(Exception::class)
    fun testEncodeObject() { // With a single raw binary
        var bits = ByteArray(1)
        var l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("00000000", l_encoded)
        bits = ByteArray(1)
        bits[0] = BIT_0.toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("00000001", l_encoded)
        bits = ByteArray(1)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("00000011", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("00000111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("00001111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("00011111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("00111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("01111111", l_encoded)
        bits = ByteArray(1)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("11111111", l_encoded)
        // With a two raw binaries
        bits = ByteArray(2)
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000000000000", l_encoded)
        bits = ByteArray(2)
        bits[0] = BIT_0.toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000000000001", l_encoded)
        bits = ByteArray(2)
        bits[0] = (BIT_0 or BIT_1).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000000000011", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000000000111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000000001111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000000011111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000000111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000001111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000011111111", l_encoded)
        // work on the other byte now
        bits = ByteArray(2)
        bits[1] = BIT_0.toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000000111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] = (BIT_0 or BIT_1).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000001111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000011111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0000111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0001111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0011111111111111", l_encoded)
        bits = ByteArray(2)
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6).toByte()
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("0111111111111111", l_encoded)
        bits = ByteArray(2)
        bits[0] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        bits[1] =
            (BIT_0 or BIT_1 or BIT_2 or BIT_3 or BIT_4 or BIT_5 or BIT_6 or BIT_7).toByte()
        l_encoded = String((instance!!.encode(bits as Any) as CharArray?)!!)
        Assert.assertEquals("1111111111111111", l_encoded)
    }

    companion object {
        private val CHARSET_UTF8 = StandardCharsets.UTF_8
        /**
         * Mask with bit zero-based index 0 raised.
         */
        private const val BIT_0 = 0x01
        /**
         * Mask with bit zero-based index 1 raised.
         */
        private const val BIT_1 = 0x02
        /**
         * Mask with bit zero-based index 2 raised.
         */
        private const val BIT_2 = 0x04
        /**
         * Mask with bit zero-based index 3 raised.
         */
        private const val BIT_3 = 0x08
        /**
         * Mask with bit zero-based index 4 raised.
         */
        private const val BIT_4 = 0x10
        /**
         * Mask with bit zero-based index 5 raised.
         */
        private const val BIT_5 = 0x20
        /**
         * Mask with bit zero-based index 6 raised.
         */
        private const val BIT_6 = 0x40
        /**
         * Mask with bit zero-based index 7 raised.
         */
        private const val BIT_7 = 0x80
    }
}
