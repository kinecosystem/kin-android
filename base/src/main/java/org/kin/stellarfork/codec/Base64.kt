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

import java.math.BigInteger

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
 * Provides Base64 encoding and decoding as defined by [RFC 2045](http://www.ietf.org/rfc/rfc2045.txt).
 *
 *
 *
 * This class implements section <cite>6.8. Base64 Content-Transfer-Encoding</cite> from RFC 2045 <cite>Multipurpose
 * Internet Mail Extensions (MIME) Part One: Format of Internet Message Bodies</cite> by Freed and Borenstein.
 *
 *
 *
 * The class can be parameterized in the following manner with various constructors:
 *
 *  * URL-safe mode: Default off.
 *  * Line length: Default 76. Line length that aren't multiples of 4 will still essentially end up being multiples of
 * 4 in the encoded data.
 *  * Line separator: Default is CRLF ("\r\n")
 *
 *
 *
 *
 * Since this class operates directly on byte streams, and not character streams, it is hard-coded to only encode/decode
 * character encodings which are compatible with the lower 127 ASCII chart (ISO-8859-1, Windows-1252, UTF-8, etc).
 *
 *
 *
 * This class is not thread-safe. Each thread should use its own instance.
 *
 *
 * @author Apache Software Foundation
 * @version $Revision$
 * @see [RFC 2045](http://www.ietf.org/rfc/rfc2045.txt)
 *
 * @since 1.0
 */
class Base64 @JvmOverloads constructor(
    lineLength: Int = 0,
    lineSeparator: ByteArray? = CHUNK_SEPARATOR,
    urlSafe: Boolean = false
) : BaseNCodec(
    BYTES_PER_UNENCODED_BLOCK,
    BYTES_PER_ENCODED_BLOCK,
    lineLength,
    lineSeparator?.size ?: 0
) {
    // The static final fields above are used for the original static byte[] methods on Base64.
    // The private member fields below are used with the new streaming approach, which requires
    // some state be preserved between calls of encode() and decode().
    /**
     * Encode table to use: either STANDARD or URL_SAFE. Note: the DECODE_TABLE above remains static because it is able
     * to decode both STANDARD and URL_SAFE streams, but the encodeTable must be a member variable so we can switch
     * between the two modes.
     */
    private val encodeTable: ByteArray
    // Only one decode table currently; keep for consistency with Base32 code
    private val decodeTable = DECODE_TABLE
    /**
     * Line separator for encoding. Not used when decoding. Only used if lineLength > 0.
     */
    private var lineSeparator: ByteArray? = null
    /**
     * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
     * `decodeSize = 3 + lineSeparator.length;`
     */
    private val decodeSize: Int
    /**
     * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
     * `encodeSize = 4 + lineSeparator.length;`
     */
    private var encodeSize = 0
    /**
     * Place holder for the bytes we're dealing with for our based logic.
     * Bitwise operations store and extract the encoding or decoding from this variable.
     */
    private var bitWorkArea = 0

    /**
     * Creates a Base64 codec used for decoding (all modes) and encoding in the given URL-safe mode.
     *
     *
     * When encoding the line length is 76, the line separator is CRLF, and the encoding table is STANDARD_ENCODE_TABLE.
     *
     *
     *
     *
     * When decoding all variants are supported.
     *
     *
     * @param urlSafe if `true`, URL-safe encoding is used. In most cases this should be set to
     * `false`.
     * @since 1.4
     */
    constructor(urlSafe: Boolean) : this(
        MIME_CHUNK_SIZE,
        CHUNK_SEPARATOR,
        urlSafe
    ) {
    }

    /**
     * Returns our current encode mode. True if we're URL-SAFE, false otherwise.
     *
     * @return true if we're in URL-SAFE mode, false otherwise.
     * @since 1.4
     */
    val isUrlSafe: Boolean
        get() = encodeTable == URL_SAFE_ENCODE_TABLE

    /**
     *
     *
     * Encodes all of the provided data, starting at inPos, for inAvail bytes. Must be called at least twice: once with
     * the data to encode, and once with inAvail set to "-1" to alert encoder that EOF has been reached, so flush last
     * remaining bytes (if not multiple of 3).
     *
     *
     *
     * Thanks to "commons" project in ws.apache.org for the bitwise operations, and general approach.
     * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
     *
     *
     * @param pArray      byte[] array of binary data to base64 encode.
     * @param i   Position to start reading data from.
     * @param length Amount of bytes available from input for encoding.
     */
    override fun encode(pArray: ByteArray, i: Int, length: Int) {
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
                        buffer[pos++] = encodeTable[bitWorkArea shr 2 and MASK_6BITS] // top 6 bits
                        buffer[pos++] = encodeTable[bitWorkArea shl 4 and MASK_6BITS] // remaining 2
                        // URL-SAFE skips the padding to further reduce size.
                        if (encodeTable.contentEquals(STANDARD_ENCODE_TABLE)) {
                            buffer[pos++] = PAD
                            buffer[pos++] = PAD
                        }
                    }
                    2 -> {
                        buffer[pos++] = encodeTable[bitWorkArea shr 10 and MASK_6BITS]
                        buffer[pos++] = encodeTable[bitWorkArea shr 4 and MASK_6BITS]
                        buffer[pos++] = encodeTable[bitWorkArea shl 2 and MASK_6BITS]
                        // URL-SAFE skips the padding to further reduce size.
                        if (encodeTable.contentEquals(STANDARD_ENCODE_TABLE)) {
                            buffer[pos++] = PAD
                        }
                    }
                }
            }
            currentLinePos += pos - savedPos // keep track of current line position
            // if currentPos == 0 we are at the start of a line, so don't add CRLF
            lineSeparator?.let { lineSeparator ->
                if (lineLength > 0 && currentLinePos > 0) {
                    System.arraycopy(lineSeparator, 0, buffer, pos, lineSeparator.size)
                    pos += lineSeparator.size
                }
            }
        } else {
            for (n in 0 until length) {
                ensureBufferSize(encodeSize)
                modulus =
                    (modulus + 1) % BYTES_PER_UNENCODED_BLOCK
                var b = pArray[inPos++].toInt()
                if (b < 0) {
                    b += 256
                }
                bitWorkArea = (bitWorkArea shl 8) + b //  BITS_PER_BYTE
                if (0 == modulus) { // 3 bytes = 24 bits = 4 * 6 bits to extract
                    buffer?.let { buffer ->
                        buffer[pos++] =
                            encodeTable[bitWorkArea shr 18 and MASK_6BITS]
                        buffer[pos++] =
                            encodeTable[bitWorkArea shr 12 and MASK_6BITS]
                        buffer[pos++] =
                            encodeTable[bitWorkArea shr 6 and MASK_6BITS]
                        buffer[pos++] =
                            encodeTable[bitWorkArea and MASK_6BITS]

                        currentLinePos += BYTES_PER_ENCODED_BLOCK
                        lineSeparator?.let { lineSeparator ->
                            if (lineLength in 1..currentLinePos) {
                                System.arraycopy(
                                    lineSeparator,
                                    0,
                                    buffer,
                                    pos,
                                    lineSeparator.size
                                )
                                pos += lineSeparator.size
                                currentLinePos = 0
                            }
                        }
                    }
                }
            }
        }
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
     * Ignores all non-base64 characters. This is how chunked (e.g. 76 character) data is handled, since CR and LF are
     * silently ignored, but has implications for other bytes, too. This method subscribes to the garbage-in,
     * garbage-out philosophy: it will not check the provided data for validity.
     *
     *
     *
     * Thanks to "commons" project in ws.apache.org for the bitwise operations, and general approach.
     * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
     *
     *
     * @param pArray      byte[] array of ascii data to base64 decode.
     * @param i   Position to start reading data from.
     * @param length Amount of bytes available from input for encoding.
     */
    override fun decode(pArray: ByteArray, i: Int, length: Int) {
        var inPos = i
        if (eof) {
            return
        }
        if (length < 0) {
            eof = true
        }
        for (n in 0 until length) {
            ensureBufferSize(decodeSize)
            val b = pArray[inPos++]
            if (b == PAD) { // We're done.
                eof = true
                break
            } else {
                if (b >= 0 && b < DECODE_TABLE.size) {
                    val result =
                        DECODE_TABLE[b.toInt()].toInt()
                    if (result >= 0) {
                        modulus = (modulus + 1) % BYTES_PER_ENCODED_BLOCK
                        bitWorkArea = (bitWorkArea shl BITS_PER_ENCODED_BYTE) + result
                        if (modulus == 0) {
                            buffer?.let { buffer ->
                                buffer[pos++] = (bitWorkArea shr 16 and MASK_8BITS).toByte()
                                buffer[pos++] = (bitWorkArea shr 8 and MASK_8BITS).toByte()
                                buffer[pos++] = (bitWorkArea and MASK_8BITS).toByte()
                            }
                        }
                    }
                }
            }
        }
        // Two forms of EOF as far as base64 decoder is concerned: actual
// EOF (-1) and first time '=' character is encountered in stream.
// This approach makes the '=' padding characters completely optional.
        if (eof && modulus != 0) {
            ensureBufferSize(decodeSize)
            buffer?.let { buffer ->
                when (modulus) {
                    2 -> {
                        bitWorkArea = bitWorkArea shr 4 // dump the extra 4 bits
                        buffer[pos++] =
                            (bitWorkArea and MASK_8BITS).toByte()
                    }
                    3 -> {
                        bitWorkArea = bitWorkArea shr 2 // dump 2 bits
                        buffer[pos++] = (bitWorkArea shr 8 and MASK_8BITS).toByte()
                        buffer[pos++] = (bitWorkArea and MASK_8BITS).toByte()
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
    override fun isInAlphabet(value: Byte): Boolean {
        return value >= 0 && value < decodeTable.size && decodeTable[value.toInt()].toInt() != -1
    }

    companion object {
        /**
         * BASE32 characters are 6 bits in length.
         * They are formed by taking a block of 3 octets to form a 24-bit string,
         * which is converted into 4 BASE64 characters.
         */
        private const val BITS_PER_ENCODED_BYTE = 6
        private const val BYTES_PER_UNENCODED_BLOCK = 3
        private const val BYTES_PER_ENCODED_BLOCK = 4
        /**
         * Chunk separator per RFC 2045 section 2.1.
         *
         *
         *
         * N.B. The next major release may break compatibility and make this field private.
         *
         *
         * @see [RFC 2045 section 2.1](http://www.ietf.org/rfc/rfc2045.txt)
         */

        @JvmField
        val CHUNK_SEPARATOR: ByteArray = byteArrayFromChars('\r', '\n')
        /**
         * This array is a lookup table that translates 6-bit positive integer index values into their "Base64 Alphabet"
         * equivalents as specified in Table 1 of RFC 2045.
         *
         *
         * Thanks to "commons" project in ws.apache.org for this code.
         * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
         */
        private val STANDARD_ENCODE_TABLE = byteArrayFromChars(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        )
        /**
         * This is a copy of the STANDARD_ENCODE_TABLE above, but with + and /
         * changed to - and _ to make the encoded Base64 results more URL-SAFE.
         * This table is only used when the Base64's mode is set to URL-SAFE.
         */
        private val URL_SAFE_ENCODE_TABLE = byteArrayFromChars(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
        )
        /**
         * This array is a lookup table that translates Unicode characters drawn from the "Base64 Alphabet" (as specified in
         * Table 1 of RFC 2045) into their 6-bit positive integer equivalents. Characters that are not in the Base64
         * alphabet but fall within the bounds of the array are translated to -1.
         *
         *
         * Note: '+' and '-' both decode to 62. '/' and '_' both decode to 63. This means decoder seamlessly handles both
         * URL_SAFE and STANDARD base64. (The encoder, on the other hand, needs to know ahead of time what to emit).
         *
         *
         * Thanks to "commons" project in ws.apache.org for this code.
         * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
         */
        private val DECODE_TABLE = byteArrayOf(
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54,
            55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
            5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
            24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
            35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
        )
        /**
         * Base64 uses 6-bit fields.
         */
        /**
         * Mask used to extract 6 bits, used when encoding
         */
        private const val MASK_6BITS = 0x3f

        /**
         * Returns whether or not the `octet` is in the base 64 alphabet.
         *
         * @param octet The value to test
         * @return `true` if the value is defined in the the base 64 alphabet, `false` otherwise.
         * @since 1.4
         */
        @JvmStatic
        fun isBase64(octet: Byte): Boolean {
            return octet == PAD_DEFAULT || octet >= 0 && octet < DECODE_TABLE.size && DECODE_TABLE[octet.toInt()].toInt() != -1
        }

        /**
         * Tests a given String to see if it contains only valid characters within the Base64 alphabet. Currently the
         * method treats whitespace as valid.
         *
         * @param base64 String to test
         * @return `true` if all characters in the String are valid characters in the Base64 alphabet or if
         * the String is empty; `false`, otherwise
         * @since 1.5
         */
        @JvmStatic
        fun isBase64(base64: String): Boolean {
            return isBase64(StringUtils.getBytesUtf8(base64)!!)
        }

        /**
         * Tests a given byte array to see if it contains only valid characters within the Base64 alphabet. Currently the
         * method treats whitespace as valid.
         *
         * @param arrayOctet byte array to test
         * @return `true` if all bytes are valid characters in the Base64 alphabet or if the byte array is empty;
         * `false`, otherwise
         */
        @JvmStatic
        @Deprecated("1.5 Use {@link #isBase64(byte[])}, will be removed in 2.0.", ReplaceWith(
            "isBase64(arrayOctet)",
            "org.kin.stellarfork.codec.Base64.Companion.isBase64"
        )
        )
        fun isArrayByteBase64(arrayOctet: ByteArray): Boolean {
            return isBase64(arrayOctet)
        }

        /**
         * Tests a given byte array to see if it contains only valid characters within the Base64 alphabet. Currently the
         * method treats whitespace as valid.
         *
         * @param arrayOctet byte array to test
         * @return `true` if all bytes are valid characters in the Base64 alphabet or if the byte array is empty;
         * `false`, otherwise
         * @since 1.5
         */
        @JvmStatic
        fun isBase64(arrayOctet: ByteArray): Boolean {
            for (i in arrayOctet.indices) {
                if (!isBase64(arrayOctet[i]) && !isWhiteSpace(
                        arrayOctet[i]
                    )
                ) {
                    return false
                }
            }
            return true
        }

        /**
         * Encodes binary data using the base64 algorithm but does not chunk the output.
         *
         *
         * NOTE:  We changed the behaviour of this method from multi-line chunking (commons-codec-1.4) to
         * single-line non-chunking (commons-codec-1.5).
         *
         * @param binaryData binary data to encode
         * @return String containing Base64 characters.
         * @since 1.4 (NOTE:  1.4 chunked the output, whereas 1.5 does not).
         */
        @JvmStatic
        fun encodeBase64String(binaryData: ByteArray?): String? {
            return StringUtils.newStringUtf8(
                encodeBase64(
                    binaryData,
                    false
                )
            )
        }

        /**
         * Encodes binary data using a URL-safe variation of the base64 algorithm but does not chunk the output. The
         * url-safe variation emits - and _ instead of + and / characters.
         *
         * @param binaryData binary data to encode
         * @return byte[] containing Base64 characters in their UTF-8 representation.
         * @since 1.4
         */
        @JvmStatic
        fun encodeBase64URLSafe(binaryData: ByteArray?): ByteArray? {
            return encodeBase64(binaryData, false, true)
        }

        /**
         * Encodes binary data using a URL-safe variation of the base64 algorithm but does not chunk the output. The
         * url-safe variation emits - and _ instead of + and / characters.
         *
         * @param binaryData binary data to encode
         * @return String containing Base64 characters
         * @since 1.4
         */
        @JvmStatic
        fun encodeBase64URLSafeString(binaryData: ByteArray?): String {
            return StringUtils.newStringUtf8(
                encodeBase64(
                    binaryData,
                    false,
                    true
                )
            )!!
        }

        /**
         * Encodes binary data using the base64 algorithm and chunks the encoded output into 76 character blocks
         *
         * @param binaryData binary data to encode
         * @return Base64 characters chunked in 76 character blocks
         */
        @JvmStatic
        fun encodeBase64Chunked(binaryData: ByteArray?): ByteArray? {
            return encodeBase64(binaryData, true)
        }
        /**
         * Encodes binary data using the base64 algorithm, optionally chunking the output into 76 character blocks.
         *
         * @param binaryData    Array containing binary data to encode.
         * @param isChunked     if `true` this encoder will chunk the base64 output into 76 character blocks
         * @param urlSafe       if `true` this encoder will emit - and _ instead of the usual + and / characters.
         * @param maxResultSize The maximum result size to accept.
         * @return Base64-encoded data.
         * @throws IllegalArgumentException Thrown when the input array needs an output array bigger than maxResultSize
         * @since 1.4
         */
        /**
         * Encodes binary data using the base64 algorithm, optionally chunking the output into 76 character blocks.
         *
         * @param binaryData Array containing binary data to encode.
         * @param isChunked  if `true` this encoder will chunk the base64 output into 76 character blocks
         * @param urlSafe    if `true` this encoder will emit - and _ instead of the usual + and / characters.
         * @return Base64-encoded data.
         * @throws IllegalArgumentException Thrown when the input array needs an output array bigger than [Integer.MAX_VALUE]
         * @since 1.4
         */
        /**
         * Encodes binary data using the base64 algorithm, optionally chunking the output into 76 character blocks.
         *
         * @param binaryData Array containing binary data to encode.
         * @param isChunked  if `true` this encoder will chunk the base64 output into 76 character blocks
         * @return Base64-encoded data.
         * @throws IllegalArgumentException Thrown when the input array needs an output array bigger than [Integer.MAX_VALUE]
         */
        /**
         * Encodes binary data using the base64 algorithm but does not chunk the output.
         *
         * @param binaryData binary data to encode
         * @return byte[] containing Base64 characters in their UTF-8 representation.
         */
        @JvmOverloads
        @JvmStatic
        fun encodeBase64(
            binaryData: ByteArray?,
            isChunked: Boolean = false,
            urlSafe: Boolean = false,
            maxResultSize: Int = Int.MAX_VALUE
        ): ByteArray? {
            if (binaryData == null || binaryData.isEmpty()) {
                return binaryData
            }
            // Create this so can use the super-class method
// Also ensures that the same roundings are performed by the ctor and the code
            val b64 =
                if (isChunked) Base64(urlSafe) else Base64(
                    0,
                    CHUNK_SEPARATOR,
                    urlSafe
                )
            val len = b64.getEncodedLength(binaryData)
            require(len <= maxResultSize) {
                "Input array too big, the output array would be bigger (" +
                        len +
                        ") than the specified maximum size of " +
                        maxResultSize
            }
            return b64.encode(binaryData)
        }

        /**
         * Decodes a Base64 String into octets
         *
         * @param base64String String containing Base64 data
         * @return Array containing decoded data.
         * @since 1.4
         */
        @JvmStatic
        fun decodeBase64(base64String: String?): ByteArray? {
            return Base64().decode(base64String)
        }

        /**
         * Decodes Base64 data into octets
         *
         * @param base64Data Byte array containing Base64 data
         * @return Array containing decoded data.
         */
        @JvmStatic
        fun decodeBase64(base64Data: ByteArray?): ByteArray? {
            return Base64().decode(base64Data)
        }
        // Implementation of the Encoder Interface
// Implementation of integer encoding used for crypto
        /**
         * Decodes a byte64-encoded integer according to crypto standards such as W3C's XML-Signature
         *
         * @param pArray a byte array containing base64 character data
         * @return A BigInteger
         * @since 1.4
         */
        @JvmStatic
        fun decodeInteger(pArray: ByteArray): BigInteger {
            return BigInteger(
                1,
                decodeBase64(pArray)
            )
        }

        /**
         * Encodes to a byte64-encoded integer according to crypto standards such as W3C's XML-Signature
         *
         * @param bigInt a BigInteger
         * @return A byte array containing base64 character data
         * @throws NullPointerException if null is passed in
         * @since 1.4
         */
        @JvmStatic
        fun encodeInteger(bigInt: BigInteger?): ByteArray? {
            if (bigInt == null) {
                throw NullPointerException("encodeInteger called with null parameter")
            }
            return encodeBase64(
                toIntegerBytes(bigInt),
                false
            )
        }

        /**
         * Returns a byte-array representation of a `BigInteger` without sign bit.
         *
         * @param bigInt `BigInteger` to be converted
         * @return a byte array representation of the BigInteger parameter
         */
        @JvmStatic
        fun toIntegerBytes(bigInt: BigInteger): ByteArray {
            var bitlen = bigInt.bitLength()
            // round bitlen
            bitlen = bitlen + 7 shr 3 shl 3
            val bigBytes = bigInt.toByteArray()
            if (bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
                return bigBytes
            }
            // set up params for copying everything but sign bit
            var startSrc = 0
            var len = bigBytes.size
            // if bigInt is exactly byte-aligned, just skip signbit in copy
            if (bigInt.bitLength() % 8 == 0) {
                startSrc = 1
                len--
            }
            val startDst = bitlen / 8 - len // to pad w/ nulls as per spec
            val resizedBytes = ByteArray(bitlen / 8)
            System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len)
            return resizedBytes
        }
    }
    /**
     * Creates a Base64 codec used for decoding (all modes) and encoding in URL-unsafe mode.
     *
     *
     * When encoding the line length and line separator are given in the constructor, and the encoding table is
     * STANDARD_ENCODE_TABLE.
     *
     *
     *
     * Line lengths that aren't multiples of 4 will still essentially end up being multiples of 4 in the encoded data.
     *
     *
     *
     * When decoding all variants are supported.
     *
     *
     * @param lineLength    Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 4).
     * If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     * @param lineSeparator Each line of encoded data will end with this sequence of bytes.
     * @param urlSafe       Instead of emitting '+' and '/' we emit '-' and '_' respectively. urlSafe is only applied to encode
     * operations. Decoding seamlessly handles both modes.
     * @throws IllegalArgumentException The provided lineSeparator included some base64 characters. That's not going to work!
     * @since 1.4
     */
    /**
     * Creates a Base64 codec used for decoding (all modes) and encoding in URL-unsafe mode.
     *
     *
     * When encoding the line length and line separator are given in the constructor, and the encoding table is
     * STANDARD_ENCODE_TABLE.
     *
     *
     *
     * Line lengths that aren't multiples of 4 will still essentially end up being multiples of 4 in the encoded data.
     *
     *
     *
     * When decoding all variants are supported.
     *
     *
     * @param lineLength    Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 4).
     * If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     * @param lineSeparator Each line of encoded data will end with this sequence of bytes.
     * @throws IllegalArgumentException Thrown when the provided lineSeparator included some base64 characters.
     * @since 1.4
     */
    /**
     * Creates a Base64 codec used for decoding (all modes) and encoding in URL-unsafe mode.
     *
     *
     * When encoding the line length is given in the constructor, the line separator is CRLF, and the encoding table is
     * STANDARD_ENCODE_TABLE.
     *
     *
     *
     * Line lengths that aren't multiples of 4 will still essentially end up being multiples of 4 in the encoded data.
     *
     *
     *
     * When decoding all variants are supported.
     *
     *
     * @param lineLength Each line of encoded data will be at most of the given length (rounded down to nearest multiple of 4).
     * If lineLength <= 0, then the output will not be divided into lines (chunks). Ignored when decoding.
     * @since 1.4
     */
    /**
     * Creates a Base64 codec used for decoding (all modes) and encoding in URL-unsafe mode.
     *
     *
     * When encoding the line length is 0 (no chunking), and the encoding table is STANDARD_ENCODE_TABLE.
     *
     *
     *
     *
     * When decoding all variants are supported.
     *
     */
    init {
        // TODO could be simplified if there is no requirement to reject invalid line sep when length <=0
// @see test case Base64Test.testConstructors()
        if (lineSeparator != null) {
            if (containsAlphabetOrPad(lineSeparator)) {
                val sep =
                    StringUtils.newStringUtf8(lineSeparator)
                throw IllegalArgumentException("lineSeparator must not contain base64 characters: [$sep]")
            }
            if (lineLength > 0) { // null line-sep forces no chunking rather than throwing IAE
                encodeSize =
                    BYTES_PER_ENCODED_BLOCK + lineSeparator.size
                this.lineSeparator = ByteArray(lineSeparator.size)
                System.arraycopy(
                    lineSeparator,
                    0,
                    this.lineSeparator,
                    0,
                    lineSeparator.size
                )
            } else {
                encodeSize = BYTES_PER_ENCODED_BLOCK
                this.lineSeparator = null
            }
        } else {
            encodeSize = BYTES_PER_ENCODED_BLOCK
            this.lineSeparator = null
        }
        decodeSize = encodeSize - 1
        encodeTable =
            if (urlSafe) URL_SAFE_ENCODE_TABLE else STANDARD_ENCODE_TABLE
    }
}
