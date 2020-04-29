package kin.sdk.internal

import kin.sdk.BackupRestore
import kin.sdk.exception.CorruptedDataException
import kin.sdk.exception.CryptoException
import org.json.JSONException
import org.json.JSONObject
import org.kin.stellarfork.KeyPair
import org.libsodium.jni.NaCl
import org.libsodium.jni.Sodium
import java.io.UnsupportedEncodingException

internal class BackupRestoreImpl : BackupRestore {
    private companion object {
        private const val SALT_LENGTH_BYTES = 16
        private const val HASH_LENGTH_BYTES = 32

        init {
            try {
                NaCl.sodium()
            } catch (t: Throwable) {
                // do nothing
            }
        }

        private fun generateRandomBytes(len: Int): ByteArray =
            ByteArray(len).also { randomBuffer ->
                Sodium.randombytes_buf(
                    randomBuffer,
                    len
                )
            }

        @Throws(CryptoException::class)
        private fun keyHash(passphraseBytes: ByteArray, saltBytes: ByteArray): ByteArray =
            ByteArray(HASH_LENGTH_BYTES)
                .also { hash ->
                    val keyHashSuccess = Sodium.crypto_pwhash(
                        hash,
                        HASH_LENGTH_BYTES,
                        passphraseBytes,
                        passphraseBytes.size,
                        saltBytes,
                        Sodium.crypto_pwhash_opslimit_interactive(),
                        Sodium.crypto_pwhash_memlimit_interactive(),
                        Sodium.crypto_pwhash_alg_default()
                    ) == 0

                    if (!keyHashSuccess) {
                        throw CryptoException("Generating hash failed.")
                    }
                }

        @Throws(CryptoException::class)
        private fun decryptSecretSeed(seedBytes: ByteArray, keyHash: ByteArray): ByteArray {
            val nonceBytes = seedBytes.copyOfRange(0, Sodium.crypto_secretbox_noncebytes())
            val cipherBytes = seedBytes.copyOfRange(nonceBytes.size, seedBytes.size)

            val decryptedBytes = ByteArray(cipherBytes.size - Sodium.crypto_secretbox_macbytes())
            val decryptionSuccess =
                Sodium.crypto_secretbox_open_easy(
                    decryptedBytes,
                    cipherBytes,
                    cipherBytes.size,
                    nonceBytes,
                    keyHash
                ) == 0

            if (!decryptionSuccess) {
                throw CryptoException("Decrypting data failed.")
            }
            return decryptedBytes
        }

        @Throws(CryptoException::class)
        private fun encryptSecretSeed(hash: ByteArray, secretSeedBytes: ByteArray): ByteArray {
            val cipherText = ByteArray(secretSeedBytes.size + Sodium.crypto_secretbox_macbytes())
            val nonceBytes = Companion.generateRandomBytes(Sodium.crypto_secretbox_noncebytes())

            val encryptionSuccess =
                Sodium.crypto_secretbox_easy(
                    cipherText,
                    secretSeedBytes,
                    secretSeedBytes.size,
                    nonceBytes, hash
                ) == 0

            if (!encryptionSuccess) {
                throw CryptoException("Encrypting data failed.")
            }
            return nonceBytes + cipherText
        }
    }

    @Throws(CryptoException::class)
    fun exportAccountBackup(keyPair: KeyPair, passphrase: String): AccountBackup {
        val saltBytes = Companion.generateRandomBytes(SALT_LENGTH_BYTES)

        val passphraseBytes = passphrase.toUTF8ByteArray()
        val hash = Companion.keyHash(passphraseBytes, saltBytes)
        val secretSeedBytes = keyPair.rawSecretSeed

        val encryptedSeed = Companion.encryptSecretSeed(hash, secretSeedBytes!!)

        val salt = saltBytes.bytesToHex()
        val seed = encryptedSeed.bytesToHex()
        return AccountBackup(keyPair.accountId, salt, seed)
    }

    @Throws(CryptoException::class)
    fun importAccountBackup(accountBackup: AccountBackup, passphrase: String): KeyPair {
        val passphraseBytes = passphrase.toUTF8ByteArray()
        val saltBytes = accountBackup.saltHexString.hexStringToByteArray()
        val keyHash = Companion.keyHash(passphraseBytes, saltBytes)
        val seedBytes = accountBackup.encryptedSeedHexString.hexStringToByteArray()

        val decryptedBytes = Companion.decryptSecretSeed(seedBytes, keyHash)
        return KeyPair.fromSecretSeed(decryptedBytes)
    }

    override fun exportWallet(keyPair: KeyPair, passphrase: String): String =
        exportAccountBackup(keyPair, passphrase).jsonString

    override fun importWallet(exportedJson: String, passphrase: String): KeyPair {
        return try {
            importAccountBackup(AccountBackup(exportedJson), passphrase)
        } catch (e: JSONException) {
            throw CorruptedDataException(e)
        }
    }


    @Throws(CryptoException::class)
    private fun String.toUTF8ByteArray(): ByteArray =
        try {
            toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            throw CryptoException(e)
        }

    data class AccountBackup(
        val publicAddress: String,
        val saltHexString: String,
        val encryptedSeedHexString: String
    ) {
        private companion object {
            private const val JSON_KEY_PUBLIC_KEY = "pkey"
            private const val JSON_KEY_SEED = "seed"
            private const val JSON_KEY_SALT = "salt"
            private const val OUTPUT_JSON_INDENT_SPACES = 2
        }

        private constructor(jsonObject: JSONObject) : this(
            jsonObject.getString(JSON_KEY_PUBLIC_KEY),
            jsonObject.getString(JSON_KEY_SALT),
            jsonObject.getString(JSON_KEY_SEED)
        )

        constructor(jsonString: String) : this(JSONObject(jsonString))

        val jsonString: String by lazy {
            JSONObject()
                .apply {
                    put(JSON_KEY_PUBLIC_KEY, publicAddress)
                    put(JSON_KEY_SEED, encryptedSeedHexString)
                    put(JSON_KEY_SALT, saltHexString)
                }
                .toString(OUTPUT_JSON_INDENT_SPACES)
        }

        override fun toString(): String {
            return jsonString
        }
    }

    private fun String.hexStringToByteArray(): ByteArray =
        ByteArray(length / 2)
            .also {
                for (i in 0 until length step 2) {
                    it[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(
                        this[i + 1],
                        16
                    )).toByte()
                }
            }

    private fun ByteArray.bytesToHex(): String =
        StringBuilder()
            .also { sb ->
                forEach {
                    sb.append(String.format("%02x", it))
                }
            }
            .toString()

}
