@file:Suppress("DEPRECATION")

package org.kin.stellarfork

import org.kin.stellarfork.StrKey.decodeStellarAccountId
import org.kin.stellarfork.StrKey.decodeStellarSecretSeed
import org.kin.stellarfork.StrKey.encodeStellarAccountId
import org.kin.stellarfork.StrKey.encodeStellarSecretSeed
import org.kin.stellarfork.xdr.DecoratedSignature
import org.kin.stellarfork.xdr.PublicKeyType
import org.kin.stellarfork.xdr.SignatureHint
import org.kin.stellarfork.xdr.SignerKey
import org.kin.stellarfork.xdr.SignerKeyType
import org.kin.stellarfork.xdr.Uint256
import org.kin.stellarfork.xdr.XdrDataOutputStream
import org.libsodium.jni.NaCl
import org.libsodium.jni.Sodium
import org.libsodium.jni.SodiumConstants
import org.libsodium.jni.SodiumConstants.PUBLICKEY_BYTES
import org.libsodium.jni.SodiumConstants.SECRETKEY_BYTES
import org.libsodium.jni.crypto.Util
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.Arrays
import org.kin.stellarfork.xdr.PublicKey as XDRPublicKey

/**
 * Holds a Stellar keypair.
 */
data class KeyPairNativeJNIImpl @JvmOverloads
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
    private val mPublicKey: PublicKey,
    private val mPrivateKey: PrivateKey? = null,
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
        get() = encodeStellarAccountId(mPublicKey.bytes)

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
        get() = mPublicKey.bytes

    override val signatureHint: SignatureHint
        get() = try {
            val publicKeyBytesStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(publicKeyBytesStream)
            XDRPublicKey.encode(xdrOutputStream, xdrPublicKey)
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

    override val xdrPublicKey: XDRPublicKey
        get() {
            return XDRPublicKey().apply {
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
        mPrivateKey
            ?: throw RuntimeException("KeyPair does not contain secret key. Use KeyPair.fromSecretSeed method to create a new KeyPair with a secret key.")
        return try {
            val signature = Util.prependZeros(
                SodiumConstants.SIGNATURE_BYTES,
                data
            )
            val bufferLen = IntArray(1)
            val seed: ByteArray = mPrivateKey.seed
            val publicKey: ByteArray = Util.zeros(PUBLICKEY_BYTES)
            val secretKey: ByteArray = Util.zeros(SECRETKEY_BYTES * 2)
            Sodium.crypto_sign_ed25519_seed_keypair(publicKey, secretKey, seed)

            Sodium.crypto_sign_ed25519(
                signature,
                bufferLen,
                data,
                data!!.size,
                secretKey
            )
            Util.slice(
                signature,
                0,
                SodiumConstants.SIGNATURE_BYTES
            )
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
            Util.checkLength(
                signature,
                SodiumConstants.SIGNATURE_BYTES
            )
            val sigAndMsg = Util.merge(signature, data)
            val buffer = Util.zeros(sigAndMsg.size)
            val bufferLen = IntArray(1)

            Util.isValid(
                Sodium.crypto_sign_ed25519_open(
                    buffer,
                    bufferLen,
                    sigAndMsg,
                    sigAndMsg.size,
                    mPublicKey.bytes
                ), "signature was forged or corrupted"
            )
        } catch (t: Throwable) {
            false
        }
    }

    companion object {
        init {
            try {
                NaCl.sodium()
            } catch (t: Throwable) {
                // do nothing
            }
        }

        /**
         * Creates a new Stellar KeyPair from a strkey encoded Stellar secret seed.
         *
         * @param seed Char array containing strkey encoded Stellar secret seed.
         * @return [KeyPairNativeJNIImpl]
         */
        @JvmStatic
        fun fromSecretSeed(seed: CharArray): KeyPairNativeJNIImpl {
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
         * @return [KeyPairNativeJNIImpl]
         * @see [Using Password-Based Encryption](http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html.PBEEx)
         */
        @JvmStatic
        fun fromSecretSeed(seed: String): KeyPairNativeJNIImpl {
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
         * @return [KeyPairNativeJNIImpl]
         */
        @JvmStatic
        fun fromSecretSeed(seed: ByteArray): KeyPairNativeJNIImpl {
            val publicKey: ByteArray = Util.zeros(PUBLICKEY_BYTES)
            val secretKey: ByteArray = Util.zeros(SECRETKEY_BYTES * 2)
            Sodium.crypto_sign_ed25519_seed_keypair(publicKey, secretKey, seed)

            return KeyPairNativeJNIImpl(
                PublicKey(publicKey),
                PrivateKey(secretKey, seed)
            )
        }

        /**
         * Creates a new Stellar KeyPair from a strkey encoded Stellar account ID.
         *
         * @param accountId The strkey encoded Stellar account ID.
         * @return [KeyPairNativeJNIImpl]
         */
        @JvmStatic
        fun fromAccountId(accountId: String): KeyPairNativeJNIImpl =
            fromPublicKey(decodeStellarAccountId(accountId))

        /**
         * Creates a new Stellar keypair from a 32 byte address.
         *
         * @param publicKey The 32 byte public key.
         * @return [KeyPairNativeJNIImpl]
         */
        @JvmStatic
        fun fromPublicKey(publicKey: ByteArray): KeyPairNativeJNIImpl = KeyPairNativeJNIImpl(
            PublicKey(
                publicKey
            )
        )

        /**
         * Generates a random Stellar keypair.
         *
         * @return a random Stellar keypair.
         */
        @JvmStatic
        fun random(): KeyPairNativeJNIImpl {
            val publicKey: ByteArray = Util.zeros(PUBLICKEY_BYTES)
            val secretKey: ByteArray = Util.zeros(SECRETKEY_BYTES * 2)
            Sodium.crypto_box_curve25519xsalsa20poly1305_keypair(publicKey, secretKey)
            val seed = ByteArray(SECRETKEY_BYTES)
            Sodium.crypto_sign_ed25519_sk_to_seed(seed, secretKey)


            val publicKey2: ByteArray = Util.zeros(PUBLICKEY_BYTES)
            val secretKey2: ByteArray = Util.zeros(SECRETKEY_BYTES * 2)
            Sodium.crypto_sign_ed25519_seed_keypair(publicKey2, secretKey2, seed)

            return KeyPairNativeJNIImpl(
                PublicKey(publicKey2),
                PrivateKey(secretKey2, seed)
            )
        }
    }

    data class PublicKey(val bytes: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PublicKey) return false

            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }

    data class PrivateKey(val bytes: ByteArray, val seed: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PrivateKey) return false

            if (!bytes.contentEquals(other.bytes)) return false
            if (!seed.contentEquals(other.seed)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = bytes.contentHashCode()
            result = 31 * result + seed.contentHashCode()
            return result
        }
    }
}
