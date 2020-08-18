[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [buildTransaction](./build-transaction.md)

# buildTransaction

`abstract fun buildTransaction(@NonNull publicAddress: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @NonNull amount: `[`BigDecimal`](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html)`, fee: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Request`](../../kin.utils/-request/index.md)`<`[`Transaction`](../-transaction/index.md)`!>!`

Build a Transaction object of the given amount in kin, to the specified public address.

 See ``[`KinAccount#buildTransactionSync(String, BigDecimal, int)`](build-transaction-sync.md) for possibles errors

### Parameters

`publicAddress` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html): the account address to send the specified kin amount.

`amount` - [BigDecimal](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html): the amount of kin to transfer.

`fee` - [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html): the amount of fee(in stroops) for this transfer.

**Return**
[Request](../../kin.utils/-request/index.md)&lt;[Transaction](../-transaction/index.md)!&gt;!: `Request<TransactionId>`, TransactionId - the transaction identifier.

`abstract fun buildTransaction(@NonNull publicAddress: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, @NonNull amount: `[`BigDecimal`](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html)`, fee: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, @Nullable memo: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Request`](../../kin.utils/-request/index.md)`<`[`Transaction`](../-transaction/index.md)`!>!`

Build a Transaction object of the given amount in kin, to the specified public address and with a memo(that can be empty or null).

 See ``[`KinAccount#buildTransactionSync(String, BigDecimal, int, String)`](build-transaction-sync.md) for possibles errors

### Parameters

`publicAddress` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html): the account address to send the specified kin amount.

`amount` - [BigDecimal](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html): the amount of kin to transfer.

`fee` - [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html): the amount of fee(in stroops) for this transfer.

`memo` - [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?: An optional string, can contain a utf-8 string up to 21 bytes in length, included on the transaction record.

**Return**
[Request](../../kin.utils/-request/index.md)&lt;[Transaction](../-transaction/index.md)!&gt;!: `Request<TransactionId>`, TransactionId - the transaction identifier

