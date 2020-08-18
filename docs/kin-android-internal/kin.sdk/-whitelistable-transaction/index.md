[kin-android](../../index.md) / [kin.sdk](../index.md) / [WhitelistableTransaction](./index.md)

# WhitelistableTransaction

`data class WhitelistableTransaction`

This class wraps a transaction envelope xdr in base 64(transaction payload)
and a network passphrase(the network id as string). *
Those fields are necessary for the whitelist server in order to sign this transaction to be a whitelist transaction.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | This class wraps a transaction envelope xdr in base 64(transaction payload) and a network passphrase(the network id as string). * Those fields are necessary for the whitelist server in order to sign this transaction to be a whitelist transaction.`WhitelistableTransaction(transactionPayload: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, networkPassphrase: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [networkPassphrase](network-passphrase.md) | `val networkPassphrase: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [transactionPayload](transaction-payload.md) | `val transactionPayload: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
