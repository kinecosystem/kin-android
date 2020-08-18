[kin-android](../../index.md) / [org.kin.sdk.base.network.api.horizon](../index.md) / [KinFriendBotApi](./index.md)

# KinFriendBotApi

`interface KinFriendBotApi`

### Functions

| Name | Summary |
|---|---|
| [createAccount](create-account.md) | `abstract fun createAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [fundAccount](fund-account.md) | `abstract fun fundAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [FriendBotApi](../../org.kin.sdk.base.network.api/-friend-bot-api/index.md) | This is valid for testnet only`class FriendBotApi : `[`KinFriendBotApi`](./index.md) |
