package org.kin.stellarfork.xdr

import org.kin.stellarfork.Util.CHARSET_UTF8
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.Charset

class XdrDataOutputStream(out: OutputStream) : DataOutputStream(XdrOutputStream(out)) {
    private val mOut: XdrOutputStream
    @Throws(IOException::class)
    fun writeString(s: String) {
        val chars = s.toByteArray(Charset.forName(CHARSET_UTF8))
        writeInt(chars.size)
        write(chars)
    }

    @Throws(IOException::class)
    fun writeIntArray(a: IntArray) {
        writeInt(a.size)
        writeIntArray(a, a.size)
    }

    @Throws(IOException::class)
    private fun writeIntArray(a: IntArray, l: Int) {
        (0 until l).forEach { i ->
            writeInt(a[i])
        }
    }

    @Throws(IOException::class)
    fun writeFloatArray(a: FloatArray) {
        writeInt(a.size)
        writeFloatArray(a, a.size)
    }

    @Throws(IOException::class)
    private fun writeFloatArray(a: FloatArray, l: Int) {
        for (i in 0 until l) {
            writeFloat(a[i])
        }
    }

    @Throws(IOException::class)
    fun writeDoubleArray(a: DoubleArray) {
        writeInt(a.size)
        writeDoubleArray(a, a.size)
    }

    @Throws(IOException::class)
    private fun writeDoubleArray(a: DoubleArray, l: Int) {
        for (i in 0 until l) {
            writeDouble(a[i])
        }
    }

    private class XdrOutputStream(private val mOut: OutputStream) : OutputStream() {
        // Number of bytes written
        private var mCount = 0

        @Throws(IOException::class)
        override fun write(b: Int) {
            mOut.write(b)
            // https://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html#write(int):
// > The byte to be written is the eight low-order bits of the argument b.
// > The 24 high-order bits of b are ignored.
            mCount++
        }

        @Throws(IOException::class)
        override fun write(b: ByteArray) { // https://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html#write(byte[]):
// > The general contract for write(b) is that it should have exactly the same effect
// > as the call write(b, 0, b.length).
            write(b, 0, b.size)
        }

        @Throws(IOException::class)
        override fun write(b: ByteArray, offset: Int, length: Int) {
            mOut.write(b, offset, length)
            mCount += length
            pad()
        }

        @Throws(IOException::class)
        fun pad() {
            var pad = 0
            val mod = mCount % 4
            if (mod > 0) {
                pad = 4 - mod
            }
            while (pad-- > 0) {
                write(0)
            }
        }

    }

    init {
        mOut = super.out as XdrOutputStream
    }
}
