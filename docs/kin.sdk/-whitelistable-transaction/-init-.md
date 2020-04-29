[kin-android](../../index.md) / [kin.sdk](../index.md) / [WhitelistableTransaction](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`WhitelistableTransaction(transactionPayload: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, networkPassphrase: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

This class wraps a transaction envelope xdr in base 64(transaction payload)
and a network passphrase(the network id as string). *
Those fields are necessary for the whitelist server in order to sign this transaction to be a whitelist transaction.

