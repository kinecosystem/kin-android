[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [sendWhitelistTransactionSync](./send-whitelist-transaction-sync.md)

# sendWhitelistTransactionSync

`@NonNull abstract fun sendWhitelistTransactionSync(whitelist: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`TransactionId`](../-transaction-id/index.md)

send a whitelist transaction. whitelist a transaction means that the user will not pay any fee(if your App is in the Kin whitelist)

**Note:** This method accesses the network, and should not be called on the android main thread.

### Parameters

`whitelist` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)!: is the whitelist data (got from the server) which will be used to send the transaction.

### Exceptions

`AccountNotFoundException` - if the sender or destination account was not created.

`InsufficientKinException` - if account balance has not enough kin.

`TransactionFailedException` - if transaction failed, contains blockchain failure details.

`OperationFailedException` - other error occurred.

**Return**
[TransactionId](../-transaction-id/index.md): TransactionId the transaction identifier.

