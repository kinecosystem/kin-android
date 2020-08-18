[kin-android](../../index.md) / [org.kin.sdk.base.network.api.horizon](../index.md) / [DefaultHorizonKinAccountCreationApi](index.md) / [createAccount](./create-account.md)

# createAccount

`fun createAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Developers are expected to call their back-end's to register
this address with the main-net Kin Blockchain.

Note: [FriendBotApi](../../org.kin.sdk.base.network.api/-friend-bot-api/index.md) via the [DefaultAccountCreationAPI](#)
    can be used for test-net

