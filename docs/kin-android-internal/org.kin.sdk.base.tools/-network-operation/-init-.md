[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [NetworkOperation](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`NetworkOperation(onSuccess: (ResponseType) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, onError: ((`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)? = null, id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = generateRandomId(), timeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = DEFAULT_TIMEOUT, backoffStrategy: `[`BackoffStrategy`](../-backoff-strategy/index.md)` = BackoffStrategy.Exponential(
            maximumWaitTime = timeout
        ), callback: (`[`PromisedCallback`](../-promised-callback/index.md)`<ResponseType>, error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)``NetworkOperation(onCompleted: `[`PromisedCallback`](../-promised-callback/index.md)`<ResponseType>, id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = generateRandomId(), timeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)` = DEFAULT_TIMEOUT, backoffStrategy: `[`BackoffStrategy`](../-backoff-strategy/index.md)` = BackoffStrategy.Exponential(
        maximumWaitTime = timeout
    ), callback: (`[`PromisedCallback`](../-promised-callback/index.md)`<ResponseType>, error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, shouldRetryError: ((`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`)? = null)`

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
