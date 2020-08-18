[kin-android](../index.md) / [kin.sdk](./index.md)

## Package kin.sdk

### Types

| Name | Summary |
|---|---|
| [BackupRestore](-backup-restore/index.md) | `interface BackupRestore` |
| [Balance](-balance/index.md) | `interface Balance` |
| [Environment](-environment/index.md) | Provides blockchain network details`class Environment` |
| [EventListener](-event-listener/index.md) | `interface EventListener<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`!>` |
| [KeyStore](-key-store/index.md) | `interface KeyStore` |
| [KinAccount](-kin-account/index.md) | Represents an account which holds Kin.`interface KinAccount` |
| [KinClient](-kin-client/index.md) | An account manager for a [KinAccount].`open class KinClient` |
| [ListenerRegistration](-listener-registration/index.md) | Represents a listener to events, that can be removed using [.remove](#).`class ListenerRegistration` |
| [PaymentInfo](-payment-info/index.md) | Represents payment issued on the blockchain.`interface PaymentInfo` |
| [Transaction](-transaction/index.md) | `data class Transaction` |
| [TransactionId](-transaction-id/index.md) | Identifier of the transaction, useful for finding information about the transaction.`interface TransactionId` |
| [WhitelistableTransaction](-whitelistable-transaction/index.md) | This class wraps a transaction envelope xdr in base 64(transaction payload) and a network passphrase(the network id as string). * Those fields are necessary for the whitelist server in order to sign this transaction to be a whitelist transaction.`data class WhitelistableTransaction` |

### Annotations

| Name | Summary |
|---|---|
| [AccountStatus](-account-status/index.md) | `annotation class AccountStatus` |
