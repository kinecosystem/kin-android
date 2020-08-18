[kin-android](../../../../../../index.md) / [org.kin.sdk.base.network.api](../../../../../index.md) / [KinTransactionApi](../../../../index.md) / [SubmitTransactionResponse](../../../index.md) / [Result](../../index.md) / [InvoiceErrors](../index.md) / [InvoiceError](./index.md)

# InvoiceError

`sealed class InvoiceError`

### Types

| Name | Summary |
|---|---|
| [ALREADY_PAID](-a-l-r-e-a-d-y_-p-a-i-d.md) | The provided invoice has already been paid for.`object ALREADY_PAID : InvoiceError` |
| [SKU_NOT_FOUND](-s-k-u_-n-o-t_-f-o-u-n-d.md) | One or more SKUs in the invoice was not found.`object SKU_NOT_FOUND : InvoiceError` |
| [UNKNOWN](-u-n-k-n-o-w-n.md) | `object UNKNOWN : InvoiceError` |
| [WRONG_DESTINATION](-w-r-o-n-g_-d-e-s-t-i-n-a-t-i-o-n.md) | The destination in the operation corresponding to this invoice is incorrect.`object WRONG_DESTINATION : InvoiceError` |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
