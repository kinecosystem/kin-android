[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountContextImpl](index.md) / [sendKinPayment](./send-kin-payment.md)

# sendKinPayment

`fun sendKinPayment(amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`, destinationAccount: Id, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>`

Send an amount of Kin to a [destinationAccount](../-kin-payment-write-operations/send-kin-payment.md#org.kin.sdk.base.KinPaymentWriteOperations$sendKinPayment(org.kin.sdk.base.models.KinAmount, org.kin.sdk.base.models.KinAccount.Id, org.kin.sdk.base.models.KinMemo)/destinationAccount) to the Kin Blockchain for processing.

### Parameters

`the` - amount of Kin to be sent

`destinationAccount` - the account the Kin is to be transferred to

`memo` - (optional) a memo can be provided to reference what the payment was for.
If no memo is desired, then set it to [KinMemo.NONE](../../org.kin.sdk.base.models/-kin-memo/-n-o-n-e.md)

**Return**
a [Promise](../../org.kin.sdk.base.tools/-promise/index.md) with the blockchain confirmed [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)

`fun sendKinPayment(amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`, destinationAccount: Id, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`, paymentCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

**See Also**

[KinPaymentWriteOperations.sendKinPayment](../-kin-payment-write-operations/send-kin-payment.md)

