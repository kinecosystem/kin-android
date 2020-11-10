[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinAccountCreationApiV4](./index.md)

# KinAccountCreationApiV4

`interface KinAccountCreationApiV4`

### Types

| Name | Summary |
|---|---|
| [CreateAccountRequest](-create-account-request/index.md) | `data class CreateAccountRequest` |
| [CreateAccountResponse](-create-account-response/index.md) | `data class CreateAccountResponse` |

### Functions

| Name | Summary |
|---|---|
| [createAccount](create-account.md) | `abstract fun createAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinAccountCreationApiV4](../../org.kin.sdk.base.network.api.agora/-agora-kin-account-creation-api-v4/index.md) | `class AgoraKinAccountCreationApiV4 : `[`GrpcApi`](../../org.kin.sdk.base.network.api.agora/-grpc-api/index.md)`, `[`KinAccountCreationApiV4`](./index.md) |
