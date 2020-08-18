[kin-android](../../index.md) / [org.kin.sdk.base.repository](../index.md) / [InMemoryInvoiceRepositoryImpl](./index.md)

# InMemoryInvoiceRepositoryImpl

`class InMemoryInvoiceRepositoryImpl : `[`InvoiceRepository`](../-invoice-repository/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `InMemoryInvoiceRepositoryImpl(storage: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<Id, `[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`> = mutableMapOf(), executorService: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)` = Executors.newSingleThreadExecutor())` |

### Functions

| Name | Summary |
|---|---|
| [addAllInvoices](add-all-invoices.md) | `fun addAllInvoices(invoices: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>>` |
| [addInvoice](add-invoice.md) | `fun addInvoice(invoice: `[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>` |
| [allInvoices](all-invoices.md) | `fun allInvoices(): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>>` |
| [invoiceById](invoice-by-id.md) | `fun invoiceById(id: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>>` |
