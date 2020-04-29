[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Disposable](./index.md)

# Disposable

`interface Disposable<T>`

### Functions

| Name | Summary |
|---|---|
| [dispose](dispose.md) | `abstract fun dispose(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [disposedBy](disposed-by.md) | `abstract fun disposedBy(disposeBag: `[`DisposeBag`](../-dispose-bag/index.md)`): `[`Observer`](../-observer/index.md)`<T>` |
| [doOnDisposed](do-on-disposed.md) | `abstract fun doOnDisposed(onDisposed: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Observer`](../-observer/index.md)`<T>` |

### Inheritors

| Name | Summary |
|---|---|
| [Observer](../-observer/index.md) | `interface Observer<T> : `[`Disposable`](./index.md)`<T>` |
