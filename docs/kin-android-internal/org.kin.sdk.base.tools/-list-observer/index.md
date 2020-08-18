[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [ListObserver](./index.md)

# ListObserver

`interface ListObserver<T> : `[`Observer`](../-observer/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>, `[`ListOperations`](../-list-operations/index.md)`<T>`

### Functions

| Name | Summary |
|---|---|
| [add](add.md) | `abstract fun add(listener: (`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`ListObserver`](./index.md)`<T>` |

### Extension Functions

| Name | Summary |
|---|---|
| [listen](../listen.md) | `fun <T> `[`ListObserver`](./index.md)`<T>.listen(listener: `[`ValueListener`](../-value-listener/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>): `[`ListObserver`](./index.md)`<T>`<br>`fun <T> `[`Observer`](../-observer/index.md)`<T>.listen(listener: `[`ValueListener`](../-value-listener/index.md)`<T>): `[`Observer`](../-observer/index.md)`<T>` |

### Inheritors

| Name | Summary |
|---|---|
| [ListSubject](../-list-subject/index.md) | `class ListSubject<T> : `[`ValueSubject`](../-value-subject/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>, `[`ListObserver`](./index.md)`<T>` |
