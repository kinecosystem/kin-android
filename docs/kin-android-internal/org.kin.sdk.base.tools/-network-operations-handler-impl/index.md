[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [NetworkOperationsHandlerImpl](./index.md)

# NetworkOperationsHandlerImpl

`class NetworkOperationsHandlerImpl : `[`NetworkOperationsHandler`](../-network-operations-handler/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `NetworkOperationsHandlerImpl(ioScheduler: `[`ScheduledExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledExecutorService.html)` = Executors.newSingleThreadScheduledExecutor(), ioExecutor: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)` = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()), logger: ILoggerFactory = LoggerFactory.getILoggerFactory(), shouldRetryError: (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = { false })` |

### Functions

| Name | Summary |
|---|---|
| [queueOperation](queue-operation.md) | `fun <ResponseType> queueOperation(op: `[`NetworkOperation`](../-network-operation/index.md)`<ResponseType>): `[`NetworkOperation`](../-network-operation/index.md)`<ResponseType>` |

### Extension Functions

| Name | Summary |
|---|---|
| [queueWork](../queue-work.md) | `fun <T> `[`NetworkOperationsHandler`](../-network-operations-handler/index.md)`.queueWork(work: (`[`PromisedCallback`](../-promised-callback/index.md)`<T>) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Promise`](../-promise/index.md)`<T>` |
