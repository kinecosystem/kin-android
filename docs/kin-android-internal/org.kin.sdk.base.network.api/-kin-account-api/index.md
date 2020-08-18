[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinAccountApi](./index.md)

# KinAccountApi

`interface KinAccountApi`

### Types

| Name | Summary |
|---|---|
| [GetAccountRequest](-get-account-request/index.md) | `data class GetAccountRequest` |
| [GetAccountResponse](-get-account-response/index.md) | `data class GetAccountResponse` |

### Functions

| Name | Summary |
|---|---|
| [getAccount](get-account.md) | `abstract fun getAccount(request: GetAccountRequest, onCompleted: (GetAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinAccountsApi](../../org.kin.sdk.base.network.api.agora/-agora-kin-accounts-api/index.md) | `class AgoraKinAccountsApi : `[`GrpcApi`](../../org.kin.sdk.base.network.api.agora/-grpc-api/index.md)`, `[`KinAccountApi`](./index.md)`, `[`KinStreamingApi`](../-kin-streaming-api/index.md)`, `[`KinAccountCreationApi`](../-kin-account-creation-api/index.md) |
| [HorizonKinApi](../../org.kin.sdk.base.network.api.horizon/-horizon-kin-api/index.md) | `class HorizonKinApi : `[`KinJsonApi`](../../org.kin.sdk.base.network.api.horizon/-kin-json-api/index.md)`, `[`KinAccountApi`](./index.md)`, `[`KinTransactionApi`](../-kin-transaction-api/index.md)`, `[`KinStreamingApi`](../-kin-streaming-api/index.md) |
