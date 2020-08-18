[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountReadOperations](index.md) / [getAccount](./get-account.md)

# getAccount

`abstract fun getAccount(forceUpdate: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>`

Returns the account info

### Parameters

`forceUpdate` -
* forces an update from the network

**Return**
a [Promise](../../org.kin.sdk.base.tools/-promise/index.md) containing the [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) or an error

`abstract fun getAccount(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>`

Returns the account info

**Return**
a [Promise](../../org.kin.sdk.base.tools/-promise/index.md) containing the [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) or an error

