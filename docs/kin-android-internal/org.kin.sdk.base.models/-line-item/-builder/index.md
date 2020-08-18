[kin-android](../../../index.md) / [org.kin.sdk.base.models](../../index.md) / [LineItem](../index.md) / [Builder](./index.md)

# Builder

`data class Builder`

### Exceptions

| Name | Summary |
|---|---|
| [LineItemFormatException](-line-item-format-exception/index.md) | `data class LineItemFormatException : `[`IllegalArgumentException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-argument-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Builder(title: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, amount: `[`KinAmount`](../../-kin-amount/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | `val amount: `[`KinAmount`](../../-kin-amount/index.md) |
| [title](title.md) | `val title: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [build](build.md) | `fun build(): `[`LineItem`](../index.md) |
| [setDescription](set-description.md) | `fun setDescription(description: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Builder` |
| [setSKU](set-s-k-u.md) | `fun setSKU(sku: `[`SKU`](../../-s-k-u/index.md)`): Builder` |
