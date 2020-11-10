package org.kin.sdk.base.models

import org.kin.sdk.base.tools.Base58
import org.kin.stellarfork.KeyPair

sealed class Key {
    abstract val value: ByteArray

    data class PublicKey constructor(override val value: ByteArray) : Key() {

        constructor(publicKeyString: String) : this(KeyPair.fromAccountId(publicKeyString).publicKey)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PublicKey

            if (!value.contentEquals(other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return value.contentHashCode()
        }

        override fun toString(): String {
            return "Key.PublicKey(value=${stellarBase32Encode()}, b58=${base58Encode()})"
        }

        fun stellarBase32Encode(): String = KeyPair.fromPublicKey(value).accountId

        fun base58Encode(): String = Base58.encode(value)

        fun verify(data: ByteArray, value: ByteArray): Boolean {
            return KeyPair.fromPublicKey(this.value).verify(data, value)
        }

        companion object {
            @JvmStatic
            fun decode(value: String): PublicKey = KeyPair.fromAccountId(value).asPublicKey()
        }
    }

    data class PrivateKey constructor(override val value: ByteArray) : Key() {

        constructor(privateKeyString: String) : this(KeyPair.fromSecretSeed(privateKeyString).rawSecretSeed!!)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PrivateKey

            if (!value.contentEquals(other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return value.contentHashCode()
        }

        override fun toString(): String {
            return "Key.PrivateKey(value=XXXXXXXX<Private>XXXXXXXX)"
        }

        fun stellarBase32Encode(): String = String(KeyPair.fromSecretSeed(value).secretSeed)

        fun base58Encode(): String = Base58.encode(value)

        fun sign(data: ByteArray): ByteArray = KeyPair.fromSecretSeed(value).sign(data)!!

        companion object {
            @JvmStatic
            fun decode(value: String): PrivateKey = KeyPair.fromSecretSeed(value).asPrivateKey()

            @JvmStatic
            fun random(): PrivateKey {
                return PrivateKey(KeyPair.random().rawSecretSeed!!)
            }
        }
    }
}
