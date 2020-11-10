package org.kin.sdk.base.tools

import org.kin.sdk.base.tools.Base58.AddressFormatException.InvalidCharacter
import org.kin.sdk.base.tools.Base58.AddressFormatException.InvalidChecksum
import org.kin.sdk.base.tools.Base58.AddressFormatException.InvalidDataLength
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

/**
 * Taken and adapted from https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Base58.java
 *
 * Copyright 2011 Google Inc.
 * Copyright 2018 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Base58 is a way to encode Bitcoin addresses (or arbitrary data) as alphanumeric strings.
 *
 *
 * Note that this is not the same base58 as used by Flickr, which you may find referenced around the Internet.
 *
 *
 * Satoshi explains: why base-58 instead of standard base-64 encoding?
 *
 *  * Don't want 0OIl characters that look the same in some fonts and
 * could be used to create visually identical looking account numbers.
 *  * A string with non-alphanumeric characters is not as easily accepted as an account number.
 *  * E-mail usually won't line-break if there's no punctuation to break at.
 *  * Doubleclicking selects the whole number as one word if it's all alphanumeric.
 *
 *
 *
 * However, note that the encoding/decoding runs in O(n) time, so it is not useful for large data.
 *
 *
 * The basic idea of the encoding is to treat the data bytes as a large number represented using
 * base-256 digits, convert the number to be represented using base-58 digits, preserve the exact
 * number of leading zeros (which are otherwise lost during the mathematical operations on the
 * numbers), and finally represent the resulting base-58 digits as alphanumeric ASCII characters.
 */
object Base58 {
    private val ALPHABET =
        "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()
    private val ENCODED_ZERO = ALPHABET[0]
    private val INDEXES = IntArray(128)

    sealed class AddressFormatException(message: String?) : IllegalArgumentException(message) {

        class InvalidCharacter(val character: Char, val position: Int) :
            AddressFormatException("Invalid character '" + Character.toString(character) + "' at position " + position)

        class InvalidDataLength(message: String?) : AddressFormatException(message)

        class InvalidChecksum : AddressFormatException("Checksum does not validate")
    }

    /**
     * Encodes the given bytes as a base58 string (no checksum is appended).
     *
     * @param input the bytes to encode
     * @return the base58-encoded string
     */
    fun encode(input: ByteArray): String {
        var inputCopy = input
        if (inputCopy.size == 0) {
            return ""
        }
        // Count leading zeros.
        var zeros = 0
        while (zeros < inputCopy.size && inputCopy[zeros] == 0.toByte()) {
            ++zeros
        }
        // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
        inputCopy = Arrays.copyOf(inputCopy, inputCopy.size) // since we modify it in-place
        val encoded = CharArray(inputCopy.size * 2) // upper bound
        var outputStart = encoded.size
        var inputStart = zeros
        while (inputStart < inputCopy.size) {
            encoded[--outputStart] = ALPHABET[divmod(inputCopy, inputStart, 256, 58).toInt()]
            if (inputCopy[inputStart] == 0.toByte()) {
                ++inputStart // optimization - skip leading zeros
            }
        }
        // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
        while (outputStart < encoded.size && encoded[outputStart] == ENCODED_ZERO) {
            ++outputStart
        }
        while (--zeros >= 0) {
            encoded[--outputStart] = ENCODED_ZERO
        }
        // Return encoded string (including encoded leading zeros).
        return String(encoded, outputStart, encoded.size - outputStart)
    }

    /**
     * Encodes the given version and bytes as a base58 string. A checksum is appended.
     *
     * @param version the version to encode
     * @param payload the bytes to encode, e.g. pubkey hash
     * @return the base58-encoded string
     */
    fun encodeChecked(version: Int, payload: ByteArray): String {
        require(!(version < 0 || version > 255)) { "Version not in range." }

        // A stringified buffer is:
        // 1 byte version + data bytes + 4 bytes check code (a truncated hash)
        val addressBytes = ByteArray(1 + payload.size + 4)
        addressBytes[0] = version.toByte()
        System.arraycopy(payload, 0, addressBytes, 1, payload.size)
        val checksum: ByteArray = hashTwice(addressBytes, 0, payload.size + 1)
        System.arraycopy(checksum, 0, addressBytes, payload.size + 1, 4)
        return encode(addressBytes)
    }

    /**
     * Decodes the given base58 string into the original data bytes.
     *
     * @param input the base58-encoded string to decode
     * @return the decoded data bytes
     * @throws AddressFormatException if the given string is not a valid base58 string
     */
    @Throws(AddressFormatException::class)
    fun decode(input: String): ByteArray {
        if (input.length == 0) {
            return ByteArray(0)
        }
        // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
        val input58 = ByteArray(input.length)
        for (i in 0 until input.length) {
            val c = input[i]
            val digit = if (c.toInt() < 128) INDEXES[c.toInt()] else -1
            if (digit < 0) {
                throw InvalidCharacter(c, i)
            }
            input58[i] = digit.toByte()
        }
        // Count leading zeros.
        var zeros = 0
        while (zeros < input58.size && input58[zeros] == 0.toByte()) {
            ++zeros
        }
        // Convert base-58 digits to base-256 digits.
        val decoded = ByteArray(input.length)
        var outputStart = decoded.size
        var inputStart = zeros
        while (inputStart < input58.size) {
            decoded[--outputStart] = divmod(input58, inputStart, 58, 256)
            if (input58[inputStart] == 0.toByte()) {
                ++inputStart // optimization - skip leading zeros
            }
        }
        // Ignore extra leading zeroes that were added during the calculation.
        while (outputStart < decoded.size && decoded[outputStart] == 0.toByte()) {
            ++outputStart
        }
        // Return decoded data (including original number of leading zeros).
        return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.size)
    }

    @Throws(AddressFormatException::class)
    fun decodeToBigInteger(input: String): BigInteger {
        return BigInteger(1, decode(input))
    }

    /**
     * Decodes the given base58 string into the original data bytes, using the checksum in the
     * last 4 bytes of the decoded data to verify that the rest are correct. The checksum is
     * removed from the returned data.
     *
     * @param input the base58-encoded string to decode (which should include the checksum)
     * @throws AddressFormatException if the input is not base 58 or the checksum does not validate.
     */
    @Throws(AddressFormatException::class)
    fun decodeChecked(input: String): ByteArray {
        val decoded = decode(input)
        if (decoded.size < 4) throw InvalidDataLength("Input too short: " + decoded.size)
        val data = Arrays.copyOfRange(decoded, 0, decoded.size - 4)
        val checksum = Arrays.copyOfRange(decoded, decoded.size - 4, decoded.size)
        val actualChecksum: ByteArray = Arrays.copyOfRange(hashTwice(data), 0, 4)
        if (!Arrays.equals(checksum, actualChecksum)) throw InvalidChecksum()
        return data
    }

    /**
     * Divides a number, represented as an array of bytes each containing a single digit
     * in the specified base, by the given divisor. The given number is modified in-place
     * to contain the quotient, and the return value is the remainder.
     *
     * @param number the number to divide
     * @param firstDigit the index within the array of the first non-zero digit
     * (this is used for optimization by skipping the leading zeros)
     * @param base the base in which the number's digits are represented (up to 256)
     * @param divisor the number to divide by (up to 256)
     * @return the remainder of the division operation
     */
    private fun divmod(number: ByteArray, firstDigit: Int, base: Int, divisor: Int): Byte {
        // this is just long division which accounts for the base of the input digits
        var remainder = 0
        for (i in firstDigit until number.size) {
            val digit = number[i].toInt() and 0xFF
            val temp = remainder * base + digit
            number[i] = (temp / divisor).toByte()
            remainder = temp % divisor
        }
        return remainder.toByte()
    }

    /**
     * Calculates the SHA-256 hash of the given byte range,
     * and then hashes the resulting hash again.
     *
     * @param input the array containing the bytes to hash
     * @param offset the offset within the array of the bytes to hash
     * @param length the number of bytes to hash
     * @return the double-hash (in big-endian order)
     */
    @JvmOverloads
    fun hashTwice(input: ByteArray, offset: Int = 0, length: Int = input.size): ByteArray {
        val digest = try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e) // Can't happen.
        }
        digest.update(input, offset, length)
        return digest.digest(digest.digest())
    }

    init {
        Arrays.fill(INDEXES, -1)
        for (i in ALPHABET.indices) {
            INDEXES[ALPHABET[i].toInt()] = i
        }
    }
}
