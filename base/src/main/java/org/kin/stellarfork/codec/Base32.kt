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
 * Provides Base32 encoding and decoding as defined by [RFC 4648](http://www.ietf.org/rfc/rfc4648.txt).
 *
 *
 *
 * The class can be parameterized in the following manner with various constructors:
 *
 *  * Whether to use the "base32hex" variant instead of the default "base32"
 *  * Line length: Default 76. Line length that aren't multiples of 8 will still essentially end up being multiples of
 * 8 in the encoded data.
 *  * Line separator: Default is CRLF ("\r\n")
 *
 *
 *
 *
 * This class operates directly on byte streams, and not character streams.
 *
 *
 *
 * This class is not thread-safe. Each thread should use its own instance.
 *
 *
 * @version $Revision$
 * @see [RFC 4648](http://www.ietf.org/rfc/rfc4648.txt)
 *
 * @since 1.5
 */
class Base32 @JvmOverloads constructor(
    lineLength: Int,
    lineSeparator: ByteArray? = CHUNK_SEPARATOR,
    useHex: Boolean = false
) : BaseNCodec(
    BYTES_PER_UNENCODED_BLOCK, BYTES_PER_ENCODED_BLOCK,
    lineLength,
    lineSeparator?.size ?: 0
) {
    // The static final fields above are used for the original static byte[] methods on Base32.
    // The private member fields below are used with the new streaming approach, which requires
    // some state be preserved between calls of encode() and decode().
    /**
     * Place holder for the bytes we're dealing with for our based logic.
     * Bitwise operations store and extract the encoding or decoding from this variable.
     */
    private var bitWorkArea: Long = 0
    /**
     * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
     */
    private val decodeSize: Int
    /**
     * Decode table to use.
     */
    private val decodeTable: ByteArray
    /**
     * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
     */
    private var encodeSize = 0
    /**
     * Encode table to use.
     */
    private val encodeTable: ByteArray
    /**
     * Line separator for encoding. Not used when decoding. Only used if lineLength > 0.
     */
    private val lineSeparator: ByteArray?
    /**
     * Creates a Base32 codec used for decoding and encoding.
     *
     *
     * When encoding the line length is 0 (no chunking).
     *
     *
     * @param useHex if `true` then use Base32 Hex alphabet
     */
    /**
     * Creates a Base32 codec used for decoding and encoding.
     *
     *
     * When encoding the line length is 0 (no chunking).
     *
     */
    @JvmOverloads
    constructor(useHex: Boolean = false) : this(0, null, useHex) {
    }

    /**
     *
     *
     * Decodes all of the provided data, starting at inPos, for inAvail bytes. Should be called at least twice: once
     * with the data to decode, and once with inAvail set to "-1" to alert decoder that EOF has been reached. The "-1"
     * call is not necessary when decoding, but it doesn't hurt, either.
     *
     *
     *
     * Ignores all non-Base32 characters. This is how chunked (e.g. 76 character) data is handled, since CR and LF are
     * silently ignored, but has implications for other bytes, too. This method subscribes to the garbage-in,
     * garbage-out philosophy: it will not check the provided data for validity.
     *
     *
     * @param pArray      byte[] array of ascii data to Base32 decode.
     * @param i   Position to start reading data from.
     * @param length Amount of bytes available from input for encoding.
     */
    override fun decode(
        pArray: ByteArray,
        i: Int,
        length: Int
    ) { // package protected for access from I/O streams
        var inPos = i
        if (eof) {
            return
        }
        if (length < 0) {
            eof = true
        }
        for (n in 0 until length) {
            val b = pArray[inPos++]
            if (b == PAD) { // We're done.
                eof = true
                break
            } else {
                ensureBufferSize(decodeSize)
                if (b >= 0 && b < decodeTable.size) {
                    val result = decodeTable[b.toInt()].toInt()
                    if (result >= 0) {
                        modulus = (modulus + 1) % BYTES_PER_ENCODED_BLOCK
                        bitWorkArea =
                            (bitWorkArea shl BITS_PER_ENCODED_BYTE) + result // collect decoded bytes
                        if (modulus == 0) { // we can output the 5 bytes
                            buffer?.let { buffer ->
                                buffer[pos++] =
                                    (bitWorkArea shr 32 and MASK_8BITS.toLong()).toByte()
                                buffer[pos++] =
                                    (bitWorkArea shr 24 and MASK_8BITS.toLong()).toByte()
                                buffer[pos++] =
                                    (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                                buffer[pos++] =
                                    (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                                buffer[pos++] =
                                    (bitWorkArea and MASK_8BITS.toLong()).toByte()
                            }
                        }
                    }
                }
            }
        }
        // Two forms of EOF as far as Base32 decoder is concerned: actual
// EOF (-1) and first time '=' character is encountered in stream.
// This approach makes the '=' padding characters completely optional.
        if (eof && modulus >= 2) { // if modulus < 2, nothing to do
            ensureBufferSize(decodeSize)
            buffer?.let { buffer ->
                when (modulus) {
                    2 -> buffer[pos++] =
                        (bitWorkArea shr 2 and MASK_8BITS.toLong()).toByte()
                    3 -> buffer[pos++] =
                        (bitWorkArea shr 7 and MASK_8BITS.toLong()).toByte()
                    4 -> {
                        bitWorkArea = bitWorkArea shr 4 // drop 4 bits
                        buffer[pos++] =
                            (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                        buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                    }
                    5 -> {
                        bitWorkArea = bitWorkArea shr 1
                        buffer[pos++] =
                            (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                        buffer[pos++] =
                            (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                        buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                    }
                    6 -> {
                        bitWorkArea = bitWorkArea shr 6
                        buffer[pos++] =
                            (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                        buffer[pos++] =
                            (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                        buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                    }
                    7 -> {
                        bitWorkArea = bitWorkArea shr 3
                        buffer[pos++] =
                            (bitWorkArea shr 24 and MASK_8BITS.toLong()).toByte()
                        buffer[pos++] =
                            (bitWorkArea shr 16 and MASK_8BITS.toLong()).toByte()
                        buffer[pos++] =
                            (bitWorkArea shr 8 and MASK_8BITS.toLong()).toByte()
                        buffer[pos++] = (bitWorkArea and MASK_8BITS.toLong()).toByte()
                    }
                }
            }
        }
    }

    /**
     *
     *
     * Encodes all of the provided data, starting at inPos, for inAvail bytes. Must be called at least twice: once with
     * the data to encode, and once with inAvail set to "-1" to alert encoder that EOF has been reached, so flush last
     * remaining bytes (if not multiple of 5).
     *
     *
     * @param pArray      byte[] array of binary data to Base32 encode.
     * @param i   Position to start reading data from.
     * @param length Amount of bytes available from input for encoding.
     */
    override fun encode(
        pArray: ByteArray,
        i: Int,
        length: Int
    ) { // package protected for access from I/O streams
        var inPos = i
        if (eof) {
            return
        }
        // inAvail < 0 is how we're informed of EOF in the underlying data we're
// encoding.
        if (length < 0) {
            eof = true
            if (0 == modulus && lineLength == 0) {
                return  // no leftovers to process and not using chunking
            }
            ensureBufferSize(encodeSize)
            val savedPos = pos
            buffer?.let { buffer ->
                when (modulus) {
                    1 -> {
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 3).toInt() and MASK_5BITS] // 8-1*5 = 3
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shl 2).toInt() and MASK_5BITS] // 5-3=2
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                    }
                    2 -> {
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 11).toInt() and MASK_5BITS] // 16-1*5 = 11
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 6).toInt() and MASK_5BITS] // 16-2*5 = 6
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 1).toInt() and MASK_5BITS] // 16-3*5 = 1
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shl 4).toInt() and MASK_5BITS] // 5-1 = 4
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                    }
                    3 -> {
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 19).toInt() and MASK_5BITS] // 24-1*5 = 19
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 14).toInt() and MASK_5BITS] // 24-2*5 = 14
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 9).toInt() and MASK_5BITS] // 24-3*5 = 9
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 4).toInt() and MASK_5BITS] // 24-4*5 = 4
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shl 1).toInt() and MASK_5BITS] // 5-4 = 1
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                        buffer[pos++] = PAD
                    }
                    4 -> {
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 27).toInt() and MASK_5BITS] // 32-1*5 = 27
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 22).toInt() and MASK_5BITS] // 32-2*5 = 22
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 17).toInt() and MASK_5BITS] // 32-3*5 = 17
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 12).toInt() and MASK_5BITS] // 32-4*5 = 12
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 7).toInt() and MASK_5BITS] // 32-5*5 =  7
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shr 2).toInt() and MASK_5BITS] // 32-6*5 =  2
                        buffer[pos++] =
                            encodeTable[(bitWorkArea shl 3).toInt() and MASK_5BITS] // 5-2 = 3
                        buffer[pos++] = PAD
                    }
                }
            }
            currentLinePos += pos - savedPos // keep track of current line position
            // if currentPos == 0 we are at the start of a line, so don't add CRLF
            if (lineLength > 0 && currentLinePos > 0) { // add chunk separator if required
                System.arraycopy(lineSeparator, 0, buffer, pos, lineSeparator!!.size)
                pos += lineSeparator.size
            }
        } else {
            for (n in 0 until length) {
                ensureBufferSize(encodeSize)
                modulus = (modulus + 1) % BYTES_PER_UNENCODED_BLOCK
                var b = pArray[inPos++].toInt()
                if (b < 0) {
                    b += 256
                }
                bitWorkArea = (bitWorkArea shl 8) + b // BITS_PER_BYTE
                if (0 == modulus) { // we have enough bytes to create our output
                    buffer?.let { buffer ->
                        buffer[pos++] = encodeTable[(bitWorkArea shr 35).toInt() and MASK_5BITS]
                        buffer[pos++] = encodeTable[(bitWorkArea shr 30).toInt() and MASK_5BITS]
                        buffer[pos++] = encodeTable[(bitWorkArea shr 25).toInt() and MASK_5BITS]
                        buffer[pos++] = encodeTable[(bitWorkArea shr 20).toInt() and MASK_5BITS]
                        buffer[pos++] = encodeTable[(bitWorkArea shr 15).toInt() and MASK_5BITS]
                        buffer[pos++] = encodeTable[(bitWorkArea shr 10).toInt() and MASK_5BITS]
                        buffer[pos++] = encodeTable[(bitWorkArea shr 5).toInt() and MASK_5BITS]
                        buffer[pos++] = encodeTable[bitWorkArea.toInt() and MASK_5BITS]
                    }
                    currentLinePos += BYTES_PER_ENCODED_BLOCK
                    if (lineLength > 0 && lineLength <= currentLinePos) {
                        System.arraycopy(
                            lineSeparator,
                            0,
                            buffer,
                            pos,
                            lineSeparator!!.size
                        )
                        pos += lineSeparator.size
                        currentLinePos = 0
                    }
                }
            }
        }
    }

    /**
     * Returns whether or not the `octet` is in the Base32 alphabet.
     *
     * @param value The value to test
     * @return `true` if the value is defined in the the Base32 alphabet `false` otherwise.
     */
    public override fun isInAlphabet(value: Byte): Boolean {
        return value >= 0 && value < decodeTable.size && decodeTable[value.toInt()].toInt() != -1
    }

    companion object {
        /**
         * BASE32 characters are 5 bits in length.
         * They are formed by taking a block of five octets to form a 40-bit string,
         * which is converted into eight BASE32 characters.
         */
        private const val BITS_PER_ENCODED_BYTE = 5
        private const val BYTES_PER_ENCODED_BLOCK = 8
        private const val BYTES_PER_UNENCODED_BLOCK = 5

        /**
         * Chunk separator per RFC 2045 section 2.1.
         *
         * @see [RFC 2045 section 2.1](http://www.ietf.org/rfc/rfc2045.txt)
         */
        private val CHUNK_SEPARATOR = byteArrayFromChars('\r', '\n')

        /**
         * This array is a lookup table that translates Unicode characters drawn from the "Base32 Alphabet" (as specified in
         * Table 3 of RFC 2045) into their 5-bit positive integer equivalents. Characters that are not in the Base32
         * alphabet but fall within the bounds of the array are translated to -1.
         */
        private val DECODE_TABLE =
            byteArrayOf( //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 00-0f
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 10-1f
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63,  // 20-2f
                -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1,  // 30-3f 2-7
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,  // 40-4f A-N
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
            )
        /**
         * This array is a lookup table that translates 5-bit positive integer index values into their "Base32 Alphabet"
         * equivalents as specified in Table 3 of RFC 2045.
         */
        private val ENCODE_TABLE = byteArrayFromChars(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '2', '3', '4', '5', '6', '7'
        )
        /**
         * This array is a lookup table that translates Unicode characters drawn from the "Base32 |Hex Alphabet" (as specified in
         * Table 3 of RFC 2045) into their 5-bit positive integer equivalents. Characters that are not in the Base32 Hex
         * alphabet but fall within the bounds of the array are translated to -1.
         */
        private val HEX_DECODE_TABLE =
            byteArrayOf(
                //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 00-0f
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 10-1f
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63,  // 20-2f
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1,  // 30-3f 2-7
                -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,  // 40-4f A-N
                25, 26, 27, 28, 29, 30, 31, 32
            )
        /**
         * This array is a lookup table that translates 5-bit positive integer index values into their "Base32 Hex Alphabet"
         * equivalents as specified in Table 3 of RFC 2045.
         */
        private val HEX_ENCODE_TABLE = byteArrayFromChars(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V'
        )
        /**
         * Mask used to extract 5 bits, used when encoding Base32 bytes
         */
        private const val MASK_5BITS = 0x1f
    }
    /**
     * Creates a Base32 / Base32 Hex codec used for decoding and encoding.
     *
     *
     * When encoding the line length and line separator are given in the constructor.
     *
     *
     *
     * Line lengths that aren't multiples of 8 will still essentially end up being multiples of 8 in the encoded data.
     *
     *
     * @param lineLength    Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 8).
     * If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     * @param lineSeparator Each line of encoded data will end with this sequence of bytes.
     * @param useHex        if `true`, then use Base32 Hex alphabet, otherwise use Base32 alphabet
     * @throws IllegalArgumentException The provided lineSeparator included some Base32 characters. That's not going to work!
     * Or the lineLength > 0 and lineSeparator is null.
     */
    /**
     * Creates a Base32 codec used for decoding and encoding.
     *
     *
     * When encoding the line length and line separator are given in the constructor.
     *
     *
     *
     * Line lengths that aren't multiples of 8 will still essentially end up being multiples of 8 in the encoded data.
     *
     *
     * @param lineLength    Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 8).
     * If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     * @param lineSeparator Each line of encoded data will end with this sequence of bytes.
     * @throws IllegalArgumentException The provided lineSeparator included some Base32 characters. That's not going to work!
     */
    /**
     * Creates a Base32 codec used for decoding and encoding.
     *
     *
     * When encoding the line length is given in the constructor, the line separator is CRLF.
     *
     *
     * @param lineLength Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 8).
     * If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     */
    init {
        if (useHex) {
            encodeTable = HEX_ENCODE_TABLE
            decodeTable = HEX_DECODE_TABLE
        } else {
            encodeTable = ENCODE_TABLE
            decodeTable = DECODE_TABLE
        }
        if (lineLength > 0) {
            requireNotNull(lineSeparator) { "lineLength $lineLength > 0, but lineSeparator is null" }
            // Must be done after initializing the tables
            if (containsAlphabetOrPad(lineSeparator)) {
                val sep =
                    StringUtils.newStringUtf8(lineSeparator)
                throw IllegalArgumentException("lineSeparator must not contain Base32 characters: [$sep]")
            }
            encodeSize = BYTES_PER_ENCODED_BLOCK + lineSeparator.size
            this.lineSeparator = ByteArray(lineSeparator.size)
            System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.size)
        } else {
            encodeSize = BYTES_PER_ENCODED_BLOCK
            this.lineSeparator = null
        }
        decodeSize = encodeSize - 1
    }
}
