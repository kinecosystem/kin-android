[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [Invoice](./index.md)

# Invoice

`data class Invoice`

Contains the information about what a given [KinPayment](../-kin-payment/index.md) was for.

### Parameters

`id` -
* identifier for the [Invoice](./index.md) that contains a SHA-224 of the [lineItems](line-items.md) data

`lineItems` -
* 1-1024 [LineItem](../-line-item/index.md)s describing an itemized list of what the [Invoice](./index.md) is for.

### Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | `class Builder` |
| [Id](-id/index.md) | `data class Id` |

### Properties

| Name | Summary |
|---|---|
| [id](id.md) | <ul><li>identifier for the [Invoice](./index.md) that contains a SHA-224 of the [lineItems](line-items.md) data</li></ul>`val id: Id` |
| [lineItems](line-items.md) | <ul><li>1-1024 [LineItem](../-line-item/index.md)s describing an itemized list of what the [Invoice](./index.md) is for.</li></ul>`val lineItems: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`LineItem`](../-line-item/index.md)`>` |
| [total](total.md) | `val total: `[`KinAmount`](../-kin-amount/index.md) |

### Functions

| Name | Summary |
|---|---|
| [toProtoBytes](to-proto-bytes.md) | `fun toProtoBytes(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [parseFrom](parse-from.md) | `fun parseFrom(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`Invoice`](./index.md) |

### Extension Functions

| Name | Summary |
|---|---|
| [toProto](../../org.kin.sdk.base.network.api.agora/to-proto.md) | `fun `[`Invoice`](./index.md)`.toProto(): Invoice!` |
| [toRenderableInvoice](../../org.kin.sdk.base.viewmodel.utils/to-renderable-invoice.md) | `fun `[`Invoice`](./index.md)`.toRenderableInvoice(amountPaid: `[`KinAmount`](../-kin-amount/index.md)` = KinAmount.ZERO): RenderableInvoice` |
