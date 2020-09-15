[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountContextBase](./index.md)

# KinAccountContextBase

`abstract class KinAccountContextBase : `[`KinAccountReadOperations`](../-kin-account-read-operations/index.md)`, `[`KinPaymentReadOperations`](../-kin-payment-read-operations/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `KinAccountContextBase()` |

### Properties

| Name | Summary |
|---|---|
| [accountId](account-id.md) | `abstract val accountId: Id` |
| [executors](executors.md) | `abstract val executors: `[`ExecutorServices`](../../org.kin.sdk.base.tools/-executor-services/index.md) |
| [logger](logger.md) | `abstract val logger: `[`KinLoggerFactory`](../../org.kin.sdk.base.tools/-kin-logger-factory/index.md) |
| [service](service.md) | `abstract val service: `[`KinService`](../../org.kin.sdk.base.network.services/-kin-service/index.md) |
| [storage](storage.md) | `abstract val storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md) |

### Functions

| Name | Summary |
|---|---|
| [calculateFee](calculate-fee.md) | `open fun calculateFee(numberOfOperations: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>` |
| [clearStorage](clear-storage.md) | Deletes the storage associated with the [accountId](#)`open fun clearStorage(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [computeExpectedNewBalance](compute-expected-new-balance.md) | `fun computeExpectedNewBalance(transaction: `[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>` |
| [fetchUpdatedBalance](fetch-updated-balance.md) | `fun fetchUpdatedBalance(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>` |
| [fetchUpdatedTransactionHistory](fetch-updated-transaction-history.md) | `fun fetchUpdatedTransactionHistory(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [getAccount](get-account.md) | Returns the account info`open fun getAccount(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>``open fun getAccount(forceUpdate: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, accountCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getPaymentsForTransactionHash](get-payments-for-transaction-hash.md) | Retrieves the [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s that were processed in the referred [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`open fun getPaymentsForTransactionHash(transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>` |
| [maybeFetchAccountDetails](maybe-fetch-account-details.md) | `fun maybeFetchAccountDetails(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [observeBalance](observe-balance.md) | Returns the current [Balance](#) and listens to future account balance changes.`open fun observeBalance(mode: `[`ObservationMode`](../-observation-mode/index.md)`): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>``open fun observeBalance(mode: `[`ObservationMode`](../-observation-mode/index.md)`, balanceListener: `[`ValueListener`](../../org.kin.sdk.base.tools/-value-listener/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>` |
| [observePayments](observe-payments.md) | Retrieves the last N [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s sent or received by the account and listens for future payments over time.`open fun observePayments(mode: `[`ObservationMode`](../-observation-mode/index.md)`): `[`ListObserver`](../../org.kin.sdk.base.tools/-list-observer/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>``open fun observePayments(mode: `[`ObservationMode`](../-observation-mode/index.md)`, paymentsListener: `[`ValueListener`](../../org.kin.sdk.base.tools/-value-listener/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>): `[`ListObserver`](../../org.kin.sdk.base.tools/-list-observer/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |
| [storeAndNotifyOfBalanceUpdate](store-and-notify-of-balance-update.md) | `fun storeAndNotifyOfBalanceUpdate(newBalance: `[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [KinAccountContextImpl](../-kin-account-context-impl/index.md) | Instantiate a [KinAccountContextImpl](../-kin-account-context-impl/index.md) to operate on a [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) when you have a [PrivateKey](#) Can be used to:`class KinAccountContextImpl : `[`KinAccountContextBase`](./index.md)`, `[`KinAccountContext`](../-kin-account-context/index.md) |
| [KinAccountContextReadOnlyImpl](../-kin-account-context-read-only-impl/index.md) | Instantiate a [KinAccountContextReadOnlyImpl](../-kin-account-context-read-only-impl/index.md) to operate on a [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) when you only have a [PublicKey](#) Can be used to:`class KinAccountContextReadOnlyImpl : `[`KinAccountContextBase`](./index.md)`, `[`KinAccountContextReadOnly`](../-kin-account-context-read-only/index.md) |
