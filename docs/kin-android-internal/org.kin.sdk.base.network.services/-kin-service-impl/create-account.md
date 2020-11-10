[kin-android](../../index.md) / [org.kin.sdk.base.network.services](../index.md) / [KinServiceImpl](index.md) / [createAccount](./create-account.md)

# createAccount

`fun createAccount(accountId: Id, signer: PrivateKey): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>`

Creates a [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) and activates it on the network.

### Parameters

`signer` - only ever used to sign a request, never transmitted