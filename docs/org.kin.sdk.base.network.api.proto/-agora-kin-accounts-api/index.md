[kin-android](../../index.md) / [org.kin.sdk.base.network.api.proto](../index.md) / [AgoraKinAccountsApi](./index.md)

# AgoraKinAccountsApi

`class AgoraKinAccountsApi : `[`GrpcApi`](../-grpc-api/index.md)`, `[`KinAccountApi`](../../org.kin.sdk.base.network.api/-kin-account-api/index.md)`, `[`KinAccountCreationApi`](../../org.kin.sdk.base.network.api/-kin-account-creation-api/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AgoraKinAccountsApi(managedChannel: ManagedChannel)` |

### Functions

| Name | Summary |
|---|---|
| [createAccount](create-account.md) | Developers are expected to call their back-end's to register this address with the main-net Kin Blockchain.`fun createAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getAccount](get-account.md) | `fun getAccount(request: GetAccountRequest, onCompleted: (GetAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [streamAccount](stream-account.md) | `fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
