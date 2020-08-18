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
| [streamAccount](stream-account.md) | `abstract fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinAccountsApi](../../org.kin.sdk.base.network.api.proto/-agora-kin-accounts-api/index.md) | `class AgoraKinAccountsApi : `[`GrpcApi`](../../org.kin.sdk.base.network.api.proto/-grpc-api/index.md)`, `[`KinAccountApi`](./index.md)`, `[`KinAccountCreationApi`](../-kin-account-creation-api/index.md) |
| [HorizonKinApi](../../org.kin.sdk.base.network.api.rest/-horizon-kin-api/index.md) | `class HorizonKinApi : `[`KinJsonApi`](../../org.kin.sdk.base.network.api.rest/-kin-json-api/index.md)`, `[`KinAccountApi`](./index.md)`, `[`KinTransactionApi`](../-kin-transaction-api/index.md) |
