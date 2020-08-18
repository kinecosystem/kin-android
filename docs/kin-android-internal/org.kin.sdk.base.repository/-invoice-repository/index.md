[kin-android](../../index.md) / [org.kin.sdk.base.repository](../index.md) / [InvoiceRepository](./index.md)

# InvoiceRepository

`interface InvoiceRepository`

### Functions

| Name | Summary |
|---|---|
| [addAllInvoices](add-all-invoices.md) | `abstract fun addAllInvoices(invoices: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>>` |
| [addInvoice](add-invoice.md) | `abstract fun addInvoice(invoice: `[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>` |
| [allInvoices](all-invoices.md) | `abstract fun allInvoices(): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>>` |
| [invoiceById](invoice-by-id.md) | `abstract fun invoiceById(id: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>>` |

### Inheritors

| Name | Summary |
|---|---|
| [InMemoryInvoiceRepositoryImpl](../-in-memory-invoice-repository-impl/index.md) | `class InMemoryInvoiceRepositoryImpl : `[`InvoiceRepository`](./index.md) |
