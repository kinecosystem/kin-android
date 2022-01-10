package org.kin.sdk.base.models

import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec
import org.kin.stellarfork.KeyPair
import org.junit.Test
import org.kin.stellarfork.KeyPairJvmImpl
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StellarBaseTypeConversionsTest {

    @Test
    fun KinAccountIdToPublicKeyKeyPair() {
        val keyPairStart = KeyPair.random()
        val pubKey = keyPairStart.asPublicKey()
        val accountId = KinAccount.Id(pubKey.value)
        val keyPairEnd = accountId.toKeyPair()
        assertEquals(keyPairStart.accountId, keyPairEnd.accountId)
    }

    @Test
    fun KinAccountToSigningKeyPair() {
        val keyPairStart = KeyPair.random()
        val kinAccount = KinAccount(keyPairStart.asPrivateKey())
        val keyPairEnd = kinAccount.toSigningKeyPair()
        assertEquals(keyPairStart, keyPairEnd)
    }

    @Test
    fun PrivateKeyAsPublicKeyWithSeed() {
        val privateKey = Key.PrivateKey.random()
        val keyPair = KeyPair.fromSecretSeed(privateKey.value)
        val expectedPublicKey = keyPair.asPublicKey()
        val publicKey = privateKey.asPublicKey()
        assertEquals(expectedPublicKey, publicKey)
    }

    @Test
    fun PrivateKeyAsPublicKeyWithoutSeed() {
        val expectedPublicKey = Key.PublicKey("BrKcWNX5N7xM358U9Xyj6MExcmi6X6Zf3sR1siyTyJ52")
        val privateKeyBytes = byteArrayOf(16, -119, 46, 117, -32, -90, -116, 112, -124, -18, 22, -125, -31, -22, -3, -119, 100, 69, -128, 60, -53, -40, 110, -10, 90, -115, 8, -115, 104, -119, 75, 119, 15, 118, 11, 77, 19, 44, 16, 59, -108, -119, 67, -34, 119, -80, -66, 4, -46, 55, -8, -67, -82, -91, -115, -42, 57, -24, 18, -19, -101, 65, -14, -41)
        val privKeySpec = EdDSAPrivateKeySpec(KeyPairJvmImpl.ed25519, privateKeyBytes)
        val publicKeySpec = EdDSAPublicKeySpec(privKeySpec.a.toByteArray(), KeyPairJvmImpl.ed25519)
        val i = KeyPairJvmImpl(
            EdDSAPublicKey(publicKeySpec),
            EdDSAPrivateKey(privKeySpec)
        )
        val keyPairWithoutSeed = KeyPair(i)
        val privateKey = Key.PrivateKey(keyPairWithoutSeed.privateKey!!)
        val publicKey = privateKey.asPublicKey()
        assertEquals(expectedPublicKey, publicKey)
    }

    @Test
    fun KeyPairWithSeedAsPrivateKey() {
        val keyPair = KeyPair.random()
        val keyPairKeyBytes = keyPair.rawSecretSeed
        val privateKey = keyPair.asPrivateKey()
        val privateKeyBytes = privateKey.value
        assertEquals(keyPairKeyBytes, privateKeyBytes)
    }

    @Test
    fun a() {
        val keyPairStart = KeyPair.random()
        val signingAccount = KinAccount(keyPairStart.asPrivateKey())

        assertEquals(keyPairStart.rawSecretSeed, signingAccount.key.value)
        assertEquals(keyPairStart.rawSecretSeed, signingAccount.toSigningKeyPair().rawSecretSeed)
    }
}
