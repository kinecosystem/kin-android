[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [KinPayment](./index.md)

# KinPayment

`data class KinPayment`

### Types

| Name | Summary |
|---|---|
| [Id](-id/index.md) | `data class Id` |
| [Status](-status/index.md) | `sealed class Status` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `KinPayment(id: Id, status: Status, sourceAccountId: Id, destinationAccountId: Id, amount: `[`KinAmount`](../-kin-amount/index.md)`, fee: `[`QuarkAmount`](../-quark-amount/index.md)`, memo: `[`KinMemo`](../-kin-memo/index.md)`, timestamp: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, invoice: `[`Invoice`](../-invoice/index.md)`? = null)` |

### Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | `val amount: `[`KinAmount`](../-kin-amount/index.md) |
| [destinationAccountId](destination-account-id.md) | `val destinationAccountId: Id` |
| [fee](fee.md) | `val fee: `[`QuarkAmount`](../-quark-amount/index.md) |
| [id](id.md) | `val id: Id` |
| [invoice](invoice.md) | `val invoice: `[`Invoice`](../-invoice/index.md)`?` |
| [memo](memo.md) | `val memo: `[`KinMemo`](../-kin-memo/index.md) |
| [sourceAccountId](source-account-id.md) | `val sourceAccountId: Id` |
| [status](status.md) | `val status: Status` |
| [timestamp](timestamp.md) | `val timestamp: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
