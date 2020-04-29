[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinPaymentReadOperations](./index.md)

# KinPaymentReadOperations

`interface KinPaymentReadOperations : `[`KinPaymentReadOperationsAltIdioms`](../-kin-payment-read-operations-alt-idioms/index.md)

### Functions

| Name | Summary |
|---|---|
| [getPaymentsForTransactionHash](get-payments-for-transaction-hash.md) | Retrieves the [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s that were processed in the referred [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`abstract fun getPaymentsForTransactionHash(transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>` |
| [observePayments](observe-payments.md) | Retrieves the last N [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s sent or received by the account and listens for future payments over time.`abstract fun observePayments(mode: `[`ObservationMode`](../-observation-mode/index.md)` = Passive): `[`ListObserver`](../../org.kin.sdk.base.tools/-list-observer/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinAccountContextBase](../-kin-account-context-base/index.md) | `abstract class KinAccountContextBase : `[`KinAccountReadOperations`](../-kin-account-read-operations/index.md)`, `[`KinPaymentReadOperations`](./index.md) |
| [KinAccountContextReadOnly](../-kin-account-context-read-only/index.md) | `interface KinAccountContextReadOnly : `[`KinAccountReadOperations`](../-kin-account-read-operations/index.md)`, `[`KinPaymentReadOperations`](./index.md) |
