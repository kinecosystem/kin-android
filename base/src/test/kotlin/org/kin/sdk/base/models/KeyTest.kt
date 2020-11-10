package org.kin.sdk.base.models

import org.junit.Test
import org.kin.sdk.base.tools.TestUtils
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class KeyTest {

    companion object {
        val privateKey1 = TestUtils.newPrivateKey()
        val privateKey2 = TestUtils.newPrivateKey()
        val privateKey3 = TestUtils.newPrivateKey()
        val publicKey4 = TestUtils.newPublicKey()
        val publicKey5 = TestUtils.newPublicKey()
    }

    @Test
    fun testEquality() {
        assertEquals(privateKey1, privateKey1)
        assertEquals(privateKey1, Key.PrivateKey(privateKey1.value))
        assertEquals(privateKey1.toString(), Key.PrivateKey(privateKey1.value).toString())
        assertEquals(privateKey1.stellarBase32Encode(), Key.PrivateKey(privateKey1.value).stellarBase32Encode())
        assertEquals(privateKey1.hashCode(), Key.PrivateKey(privateKey1.value).hashCode())
        assertEquals(publicKey4, publicKey4)
        assertEquals(publicKey4, Key.PublicKey(publicKey4.value))
        assertEquals(publicKey4.toString(), Key.PublicKey(publicKey4.value).toString())
        assertEquals(publicKey4.stellarBase32Encode(), Key.PublicKey(publicKey4.value).stellarBase32Encode())
        assertEquals(publicKey4.hashCode(), Key.PublicKey(publicKey4.value).hashCode())
        assertNotEquals(privateKey1, privateKey2)
        assertNotEquals(privateKey1.hashCode(), privateKey2.hashCode())
        assertNotEquals(privateKey1, privateKey3)
        assertNotEquals<Key>(privateKey1, publicKey4)
        assertNotEquals(publicKey4, publicKey5)
        assertEquals(privateKey1, Key.PrivateKey.decode(privateKey1.stellarBase32Encode()))
        assertEquals(publicKey4, Key.PublicKey.decode(publicKey4.stellarBase32Encode()))
    }
}
