[kin-android](../../index.md) / [kin.sdk.exception](../index.md) / [OperationFailedException](./index.md)

# OperationFailedException

`open class OperationFailedException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `OperationFailedException(cause: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`?)`<br>`OperationFailedException(message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?)`<br>`OperationFailedException(message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, cause: `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html)`?)` |

### Inheritors

| Name | Summary |
|---|---|
| [AccountDeletedException](../-account-deleted-exception/index.md) | Account was deleted using [KinClient.deleteAccount](#), and cannot be used any more.`class AccountDeletedException : `[`OperationFailedException`](./index.md) |
| [AccountNotFoundException](../-account-not-found-exception/index.md) | Account was not created on the blockchain`class AccountNotFoundException : `[`OperationFailedException`](./index.md) |
| [IllegalAmountException](../-illegal-amount-exception/index.md) | amount was not legal`class IllegalAmountException : `[`OperationFailedException`](./index.md) |
| [InsufficientFeeException](../-insufficient-fee-exception/index.md) | `class InsufficientFeeException : `[`OperationFailedException`](./index.md) |
| [InsufficientKinException](../-insufficient-kin-exception/index.md) | Transaction failed due to insufficient kin.`class InsufficientKinException : `[`OperationFailedException`](./index.md) |
| [TransactionFailedException](../-transaction-failed-exception/index.md) | Blockchain transaction failure has happened, contains blockchain specific error details`class TransactionFailedException : `[`OperationFailedException`](./index.md) |
