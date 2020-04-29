[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinAccount](index.md) / [addPaymentListener](./add-payment-listener.md)

# addPaymentListener

`abstract fun addPaymentListener(@NonNull listener: `[`EventListener`](../-event-listener/index.md)`<`[`PaymentInfo`](../-payment-info/index.md)`!>): `[`ListenerRegistration`](../-listener-registration/index.md)`!`

Creates and adds listener for payments concerning this account, use returned ``[`ListenerRegistration`](../-listener-registration/index.md) to stop listening.

**Note:** Events will be fired on background thread.

### Parameters

`listener` - [EventListener](../-event-listener/index.md)&lt;[PaymentInfo](../-payment-info/index.md)!&gt;: listener object for payment events