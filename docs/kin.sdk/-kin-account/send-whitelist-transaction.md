[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [sendWhitelistTransaction](./send-whitelist-transaction.md)

# sendWhitelistTransaction

`@NonNull abstract fun sendWhitelistTransaction(whitelist: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Request`](../../kin.utils/-request/index.md)`<`[`TransactionId`](../-transaction-id/index.md)`!>`

Create ``[`Request`](../../kin.utils/-request/index.md) for signing and sending a transaction from a whitelist. whitelist a transaction means that the user will not pay any fee(if your App is in the Kin whitelist)

 See ``[`KinAccount#sendWhitelistTransactionSync(String)`](send-whitelist-transaction-sync.md) for possibles errors

### Parameters

`whitelist` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)!: is the whitelist data (got from the server) which will be used to send the transaction.

**Return**
[Request](../../kin.utils/-request/index.md)&lt;[TransactionId](../-transaction-id/index.md)!&gt;: `Request<TransactionId>`, TransactionId - the transaction identifier.

