[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [addBalanceListener](./add-balance-listener.md)

# addBalanceListener

`abstract fun addBalanceListener(@NonNull listener: `[`EventListener`](../-event-listener/index.md)`<`[`Balance`](../-balance/index.md)`!>): `[`ListenerRegistration`](../-listener-registration/index.md)`!`

Creates and adds listener for balance changes of this account, use returned ``[`ListenerRegistration`](../-listener-registration/index.md) to stop listening.

**Note:** Events will be fired on background thread.

### Parameters

`listener` - [EventListener](../-event-listener/index.md)&lt;[Balance](../-balance/index.md)!&gt;: listener object for payment events