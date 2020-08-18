[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinTransactionWhitelistingApi](./index.md)

# KinTransactionWhitelistingApi

`interface KinTransactionWhitelistingApi`

### Types

| Name | Summary |
|---|---|
| [WhitelistTransactionRequest](-whitelist-transaction-request/index.md) | `data class WhitelistTransactionRequest` |
| [WhitelistTransactionResponse](-whitelist-transaction-response/index.md) | `data class WhitelistTransactionResponse` |

### Properties

| Name | Summary |
|---|---|
| [isWhitelistingAvailable](is-whitelisting-available.md) | `abstract val isWhitelistingAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Functions

| Name | Summary |
|---|---|
| [whitelistTransaction](whitelist-transaction.md) | `abstract fun whitelistTransaction(request: WhitelistTransactionRequest, onCompleted: (WhitelistTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinTransactionsApi](../../org.kin.sdk.base.network.api.proto/-agora-kin-transactions-api/index.md) | `class AgoraKinTransactionsApi : `[`GrpcApi`](../../org.kin.sdk.base.network.api.proto/-grpc-api/index.md)`, `[`KinTransactionApi`](../-kin-transaction-api/index.md)`, `[`KinTransactionWhitelistingApi`](./index.md) |
| [DefaultHorizonKinTransactionWhitelistingApi](../../org.kin.sdk.base.network.api.rest/-default-horizon-kin-transaction-whitelisting-api/index.md) | `class DefaultHorizonKinTransactionWhitelistingApi : `[`KinTransactionWhitelistingApi`](./index.md) |
