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
 * Abstract superclass for Base-N encoders and decoders.
 *
 *
 *
 * This class is not thread-safe.
 * Each thread should use its own instance.
 *
 */
abstract class BaseNCodec protected constructor(
    /**
     * Number of bytes in each full block of unencoded data, e.g. 4 for Base64 and 5 for Base32
     */
    private val unencodedBlockSize: Int,
    /**
     * Number of bytes in each full block of encoded data, e.g. 3 for Base64 and 8 for Base32
     */
    private val encodedBlockSize: Int,
    lineLength: Int,
    /**
     * Size of chunk separator. Not used unless [.lineLength] > 0.
     */
    private val chunkSeparatorLength: Int
) : BinaryEncoder, BinaryDecoder {
    protected val PAD = PAD_DEFAULT // instance variable just in case it needs to vary later
    /**
     * Chunksize for encoding. Not used when decoding.
     * A value of zero or less implies no chunking of the encoded data.
     * Rounded down to nearest multiple of encodedBlockSize.
     */
    @JvmField
    val lineLength: Int =
        if (lineLength > 0 && chunkSeparatorLength > 0) lineLength / encodedBlockSize * encodedBlockSize else 0
    /**
     * Buffer for streaming.
     */
    protected var buffer: ByteArray? = null
    /**
     * Position where next character should be written in the buffer.
     */
    protected var pos = 0
    /**
     * Position where next character should be read from the buffer.
     */
    private var readPos = 0
    /**
     * Boolean flag to indicate the EOF has been reached. Once EOF has been reached, this object becomes useless,
     * and must be thrown away.
     */
    protected var eof = false
    /**
     * Variable tracks how many characters have been written to the current line. Only used when encoding. We use it to
     * make sure each encoded line never goes beyond lineLength (if lineLength > 0).
     */
    protected var currentLinePos = 0
    /**
     * Writes to the buffer only occur after every 3/5 reads when encoding, and every 4/8 reads when decoding.
     * This variable helps track that.
     */
    protected var modulus = 0

    /**
     * Returns true if this object has buffered data for reading.
     *
     * @return true if there is data still available for reading.
     */
    fun hasData(): Boolean { // package protected for access from I/O streams
        return buffer != null
    }

    /**
     * Returns the amount of buffered data available for reading.
     *
     * @return The amount of buffered data available for reading.
     */
    fun available(): Int { // package protected for access from I/O streams
        return if (buffer != null) pos - readPos else 0
    }

    /**
     * Increases our buffer by the [.DEFAULT_BUFFER_RESIZE_FACTOR].
     */
    private fun resizeBuffer() {
        if (buffer == null) {
            buffer = ByteArray(defaultBufferSize)
            pos = 0
            readPos = 0
        } else {
            buffer?.let { buffer ->
                val b = ByteArray(buffer.size * DEFAULT_BUFFER_RESIZE_FACTOR)
                System.arraycopy(buffer, 0, b, 0, buffer.size)
                this.buffer = b
            }
        }
    }

    /**
     * Ensure that the buffer has room for `size` bytes
     *
     * @param size minimum spare space required
     */
    protected fun ensureBufferSize(size: Int) {
        if (buffer == null || buffer!!.size < pos + size) {
            resizeBuffer()
        }
    }

    /**
     * Extracts buffered data into the provided byte[] array, starting at position bPos,
     * up to a maximum of bAvail bytes. Returns how many bytes were actually extracted.
     *
     * @param b      byte[] array to extract the buffered data into.
     * @param bPos   position in byte[] array to start extraction at.
     * @param bAvail amount of bytes we're allowed to extract. We may extract fewer (if fewer are available).
     * @return The number of bytes successfully extracted into the provided byte[] array.
     */
    fun readResults(
        b: ByteArray?,
        bPos: Int,
        bAvail: Int
    ): Int { // package protected for access from I/O streams
        buffer?.let { buffer ->
            val len = Math.min(available(), bAvail)
            System.arraycopy(buffer, readPos, b, bPos, len)
            readPos += len
            if (readPos >= pos) {
                this.buffer = null // so hasData() will return false, and this method can return -1
            }
            return len
        }
        return if (eof) -1 else 0
    }

    /**
     * Resets this object to its initial newly constructed state.
     */
    private fun reset() {
        buffer = null
        pos = 0
        readPos = 0
        currentLinePos = 0
        modulus = 0
        eof = false
    }

    /**
     * Encodes an Object using the Base-N algorithm. This method is provided in order to satisfy the requirements of the
     * Encoder interface, and will throw an EncoderException if the supplied object is not of type byte[].
     *
     * @param source Object to encode
     * @return An object (of type byte[]) containing the Base-N encoded data which corresponds to the byte[] supplied.
     * @throws EncoderException if the parameter supplied is not of type byte[]
     */
    @Throws(EncoderException::class)
    override fun encode(source: Any?): Any? {
        if (source !is ByteArray) {
            throw EncoderException("Parameter supplied to Base-N encode is not a byte[]")
        }
        return encode(source)
    }

    /**
     * Encodes a byte[] containing binary data, into a String containing characters in the Base-N alphabet.
     *
     * @param pArray a byte array containing binary data
     * @return A String containing only Base-N character data
     */
    fun encodeToString(pArray: ByteArray?): String? {
        return StringUtils.newStringUtf8(encode(pArray))
    }

    /**
     * Decodes an Object using the Base-N algorithm. This method is provided in order to satisfy the requirements of the
     * Decoder interface, and will throw a DecoderException if the supplied object is not of type byte[] or String.
     *
     * @param source Object to decode
     * @return An object (of type byte[]) containing the binary data which corresponds to the byte[] or String supplied.
     * @throws DecoderException if the parameter supplied is not of type byte[]
     */
    @Throws(DecoderException::class)
    override fun decode(source: Any?): Any? {
        return if (source is ByteArray) {
            decode(source)
        } else if (source is String) {
            decode(source)
        } else {
            throw DecoderException("Parameter supplied to Base-N decode is not a byte[] or a String")
        }
    }

    /**
     * Decodes a String containing characters in the Base-N alphabet.
     *
     * @param pArray A String containing Base-N character data
     * @return a byte array containing binary data
     */
    fun decode(pArray: String?): ByteArray? {
        return decode(StringUtils.getBytesUtf8(pArray))
    }

    /**
     * Decodes a byte[] containing characters in the Base-N alphabet.
     *
     * @param source A byte array containing Base-N character data
     * @return a byte array containing binary data
     */
    override fun decode(source: ByteArray?): ByteArray? {
        reset()
        if (source == null || source.isEmpty()) {
            return source
        }
        decode(source, 0, source.size)
        decode(source, 0, -1) // Notify decoder of EOF.
        val result = ByteArray(pos)
        readResults(result, 0, result.size)
        return result
    }

    /**
     * Encodes a byte[] containing binary data, into a byte[] containing characters in the alphabet.
     *
     * @param source a byte array containing binary data
     * @return A byte array containing only the basen alphabetic character data
     */
    override fun encode(source: ByteArray?): ByteArray? {
        reset()
        if (source == null || source.isEmpty()) {
            return source
        }
        encode(source, 0, source.size)
        encode(source, 0, -1) // Notify encoder of EOF.
        val buf = ByteArray(pos - readPos)
        readResults(buf, 0, buf.size)
        return buf
    }

    /**
     * Encodes a byte[] containing binary data, into a String containing characters in the appropriate alphabet.
     * Uses UTF8 encoding.
     *
     * @param pArray a byte array containing binary data
     * @return String containing only character data in the appropriate alphabet.
     */
    fun encodeAsString(pArray: ByteArray): String {
        return StringUtils.newStringUtf8(encode(pArray))!!
    }

    abstract fun encode(
        pArray: ByteArray,
        i: Int,
        length: Int
    ) // package protected for access from I/O streams

    abstract fun decode(
        pArray: ByteArray,
        i: Int,
        length: Int
    ) // package protected for access from I/O streams

    /**
     * Returns whether or not the `octet` is in the current alphabet.
     * Does not allow whitespace or pad.
     *
     * @param value The value to test
     * @return `true` if the value is defined in the current alphabet, `false` otherwise.
     */
    abstract fun isInAlphabet(value: Byte): Boolean

    /**
     * Tests a given byte array to see if it contains only valid characters within the alphabet.
     * The method optionally treats whitespace and pad as valid.
     *
     * @param arrayOctet byte array to test
     * @param allowWSPad if `true`, then whitespace and PAD are also allowed
     * @return `true` if all bytes are valid characters in the alphabet or if the byte array is empty;
     * `false`, otherwise
     */
    fun isInAlphabet(arrayOctet: ByteArray?, allowWSPad: Boolean): Boolean {
        arrayOctet?.let { theArrayOctet->
            for (i in theArrayOctet.indices) {
                if (!isInAlphabet(theArrayOctet[i]) &&
                    (!allowWSPad || theArrayOctet[i] != PAD && !isWhiteSpace(
                        theArrayOctet[i]
                    ))
                ) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Tests a given String to see if it contains only valid characters within the alphabet.
     * The method treats whitespace and PAD as valid.
     *
     * @param basen String to test
     * @return `true` if all characters in the String are valid characters in the alphabet or if
     * the String is empty; `false`, otherwise
     * @see .isInAlphabet
     */
    fun isInAlphabet(basen: String): Boolean {
        return isInAlphabet(StringUtils.getBytesUtf8(basen), true)
    }

    /**
     * Tests a given byte array to see if it contains any characters within the alphabet or PAD.
     *
     *
     * Intended for use in checking line-ending arrays
     *
     * @param arrayOctet byte array to test
     * @return `true` if any byte is a valid character in the alphabet or PAD; `false` otherwise
     */
    fun containsAlphabetOrPad(arrayOctet: ByteArray?): Boolean {
        if (arrayOctet == null) {
            return false
        }
        for (i in arrayOctet.indices) {
            if (PAD == arrayOctet[i] || isInAlphabet(arrayOctet[i])) {
                return true
            }
        }
        return false
    }

    /**
     * Calculates the amount of space needed to encode the supplied array.
     *
     * @param pArray byte[] array which will later be encoded
     * @return amount of space needed to encoded the supplied array.
     * Returns a long since a max-len array will require > Integer.MAX_VALUE
     */
    fun getEncodedLength(pArray: ByteArray): Long { // Calculate non-chunked size - rounded up to allow for padding
// cast to long is needed to avoid possibility of overflow
        var len =
            (pArray.size + unencodedBlockSize - 1) / unencodedBlockSize * encodedBlockSize.toLong()
        if (lineLength > 0) { // We're using chunking
// Round up to nearest multiple
            len += (len + lineLength - 1) / lineLength * chunkSeparatorLength
        }
        return len
    }

    companion object {
        /**
         * MIME chunk size per RFC 2045 section 6.8.
         *
         *
         *
         * The {@value} character limit does not count the trailing CRLF, but counts all other characters, including any
         * equal signs.
         *
         *
         * @see [RFC 2045 section 6.8](http://www.ietf.org/rfc/rfc2045.txt)
         */
        const val MIME_CHUNK_SIZE = 76
        /**
         * PEM chunk size per RFC 1421 section 4.3.2.4.
         *
         *
         *
         * The {@value} character limit does not count the trailing CRLF, but counts all other characters, including any
         * equal signs.
         *
         *
         * @see [RFC 1421 section 4.3.2.4](http://tools.ietf.org/html/rfc1421)
         */
        const val PEM_CHUNK_SIZE = 64
        private const val DEFAULT_BUFFER_RESIZE_FACTOR = 2
        /**
         * Get the default buffer size. Can be overridden.
         *
         * @return [.DEFAULT_BUFFER_SIZE]
         */
        /**
         * Defines the default buffer size - currently {@value}
         * - must be large enough for at least one encoded block+separator
         */
        protected const val defaultBufferSize = 8192
        /**
         * Mask used to extract 8 bits, used in decoding bytes
         */
        @JvmStatic
        protected val MASK_8BITS = 0xff
        /**
         * Byte used to pad output.
         */
        @JvmStatic
        protected val PAD_DEFAULT = '=' // Allow static access to default
            .toByte()

        /**
         * Checks if a byte value is whitespace or not.
         * Whitespace is taken to mean: space, tab, CR, LF
         *
         * @param byteToCheck the byte to check
         * @return true if byte is whitespace, false otherwise
         */
        @JvmStatic
        fun isWhiteSpace(byteToCheck: Byte): Boolean {
            return when (byteToCheck.toChar()) {
                ' ', '\n', '\r', '\t' -> true
                else -> false
            }
        }

        @JvmStatic
        fun byteArrayFromChars(vararg chars: Char) = chars.map { it.toByte() }.toByteArray()
    }

}
