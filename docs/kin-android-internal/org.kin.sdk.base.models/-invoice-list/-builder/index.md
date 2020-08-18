[kin-android](../../../index.md) / [org.kin.sdk.base.models](../../index.md) / [InvoiceList](../index.md) / [Builder](./index.md)

# Builder

`class Builder`

### Exceptions

| Name | Summary |
|---|---|
| [InvoiceListFormatException](-invoice-list-format-exception/index.md) | `data class InvoiceListFormatException : `[`IllegalArgumentException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-argument-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Builder()` |

### Functions

| Name | Summary |
|---|---|
| [addInvoice](add-invoice.md) | `fun addInvoice(invoice: `[`Invoice`](../../-invoice/index.md)`): Builder` |
| [addInvoices](add-invoices.md) | `fun addInvoices(invoices: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Invoice`](../../-invoice/index.md)`>): Builder` |
| [build](build.md) | `fun build(): `[`InvoiceList`](../index.md) |
