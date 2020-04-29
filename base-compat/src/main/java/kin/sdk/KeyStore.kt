package kin.sdk

import kin.sdk.exception.CorruptedDataException
import kin.sdk.exception.CreateAccountException
import kin.sdk.exception.CryptoException
import kin.sdk.exception.DeleteAccountException
import kin.sdk.exception.LoadAccountException
import org.kin.stellarfork.KeyPair

interface KeyStore {
    @Throws(LoadAccountException::class)
    fun loadAccounts(): List<KeyPair>

    @Throws(DeleteAccountException::class)
    fun deleteAccount(publicAddress: String)

    @Throws(CreateAccountException::class)
    fun newAccount(): KeyPair

    @Throws(
        CryptoException::class,
        CreateAccountException::class,
        CorruptedDataException::class
    )
    fun importAccount(
        json: String,
        passphrase: String
    ): KeyPair

    fun clearAllAccounts()
}
