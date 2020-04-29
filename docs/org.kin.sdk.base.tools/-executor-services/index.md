[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [ExecutorServices](./index.md)

# ExecutorServices

`data class ExecutorServices`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ExecutorServices(sequentialIO: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)` = Executors.newSingleThreadExecutor(), parallelIO: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)` = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), sequentialScheduled: `[`ScheduledExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledExecutorService.html)` = Executors.newSingleThreadScheduledExecutor())` |

### Properties

| Name | Summary |
|---|---|
| [parallelIO](parallel-i-o.md) | `val parallelIO: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html) |
| [sequentialIO](sequential-i-o.md) | `val sequentialIO: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html) |
| [sequentialScheduled](sequential-scheduled.md) | `val sequentialScheduled: `[`ScheduledExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledExecutorService.html) |
