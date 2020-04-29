[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinPaymentWriteOperationsAltIdioms](./index.md)

# KinPaymentWriteOperationsAltIdioms

`interface KinPaymentWriteOperationsAltIdioms`

### Functions

| Name | Summary |
|---|---|
| [sendKinPayment](send-kin-payment.md) | `abstract fun sendKinPayment(amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`, destinationAccount: Id, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)` = KinMemo.NONE, paymentCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendKinPayments](send-kin-payments.md) | `abstract fun sendKinPayments(payments: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)` = KinMemo.NONE, paymentsCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [KinPaymentWriteOperations](../-kin-payment-write-operations/index.md) | `interface KinPaymentWriteOperations : `[`KinPaymentWriteOperationsAltIdioms`](./index.md) |
