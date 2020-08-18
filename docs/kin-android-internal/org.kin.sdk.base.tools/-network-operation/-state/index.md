[kin-android](../../../index.md) / [org.kin.sdk.base.tools](../../index.md) / [NetworkOperation](../index.md) / [State](./index.md)

# State

`sealed class State`

### Types

| Name | Summary |
|---|---|
| [COMPLETED](-c-o-m-p-l-e-t-e-d.md) | Operation has completed`object COMPLETED : State` |
| [ERRORED](-e-r-r-o-r-e-d/index.md) | Operation has been run and has run into an [error](-e-r-r-o-r-e-d/error.md)`data class ERRORED : State` |
| [INIT](-i-n-i-t.md) | Recently created, not yet queued or scheduled`object INIT : State` |
| [QUEUED](-q-u-e-u-e-d.md) | Operation has been added to the `activeOperations` list and is awaiting scheduling.`object QUEUED : State` |
| [RUNNING](-r-u-n-n-i-n-g.md) | Operation has been run and is in-flight`object RUNNING : State` |
| [SCHEDULED](-s-c-h-e-d-u-l-e-d/index.md) | Operation has been scheduled to execute at a certain [executionTimestamp](-s-c-h-e-d-u-l-e-d/execution-timestamp.md). There should exist a [ScheduledFuture](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledFuture.html) associated with this operation's scheduling`data class SCHEDULED : State` |
