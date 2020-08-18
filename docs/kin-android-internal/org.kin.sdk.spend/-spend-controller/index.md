[kin-android](../../index.md) / [org.kin.sdk.spend](../index.md) / [SpendController](./index.md)

# SpendController

`data class SpendController`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SpendController(kinEnvironment: Agora, navigator: `[`SpendNavigator`](../../org.kin.base.viewmodel.tools/-spend-navigator/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [confirmPaymentOfInvoice](confirm-payment-of-invoice.md) | `fun confirmPaymentOfInvoice(invoice: `[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`, payerAccount: Id, processingAppInfo: `[`AppInfo`](../../org.kin.sdk.base.models/-app-info/index.md)` = kinEnvironment.appInfoProvider.appInfo, onResult: (`[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`?, Reason?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
