[kin-android](../../index.md) / [org.kin.sdk.base.network.api.horizon](../index.md) / [DefaultHorizonKinAccountCreationApi](./index.md)

# DefaultHorizonKinAccountCreationApi

`class DefaultHorizonKinAccountCreationApi : `[`KinAccountCreationApi`](../../org.kin.sdk.base.network.api/-kin-account-creation-api/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `DefaultHorizonKinAccountCreationApi(environment: `[`ApiConfig`](../../org.kin.sdk.base.stellar.models/-api-config/index.md)`, friendBotApi: `[`KinFriendBotApi`](../-kin-friend-bot-api/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [createAccount](create-account.md) | Developers are expected to call their back-end's to register this address with the main-net Kin Blockchain.`fun createAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
