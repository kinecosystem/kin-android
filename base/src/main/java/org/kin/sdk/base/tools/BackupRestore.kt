package org.kin.sdk.base.tools

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
