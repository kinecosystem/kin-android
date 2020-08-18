[kin-android](../../index.md) / [kin.sdk.exception](../index.md) / [TransactionFailedException](./index.md)

# TransactionFailedException

`class TransactionFailedException : `[`OperationFailedException`](../-operation-failed-exception/index.md)

Blockchain transaction failure has happened, contains blockchain specific error details

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Blockchain transaction failure has happened, contains blockchain specific error details`TransactionFailedException(transactionResultCode: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, operationsResultCodes: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>?)` |

### Properties

| Name | Summary |
|---|---|
| [operationsResultCodes](operations-result-codes.md) | `val operationsResultCodes: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>?` |
| [transactionResultCode](transaction-result-code.md) | `val transactionResultCode: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
