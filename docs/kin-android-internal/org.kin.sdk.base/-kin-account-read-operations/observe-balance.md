[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountReadOperations](index.md) / [observeBalance](./observe-balance.md)

# observeBalance

`abstract fun observeBalance(mode: `[`ObservationMode`](../-observation-mode/index.md)` = Passive): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>`

Returns the current [Balance](#)
and listens to future account balance changes.

Note: Running with [ObservationMode.Passive](../-observation-mode/-passive.md) is suggested unless
higher data freshness is required.

[ObservationMode.Passive](../-observation-mode/-passive.md) - will return the current balance and any balance updates
as a result of actions performed locally.
[ObservationMode.Active](../-observation-mode/-active.md) - will return the current balance and any balance updates
[ObservationMode.ActiveNewOnly](../-observation-mode/-active-new-only.md) - will *not* return the current balance, but only new
updates from now onwards.

### Parameters

`mode` - will change the frequency of updates according to
the rules set in [ObservationMode](../-observation-mode/index.md)