[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinAccountCreationApi](index.md) / [createAccount](./create-account.md)

# createAccount

`abstract fun createAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Developers are expected to call their back-end's to register
this address with the main-net Kin Blockchain.

Note: [FriendBotApi](../-friend-bot-api/index.md) via the [DefaultAccountCreationAPI](#)
    can be used for test-net

