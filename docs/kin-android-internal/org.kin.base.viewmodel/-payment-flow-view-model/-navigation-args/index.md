[kin-android](../../../index.md) / [org.kin.base.viewmodel](../../index.md) / [PaymentFlowViewModel](../index.md) / [NavigationArgs](./index.md)

# NavigationArgs

`data class NavigationArgs`

### Parameters

`invoiceId` -
* the encoded hash of the Invoice

`payerAccountId` -
* the address of the account that is to make a payment for the [invoiceId](invoice-id.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `NavigationArgs(invoiceId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, payerAccountId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, processingAppIdx: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [invoiceId](invoice-id.md) | <ul><li>the encoded hash of the Invoice</li></ul>`val invoiceId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [payerAccountId](payer-account-id.md) | <ul><li>the address of the account that is to make a payment for the [invoiceId](invoice-id.md)</li></ul>`val payerAccountId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [processingAppIdx](processing-app-idx.md) | `val processingAppIdx: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
