package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.StrKey.decodeCheck
import org.kin.stellarfork.StrKey.encodeCheck
import java.io.IOException

class StrKeyTest {
    @Test
    @Throws(IOException::class, FormatException::class)
    fun testDecodeEncode() {
        val seed = "SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE"
        val secret = decodeCheck(StrKey.VersionByte.SEED, seed.toCharArray())
        val encoded = encodeCheck(StrKey.VersionByte.SEED, secret)
        Assert.assertEquals(seed, String(encoded))
    }

    @Test
    fun testDecodeInvalidVersionByte() {
        val address = "GCZHXL5HXQX5ABDM26LHYRCQZ5OJFHLOPLZX47WEBP3V2PF5AVFK2A5D"
        try {
            decodeCheck(StrKey.VersionByte.SEED, address.toCharArray())
            Assert.fail()
        } catch (e: FormatException) {
        }
    }

    @Test
    fun testDecodeInvalidSeed() {
        val seed = "SAA6NXOBOXP3RXGAXBW6PGFI5BPK4ODVAWITS4VDOMN5C2M4B66ZML"
        try {
            decodeCheck(StrKey.VersionByte.SEED, seed.toCharArray())
            Assert.fail()
        } catch (e: FormatException) {
        }
    } // TODO more tests
}
