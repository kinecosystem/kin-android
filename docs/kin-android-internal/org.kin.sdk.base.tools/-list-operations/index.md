[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [ListOperations](./index.md)

# ListOperations

`interface ListOperations<T>`

### Functions

| Name | Summary |
|---|---|
| [requestNextPage](request-next-page.md) | `abstract fun requestNextPage(): `[`ListObserver`](../-list-observer/index.md)`<T>` |
| [requestPreviousPage](request-previous-page.md) | `abstract fun requestPreviousPage(): `[`ListObserver`](../-list-observer/index.md)`<T>` |

### Inheritors

| Name | Summary |
|---|---|
| [ListObserver](../-list-observer/index.md) | `interface ListObserver<T> : `[`Observer`](../-observer/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>>, `[`ListOperations`](./index.md)`<T>` |
