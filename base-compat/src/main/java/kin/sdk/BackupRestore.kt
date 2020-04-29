package kin.sdk

import kin.sdk.exception.CorruptedDataException
import kin.sdk.exception.CryptoException
import org.kin.stellarfork.KeyPair

interface BackupRestore {
    @Throws(CryptoException::class)
    fun exportWallet(
        keyPair: KeyPair,
        passphrase: String
    ): String


    @Throws(CryptoException::class, CorruptedDataException::class)
    fun importWallet(
        exportedJson: String,
        passphrase: String
    ): KeyPair
}
