[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [buildTransactionSync](./build-transaction-sync.md)

# buildTransactionSync

`abstract fun buildTransactionSync(@NonNull publicAddress: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @NonNull amount: `[`BigDecimal`](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html)`, fee: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Transaction`](../-transaction/index.md)`!`

Build a Transaction object of the given amount in kin, to the specified public address.

**Note:** This method accesses the network, and should not be called on the android main thread.

### Parameters

`publicAddress` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html): the account address to send the specified kin amount.

`amount` - [BigDecimal](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html): the amount of kin to transfer.

`fee` - [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html): the amount of fee(in stroops) for this transfer.

### Exceptions

`AccountNotFoundException` - if the sender or destination account was not created.

`OperationFailedException` - other error occurred.

**Return**
[Transaction](../-transaction/index.md)!: a Transaction object which also includes the transaction id.

`abstract fun buildTransactionSync(@NonNull publicAddress: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @NonNull amount: `[`BigDecimal`](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html)`, fee: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, @Nullable memo: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Transaction`](../-transaction/index.md)`!`

Build a Transaction object of the given amount in kin, to the specified public address and with a memo(that can be empty or null).

**Note:** This method accesses the network, and should not be called on the android main thread.

### Parameters

`publicAddress` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html): the account address to send the specified kin amount.

`amount` - [BigDecimal](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html): the amount of kin to transfer.

`fee` - [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html): the amount of fee(in stroops) for this transfer.

`memo` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?: An optional string, can contain a utf-8 string up to 21 bytes in length, included on the transaction record.

### Exceptions

`AccountNotFoundException` - if the sender or destination account was not created.

`OperationFailedException` - other error occurred.

**Return**
[Transaction](../-transaction/index.md)!: a Transaction object which also includes the transaction id.

