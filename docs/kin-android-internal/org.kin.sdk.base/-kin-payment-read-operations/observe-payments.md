[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinPaymentReadOperations](index.md) / [observePayments](./observe-payments.md)

# observePayments

`abstract fun observePayments(mode: `[`ObservationMode`](../-observation-mode/index.md)` = Passive): `[`ListObserver`](../../org.kin.sdk.base.tools/-list-observer/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>`

Retrieves the last N [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s sent or received by the
account and listens for future payments over time.

Note: Running with [ObservationMode.Passive](../-observation-mode/-passive.md) is suggested unless
higher data freshness is required.

[ObservationMode.Passive](../-observation-mode/-passive.md) - will return the full recorded history and new [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)s
as a result of actions performed locally.
[ObservationMode.Active](../-observation-mode/-active.md) - will return the full recorded history and all
new [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)s.
[ObservationMode.ActiveNewOnly](../-observation-mode/-active-new-only.md) - will *not* return the recorded history, but only new
updates from now onwards.

### Parameters

`mode` - will change the frequency of updates according to
the rules set in [ObservationMode](../-observation-mode/index.md).

**Return**
a [ListObserver](../../org.kin.sdk.base.tools/-list-observer/index.md) to listen to the payment history.

