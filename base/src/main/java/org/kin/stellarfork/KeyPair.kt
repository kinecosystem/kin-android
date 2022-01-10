package org.kin.stellarfork

import org.libsodium.jni.NaCl
import kotlin.properties.Delegates

interface IKeyPair {
    /**
     * Returns the human readable account ID encoded in strkey.
     */
    val accountId: String

    /**
     * Returns the human readable secret seed encoded in strkey.
     */
    val secretSeed: CharArray

    /**
     * Returns the 64 byte private key.
     */
    val privateKey: ByteArray?

    /**
     * Returns the raw 32 byte secret seed.
     */
    val rawSecretSeed: ByteArray?
    val publicKey: ByteArray

    /**
     * Returns true if this Keypair is capable of signing
     */
    fun canSign(): Boolean

    /**
     * Sign the provided data with the keypair's private key.
     *
     * @param data The data to sign.
     * @return signed bytes, null if the private key for this keypair is null.
     */
    fun sign(data: ByteArray?): ByteArray?

    /**
     * Verify the provided data and signature match this keypair's public key.
     *
     * @param data      The data that was signed.
     * @param signature The signature.
     * @return True if they match, false otherwise.
     * @throws RuntimeException
     */
    fun verify(data: ByteArray?, signature: ByteArray?): Boolean
}

/**
 * Holds a Stellar keypair.
 */
data class KeyPair
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
    private val impl: IKeyPair,
) : IKeyPair {
    /**
     * Returns true if this Keypair is capable of signing
     */
    override fun canSign(): Boolean = impl.canSign()

    /**
     * Returns the human readable account ID encoded in strkey.
     */
    override val accountId: String
        get() = impl.accountId

    /**
     * Returns the human readable secret seed encoded in strkey.
     */
    override val secretSeed: CharArray
        get() = impl.secretSeed

    /**
     * Returns the raw 32 byte secret seed.
     */
    override val rawSecretSeed: ByteArray?
        get() = impl.rawSecretSeed

    override val publicKey: ByteArray
        get() = impl.publicKey
    override val privateKey: ByteArray?
        get() = impl.privateKey

    /**
     * Sign the provided data with the keypair's private key.
     *
     * @param data The data to sign.
     * @return signed bytes, null if the private key for this keypair is null.
     */
    override fun sign(data: ByteArray?): ByteArray? = impl.sign(data)

    /**
     * Verify the provided data and signature match this keypair's public key.
     *
     * @param data      The data that was signed.
     * @param signature The signature.
     * @return True if they match, false otherwise.
     * @throws RuntimeException
     */
    override fun verify(data: ByteArray?, signature: ByteArray?): Boolean =
        impl.verify(data, signature)

    companion object {
        private var canUseNativeJNI by Delegates.notNull<Boolean>()

        init {
            canUseNativeJNI = try {
                NaCl.sodium()
                true
            } catch (t: Throwable) {
                false
            }
        }

        /**
         * Creates a new Stellar KeyPair from a strkey encoded Stellar secret seed.
         *
         * @param seed Char array containing strkey encoded Stellar secret seed.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromSecretSeed(seed: CharArray): KeyPair = KeyPair(
            if (canUseNativeJNI) KeyPairNativeJNIImpl.fromSecretSeed(seed)
            else KeyPairJvmImpl.fromSecretSeed(seed)
        )

        /**
         * **Insecure** Creates a new Stellar KeyPair from a strkey encoded Stellar secret seed.
         * This method is <u>insecure</u>. Use only if you are aware of security implications.
         *
         * @param seed The strkey encoded Stellar secret seed.
         * @return [KeyPair]
         * @see [Using Password-Based Encryption](http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html.PBEEx)
         */
        @JvmStatic
        fun fromSecretSeed(seed: String): KeyPair = KeyPair(
            if (canUseNativeJNI) KeyPairNativeJNIImpl.fromSecretSeed(seed)
            else KeyPairJvmImpl.fromSecretSeed(seed)
        )

        /**
         * Creates a new Stellar keypair from a raw 32 byte secret seed.
         *
         * @param seed The 32 byte secret seed.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromSecretSeed(seed: ByteArray): KeyPair = KeyPair(
            if (canUseNativeJNI) KeyPairNativeJNIImpl.fromSecretSeed(seed)
            else KeyPairJvmImpl.fromSecretSeed(seed)
        )

        /**
         * Creates a new keypair from a 64 byte private key.
         *
         * @param privateKey The 64 byte private key.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromPrivateKey(privateKey: ByteArray): KeyPair = KeyPair(
            KeyPairJvmImpl.fromPrivateKey(privateKey)
        )

        /**
         * Creates a new Stellar KeyPair from a strkey encoded Stellar account ID.
         *
         * @param accountId The strkey encoded Stellar account ID.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromAccountId(accountId: String): KeyPair = KeyPair(
            if (canUseNativeJNI) KeyPairNativeJNIImpl.fromAccountId(accountId)
            else KeyPairJvmImpl.fromAccountId(accountId)
        )

        /**
         * Creates a new Stellar keypair from a 32 byte address.
         *
         * @param publicKey The 32 byte public key.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromPublicKey(publicKey: ByteArray): KeyPair = KeyPair(
            if (canUseNativeJNI) KeyPairNativeJNIImpl.fromPublicKey(publicKey)
            else KeyPairJvmImpl.fromPublicKey(publicKey)
        )

        /**
         * Generates a random Stellar keypair.
         *
         * @return a random Stellar keypair.
         */
        @JvmStatic
        fun random(): KeyPair = KeyPair(
            if (canUseNativeJNI) KeyPairNativeJNIImpl.random()
            else KeyPairJvmImpl.random()
        )
    }
}
