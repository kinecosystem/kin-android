package org.kin.stellarfork

import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.codec.Hex
import org.kin.stellarfork.xdr.XdrDataInputStream
import java.io.ByteArrayInputStream
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import kotlin.experimental.and

object Util {
    const val CHARSET_UTF8 = "UTF-8"

    /**
     * Returns SHA-256 hash of `data`.
     *
     * @param data
     */
    @JvmStatic
    fun hash(data: ByteArray?): ByteArray {
        return try {
            MessageDigest.getInstance("SHA-256")
                .apply { update(data) }
                .digest()

        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("SHA-256 not implemented")
        }
    }

    /**
     * Pads `bytes` array to `length` with zeros.
     *
     * @param bytes
     * @param length
     */
    @JvmStatic
    fun paddedByteArray(bytes: ByteArray, length: Int): ByteArray {
        val finalBytes = ByteArray(length)
        Arrays.fill(finalBytes, 0.toByte())
        System.arraycopy(bytes, 0, finalBytes, 0, bytes.size)
        return finalBytes
    }

    /**
     * Pads `string` to `length` with zeros.
     *
     * @param string
     * @param length
     */
    @JvmStatic
    fun paddedByteArray(string: String, length: Int): ByteArray =
        paddedByteArray(string.toByteArray(), length)

    /**
     * Remove zeros from the end of `bytes` array.
     *
     * @param bytes
     */
    @JvmStatic
    fun paddedByteArrayToString(bytes: ByteArray?): String {
        return String(bytes!!).split("\u0000").toTypedArray()[0]
    }

    @JvmStatic
    fun checkArgument(expression: Boolean, errorMessage: Any) =
        require(expression) { errorMessage.toString() }

    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    fun createXdrDataInputStream(envelopeXdr: String): XdrDataInputStream =
        XdrDataInputStream(
            ByteArrayInputStream(Base64().decode(envelopeXdr.toByteArray(charset(CHARSET_UTF8))))
        )
}
