[kin-android](../index.md) / [org.kin.sdk.base.tools](./index.md)

## Package org.kin.sdk.base.tools

### Types

| Name | Summary |
|---|---|
| [BackoffStrategy](-backoff-strategy/index.md) | `sealed class BackoffStrategy` |
| [Callback](-callback/index.md) | [onCompleted](-callback/on-completed.md) to be called when callback is complete with *either* a non null value or an error but never both.`interface Callback<T> : `[`Function`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-function/index.html)`<T>` |
| [Disposable](-disposable/index.md) | `interface Disposable<T>` |
| [DisposeBag](-dispose-bag/index.md) | `class DisposeBag` |
| [ExecutorServices](-executor-services/index.md) | `data class ExecutorServices` |
| [ListObserver](-list-observer/index.md) | `interface ListObserver<T> : `[`Observer`](-observer/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>, `[`ListOperations`](-list-operations/index.md)`<T>` |
| [ListOperations](-list-operations/index.md) | `interface ListOperations<T>` |
| [ListSubject](-list-subject/index.md) | `class ListSubject<T> : `[`ValueSubject`](-value-subject/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>, `[`ListObserver`](-list-observer/index.md)`<T>` |
| [ManagedServerSentEventStream](-managed-server-sent-event-stream/index.md) | `class ManagedServerSentEventStream<ResponseType>` |
| [NetworkOperation](-network-operation/index.md) | `data class NetworkOperation<ResponseType>` |
| [NetworkOperationsHandler](-network-operations-handler/index.md) | `interface NetworkOperationsHandler` |
| [NetworkOperationsHandlerImpl](-network-operations-handler-impl/index.md) | `class NetworkOperationsHandlerImpl : `[`NetworkOperationsHandler`](-network-operations-handler/index.md) |
| [Observer](-observer/index.md) | `interface Observer<T> : `[`Disposable`](-disposable/index.md)`<T>` |
| [Optional](-optional/index.md) | `class Optional<T>` |
| [Promise](-promise/index.md) | `interface Promise<out T>` |
| [PromisedCallback](-promised-callback/index.md) | `class PromisedCallback<T>` |
| [PromiseQueue](-promise-queue/index.md) | `class PromiseQueue<T>` |
| [ValueListener](-value-listener/index.md) | May call [onNext](-value-listener/on-next.md) or [onError](-value-listener/on-error.md) in a sequence of value updates. Should not emit onNext updates after an onError event.`interface ValueListener<T>` |
| [ValueSubject](-value-subject/index.md) | `open class ValueSubject<T> : `[`Observer`](-observer/index.md)`<T>` |

### Annotations

| Name | Summary |
|---|---|
| [KinExperimental](-kin-experimental/index.md) | `annotation class KinExperimental` |

### Exceptions

| Name | Summary |
|---|---|
| [NetworkOperationsHandlerException](-network-operations-handler-exception/index.md) | `sealed class NetworkOperationsHandlerException : `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) |
| [RetriesExceededException](-retries-exceeded-exception/index.md) | `class RetriesExceededException : `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html) |

### Functions

| Name | Summary |
|---|---|
| [callback](callback.md) | `fun <T> `[`Promise`](-promise/index.md)`<T>.callback(callback: `[`Callback`](-callback/index.md)`<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [listen](listen.md) | `fun <T> `[`Observer`](-observer/index.md)`<T>.listen(listener: `[`ValueListener`](-value-listener/index.md)`<T>): `[`Observer`](-observer/index.md)`<T>`<br>`fun <T> `[`ListObserver`](-list-observer/index.md)`<T>.listen(listener: `[`ValueListener`](-value-listener/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>): `[`ListObserver`](-list-observer/index.md)`<T>` |
| [queueWork](queue-work.md) | `fun <T> `[`NetworkOperationsHandler`](-network-operations-handler/index.md)`.queueWork(work: (`[`PromisedCallback`](-promised-callback/index.md)`<T>) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Promise`](-promise/index.md)`<T>` |
| [submitOrRunOn](submit-or-run-on.md) | `fun submitOrRunOn(maybeExecutor: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)`?, work: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |
