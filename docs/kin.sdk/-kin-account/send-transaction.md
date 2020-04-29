[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [sendTransaction](./send-transaction.md)

# sendTransaction

`@NonNull abstract fun sendTransaction(transaction: `[`Transaction`](../-transaction/index.md)`!): `[`Request`](../../kin.utils/-request/index.md)`<`[`TransactionId`](../-transaction-id/index.md)`!>`

Create ``[`Request`](../../kin.utils/-request/index.md) for signing and sending a transaction

 See ``[`KinAccount#sendTransactionSync(Transaction)`](send-transaction-sync.md) for possibles errors

### Parameters

`transaction` - [Transaction](../-transaction/index.md)!: is the transaction object to send.

**Return**
[Request](../../kin.utils/-request/index.md)&lt;[TransactionId](../-transaction-id/index.md)!&gt;: `Request<TransactionId>`, TransactionId - the transaction identifier.

