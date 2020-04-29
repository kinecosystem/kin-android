[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinPaymentWriteOperations](index.md) / [sendKinPayment](./send-kin-payment.md)

# sendKinPayment

`abstract fun sendKinPayment(amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`, destinationAccount: Id, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)` = KinMemo.NONE): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>`

Send an amount of Kin to a [destinationAccount](send-kin-payment.md#org.kin.sdk.base.KinPaymentWriteOperations$sendKinPayment(org.kin.sdk.base.models.KinAmount, org.kin.sdk.base.models.KinAccount.Id, org.kin.sdk.base.models.KinMemo)/destinationAccount) to the Kin Blockchain for processing.

### Parameters

`the` - amount of Kin to be sent

`destinationAccount` - the account the Kin is to be transferred to

`memo` - (optional) a memo can be provided to reference what the payment was for.
If no memo is desired, then set it to [KinMemo.NONE](../../org.kin.sdk.base.models/-kin-memo/-n-o-n-e.md)

**Return**
a [Promise](../../org.kin.sdk.base.tools/-promise/index.md) with the blockchain confirmed [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)

