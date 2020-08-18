package org.kin.stellarfork.xdr

import junit.framework.TestCase
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

@Suppress("UNUSED_VARIABLE")
class PaddingTest : TestCase() {
    @Test
    @Throws(IOException::class)
    fun testString() {
        val bytes = byteArrayOf(0, 0, 0, 2, 'a'.toByte(), 'b'.toByte(), 1, 0)
        try {
            val xdrObject =
                String32.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
            fail("Didn't throw IOException")
        } catch (expectedException: IOException) {
            assertEquals("non-zero padding", expectedException.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun testVarOpaque() {
        val bytes = byteArrayOf(0, 0, 0, 2, 'a'.toByte(), 'b'.toByte(), 1, 0)
        try {
            val xdrObject =
                DataValue.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
            fail("Didn't throw IOException")
        } catch (expectedException: IOException) {
            assertEquals("non-zero padding", expectedException.message)
        }
    }
}
