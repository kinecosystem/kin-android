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
import org.kin.stellarfork.codec.StringUtils.getBytesIso8859_1
import org.kin.stellarfork.codec.StringUtils.getBytesUnchecked
import org.kin.stellarfork.codec.StringUtils.getBytesUsAscii
import org.kin.stellarfork.codec.StringUtils.getBytesUtf16
import org.kin.stellarfork.codec.StringUtils.getBytesUtf16Be
import org.kin.stellarfork.codec.StringUtils.getBytesUtf16Le
import org.kin.stellarfork.codec.StringUtils.getBytesUtf8
import org.kin.stellarfork.codec.StringUtils.newString
import org.kin.stellarfork.codec.StringUtils.newStringIso8859_1
import org.kin.stellarfork.codec.StringUtils.newStringUsAscii
import org.kin.stellarfork.codec.StringUtils.newStringUtf16Be
import org.kin.stellarfork.codec.StringUtils.newStringUtf16Le
import org.kin.stellarfork.codec.StringUtils.newStringUtf8
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.Arrays

/**
 * Tests [StringUtils]
 */
class StringUtilsTest {
    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testGetBytesIso8859_1() {
        val charsetName = "ISO-8859-1"
        testGetBytesUnchecked(charsetName)
        val expected =
            STRING_FIXTURE.toByteArray(charset(charsetName))
        val actual =
            getBytesIso8859_1(STRING_FIXTURE)
        Assert.assertTrue(Arrays.equals(expected, actual))
    }

    @Throws(UnsupportedEncodingException::class)
    private fun testGetBytesUnchecked(charsetName: String) {
        val expected =
            STRING_FIXTURE.toByteArray(charset(charsetName))
        val actual = getBytesUnchecked(
            STRING_FIXTURE,
            charsetName
        )
        Assert.assertTrue(Arrays.equals(expected, actual))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testGetBytesUsAscii() {
        val charsetName = "US-ASCII"
        testGetBytesUnchecked(charsetName)
        val expected =
            STRING_FIXTURE.toByteArray(charset(charsetName))
        val actual =
            getBytesUsAscii(STRING_FIXTURE)
        Assert.assertTrue(Arrays.equals(expected, actual))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testGetBytesUtf16() {
        val charsetName = "UTF-16"
        testGetBytesUnchecked(charsetName)
        val expected =
            STRING_FIXTURE.toByteArray(charset(charsetName))
        val actual =
            getBytesUtf16(STRING_FIXTURE)
        Assert.assertTrue(Arrays.equals(expected, actual))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testGetBytesUtf16Be() {
        val charsetName = "UTF-16BE"
        testGetBytesUnchecked(charsetName)
        val expected =
            STRING_FIXTURE.toByteArray(charset(charsetName))
        val actual =
            getBytesUtf16Be(STRING_FIXTURE)
        Assert.assertTrue(Arrays.equals(expected, actual))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testGetBytesUtf16Le() {
        val charsetName = "UTF-16LE"
        testGetBytesUnchecked(charsetName)
        val expected =
            STRING_FIXTURE.toByteArray(charset(charsetName))
        val actual =
            getBytesUtf16Le(STRING_FIXTURE)
        Assert.assertTrue(Arrays.equals(expected, actual))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testGetBytesUtf8() {
        val charsetName = "UTF-8"
        testGetBytesUnchecked(charsetName)
        val expected =
            STRING_FIXTURE.toByteArray(charset(charsetName))
        val actual =
            getBytesUtf8(STRING_FIXTURE)
        Assert.assertTrue(Arrays.equals(expected, actual))
    }

    @Test
    fun testGetBytesUncheckedBadName() {
        try {
            getBytesUnchecked(
                STRING_FIXTURE,
                "UNKNOWN"
            )
            Assert.fail("Expected " + IllegalStateException::class.java.name)
        } catch (e: IllegalStateException) { // Expected
        } catch (e: UnsupportedCharsetException) {
        }
    }

    @Test
    fun testGetBytesUncheckedNullInput() {
        Assert.assertNull(
            getBytesUnchecked(
                null,
                "UNKNOWN"
            )
        )
    }

    @Throws(UnsupportedEncodingException::class)
    private fun testNewString(charsetName: String) {
        val expected = String(BYTES_FIXTURE, Charset.forName(charsetName))
        val actual = newString(
            BYTES_FIXTURE,
            charsetName
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testNewStringBadEnc() {
        try {
            newString(
                BYTES_FIXTURE,
                "UNKNOWN"
            )
            Assert.fail("Expected " + IllegalStateException::class.java.name)
        } catch (e: IllegalStateException) { // Expected
        } catch (e: UnsupportedCharsetException) {
        }
    }

    @Test
    fun testNewStringNullInput() {
        Assert.assertNull(
            newString(
                null,
                "UNKNOWN"
            )
        )
    }

    @Test
    fun testNewStringNullInput_CODEC229() {
        Assert.assertNull(newStringUtf8(null))
        Assert.assertNull(newStringIso8859_1(null))
        Assert.assertNull(newStringUsAscii(null))
        Assert.assertNull(newStringUtf16Be(null))
        Assert.assertNull(newStringUtf16Le(null))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testNewStringIso8859_1() {
        val charsetName = "ISO-8859-1"
        testNewString(charsetName)
        val expected = String(BYTES_FIXTURE, Charset.forName(charsetName))
        val actual =
            newStringIso8859_1(BYTES_FIXTURE)
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testNewStringUsAscii() {
        val charsetName = "US-ASCII"
        testNewString(charsetName)
        val expected = String(BYTES_FIXTURE, Charset.forName(charsetName))
        val actual =
            newStringUsAscii(BYTES_FIXTURE)
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testNewStringUtf16Be() {
        val charsetName = "UTF-16BE"
        testNewString(charsetName)
        val expected =
            String(BYTES_FIXTURE_16BE, Charset.forName(charsetName))
        val actual =
            newStringUtf16Be(BYTES_FIXTURE_16BE)
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testNewStringUtf16Le() {
        val charsetName = "UTF-16LE"
        testNewString(charsetName)
        val expected =
            String(BYTES_FIXTURE_16LE, Charset.forName(charsetName))
        val actual =
            newStringUtf16Le(BYTES_FIXTURE_16LE)
        Assert.assertEquals(expected, actual)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun testNewStringUtf8() {
        val charsetName = "UTF-8"
        testNewString(charsetName)
        val expected = String(BYTES_FIXTURE, Charset.forName(charsetName))
        val actual =
            newStringUtf8(BYTES_FIXTURE)
        Assert.assertEquals(expected, actual)
    }

    companion object {
        private val BYTES_FIXTURE = byteArrayOf('a'.toByte(), 'b'.toByte(), 'c'.toByte())
        // This is valid input for UTF-16BE
        private val BYTES_FIXTURE_16BE =
            byteArrayOf(0, 'a'.toByte(), 0, 'b'.toByte(), 0, 'c'.toByte())
        // This is valid for UTF-16LE
        private val BYTES_FIXTURE_16LE =
            byteArrayOf('a'.toByte(), 0, 'b'.toByte(), 0, 'c'.toByte(), 0)
        private const val STRING_FIXTURE = "ABC"
    }
}
