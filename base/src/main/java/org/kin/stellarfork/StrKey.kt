package org.kin.stellarfork

import org.kin.stellarfork.codec.Base32
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Arrays

object StrKey {

    @JvmStatic
    fun encodeStellarAccountId(data: ByteArray): String =
        String(encodeCheck(VersionByte.ACCOUNT_ID, data))

    @JvmStatic
    fun decodeStellarAccountId(data: String): ByteArray =
        decodeCheck(VersionByte.ACCOUNT_ID, data.toCharArray())

    @JvmStatic
    fun encodeStellarSecretSeed(data: ByteArray): CharArray =
        encodeCheck(VersionByte.SEED, data)

    @JvmStatic
    fun decodeStellarSecretSeed(data: CharArray): ByteArray =
        decodeCheck(VersionByte.SEED, data)

    @JvmStatic
    fun encodePreAuthTx(data: ByteArray): String =
        String(encodeCheck(VersionByte.PRE_AUTH_TX, data))

    @JvmStatic
    fun decodePreAuthTx(data: String): ByteArray =
        decodeCheck(VersionByte.PRE_AUTH_TX, data.toCharArray())

    @JvmStatic
    fun encodeSha256Hash(data: ByteArray): String =
        String(encodeCheck(VersionByte.SHA256_HASH, data))

    @JvmStatic
    fun decodeSha256Hash(data: String): ByteArray =
        decodeCheck(VersionByte.SHA256_HASH, data.toCharArray())

    @JvmStatic
    fun encodeCheck(versionByte: VersionByte, data: ByteArray): CharArray {
        return try {
            val outputStream = ByteArrayOutputStream().apply {
                write(versionByte.getValue())
                write(data)
            }
            val payload = outputStream.toByteArray()
            val checksum = calculateChecksum(payload)
            outputStream.write(checksum)
            val unencoded = outputStream.toByteArray()

            Base32().encode(unencoded)?.let { bytesEncoded ->
                val charsEncoded = CharArray(bytesEncoded.size)
                for (i in bytesEncoded.indices) {
                    charsEncoded[i] = bytesEncoded[i].toChar()
                }
                if (VersionByte.SEED == versionByte) {
                    Arrays.fill(unencoded, 0.toByte())
                    Arrays.fill(payload, 0.toByte())
                    Arrays.fill(bytesEncoded, 0.toByte())
                }
                charsEncoded
            } ?: CharArray(0)
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }

    @JvmStatic
    fun decodeCheck(versionByte: VersionByte, encoded: CharArray): ByteArray {
        val bytes = ByteArray(encoded.size)
        encoded.indices.forEach { i ->
            require(encoded[i].toInt() <= 127) { "Illegal characters in encoded char array." }
            bytes[i] = encoded[i].toByte()
        }
        Base32().decode(bytes)?.let { decoded ->
            val decodedVersionByte = decoded[0]
            val payload = decoded.copyOfRange(0, decoded.size - 2)
            val data = payload.copyOfRange(1, payload.size)
            val checksum = decoded.copyOfRange(decoded.size - 2, decoded.size)
            if (decodedVersionByte.toInt() != versionByte.getValue()) {
                throw FormatException("Version byte is invalid")
            }
            val expectedChecksum = calculateChecksum(payload)
            if (!expectedChecksum.contentEquals(checksum)) {
                throw FormatException("Checksum invalid")
            }
            if (VersionByte.SEED.getValue() == decodedVersionByte.toInt()) {
                Arrays.fill(bytes, 0.toByte())
                Arrays.fill(decoded, 0.toByte())
                Arrays.fill(payload, 0.toByte())
            }
            return data
        }

        return ByteArray(0)
    }

    @JvmStatic
    internal fun calculateChecksum(bytes: ByteArray): ByteArray {
        // This code calculates CRC16-XModem checksum
        // Ported from https://github.com/alexgorbatchev/node-crc
        var crc = 0x0000
        var count = bytes.size
        var i = 0
        var code: Int
        while (count > 0) {
            code = crc ushr 8 and 0xFF
            code = code xor (bytes[i++].toInt() and 0xFF)
            code = code xor (code ushr 4)
            crc = crc shl 8 and 0xFFFF
            crc = crc xor code
            code = code shl 5 and 0xFFFF
            crc = crc xor code
            code = code shl 7 and 0xFFFF
            crc = crc xor code
            count--
        }
        // little-endian
        return byteArrayOf(crc.toByte(), (crc ushr 8).toByte())
    }

    enum class VersionByte(
        private val value: Byte // X
    ) {
        ACCOUNT_ID((6 shl 3).toByte()),  // G
        SEED((18 shl 3).toByte()),  // S
        PRE_AUTH_TX((19 shl 3).toByte()),  // T
        SHA256_HASH((23 shl 3).toByte());

        fun getValue(): Int {
            return value.toInt()
        }
    }
}
