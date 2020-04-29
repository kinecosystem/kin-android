package kin.sdk.internal

import kin.sdk.BackupRestore
import kin.sdk.KeyStore
import kin.sdk.exception.CorruptedDataException
import kin.sdk.exception.CreateAccountException
import kin.sdk.exception.CryptoException
import kin.sdk.exception.DeleteAccountException
import kin.sdk.exception.LoadAccountException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.KeyPair.Companion.fromSecretSeed
import org.kin.stellarfork.KeyPair.Companion.random
import java.util.ArrayList

internal class KeyStoreImpl(
    private val store: Store,
    private val backupRestore: BackupRestore
) : KeyStore {
    @Throws(LoadAccountException::class)
    override fun loadAccounts(): List<KeyPair> {
        val accounts = ArrayList<KeyPair>()
        try {
            val jsonArray = loadJsonArray()
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val accountJson = jsonArray.getJSONObject(i)
                    val seed =
                        accountJson.getString(JSON_KEY_ENCRYPTED_SEED)
                    accounts.add(fromSecretSeed(seed))
                }
            }
        } catch (e: JSONException) {
            throw LoadAccountException(e.message, e)
        }
        return accounts
    }

    @Throws(JSONException::class)
    private fun loadJsonArray(): JSONArray? {
        val version = store.getString(VERSION_KEY)
        //ensure current version, drop data if it's a different version
        if (ENCRYPTION_VERSION_NAME == version) {
            val seedsJson = store.getString(STORE_KEY_ACCOUNTS)
            if (seedsJson != null) {
                val json = JSONObject(seedsJson)
                return json.getJSONArray(JSON_KEY_ACCOUNTS_ARRAY)
            }
        } else {
            store.clear(STORE_KEY_ACCOUNTS)
            store.saveString(
                VERSION_KEY,
                ENCRYPTION_VERSION_NAME
            )
        }
        return null
    }

    @Throws(DeleteAccountException::class)
    override fun deleteAccount(publicAddress: String) {
        val json = JSONObject()
        try {
            val jsonArray = loadJsonArray()
            if (jsonArray != null) {
                val newJsonArray = JSONArray()
                for (i in 0 until jsonArray.length()) {
                    val account = jsonArray[i]
                    if ((account as JSONObject)[JSON_KEY_PUBLIC_KEY] != publicAddress) {
                        newJsonArray.put(account)
                    }
                }
                json.put(JSON_KEY_ACCOUNTS_ARRAY, newJsonArray)
            }
        } catch (e: JSONException) {
            throw DeleteAccountException(e)
        }
        store.saveString(STORE_KEY_ACCOUNTS, json.toString())
    }

    @Throws(CreateAccountException::class)
    override fun newAccount(): KeyPair {
        return addKeyPairToStorage(random())
    }

    @Throws(CreateAccountException::class)
    private fun addKeyPairToStorage(newKeyPair: KeyPair): KeyPair {
        return try {
            val encryptedSeed = String(newKeyPair.secretSeed)
            val publicKey = newKeyPair.accountId
            val accounts =
                store.getString(STORE_KEY_ACCOUNTS)
            if (accounts.isNullOrEmpty() || !accounts.contains(publicKey)) {
                val accountsJson = addKeyPairToAccountsJson(encryptedSeed, publicKey)
                store.saveString(STORE_KEY_ACCOUNTS, accountsJson.toString())
            }
            newKeyPair
        } catch (e: JSONException) {
            throw CreateAccountException(e)
        }
    }

    @Throws(
        CryptoException::class,
        CreateAccountException::class,
        CorruptedDataException::class
    )
    override fun importAccount(
        json: String,
        passphrase: String
    ): KeyPair {
        val keyPair = backupRestore.importWallet(json, passphrase)
        return addKeyPairToStorage(keyPair)
    }

    @Throws(JSONException::class)
    private fun addKeyPairToAccountsJson(
        encryptedSeed: String,
        accountId: String
    ): JSONObject {
        var jsonArray = loadJsonArray()
        if (jsonArray == null) {
            jsonArray = JSONArray()
        }
        val accountJson = JSONObject()
        accountJson.put(JSON_KEY_ENCRYPTED_SEED, encryptedSeed)
        accountJson.put(JSON_KEY_PUBLIC_KEY, accountId)
        jsonArray.put(accountJson)
        val json = JSONObject()
        json.put(JSON_KEY_ACCOUNTS_ARRAY, jsonArray)
        return json
    }

    override fun clearAllAccounts() {
        store.clear(STORE_KEY_ACCOUNTS)
    }

    companion object {
        const val ENCRYPTION_VERSION_NAME = "none"
        const val STORE_KEY_ACCOUNTS = "accounts"
        const val VERSION_KEY = "encryptor_ver"
        private const val JSON_KEY_ACCOUNTS_ARRAY = "accounts"
        private const val JSON_KEY_PUBLIC_KEY = "public_key"
        private const val JSON_KEY_ENCRYPTED_SEED = "seed"
    }

}
