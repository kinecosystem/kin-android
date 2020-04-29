package org.kin.stellarfork.xdr

import org.kin.stellarfork.Util.CHARSET_UTF8
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class XdrDataInputStream(`in`: InputStream) : DataInputStream(XdrInputStream(`in`)) {
    // The underlying input stream
    private val mIn: XdrInputStream

    @Throws(IOException::class)
    fun readString(): String {
        val l = readInt()
        val bytes = ByteArray(l)
        read(bytes)
        return String(bytes, Charset.forName(CHARSET_UTF8))
    }

    @Throws(IOException::class)
    fun readIntArray(): IntArray {
        val l = readInt()
        return readIntArray(l)
    }

    @Throws(IOException::class)
    private fun readIntArray(l: Int): IntArray {
        val arr = IntArray(l)
        (0 until l).forEach { i ->
            arr[i] = readInt()
        }
        return arr
    }

    @Throws(IOException::class)
    fun readFloatArray(): FloatArray {
        val l = readInt()
        return readFloatArray(l)
    }

    @Throws(IOException::class)
    private fun readFloatArray(l: Int): FloatArray {
        val arr = FloatArray(l)
        (0 until l).forEach { i ->
            arr[i] = readFloat()
        }
        return arr
    }

    @Throws(IOException::class)
    fun readDoubleArray(): DoubleArray {
        val l = readInt()
        return readDoubleArray(l)
    }

    @Throws(IOException::class)
    private fun readDoubleArray(l: Int): DoubleArray {
        val arr = DoubleArray(l)
        (0 until l).forEach { i ->
            arr[i] = readDouble()
        }
        return arr
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return super.read()
    }

    /**
     * Need to provide a custom impl of InputStream as DataInputStream's read methods
     * are final and we need to keep track of the count for padding purposes.
     */
    private class XdrInputStream(// The underlying input stream
        private val mIn: InputStream
    ) : InputStream() {
        // The amount of bytes read so far.
        private var mCount = 0

        @Throws(IOException::class)
        override fun read(): Int {
            val read = mIn.read()
            if (read >= 0) {
                mCount++
            }
            return read
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int {
            return read(b, 0, b.size)
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            val read = mIn.read(b, off, len)
            mCount += read
            pad()
            return read
        }

        @Throws(IOException::class)
        fun pad() {
            var pad = 0
            val mod = mCount % 4
            if (mod > 0) {
                pad = 4 - mod
            }
            while (pad-- > 0) {
                val b = read()
                if (b != 0) {
                    throw IOException("non-zero padding")
                }
            }
        }

    }

    /**
     * Creates a XdrDataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    init {
        mIn = super.`in` as XdrInputStream
    }
}
