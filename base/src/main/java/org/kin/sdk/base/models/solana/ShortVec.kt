package org.kin.sdk.base.models.solana

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalUnsignedTypes::class)
object ShortVec {
    inline fun <reified T> encodeShortVecOf(
        output: ByteArrayOutputStream,
        elements: List<T>,
        byteTransform: (T) -> ByteArray,
    ) {
        encodeLen(output, elements.size)
        elements.forEach {
            output.write(byteTransform(it))
        }
    }

    inline fun <reified T> decodeShortVecOf(
        input: ByteArrayInputStream,
        sizeOfElement: Int,
        newInstance: (ByteArray) -> T? = { null },
    ): List<T> {
        val vecLength = decodeLen(input)
        val elements = mutableListOf<T>()

        (0 until vecLength).forEach { i ->
            val elemBytes = ByteArray(sizeOfElement)
            try {
                input.read(elemBytes, 0, sizeOfElement)
            } catch (t: Throwable) {
                throw RuntimeException("failed to read ${T::class.java.simpleName} at $i", t)
            }
            elements.add(elemBytes.toModel(newInstance))
        }

        return elements
    }

    private val MAX_VALUE = UShort.MAX_VALUE.toInt()

    /**
     * encodeLen encodes the specified [length] into the [output].
     *
     * @param output - the outputStream the length, of the ShortVec. is to be encoded in
     * @return - the number of bytes written. If length > UShort.MAX_VALUE.toInt() , a [RuntimeException] is thrown
     */
    fun encodeLen(output: ByteArrayOutputStream, length: Int): Int {
        if (length > MAX_VALUE) {
            throw RuntimeException("length exceeds $MAX_VALUE")
        }

        var lengthLocal = length
        val valBuf = ByteArray(1)
        var written = 0

        while (true) {
            valBuf[0] = (lengthLocal and 0x7f).toByte()
            lengthLocal = lengthLocal shr 7

            if (lengthLocal == 0) {
                output.write(valBuf)
                written += 1
                return written
            }

            valBuf[0] = (valBuf[0].toInt() or 0x80).toByte()
            output.write(valBuf)
            written += 1
        }
    }

    /**
     * decodeLen decodes a ShortVec encoded [length] from the [input].
     *
     * @param input - the input stream that the length is encoded in
     * @return - returns the decoded length of the ShortVec
     */
    fun decodeLen(input: ByteArrayInputStream): Int {
        var offset = 0
        val valBuf = ByteArray(1)
        var value = 0

        while (true) {
            input.read(valBuf)

            value = value or (valBuf[0].toInt() and 0x7f shl (offset * 7))
            offset++

            if ((valBuf[0].toInt() and 0x80) == 0) {
                break
            }
        }

        if (offset > 3) {
            throw RuntimeException("invalid size: $offset (max 3)")
        }
        return value
    }
}
