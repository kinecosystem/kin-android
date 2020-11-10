[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinTransactionApiV4](./index.md)

# KinTransactionApiV4

`interface KinTransactionApiV4`

### Types

| Name | Summary |
|---|---|
| [GetMinimumBalanceForRentExemptionRequest](-get-minimum-balance-for-rent-exemption-request/index.md) | `data class GetMinimumBalanceForRentExemptionRequest` |
| [GetMinimumBalanceForRentExemptionResponse](-get-minimum-balance-for-rent-exemption-response/index.md) | `data class GetMinimumBalanceForRentExemptionResponse` |
| [GetMiniumumKinVersionRequest](-get-miniumum-kin-version-request.md) | `object GetMiniumumKinVersionRequest` |
| [GetMiniumumKinVersionResponse](-get-miniumum-kin-version-response/index.md) | `data class GetMiniumumKinVersionResponse` |
| [GetRecentBlockHashRequest](-get-recent-block-hash-request.md) | `object GetRecentBlockHashRequest` |
| [GetRecentBlockHashResponse](-get-recent-block-hash-response/index.md) | `data class GetRecentBlockHashResponse` |
| [GetServiceConfigRequest](-get-service-config-request.md) | `object GetServiceConfigRequest` |
| [GetServiceConfigResponse](-get-service-config-response/index.md) | `data class GetServiceConfigResponse` |
| [GetTransactionHistoryRequest](-get-transaction-history-request/index.md) | `data class GetTransactionHistoryRequest` |
| [GetTransactionHistoryResponse](-get-transaction-history-response/index.md) | `data class GetTransactionHistoryResponse` |
| [GetTransactionRequest](-get-transaction-request/index.md) | `data class GetTransactionRequest` |
| [GetTransactionResponse](-get-transaction-response/index.md) | `data class GetTransactionResponse` |
| [SubmitTransactionRequest](-submit-transaction-request/index.md) | `data class SubmitTransactionRequest` |
| [SubmitTransactionResponse](-submit-transaction-response/index.md) | `data class SubmitTransactionResponse` |

### Functions

| Name | Summary |
|---|---|
| [getMinimumBalanceForRentExemption](get-minimum-balance-for-rent-exemption.md) | `abstract fun getMinimumBalanceForRentExemption(request: GetMinimumBalanceForRentExemptionRequest, onCompleted: (GetMinimumBalanceForRentExemptionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getMinKinVersion](get-min-kin-version.md) | `abstract fun getMinKinVersion(request: GetMiniumumKinVersionRequest = GetMiniumumKinVersionRequest, onCompleted: (GetMiniumumKinVersionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getRecentBlockHash](get-recent-block-hash.md) | `abstract fun getRecentBlockHash(request: GetRecentBlockHashRequest = GetRecentBlockHashRequest, onCompleted: (GetRecentBlockHashResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getServiceConfig](get-service-config.md) | `abstract fun getServiceConfig(request: GetServiceConfigRequest = GetServiceConfigRequest, onCompleted: (GetServiceConfigResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransaction](get-transaction.md) | `abstract fun getTransaction(request: GetTransactionRequest, onCompleted: (GetTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransactionHistory](get-transaction-history.md) | `abstract fun getTransactionHistory(request: GetTransactionHistoryRequest, onCompleted: (GetTransactionHistoryResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [submitTransaction](submit-transaction.md) | `abstract fun submitTransaction(request: SubmitTransactionRequest, onCompleted: (SubmitTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinTransactionsApiV4](../../org.kin.sdk.base.network.api.agora/-agora-kin-transactions-api-v4/index.md) | `class AgoraKinTransactionsApiV4 : `[`GrpcApi`](../../org.kin.sdk.base.network.api.agora/-grpc-api/index.md)`, `[`KinTransactionApiV4`](./index.md) |
