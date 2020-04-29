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

import kotlin.experimental.and
import kotlin.experimental.or

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
 * Converts between byte arrays and strings of "0"s and "1"s.
 *
 *
 * TODO: may want to add more bit vector functions like and/or/xor/nand
 * TODO: also might be good to generate boolean[] from byte[] et cetera.
 *
 * @author Apache Software Foundation
 * @version $Id$
 * @since 1.3
 */
class BinaryCodec : BinaryDecoder, BinaryEncoder {
    /**
     * Converts an array of raw binary data into an array of ASCII 0 and 1 characters.
     *
     * @param source the raw binary data to convert
     * @return 0 and 1 ASCII character bytes one for each bit of the argument
     */
    override fun encode(source: ByteArray?): ByteArray? {
        return toAsciiBytes(source)
    }

    /**
     * Converts an array of raw binary data into an array of ASCII 0 and 1 chars.
     *
     * @param source the raw binary data to convert
     * @return 0 and 1 ASCII character chars one for each bit of the argument
     * @throws EncoderException if the argument is not a byte[]
     */
    @Throws(EncoderException::class)
    override fun encode(source: Any?): Any? {
        if (source !is ByteArray) {
            throw EncoderException("argument not a byte array")
        }
        return toAsciiChars(source)
    }

    /**
     * Decodes a byte array where each byte represents an ASCII '0' or '1'.
     *
     * @param source each byte represents an ASCII '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a byte in the byte array argument
     * @throws DecoderException if argument is not a byte[], char[] or String
     */
    @Throws(DecoderException::class)
    override fun decode(source: Any?): Any? {
        if (source == null) {
            return EMPTY_BYTE_ARRAY
        }
        if (source is ByteArray) {
            return fromAscii(source)
        }
        if (source is CharArray) {
            return fromAscii(source)
        }
        if (source is String) {
            return fromAscii(source.toCharArray())
        }
        throw DecoderException("argument not a byte array")
    }

    /**
     * Decodes a byte array where each byte represents an ASCII '0' or '1'.
     *
     * @param source each byte represents an ASCII '0' or '1'
     * @return the raw encoded binary where each bit corresponds to a byte in the byte array argument
     */
    override fun decode(source: ByteArray?): ByteArray? {
        return fromAscii(source)
    }

    /**
     * Decodes a String where each char of the String represents an ASCII '0' or '1'.
     *
     * @param ascii String of '0' and '1' characters
     * @return the raw encoded binary where each bit corresponds to a byte in the byte array argument
     */
    fun toByteArray(ascii: String?): ByteArray {
        return if (ascii == null) {
            EMPTY_BYTE_ARRAY
        } else fromAscii(ascii.toCharArray())
    }

    companion object {
        /*
         * tried to avoid using ArrayUtils to minimize dependencies
         * while using these empty arrays - dep is just not worth it.
         */

        /**
         * Empty char array.
         */
        private val EMPTY_CHAR_ARRAY = CharArray(0)
        /**
         * Empty byte array.
         */
        private val EMPTY_BYTE_ARRAY = ByteArray(0)
        /**
         * Mask for bit 0 of a byte.
         */
        private const val BIT_0 = 1
        /**
         * Mask for bit 1 of a byte.
         */
        private const val BIT_1 = 0x02
        /**
         * Mask for bit 2 of a byte.
         */
        private const val BIT_2 = 0x04
        /**
         * Mask for bit 3 of a byte.
         */
        private const val BIT_3 = 0x08
        /**
         * Mask for bit 4 of a byte.
         */
        private const val BIT_4 = 0x10
        /**
         * Mask for bit 5 of a byte.
         */
        private const val BIT_5 = 0x20
        /**
         * Mask for bit 6 of a byte.
         */
        private const val BIT_6 = 0x40
        /**
         * Mask for bit 7 of a byte.
         */
        private const val BIT_7 = 0x80
        private val BITS = intArrayOf(
            BIT_0,
            BIT_1,
            BIT_2,
            BIT_3,
            BIT_4,
            BIT_5,
            BIT_6,
            BIT_7
        )
        // ------------------------------------------------------------------------
        //
        // static codec operations
        //
        // ------------------------------------------------------------------------
        /**
         * Decodes a char array where each char represents an ASCII '0' or '1'.
         *
         * @param ascii each char represents an ASCII '0' or '1'
         * @return the raw encoded binary where each bit corresponds to a char in the char array argument
         */
        @JvmStatic
        fun fromAscii(ascii: CharArray?): ByteArray {
            if (ascii == null || ascii.size == 0) {
                return EMPTY_BYTE_ARRAY
            }
            // get length/8 times bytes with 3 bit shifts to the right of the length
            val l_raw = ByteArray(ascii.size shr 3)
            /*
         * We decr index jj by 8 as we go along to not recompute indices using multiplication every time inside the
         * loop.
         */
            var ii = 0
            var jj = ascii.size - 1
            while (ii < l_raw.size) {
                for (bits in BITS.indices) {
                    if (ascii[jj - bits] == '1') {
                        l_raw[ii] = l_raw[ii] or BITS[bits].toByte()
                    }
                }
                ii++
                jj -= 8
            }
            return l_raw
        }

        /**
         * Decodes a byte array where each byte represents an ASCII '0' or '1'.
         *
         * @param ascii each byte represents an ASCII '0' or '1'
         * @return the raw encoded binary where each bit corresponds to a byte in the byte array argument
         */
        @JvmStatic
        fun fromAscii(ascii: ByteArray?): ByteArray? {
            if (isEmpty(ascii)) {
                return EMPTY_BYTE_ARRAY
            }
            // get length/8 times bytes with 3 bit shifts to the right of the length
            val l_raw = ByteArray(ascii!!.size shr 3)
            /*
         * We decr index jj by 8 as we go along to not recompute indices using multiplication every time inside the
         * loop.
         */
            var ii = 0
            var jj = ascii.size - 1
            while (ii < l_raw.size) {
                for (bits in BITS.indices) {
                    if (ascii[jj - bits] == '1'.toByte()) {
                        l_raw[ii] = l_raw[ii] or BITS[bits].toByte()
                    }
                }
                ii++
                jj -= 8
            }
            return l_raw
        }

        /**
         * Returns `true` if the given array is `null` or empty (size 0.)
         *
         * @param array the source array
         * @return `true` if the given array is `null` or empty (size 0.)
         */
        private fun isEmpty(array: ByteArray?): Boolean {
            return array == null || array.size == 0
        }

        /**
         * Converts an array of raw binary data into an array of ASCII 0 and 1 character bytes - each byte is a truncated
         * char.
         *
         * @param raw the raw binary data to convert
         * @return an array of 0 and 1 character bytes for each bit of the argument
         */
        @JvmStatic
        fun toAsciiBytes(raw: ByteArray?): ByteArray? {
            if (isEmpty(raw)) {
                return EMPTY_BYTE_ARRAY
            }
            // get 8 times the bytes with 3 bit shifts to the left of the length
            val l_ascii = ByteArray(raw!!.size shl 3)
            /*
         * We decr index jj by 8 as we go along to not recompute indices using multiplication every time inside the
         * loop.
         */
            var ii = 0
            var jj = l_ascii.size - 1
            while (ii < raw.size) {
                for (bits in BITS.indices) {
                    if ((raw[ii] and BITS[bits].toByte()).toInt() == 0) {
                        l_ascii[jj - bits] = '0'.toByte()
                    } else {
                        l_ascii[jj - bits] = '1'.toByte()
                    }
                }
                ii++
                jj -= 8
            }
            return l_ascii
        }

        /**
         * Converts an array of raw binary data into an array of ASCII 0 and 1 characters.
         *
         * @param raw the raw binary data to convert
         * @return an array of 0 and 1 characters for each bit of the argument
         */
        @JvmStatic
        fun toAsciiChars(raw: ByteArray?): CharArray? {
            if (isEmpty(raw)) {
                return EMPTY_CHAR_ARRAY
            }
            // get 8 times the bytes with 3 bit shifts to the left of the length
            val l_ascii = CharArray(raw!!.size shl 3)
            /*
         * We decr index jj by 8 as we go along to not recompute indices using multiplication every time inside the
         * loop.
         */
            var ii = 0
            var jj = l_ascii.size - 1
            while (ii < raw.size) {
                for (bits in BITS.indices) {
                    if ((raw[ii] and BITS[bits].toByte()).toInt() == 0) {
                        l_ascii[jj - bits] = '0'
                    } else {
                        l_ascii[jj - bits] = '1'
                    }
                }
                ii++
                jj -= 8
            }
            return l_ascii
        }

        /**
         * Converts an array of raw binary data into a String of ASCII 0 and 1 characters.
         *
         * @param raw the raw binary data to convert
         * @return a String of 0 and 1 characters representing the binary data
         */
        @JvmStatic
        fun toAsciiString(raw: ByteArray?): String? {
            return toAsciiChars(raw)?.let { String(it) }
        }
    }
}
