[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinTransactionApi](./index.md)

# KinTransactionApi

`interface KinTransactionApi`

### Types

| Name | Summary |
|---|---|
| [GetMinFeeForTransactionResponse](-get-min-fee-for-transaction-response/index.md) | `data class GetMinFeeForTransactionResponse` |
| [GetTransactionHistoryRequest](-get-transaction-history-request/index.md) | `data class GetTransactionHistoryRequest` |
| [GetTransactionHistoryResponse](-get-transaction-history-response/index.md) | `data class GetTransactionHistoryResponse` |
| [GetTransactionRequest](-get-transaction-request/index.md) | `data class GetTransactionRequest` |
| [GetTransactionResponse](-get-transaction-response/index.md) | `data class GetTransactionResponse` |
| [SubmitTransactionRequest](-submit-transaction-request/index.md) | `data class SubmitTransactionRequest` |
| [SubmitTransactionResponse](-submit-transaction-response/index.md) | `data class SubmitTransactionResponse` |

### Functions

| Name | Summary |
|---|---|
| [getTransaction](get-transaction.md) | `abstract fun getTransaction(request: GetTransactionRequest, onCompleted: (GetTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransactionHistory](get-transaction-history.md) | `abstract fun getTransactionHistory(request: GetTransactionHistoryRequest, onCompleted: (GetTransactionHistoryResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransactionMinFee](get-transaction-min-fee.md) | `abstract fun getTransactionMinFee(onCompleted: (GetMinFeeForTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [streamNewTransactions](stream-new-transactions.md) | `abstract fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [submitTransaction](submit-transaction.md) | `abstract fun submitTransaction(request: SubmitTransactionRequest, onCompleted: (SubmitTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinTransactionsApi](../../org.kin.sdk.base.network.api.proto/-agora-kin-transactions-api/index.md) | `class AgoraKinTransactionsApi : `[`GrpcApi`](../../org.kin.sdk.base.network.api.proto/-grpc-api/index.md)`, `[`KinTransactionApi`](./index.md)`, `[`KinTransactionWhitelistingApi`](../-kin-transaction-whitelisting-api/index.md) |
| [HorizonKinApi](../../org.kin.sdk.base.network.api.rest/-horizon-kin-api/index.md) | `class HorizonKinApi : `[`KinJsonApi`](../../org.kin.sdk.base.network.api.rest/-kin-json-api/index.md)`, `[`KinAccountApi`](../-kin-account-api/index.md)`, `[`KinTransactionApi`](./index.md) |
