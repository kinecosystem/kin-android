[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [sendTransactionSync](./send-transaction-sync.md)

# sendTransactionSync

`@NonNull abstract fun sendTransactionSync(transaction: `[`Transaction`](../-transaction/index.md)`!): `[`TransactionId`](../-transaction-id/index.md)

send a transaction.

**Note:** This method accesses the network, and should not be called on the android main thread.

### Parameters

`transaction` - [Transaction](../-transaction/index.md)!: is the transaction object to send.

### Exceptions

`AccountNotFoundException` - if the sender or destination account was not created.

`InsufficientKinException` - if account balance has not enough kin.

`TransactionFailedException` - if transaction failed, contains blockchain failure details.

`OperationFailedException` - other error occurred.

**Return**
[TransactionId](../-transaction-id/index.md): TransactionId the transaction identifier.

