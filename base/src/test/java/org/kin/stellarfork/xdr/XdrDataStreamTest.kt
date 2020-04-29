package org.kin.stellarfork.xdr

import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class XdrDataStreamTest {
    @Test
    @Throws(IOException::class)
    fun backAndForthXdrStreamingWithStandardAscii() {
        val memo = "Dollar Sign $"
        Assert.assertEquals(
            memo,
            backAndForthXdrStreaming(memo)
        )
    }

    @Test
    @Throws(IOException::class)
    fun backAndForthXdrStreamingWithNonStandardAscii() {
        val memo = "Euro Sign €"
        Assert.assertEquals(
            memo,
            backAndForthXdrStreaming(memo)
        )
    }

    @Test
    @Throws(IOException::class)
    fun backAndForthXdrStreamingWithAllNonStandardAscii() {
        val memo = "øûý™€♠♣♥†‡µ¢£€"
        Assert.assertEquals(
            memo,
            backAndForthXdrStreaming(memo)
        )
    }

    companion object {
        //helper for tests below.
        @Throws(IOException::class)
        fun backAndForthXdrStreaming(inputString: String?): String { //String to XDR
            val byteOutputStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(byteOutputStream)
            xdrOutputStream.writeString(inputString!!)
            val xdrByteOutput = byteOutputStream.toByteArray()
            //XDR back to String
            val xdrInputStream =
                XdrDataInputStream(ByteArrayInputStream(xdrByteOutput))
            return xdrInputStream.readString()
        }
    }
}
