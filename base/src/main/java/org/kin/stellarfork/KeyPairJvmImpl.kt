package org.kin.stellarfork

import net.i2p.crypto.eddsa.EdDSAEngine
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import net.i2p.crypto.eddsa.KeyPairGenerator
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec
import org.kin.stellarfork.StrKey.decodeStellarAccountId
import org.kin.stellarfork.StrKey.decodeStellarSecretSeed
import org.kin.stellarfork.StrKey.encodeStellarAccountId
import org.kin.stellarfork.StrKey.encodeStellarSecretSeed
import org.kin.stellarfork.xdr.DecoratedSignature
import org.kin.stellarfork.xdr.PublicKey
import org.kin.stellarfork.xdr.PublicKeyType
import org.kin.stellarfork.xdr.SignatureHint
import org.kin.stellarfork.xdr.SignerKey
import org.kin.stellarfork.xdr.SignerKeyType
import org.kin.stellarfork.xdr.Uint256
import org.kin.stellarfork.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.SignatureException
import java.util.Arrays

/**
 * Holds a Stellar keypair.
 */
data class KeyPairJvmImpl @JvmOverloads
/**
 * Creates a new KeyPair from the given public and private keys.
 *
 * @param publicKey
 * @param privateKey
 */
/**
 * Creates a new KeyPair without a private key. Useful to simply verify a signature from a
 * given public address.
 *
 * @param publicKey
 */
constructor(
    private val mPublicKey: EdDSAPublicKey,
    private val mPrivateKey: EdDSAPrivateKey? = null,
) : IKeyPair {
    /**
     * Returns true if this Keypair is capable of signing
     */
    override fun canSign(): Boolean {
        return mPrivateKey != null
    }

    /**
     * Returns the human readable account ID encoded in strkey.
     */
    override val accountId: String
        get() = encodeStellarAccountId(mPublicKey.abyte)

    /**
     * Returns the human readable secret seed encoded in strkey.
     */
    override val secretSeed: CharArray
        get() = encodeStellarSecretSeed(mPrivateKey!!.seed)

    /**
     * Returns the raw 32 byte secret seed.
     */
    override val rawSecretSeed: ByteArray?
        get() = mPrivateKey?.seed

    override val publicKey: ByteArray
        get() = mPublicKey.abyte

    override val signatureHint: SignatureHint
        get() = try {
            val publicKeyBytesStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(publicKeyBytesStream)
            PublicKey.encode(xdrOutputStream, xdrPublicKey)
            val publicKeyBytes = publicKeyBytesStream.toByteArray()
            val signatureHintBytes = Arrays.copyOfRange(
                publicKeyBytes,
                publicKeyBytes.size - 4,
                publicKeyBytes.size
            )
            SignatureHint().apply { signatureHint = signatureHintBytes }
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    override val xdrPublicKey: PublicKey
        get() {
            return PublicKey().apply {
                discriminant = PublicKeyType.PUBLIC_KEY_TYPE_ED25519
                ed25519 = Uint256().apply { uint256 = publicKey }
            }
        }

    override val xdrSignerKey: SignerKey
        get() {
            return SignerKey().apply {
                discriminant = SignerKeyType.SIGNER_KEY_TYPE_ED25519
                ed25519 = Uint256().apply { uint256 = publicKey }
            }
        }

    /**
     * Sign the provided data with the keypair's private key.
     *
     * @param data The data to sign.
     * @return signed bytes, null if the private key for this keypair is null.
     */
    override fun sign(data: ByteArray?): ByteArray? {
        if (mPrivateKey == null) {
            throw RuntimeException("KeyPair does not contain secret key. Use KeyPair.fromSecretSeed method to create a new KeyPair with a secret key.")
        }
        return try {
            EdDSAEngine(MessageDigest.getInstance("SHA-512"))
                .apply {
                    initSign(mPrivateKey)
                    update(data)
                }.sign()
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Sign the provided data with the keypair's private key and returns [DecoratedSignature].
     *
     * @param data
     */
    override fun signDecorated(data: ByteArray?): DecoratedSignature {
        return DecoratedSignature().apply {
            hint = signatureHint
            signature = org.kin.stellarfork.xdr.Signature().apply { signature = sign(data) }
        }
    }

    /**
     * Verify the provided data and signature match this keypair's public key.
     *
     * @param data      The data that was signed.
     * @param signature The signature.
     * @return True if they match, false otherwise.
     * @throws RuntimeException
     */
    override fun verify(data: ByteArray?, signature: ByteArray?): Boolean {
        return try {
            EdDSAEngine(MessageDigest.getInstance("SHA-512"))
                .apply {
                    initVerify(mPublicKey)
                    update(data)
                }.verify(signature)
        } catch (e: SignatureException) {
            false
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val ed25519 = EdDSANamedCurveTable.ED_25519_CURVE_SPEC

        /**
         * Creates a new Stellar KeyPair from a strkey encoded Stellar secret seed.
         *
         * @param seed Char array containing strkey encoded Stellar secret seed.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromSecretSeed(seed: CharArray): KeyPairJvmImpl {
            val decoded = decodeStellarSecretSeed(seed)
            val keypair = fromSecretSeed(decoded)
            Arrays.fill(decoded, 0.toByte())
            return keypair
        }

        /**
         * **Insecure** Creates a new Stellar KeyPair from a strkey encoded Stellar secret seed.
         * This method is <u>insecure</u>. Use only if you are aware of security implications.
         *
         * @param seed The strkey encoded Stellar secret seed.
         * @return [KeyPair]
         * @see [Using Password-Based Encryption](http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html.PBEEx)
         */
        @JvmStatic
        fun fromSecretSeed(seed: String): KeyPairJvmImpl {
            val charSeed = seed.toCharArray()
            val decoded = decodeStellarSecretSeed(charSeed)
            val keypair = fromSecretSeed(decoded)
            Arrays.fill(charSeed, ' ')
            return keypair
        }

        /**
         * Creates a new Stellar keypair from a raw 32 byte secret seed.
         *
         * @param seed The 32 byte secret seed.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromSecretSeed(seed: ByteArray?): KeyPairJvmImpl {

            val privKeySpec = EdDSAPrivateKeySpec(seed, ed25519)
            val publicKeySpec = EdDSAPublicKeySpec(privKeySpec.a.toByteArray(), ed25519)
            return KeyPairJvmImpl(
                EdDSAPublicKey(publicKeySpec),
                EdDSAPrivateKey(privKeySpec)
            )
        }

        /**
         * Creates a new Stellar KeyPair from a strkey encoded Stellar account ID.
         *
         * @param accountId The strkey encoded Stellar account ID.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromAccountId(accountId: String): KeyPairJvmImpl =
            fromPublicKey(decodeStellarAccountId(accountId))

        /**
         * Creates a new Stellar keypair from a 32 byte address.
         *
         * @param publicKey The 32 byte public key.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromPublicKey(publicKey: ByteArray?): KeyPairJvmImpl {
            val publicKeySpec = EdDSAPublicKeySpec(publicKey, ed25519)
            return KeyPairJvmImpl(EdDSAPublicKey(publicKeySpec))
        }

        /**
         * Generates a random Stellar keypair.
         *
         * @return a random Stellar keypair.
         */
        @JvmStatic
        fun random(): KeyPairJvmImpl {
            val keypair = KeyPairGenerator().generateKeyPair()
            return KeyPairJvmImpl(
                keypair.public as EdDSAPublicKey,
                keypair.private as EdDSAPrivateKey
            )
        }
    }
}
