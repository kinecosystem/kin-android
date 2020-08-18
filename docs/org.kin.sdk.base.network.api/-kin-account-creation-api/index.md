[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinAccountCreationApi](./index.md)

# KinAccountCreationApi

`interface KinAccountCreationApi`

An API for the SDK to delegate [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) registration
with the Kin Blockchain to developers.

### Types

| Name | Summary |
|---|---|
| [CreateAccountRequest](-create-account-request/index.md) | `data class CreateAccountRequest` |
| [CreateAccountResponse](-create-account-response/index.md) | `data class CreateAccountResponse` |

### Functions

| Name | Summary |
|---|---|
| [createAccount](create-account.md) | Developers are expected to call their back-end's to register this address with the main-net Kin Blockchain.`abstract fun createAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinAccountsApi](../../org.kin.sdk.base.network.api.proto/-agora-kin-accounts-api/index.md) | `class AgoraKinAccountsApi : `[`GrpcApi`](../../org.kin.sdk.base.network.api.proto/-grpc-api/index.md)`, `[`KinAccountApi`](../-kin-account-api/index.md)`, `[`KinAccountCreationApi`](./index.md) |
| [DefaultHorizonKinAccountCreationApi](../../org.kin.sdk.base.network.api.rest/-default-horizon-kin-account-creation-api/index.md) | `class DefaultHorizonKinAccountCreationApi : `[`KinAccountCreationApi`](./index.md) |
