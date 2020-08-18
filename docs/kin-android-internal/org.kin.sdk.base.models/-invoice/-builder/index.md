[kin-android](../../../index.md) / [org.kin.sdk.base.models](../../index.md) / [Invoice](../index.md) / [Builder](./index.md)

# Builder

`class Builder`

### Exceptions

| Name | Summary |
|---|---|
| [InvoiceFormatException](-invoice-format-exception/index.md) | `data class InvoiceFormatException : `[`IllegalArgumentException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-argument-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Builder()` |

### Functions

| Name | Summary |
|---|---|
| [addLineItem](add-line-item.md) | `fun addLineItem(lineItem: `[`LineItem`](../../-line-item/index.md)`): Builder` |
| [addLineItems](add-line-items.md) | `fun addLineItems(lineItems: `[`Collection`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)`<`[`LineItem`](../../-line-item/index.md)`>): Builder` |
| [build](build.md) | `fun build(): `[`Invoice`](../index.md) |
