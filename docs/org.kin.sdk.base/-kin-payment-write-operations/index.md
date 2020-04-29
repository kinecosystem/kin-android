[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinPaymentWriteOperations](./index.md)

# KinPaymentWriteOperations

`interface KinPaymentWriteOperations : `[`KinPaymentWriteOperationsAltIdioms`](../-kin-payment-write-operations-alt-idioms/index.md)

### Functions

| Name | Summary |
|---|---|
| [sendKinPayment](send-kin-payment.md) | Send an amount of Kin to a [destinationAccount](send-kin-payment.md#org.kin.sdk.base.KinPaymentWriteOperations$sendKinPayment(org.kin.sdk.base.models.KinAmount, org.kin.sdk.base.models.KinAccount.Id, org.kin.sdk.base.models.KinMemo)/destinationAccount) to the Kin Blockchain for processing.`abstract fun sendKinPayment(amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`, destinationAccount: Id, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)` = KinMemo.NONE): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |
| [sendKinPayments](send-kin-payments.md) | `abstract fun sendKinPayments(payments: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)` = KinMemo.NONE): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinAccountContext](../-kin-account-context/index.md) | `interface KinAccountContext : `[`KinAccountContextReadOnly`](../-kin-account-context-read-only/index.md)`, `[`KinPaymentWriteOperations`](./index.md) |
