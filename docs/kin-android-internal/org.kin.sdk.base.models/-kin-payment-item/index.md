[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [KinPaymentItem](./index.md)

# KinPaymentItem

`data class KinPaymentItem`

### Parameters

`amount` -
* always the amount transferred in the payment

`destinationAccount` -
* the KinAccount.Id where the funds are to be transferred to

`invoice` -
* (optional) - an Invoice that this transfer refers to. [amount](amount.md) should match invoice.total, but is not strictly enforced. Where they differ [amount](amount.md) will be the kin actually transferred.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `KinPaymentItem(amount: `[`KinAmount`](../-kin-amount/index.md)`, destinationAccount: Id, invoice: `[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Invoice`](../-invoice/index.md)`> = Optional.empty())` |

### Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | <ul><li>always the amount transferred in the payment</li></ul>`val amount: `[`KinAmount`](../-kin-amount/index.md) |
| [destinationAccount](destination-account.md) | <ul><li>the KinAccount.Id where the funds are to be transferred to</li></ul>`val destinationAccount: Id` |
| [invoice](invoice.md) | <ul><li>(optional) - an Invoice that this transfer refers to. [amount](amount.md) should match invoice.total, but is not strictly enforced. Where they differ [amount](amount.md) will be the kin actually transferred.</li></ul>`val invoice: `[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Invoice`](../-invoice/index.md)`>` |
