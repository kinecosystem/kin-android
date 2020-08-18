[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinPaymentReadOperationsAltIdioms](./index.md)

# KinPaymentReadOperationsAltIdioms

`interface KinPaymentReadOperationsAltIdioms`

### Functions

| Name | Summary |
|---|---|
| [getPaymentsForTransactionHash](get-payments-for-transaction-hash.md) | `abstract fun getPaymentsForTransactionHash(transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`, paymentsCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [observePayments](observe-payments.md) | `abstract fun observePayments(mode: `[`ObservationMode`](../-observation-mode/index.md)` = Passive, paymentsListener: `[`ValueListener`](../../org.kin.sdk.base.tools/-value-listener/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>): `[`ListObserver`](../../org.kin.sdk.base.tools/-list-observer/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinPaymentReadOperations](../-kin-payment-read-operations/index.md) | `interface KinPaymentReadOperations : `[`KinPaymentReadOperationsAltIdioms`](./index.md) |
