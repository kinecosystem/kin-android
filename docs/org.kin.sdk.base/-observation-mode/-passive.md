[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [ObservationMode](index.md) / [Passive](./-passive.md)

# Passive

`object Passive : `[`ObservationMode`](index.md)

Updates are only based on local actions
or via calling [Observer.requestInvalidation](../../org.kin.sdk.base.tools/-observer/request-invalidation.md)

A current value will always be emitted
(which may fault to network) followed
by only values as a result of local actions.

