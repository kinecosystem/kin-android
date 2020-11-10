[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinAccountApiV4](./index.md)

# KinAccountApiV4

`interface KinAccountApiV4`

### Types

| Name | Summary |
|---|---|
| [GetAccountRequest](-get-account-request/index.md) | `data class GetAccountRequest` |
| [GetAccountResponse](-get-account-response/index.md) | `data class GetAccountResponse` |
| [ResolveTokenAccountsRequest](-resolve-token-accounts-request/index.md) | `data class ResolveTokenAccountsRequest` |
| [ResolveTokenAccountsResponse](-resolve-token-accounts-response/index.md) | `data class ResolveTokenAccountsResponse` |

### Functions

| Name | Summary |
|---|---|
| [getAccount](get-account.md) | `abstract fun getAccount(request: GetAccountRequest, onCompleted: (GetAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [resolveTokenAcounts](resolve-token-acounts.md) | `abstract fun resolveTokenAcounts(request: ResolveTokenAccountsRequest, onCompleted: (ResolveTokenAccountsResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinAccountApiV4](../../org.kin.sdk.base.network.api.agora/-agora-kin-account-api-v4/index.md) | `class AgoraKinAccountApiV4 : `[`GrpcApi`](../../org.kin.sdk.base.network.api.agora/-grpc-api/index.md)`, `[`KinAccountApiV4`](./index.md)`, `[`KinStreamingApiV4`](../-kin-streaming-api-v4/index.md) |
