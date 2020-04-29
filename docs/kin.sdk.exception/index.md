[kin-android](../index.md) / [kin.sdk.exception](./index.md)

## Package kin.sdk.exception

### Exceptions

| Name | Summary |
|---|---|
| [AccountDeletedException](-account-deleted-exception/index.md) | Account was deleted using [KinClient.deleteAccount](#), and cannot be used any more.`class AccountDeletedException : `[`OperationFailedException`](-operation-failed-exception/index.md) |
| [AccountNotFoundException](-account-not-found-exception/index.md) | Account was not created on the blockchain`class AccountNotFoundException : `[`OperationFailedException`](-operation-failed-exception/index.md) |
| [CorruptedDataException](-corrupted-data-exception/index.md) | Input exported account data is corrupted and cannot be imported.`class CorruptedDataException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |
| [CreateAccountException](-create-account-exception/index.md) | `class CreateAccountException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |
| [CryptoException](-crypto-exception/index.md) | Decryption/Encryption error when importing - [KinClient.importAccount](#) or exporting [KinAccount.export](#) an account.`class CryptoException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |
| [DeleteAccountException](-delete-account-exception/index.md) | `class DeleteAccountException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |
| [IllegalAmountException](-illegal-amount-exception/index.md) | amount was not legal`class IllegalAmountException : `[`OperationFailedException`](-operation-failed-exception/index.md) |
| [InsufficientFeeException](-insufficient-fee-exception/index.md) | `class InsufficientFeeException : `[`OperationFailedException`](-operation-failed-exception/index.md) |
| [InsufficientKinException](-insufficient-kin-exception/index.md) | Transaction failed due to insufficient kin.`class InsufficientKinException : `[`OperationFailedException`](-operation-failed-exception/index.md) |
| [LoadAccountException](-load-account-exception/index.md) | `class LoadAccountException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |
| [OperationFailedException](-operation-failed-exception/index.md) | `open class OperationFailedException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |
| [TransactionFailedException](-transaction-failed-exception/index.md) | Blockchain transaction failure has happened, contains blockchain specific error details`class TransactionFailedException : `[`OperationFailedException`](-operation-failed-exception/index.md) |
