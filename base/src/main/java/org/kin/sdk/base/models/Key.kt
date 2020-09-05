package org.kin.sdk.base.models

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
            return "Key.PublicKey(value=${encode()})"
        }

        fun encode(): String = KeyPair.fromPublicKey(value).accountId

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

        fun encode(): String = String(KeyPair.fromSecretSeed(value).secretSeed)

        companion object {
            @JvmStatic
            fun decode(value: String): PrivateKey = KeyPair.fromSecretSeed(value).asPrivateKey()
        }
    }
}
