[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountContextReadOnlyImpl](./index.md)

# KinAccountContextReadOnlyImpl

`class KinAccountContextReadOnlyImpl : `[`KinAccountContextBase`](../-kin-account-context-base/index.md)`, `[`KinAccountContextReadOnly`](../-kin-account-context-read-only/index.md)

Instantiate a [KinAccountContextReadOnlyImpl](./index.md) to operate on a [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) when you only have a [PublicKey](#)
Can be used to:

* get account data, payment history, and listen to changes over time

### Types

| Name | Summary |
|---|---|
| [ReadOnlyAccountBuilder](-read-only-account-builder/index.md) | Gives you read-only access to this [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md)`class ReadOnlyAccountBuilder` |

### Properties

| Name | Summary |
|---|---|
| [accountId](account-id.md) | denoting the [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) to get information from`val accountId: Id` |
| [executors](executors.md) | defines a set of executors to be used`val executors: `[`ExecutorServices`](../../org.kin.sdk.base.tools/-executor-services/index.md) |
| [logger](logger.md) | `val logger: `[`KinLoggerFactory`](../../org.kin.sdk.base.tools/-kin-logger-factory/index.md) |
| [service](service.md) | a service used to retrieve all account and payment data`val service: `[`KinService`](../../org.kin.sdk.base.network.services/-kin-service/index.md) |
| [storage](storage.md) | stores all account and payment data. @see [KinFileStorage](#) for provided implementation.`val storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md) |

### Functions

| Name | Summary |
|---|---|
| [clearStorage](clear-storage.md) | `fun clearStorage(clearCompleteCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getAccount](get-account.md) | Returns the account info`fun getAccount(forceUpdate: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [getPaymentsForTransactionHash](get-payments-for-transaction-hash.md) | `fun getPaymentsForTransactionHash(transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`, paymentsCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
