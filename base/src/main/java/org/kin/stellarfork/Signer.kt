package org.kin.stellarfork

import org.kin.stellarfork.xdr.SignerKey
import org.kin.stellarfork.xdr.SignerKeyType
import org.kin.stellarfork.xdr.Uint256

/**
 * Signer is a helper class that creates [org.kin.stellarfork.xdr.SignerKey] objects.
 */
object Signer {
    /**
     * Create `ed25519PublicKey` [org.kin.stellarfork.xdr.SignerKey] from
     * a [KeyPair]
     *
     * @param keyPair
     * @return org.kin.stellarfork.xdr.SignerKey
     */
    @JvmStatic
    fun ed25519PublicKey(keyPair: KeyPair): SignerKey = keyPair.xdrSignerKey

    /**
     * Create `sha256Hash` [org.kin.stellarfork.xdr.SignerKey] from
     * a sha256 hash of a preimage.
     *
     * @param hash
     * @return org.kin.stellarfork.xdr.SignerKey
     */
    @JvmStatic
    fun sha256Hash(hash: ByteArray): SignerKey {
        return SignerKey().apply {
            discriminant = SignerKeyType.SIGNER_KEY_TYPE_HASH_X
            hashX = createUint256(hash)
        }
    }

    /**
     * Create `preAuthTx` [org.kin.stellarfork.xdr.SignerKey] from
     * a [org.kin.stellarfork.xdr.Transaction] hash.
     *
     * @param tx
     * @return org.kin.stellarfork.xdr.SignerKey
     */
    @JvmStatic
    fun preAuthTx(tx: Transaction): SignerKey {
        return SignerKey().apply {
            discriminant = SignerKeyType.SIGNER_KEY_TYPE_PRE_AUTH_TX
            preAuthTx = createUint256(tx.hash())
        }
    }

    /**
     * Create `preAuthTx` [org.kin.stellarfork.xdr.SignerKey] from
     * a transaction hash.
     *
     * @param hash
     * @return org.kin.stellarfork.xdr.SignerKey
     */
    @JvmStatic
    fun preAuthTx(hash: ByteArray): SignerKey {
        return SignerKey().apply {
            discriminant = SignerKeyType.SIGNER_KEY_TYPE_PRE_AUTH_TX
            preAuthTx = createUint256(hash)
        }
    }

    @JvmStatic
    private fun createUint256(hash: ByteArray): Uint256 {
        if (hash.size != 32) {
            throw RuntimeException("hash must be 32 bytes long")
        }
        val value = Uint256()
        value.uint256 = hash
        return value
    }
}
