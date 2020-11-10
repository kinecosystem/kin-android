[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [AgoraKinTransactionsApiV4](./index.md)

# AgoraKinTransactionsApiV4

`class AgoraKinTransactionsApiV4 : `[`GrpcApi`](../-grpc-api/index.md)`, `[`KinTransactionApiV4`](../../org.kin.sdk.base.network.api/-kin-transaction-api-v4/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AgoraKinTransactionsApiV4(managedChannel: ManagedChannel, networkEnvironment: `[`NetworkEnvironment`](../../org.kin.sdk.base.stellar.models/-network-environment/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [networkEnvironment](network-environment.md) | `val networkEnvironment: `[`NetworkEnvironment`](../../org.kin.sdk.base.stellar.models/-network-environment/index.md) |

### Functions

| Name | Summary |
|---|---|
| [getMinimumBalanceForRentExemption](get-minimum-balance-for-rent-exemption.md) | `fun getMinimumBalanceForRentExemption(request: GetMinimumBalanceForRentExemptionRequest, onCompleted: (GetMinimumBalanceForRentExemptionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getMinKinVersion](get-min-kin-version.md) | `fun getMinKinVersion(request: GetMiniumumKinVersionRequest, onCompleted: (GetMiniumumKinVersionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getRecentBlockHash](get-recent-block-hash.md) | `fun getRecentBlockHash(request: GetRecentBlockHashRequest, onCompleted: (GetRecentBlockHashResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getServiceConfig](get-service-config.md) | `fun getServiceConfig(request: GetServiceConfigRequest, onCompleted: (GetServiceConfigResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransaction](get-transaction.md) | `fun getTransaction(request: GetTransactionRequest, onCompleted: (GetTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransactionHistory](get-transaction-history.md) | `fun getTransactionHistory(request: GetTransactionHistoryRequest, onCompleted: (GetTransactionHistoryResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [submitTransaction](submit-transaction.md) | `fun submitTransaction(request: SubmitTransactionRequest, onCompleted: (SubmitTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
