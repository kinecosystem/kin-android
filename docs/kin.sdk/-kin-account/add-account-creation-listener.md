[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [addAccountCreationListener](./add-account-creation-listener.md)

# addAccountCreationListener

`abstract fun addAccountCreationListener(listener: `[`EventListener`](../-event-listener/index.md)`<`[`Void`](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)`!>!): `[`ListenerRegistration`](../-listener-registration/index.md)`!`

Creates and adds listener for account creation event, use returned ``[`ListenerRegistration`](../-listener-registration/index.md) to stop listening.

**Note:** Events will be fired on background thread.

### Parameters

`listener` - [EventListener](../-event-listener/index.md)&lt;[Void](https://docs.oracle.com/javase/6/docs/api/java/lang/Void.html)!&gt;!: listener object for payment events