[kin-android](../../index.md) / [kin.sdk](../index.md) / [KeyStore](./index.md)

# KeyStore

`interface KeyStore`

### Functions

| Name | Summary |
|---|---|
| [clearAllAccounts](clear-all-accounts.md) | `abstract fun clearAllAccounts(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [deleteAccount](delete-account.md) | `abstract fun deleteAccount(publicAddress: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [importAccount](import-account.md) | `abstract fun importAccount(json: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, passphrase: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): KeyPair` |
| [loadAccounts](load-accounts.md) | `abstract fun loadAccounts(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KeyPair>` |
| [newAccount](new-account.md) | `abstract fun newAccount(): KeyPair` |
