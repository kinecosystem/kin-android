[kin-android](../../../../index.md) / [org.kin.sdk.base.tools](../../../index.md) / [NetworkOperation](../../index.md) / [State](../index.md) / [SCHEDULED](./index.md)

# SCHEDULED

`data class SCHEDULED : State`

Operation has been scheduled to execute at a certain [executionTimestamp](execution-timestamp.md).
There should exist a [ScheduledFuture](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledFuture.html) associated with this operation's scheduling

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Operation has been scheduled to execute at a certain [executionTimestamp](execution-timestamp.md). There should exist a [ScheduledFuture](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledFuture.html) associated with this operation's scheduling`SCHEDULED(executionTimestamp: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, cancellable: `[`ScheduledFuture`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledFuture.html)`<*>)` |

### Properties

| Name | Summary |
|---|---|
| [cancellable](cancellable.md) | `val cancellable: `[`ScheduledFuture`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledFuture.html)`<*>` |
| [executionTimestamp](execution-timestamp.md) | `val executionTimestamp: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
