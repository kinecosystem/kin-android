[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [NetworkOperation](./index.md)

# NetworkOperation

`data class NetworkOperation<ResponseType>`

### Parameters

`onCompleted` -
* will be called when the operation has completed, successfully or with an error (including if it timed out, or failed fatally)

`id` -
* a unique identifier for the operation

`timeout` -
* task will timeout in milliseconds, if not completed within the timeout period, with [NetworkOperationsHandlerException.OperationTimeoutException](../-network-operations-handler-exception/-operation-timeout-exception/index.md)

`backoffStrategy` -
* the strategy used to retry a task that fails

`callback` -
* the work performed by the operation

### Types

| Name | Summary |
|---|---|
| [State](-state/index.md) | `sealed class State` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `NetworkOperation(onSuccess: (ResponseType) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, onError: ((`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)? = null, id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = generateRandomId(), timeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = DEFAULT_TIMEOUT, backoffStrategy: `[`BackoffStrategy`](../-backoff-strategy/index.md)` = BackoffStrategy.Exponential(
            maximumWaitTime = timeout
        ), callback: (`[`PromisedCallback`](../-promised-callback/index.md)`<ResponseType>, error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)`<br>`NetworkOperation(onCompleted: `[`PromisedCallback`](../-promised-callback/index.md)`<ResponseType>, id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = generateRandomId(), timeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = DEFAULT_TIMEOUT, backoffStrategy: `[`BackoffStrategy`](../-backoff-strategy/index.md)` = BackoffStrategy.Exponential(
        maximumWaitTime = timeout
    ), callback: (`[`PromisedCallback`](../-promised-callback/index.md)`<ResponseType>, error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, shouldRetryError: ((`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`)? = null)` |

### Properties

| Name | Summary |
|---|---|
| [backoffStrategy](backoff-strategy.md) | <ul><li>the strategy used to retry a task that fails</li></ul>`val backoffStrategy: `[`BackoffStrategy`](../-backoff-strategy/index.md) |
| [callback](callback.md) | <ul><li>the work performed by the operation</li></ul>`val callback: (`[`PromisedCallback`](../-promised-callback/index.md)`<ResponseType>, error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [expiryFuture](expiry-future.md) | `var expiryFuture: `[`ScheduledFuture`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ScheduledFuture.html)`<*>?` |
| [id](id.md) | <ul><li>a unique identifier for the operation</li></ul>`val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [onCompleted](on-completed.md) | <ul><li>will be called when the operation has completed, successfully or with an error (including if it timed out, or failed fatally)</li></ul>`val onCompleted: `[`PromisedCallback`](../-promised-callback/index.md)`<ResponseType>` |
| [shouldRetryError](should-retry-error.md) | `val shouldRetryError: ((`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`)?` |
| [state](state.md) | `var state: State` |
| [timeout](timeout.md) | <ul><li>task will timeout in milliseconds, if not completed within the timeout period, with [NetworkOperationsHandlerException.OperationTimeoutException](../-network-operations-handler-exception/-operation-timeout-exception/index.md)</li></ul>`val timeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [generateRandomId](generate-random-id.md) | `fun generateRandomId(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
