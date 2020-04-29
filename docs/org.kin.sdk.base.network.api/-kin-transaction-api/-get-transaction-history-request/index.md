[kin-android](../../../index.md) / [org.kin.sdk.base.network.api](../../index.md) / [KinTransactionApi](../index.md) / [GetTransactionHistoryRequest](./index.md)

# GetTransactionHistoryRequest

`data class GetTransactionHistoryRequest`

### Types

| Name | Summary |
|---|---|
| [Order](-order/index.md) | `sealed class Order` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GetTransactionHistoryRequest(accountId: Id, pagingToken: PagingToken? = null, order: Order = Order.Descending)` |

### Properties

| Name | Summary |
|---|---|
| [accountId](account-id.md) | `val accountId: Id` |
| [order](order.md) | `val order: Order` |
| [pagingToken](paging-token.md) | `val pagingToken: PagingToken?` |
