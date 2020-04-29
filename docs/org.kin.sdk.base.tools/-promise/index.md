[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Promise](./index.md)

# Promise

`interface Promise<out T>`

### Types

| Name | Summary |
|---|---|
| [State](-state/index.md) | `sealed class State<out T>` |

### Functions

| Name | Summary |
|---|---|
| [doOnError](do-on-error.md) | `abstract fun doOnError(onRejected: (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Promise`](./index.md)`<T>` |
| [doOnResolved](do-on-resolved.md) | `abstract fun doOnResolved(onResolved: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Promise`](./index.md)`<T>` |
| [flatMap](flat-map.md) | `abstract fun <S> flatMap(onResolved: (T) -> `[`Promise`](./index.md)`<S>, onRejected: (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Promise`](./index.md)`<S>): `[`Promise`](./index.md)`<S>`<br>`abstract fun <S> flatMap(onResolved: (T) -> `[`Promise`](./index.md)`<S>): `[`Promise`](./index.md)`<S>` |
| [map](map.md) | `abstract fun <S> map(onResolved: (T) -> S, onRejected: (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Promise`](./index.md)`<S>`<br>`abstract fun <S> map(onResolved: (T) -> S): `[`Promise`](./index.md)`<S>` |
| [resolve](resolve.md) | `abstract fun resolve(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [resolveOn](resolve-on.md) | `abstract fun resolveOn(executor: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)`): `[`Promise`](./index.md)`<T>` |
| [then](then.md) | `abstract fun then(onResolved: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, onRejected: (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`abstract fun then(onResolved: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [workOn](work-on.md) | `abstract fun workOn(executor: `[`ExecutorService`](https://docs.oracle.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html)`): `[`Promise`](./index.md)`<T>` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [all](all.md) | `fun all(vararg promises: `[`Promise`](./index.md)`<`[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>): `[`Promise`](./index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>>` |
| [create](create.md) | `fun <T> create(work: (resolve: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`, reject: (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Promise`](./index.md)`<T>` |
| [defer](defer.md) | `fun <T> defer(promise: () -> `[`Promise`](./index.md)`<T>): `[`Promise`](./index.md)`<T>` |
| [error](error.md) | `fun <T> error(value: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Promise`](./index.md)`<T>` |
| [of](of.md) | `fun <T> of(value: T): `[`Promise`](./index.md)`<T>` |

### Extension Functions

| Name | Summary |
|---|---|
| [callback](../callback.md) | `fun <T> `[`Promise`](./index.md)`<T>.callback(callback: `[`Callback`](../-callback/index.md)`<T>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
