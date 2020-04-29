[kin-android](../../index.md) / [kin.utils](../index.md) / [Request](./index.md)

# Request

`open class Request<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`!>`

Represents method invocation, each request will run sequentially on background thread, and will notify ``[`ResultCallback`](../-result-callback/index.md) witch success or error on main thread.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Should not be constructing these outside the base-compat sdk implementation. Here for to support the interface in posterity only.`Request(callable: `[`Callable`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/Callable.html)`<T>!)``Request(request: `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<T>!, mapError: ((`[`Exception`](https://docs.oracle.com/javase/6/docs/api/java/lang/Exception.html)`!) -> `[`Exception`](https://docs.oracle.com/javase/6/docs/api/java/lang/Exception.html)`!)!)` |

### Functions

| Name | Summary |
|---|---|
| [cancel](cancel.md) | Here for to support the interface in posterity only.`open fun cancel(mayInterruptIfRunning: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [run](run.md) | Run request asynchronously, notify `callback` with successful result or error`open fun run(callback: `[`ResultCallback`](../-result-callback/index.md)`<T>!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setCallbackExecutor](set-callback-executor.md) | `open static fun setCallbackExecutor(es: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
