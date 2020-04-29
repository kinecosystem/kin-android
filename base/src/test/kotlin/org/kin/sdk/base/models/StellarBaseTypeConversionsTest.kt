package org.kin.sdk.base.models

import org.kin.stellarfork.KeyPair
import org.junit.Test
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
    fun a() {
        val keyPairStart = KeyPair.random()
        val signingAccount = KinAccount(keyPairStart.asPrivateKey())

        assertEquals(keyPairStart.rawSecretSeed, signingAccount.key.value)
        assertEquals(keyPairStart.rawSecretSeed, signingAccount.toSigningKeyPair().rawSecretSeed)
    }
}
