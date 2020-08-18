[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [ListSubject](./index.md)

# ListSubject

`class ListSubject<T> : `[`ValueSubject`](../-value-subject/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>, `[`ListObserver`](../-list-observer/index.md)`<T>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ListSubject(fetchNextPage: (() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)? = null, fetchPreviousPage: (() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)? = null, triggerInvalidation: (() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)? = null)` |

### Functions

| Name | Summary |
|---|---|
| [add](add.md) | `fun add(listener: (`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`ListObserver`](../-list-observer/index.md)`<T>` |
| [requestNextPage](request-next-page.md) | `fun requestNextPage(): `[`ListObserver`](../-list-observer/index.md)`<T>` |
| [requestPreviousPage](request-previous-page.md) | `fun requestPreviousPage(): `[`ListObserver`](../-list-observer/index.md)`<T>` |

### Extension Functions

| Name | Summary |
|---|---|
| [listen](../listen.md) | `fun <T> `[`Observer`](../-observer/index.md)`<T>.listen(listener: `[`ValueListener`](../-value-listener/index.md)`<T>): `[`Observer`](../-observer/index.md)`<T>`<br>`fun <T> `[`ListObserver`](../-list-observer/index.md)`<T>.listen(listener: `[`ValueListener`](../-value-listener/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>): `[`ListObserver`](../-list-observer/index.md)`<T>` |
