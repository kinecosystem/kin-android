[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Observer](./index.md)

# Observer

`interface Observer<T> : `[`Disposable`](../-disposable/index.md)`<T>`

### Functions

| Name | Summary |
|---|---|
| [add](add.md) | `abstract fun add(listener: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Observer`](./index.md)`<T>` |
| [flatMapPromise](flat-map-promise.md) | `abstract fun <V> flatMapPromise(promise: (T) -> `[`Promise`](../-promise/index.md)`<V>): `[`Promise`](../-promise/index.md)`<V>` |
| [listenerCount](listener-count.md) | `abstract fun listenerCount(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [mapPromise](map-promise.md) | `abstract fun <V> mapPromise(map: (T) -> V): `[`Promise`](../-promise/index.md)`<V>` |
| [remove](remove.md) | `abstract fun remove(listener: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Observer`](./index.md)`<T>` |
| [requestInvalidation](request-invalidation.md) | `abstract fun requestInvalidation(): `[`Observer`](./index.md)`<T>` |

### Extension Functions

| Name | Summary |
|---|---|
| [listen](../listen.md) | `fun <T> `[`Observer`](./index.md)`<T>.listen(listener: `[`ValueListener`](../-value-listener/index.md)`<T>): `[`Observer`](./index.md)`<T>` |

### Inheritors

| Name | Summary |
|---|---|
| [ListObserver](../-list-observer/index.md) | `interface ListObserver<T> : `[`Observer`](./index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>, `[`ListOperations`](../-list-operations/index.md)`<T>` |
| [ValueSubject](../-value-subject/index.md) | `open class ValueSubject<T> : `[`Observer`](./index.md)`<T>` |
