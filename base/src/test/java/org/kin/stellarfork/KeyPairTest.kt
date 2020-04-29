package org.kin.stellarfork

import org.junit.Assert
import org.junit.Test
import org.kin.stellarfork.KeyPair.Companion.fromAccountId
import org.kin.stellarfork.KeyPair.Companion.fromSecretSeed
import org.kin.stellarfork.codec.Hex
import java.util.HashMap
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class KeyPairTest {
    @Test
    fun testSign() {
        val expectedSig =
            "587d4b472eeef7d07aafcd0b049640b0bb3f39784118c2e2b73a04fa2f64c9c538b4b2d0f5335e968a480021fdc23e98c0ddf424cb15d8131df8cb6c4bb58309"
        val keypair = fromSecretSeed(
            Hex.decodeHex(SEED)
        )
        val data = "hello world"
        val sig = keypair.sign(data.toByteArray())
        Assert.assertArrayEquals(Hex.decodeHex(expectedSig), sig)
    }

    @Test
    @Throws(Exception::class)
    fun testVerifyTrue() {
        val sig =
            "587d4b472eeef7d07aafcd0b049640b0bb3f39784118c2e2b73a04fa2f64c9c538b4b2d0f5335e968a480021fdc23e98c0ddf424cb15d8131df8cb6c4bb58309"
        val data = "hello world"
        val keypair = fromSecretSeed(
            Hex.decodeHex(SEED)
        )
        Assert.assertTrue(
            keypair.verify(
                data.toByteArray(),
                Hex.decodeHex(sig)
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun testVerifyFalse() {
        val badSig =
            "687d4b472eeef7d07aafcd0b049640b0bb3f39784118c2e2b73a04fa2f64c9c538b4b2d0f5335e968a480021fdc23e98c0ddf424cb15d8131df8cb6c4bb58309"
        val corrupt = byteArrayOf(0x00)
        val data = "hello world"
        val keypair = fromSecretSeed(
            Hex.decodeHex(SEED)
        )
        Assert.assertFalse(
            keypair.verify(
                data.toByteArray(),
                Hex.decodeHex(badSig)
            )
        )
        Assert.assertFalse(keypair.verify(data.toByteArray(), corrupt))
    }

    @Test
    @Throws(Exception::class)
    fun testFromSecretSeed() {
        val keypairs: MutableMap<String, String> =
            HashMap()
        keypairs["SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE"] =
            "GCZHXL5HXQX5ABDM26LHYRCQZ5OJFHLOPLZX47WEBP3V2PF5AVFK2A5D"
        keypairs["SDTQN6XUC3D2Z6TIG3XLUTJIMHOSON2FMSKCTM2OHKKH2UX56RQ7R5Y4"] =
            "GDEAOZWTVHQZGGJY6KG4NAGJQ6DXATXAJO3AMW7C4IXLKMPWWB4FDNFZ"
        keypairs["SDIREFASXYQVEI6RWCQW7F37E6YNXECQJ4SPIOFMMMJRU5CMDQVW32L5"] =
            "GD2EVR7DGDLNKWEG366FIKXO2KCUAIE3HBUQP4RNY7LEZR5LDKBYHMM6"
        keypairs["SDAPE6RHEJ7745VQEKCI2LMYKZB3H6H366I33A42DG7XKV57673XLCC2"] =
            "GDLXVH2BTLCLZM53GF7ELZFF4BW4MHH2WXEA4Z5Z3O6DPNZNR44A56UJ"
        keypairs["SDYZ5IYOML3LTWJ6WIAC2YWORKVO7GJRTPPGGNJQERH72I6ZCQHDAJZN"] =
            "GABXJTV7ELEB2TQZKJYEGXBUIG6QODJULKJDI65KZMIZZG2EACJU5EA7"
        for (seed in keypairs.keys) {
            val accountId = keypairs[seed]
            val keypair =
                fromSecretSeed(seed)
            Assert.assertEquals(accountId, keypair.accountId)
            Assert.assertEquals(seed, String(keypair.secretSeed))
        }
    }

    @Test
    @Throws(Exception::class)
    fun testCanSign() {
        var keypair: KeyPair
        keypair =
            fromSecretSeed("SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE")
        Assert.assertTrue(keypair.canSign())
        keypair =
            fromAccountId("GABXJTV7ELEB2TQZKJYEGXBUIG6QODJULKJDI65KZMIZZG2EACJU5EA7")
        Assert.assertFalse(keypair.canSign())
    }

    @Test
    fun testSignWithoutSecret() {
        val keypair =
            fromAccountId("GDEAOZWTVHQZGGJY6KG4NAGJQ6DXATXAJO3AMW7C4IXLKMPWWB4FDNFZ")
        val data = "hello world"
        try {
            keypair.sign(data.toByteArray())
            Assert.fail()
        } catch (e: RuntimeException) {
            Assert.assertEquals(
                "KeyPair does not contain secret key. Use KeyPair.fromSecretSeed method to create a new KeyPair with a secret key.",
                e.message
            )
        }
    }

    @Test
    fun testEquality_public() {
        val keypair =
            fromAccountId("GDEAOZWTVHQZGGJY6KG4NAGJQ6DXATXAJO3AMW7C4IXLKMPWWB4FDNFZ")
        val keypair2 =
            fromAccountId("GDEAOZWTVHQZGGJY6KG4NAGJQ6DXATXAJO3AMW7C4IXLKMPWWB4FDNFZ")
        val keypair3 =
            fromAccountId("GD2EVR7DGDLNKWEG366FIKXO2KCUAIE3HBUQP4RNY7LEZR5LDKBYHMM6")

        assertEquals(keypair, keypair2)
        assertEquals(keypair.hashCode(), keypair2.hashCode())
        assertNotEquals(keypair, keypair3)
        assertNotEquals(keypair.hashCode(), keypair3.hashCode())
        assertSame(keypair, keypair)
    }

    @Test
    fun testEquality_private() {
        val keypair =
            fromSecretSeed("SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE")
        val keypair2 =
            fromSecretSeed("SDJHRQF4GCMIIKAAAQ6IHY42X73FQFLHUULAPSKKD4DFDM7UXWWCRHBE")
        val keypair3 =
            fromSecretSeed("SCSIAZKXAF75HJBKR4HIMZUASSNBFKTMEN6LJ7M7U3FFSJQUG4DUTGXT")

        assertEquals(keypair, keypair2)
        assertEquals(keypair.hashCode(), keypair2.hashCode())
        assertNotEquals(keypair, keypair3)
        assertNotEquals(keypair.hashCode(), keypair3.hashCode())
        assertSame(keypair, keypair)
    }

    @Test
    fun testEquality_public_private() {
        val keypair =
            fromAccountId("GDEAOZWTVHQZGGJY6KG4NAGJQ6DXATXAJO3AMW7C4IXLKMPWWB4FDNFZ")
        val keypair2 =
            fromSecretSeed("SBRA7HL7TPHKJWUSSEETRNGI6HVMSGNYD2QRJMYO26G65TBU2RXNFDPR")
        val keypair3 =
            fromSecretSeed("SCSIAZKXAF75HJBKR4HIMZUASSNBFKTMEN6LJ7M7U3FFSJQUG4DUTGXT")

        assertNotEquals(keypair, keypair2)
        assertNotEquals(keypair.hashCode(), keypair2.hashCode())
        assertNotEquals(keypair, keypair3)
        assertNotEquals(keypair.hashCode(), keypair3.hashCode())
        assertSame(keypair, keypair)
    }

    companion object {
        private const val SEED =
            "1123740522f11bfef6b3671f51e159ccf589ccf8965262dd5f97d1721d383dd4"
    }
}
