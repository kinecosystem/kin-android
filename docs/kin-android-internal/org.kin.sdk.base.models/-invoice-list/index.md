[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [InvoiceList](./index.md)

# InvoiceList

`data class InvoiceList`

A collection of [Invoice](../-invoice/index.md)s.
Often submitted in the same [KinTransaction](#) together.

### Parameters

`id` -
* identifier for the [InvoiceList](./index.md) that contains a SHA-224 of the [invoices](invoices.md) data

`invoices` -
* all the [Invoice](../-invoice/index.md)s in the list

### Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | `class Builder` |
| [Id](-id/index.md) | `data class Id` |

### Properties

| Name | Summary |
|---|---|
| [id](id.md) | <ul><li>identifier for the [InvoiceList](./index.md) that contains a SHA-224 of the [invoices](invoices.md) data</li></ul>`val id: Id` |
| [invoices](invoices.md) | <ul><li>all the [Invoice](../-invoice/index.md)s in the list</li></ul>`val invoices: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Invoice`](../-invoice/index.md)`>` |

### Extension Functions

| Name | Summary |
|---|---|
| [toProto](../../org.kin.sdk.base.network.api.agora/to-proto.md) | `fun `[`InvoiceList`](./index.md)`.toProto(): InvoiceList!` |
