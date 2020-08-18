[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [KinPaymentItem](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`KinPaymentItem(amount: `[`KinAmount`](../-kin-amount/index.md)`, destinationAccount: Id, invoice: `[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Invoice`](../-invoice/index.md)`> = Optional.empty())`

### Parameters

`amount` -
* always the amount transferred in the payment

`destinationAccount` -
* the KinAccount.Id where the funds are to be transferred to

`invoice` -
* (optional) - an Invoice that this transfer refers to. [amount](amount.md) should match invoice.total, but is not strictly enforced. Where they differ [amount](amount.md) will be the kin actually transferred.
