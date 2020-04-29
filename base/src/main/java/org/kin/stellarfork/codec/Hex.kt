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

import org.kin.stellarfork.codec.StringUtils.getBytesUnchecked
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

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
 */ /**
 * Converts hexadecimal Strings. The charset used for certain operation can be set, the default is set in
 * [.DEFAULT_CHARSET_NAME]
 *
 * @author Apache Software Foundation
 * @version $Id$
 * @since 1.1
 */
class Hex : BinaryEncoder, BinaryDecoder {
    /**
     * Gets the charset name.
     *
     * @return the charset name.
     * @since 1.4
     */
    val charsetName: String

    val charset: Charset
        get() = Charset.forName(charsetName)

    /**
     * Creates a new codec with the default charset name [.DEFAULT_CHARSET_NAME]
     */
    constructor() { // use default encoding
        charsetName = DEFAULT_CHARSET_NAME
    }

    /**
     * Creates a new codec with the given charset name.
     *
     * @param csName the charset name.
     * @since 1.4
     */
    constructor(csName: String) {
        charsetName = csName
    }

    /**
     * Converts an array of character bytes representing hexadecimal values into an array of bytes of those same values.
     * The returned array will be half the length of the passed array, as it takes two characters to represent any given
     * byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param source An array of character bytes containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
     * @throws DecoderException Thrown if an odd number of characters is supplied to this function
     * @see .decodeHex
     */
    @Throws(DecoderException::class)
    override fun decode(source: ByteArray?): ByteArray? {
        if (source == null) { return source }
        return try {
            decodeHex(String(source, Charset.forName(charsetName)).toCharArray())
        } catch (e: UnsupportedEncodingException) {
            throw DecoderException(e.message, e)
        }
    }

    /**
     * Converts a String or an array of character bytes representing hexadecimal values into an array of bytes of those
     * same values. The returned array will be half the length of the passed String or array, as it takes two characters
     * to represent any given byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param source A String or, an array of character bytes containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
     * @throws DecoderException Thrown if an odd number of characters is supplied to this function or the object is not a String or
     * char[]
     * @see .decodeHex
     */
    @Throws(DecoderException::class)
    override fun decode(source: Any?): ByteArray? {
        return try {
            val charArray =
                if (source is String) source.toCharArray() else (source as CharArray)
            decodeHex(charArray)
        } catch (e: ClassCastException) {
            throw DecoderException(e.message, e)
        }
    }

    /**
     * Converts an array of bytes into an array of bytes for the characters representing the hexadecimal values of each
     * byte in order. The returned array will be double the length of the passed array, as it takes two characters to
     * represent any given byte.
     *
     *
     * The conversion from hexadecimal characters to the returned bytes is performed with the charset named by
     * [.getCharsetName].
     *
     *
     * @param source a byte[] to convert to Hex characters
     * @return A byte[] containing the bytes of the hexadecimal characters
     * @throws IllegalStateException if the charsetName is invalid. This API throws [IllegalStateException] instead of
     * [UnsupportedEncodingException] for backward compatibility.
     * @see .encodeHex
     */
    override fun encode(source: ByteArray?): ByteArray? {
        if (source == null) { return source }
        return getBytesUnchecked(
            encodeHexString(
                source
            ), charsetName
        )!!
    }

    /**
     * Converts a String or an array of bytes into an array of characters representing the hexadecimal values of each
     * byte in order. The returned array will be double the length of the passed String or array, as it takes two
     * characters to represent any given byte.
     *
     *
     * The conversion from hexadecimal characters to bytes to be encoded to performed with the charset named by
     * [.getCharsetName].
     *
     *
     * @param source a String, or byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     * @throws EncoderException Thrown if the given object is not a String or byte[]
     * @see .encodeHex
     */
    @Throws(EncoderException::class)
    override fun encode(source: Any?): CharArray? {
        return try {
            val byteArray =
                if (source is String) source.toByteArray(
                    charset(charsetName)
                ) else (source as ByteArray)
            encodeHex(byteArray)
        } catch (e: ClassCastException) {
            throw EncoderException(e.message, e)
        } catch (e: UnsupportedEncodingException) {
            throw EncoderException(e.message, e)
        }
    }

    /**
     * Returns a string representation of the object, which includes the charset name.
     *
     * @return a string representation of the object.
     */
    override fun toString(): String {
        return super.toString() + "[charsetName=" + charsetName + "]"
    }

    companion object {
        /**
         * Default charset name is [CharEncoding.UTF_8]
         *
         * @since 1.4
         */
        const val DEFAULT_CHARSET_NAME = CharEncoding.UTF_8
        /**
         * Used to build output as Hex
         */
        private val DIGITS_LOWER = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        )
        /**
         * Used to build output as Hex
         */
        private val DIGITS_UPPER = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        )

        /**
         * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
         * returned array will be half the length of the passed array, as it takes two characters to represent any given
         * byte. An exception is thrown if the passed char array has an odd number of elements.
         *
         * @param data An array of characters containing hexadecimal digits
         * @return A byte array containing binary data decoded from the supplied char array.
         * @throws DecoderException Thrown if an odd number or illegal of characters is supplied
         */
        @JvmStatic
        @Throws(DecoderException::class)
        fun decodeHex(data: String): ByteArray = decodeHex(data.toCharArray())

        /**
         * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
         * returned array will be half the length of the passed array, as it takes two characters to represent any given
         * byte. An exception is thrown if the passed char array has an odd number of elements.
         *
         * @param data An array of characters containing hexadecimal digits
         * @return A byte array containing binary data decoded from the supplied char array.
         * @throws DecoderException Thrown if an odd number or illegal of characters is supplied
         */
        @Throws(DecoderException::class)
        @JvmStatic
        fun decodeHex(data: CharArray): ByteArray {
            val len = data.size
            if (len and 0x01 != 0) {
                throw DecoderException("Odd number of characters.")
            }
            val out = ByteArray(len shr 1)
            // two characters form the hex value.
            var i = 0
            var j = 0
            while (j < len) {
                var f = toDigit(data[j], j) shl 4
                j++
                f = f or toDigit(data[j], j)
                j++
                out[i] = (f and 0xFF).toByte()
                i++
            }
            return out
        }
        /**
         * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
         * The returned array will be double the length of the passed array, as it takes two characters to represent any
         * given byte.
         *
         * @param data        a byte[] to convert to Hex characters
         * @param toLowerCase `true` converts to lowercase, `false` to uppercase
         * @return A char[] containing hexadecimal characters
         * @since 1.4
         */
        /**
         * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
         * The returned array will be double the length of the passed array, as it takes two characters to represent any
         * given byte.
         *
         * @param data a byte[] to convert to Hex characters
         * @return A char[] containing hexadecimal characters
         */
        @JvmOverloads
        @JvmStatic
        fun encodeHex(data: ByteArray, toLowerCase: Boolean = true): CharArray {
            return encodeHex(
                data,
                if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER
            )
        }

        /**
         * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
         * The returned array will be double the length of the passed array, as it takes two characters to represent any
         * given byte.
         *
         * @param data     a byte[] to convert to Hex characters
         * @param toDigits the output alphabet
         * @return A char[] containing hexadecimal characters
         * @since 1.4
         */
        @JvmStatic
        protected fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
            val l = data.size
            val out = CharArray(l shl 1)
            // two characters form the hex value.
            var i = 0
            var j = 0
            while (i < l) {
                out[j++] = toDigits[0xF0 and data[i].toInt() ushr 4]
                out[j++] = toDigits[0x0F and data[i].toInt()]
                i++
            }
            return out
        }

        /**
         * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
         * String will be double the length of the passed array, as it takes two characters to represent any given byte.
         *
         * @param data a byte[] to convert to Hex characters
         * @return A String containing hexadecimal characters
         * @since 1.4
         */
        @JvmStatic
        fun encodeHexString(data: ByteArray): String {
            return String(encodeHex(data))
        }

        /**
         * Converts a hexadecimal character to an integer.
         *
         * @param ch    A character to convert to an integer digit
         * @param index The index of the character in the source
         * @return An integer
         * @throws DecoderException Thrown if ch is an illegal hex character
         */
        @Throws(DecoderException::class)
        @JvmStatic
        protected fun toDigit(ch: Char, index: Int): Int {
            val digit = Character.digit(ch, 16)
            if (digit == -1) {
                throw DecoderException("Illegal hexadecimal character $ch at index $index")
            }
            return digit
        }
    }
}
