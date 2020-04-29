[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [ValueSubject](./index.md)

# ValueSubject

`open class ValueSubject<T> : `[`Observer`](../-observer/index.md)`<T>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ValueSubject(triggerInvalidation: (() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)? = null)` |

### Functions

| Name | Summary |
|---|---|
| [add](add.md) | `open fun add(listener: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Observer`](../-observer/index.md)`<T>` |
| [dispose](dispose.md) | `open fun dispose(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [disposedBy](disposed-by.md) | `open fun disposedBy(disposeBag: `[`DisposeBag`](../-dispose-bag/index.md)`): `[`Observer`](../-observer/index.md)`<T>` |
| [doOnDisposed](do-on-disposed.md) | `open fun doOnDisposed(onDisposed: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Observer`](../-observer/index.md)`<T>` |
| [flatMapPromise](flat-map-promise.md) | `open fun <V> flatMapPromise(promise: (T) -> `[`Promise`](../-promise/index.md)`<V>): `[`Promise`](../-promise/index.md)`<V>` |
| [listenerCount](listener-count.md) | `open fun listenerCount(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [mapPromise](map-promise.md) | `open fun <V> mapPromise(map: (T) -> V): `[`Promise`](../-promise/index.md)`<V>` |
| [onNext](on-next.md) | `fun onNext(newValue: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [remove](remove.md) | `open fun remove(listener: (T) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Observer`](../-observer/index.md)`<T>` |
| [requestInvalidation](request-invalidation.md) | `open fun requestInvalidation(): `[`Observer`](../-observer/index.md)`<T>` |

### Extension Functions

| Name | Summary |
|---|---|
| [listen](../listen.md) | `fun <T> `[`Observer`](../-observer/index.md)`<T>.listen(listener: `[`ValueListener`](../-value-listener/index.md)`<T>): `[`Observer`](../-observer/index.md)`<T>` |

### Inheritors

| Name | Summary |
|---|---|
| [ListSubject](../-list-subject/index.md) | `class ListSubject<T> : `[`ValueSubject`](./index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>, `[`ListObserver`](../-list-observer/index.md)`<T>` |
