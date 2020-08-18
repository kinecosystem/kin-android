[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountContextImpl](index.md) / [sendKinPayments](./send-kin-payments.md)

# sendKinPayments

`fun sendKinPayments(payments: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>`

### Parameters

`memo` - (optional) a memo can be provided to reference what thes batch of payments
were for. If no memo is desired, then set it to [KinMemo.NONE](../../org.kin.sdk.base.models/-kin-memo/-n-o-n-e.md)

**See Also**

[sendKinPayment](../-kin-payment-write-operations/send-kin-payment.md)

**Return**
a [Promise](../../org.kin.sdk.base.tools/-promise/index.md) with the blockchain confirmed [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s or an error

`fun sendKinPayments(payments: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`, paymentsCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

**See Also**

[KinPaymentWriteOperations.sendKinPayments](../-kin-payment-write-operations/send-kin-payments.md)

