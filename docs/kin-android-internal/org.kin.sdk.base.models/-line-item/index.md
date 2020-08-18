[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [LineItem](./index.md)

# LineItem

`data class LineItem`

An individual item in an [Invoice](../-invoice/index.md)

### Parameters

`title` -
* 1-128 characters of renderable text describing the item.

`description` -
* Optional 0-256 characters of renderable text describing the item

`amount` -
* the [KinAmount](../-kin-amount/index.md) that the item costs

`sku` -
* an app defined identifier to key the [LineItem](./index.md) on. Should at least be unique per item, if not per item + user who is purchasing it.

### Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | `data class Builder` |

### Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | <ul><li>the [KinAmount](../-kin-amount/index.md) that the item costs</li></ul>`val amount: `[`KinAmount`](../-kin-amount/index.md) |
| [description](description.md) | <ul><li>Optional 0-256 characters of renderable text describing the item</li></ul>`val description: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [sku](sku.md) | <ul><li>an app defined identifier to key the [LineItem](./index.md) on. Should at least be unique per item, if not per item + user who is purchasing it.</li></ul>`val sku: `[`SKU`](../-s-k-u/index.md)`?` |
| [title](title.md) | <ul><li>1-128 characters of renderable text describing the item.</li></ul>`val title: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Extension Functions

| Name | Summary |
|---|---|
| [toProto](../../org.kin.sdk.base.network.api.agora/to-proto.md) | `fun `[`LineItem`](./index.md)`.toProto(): LineItem!` |
