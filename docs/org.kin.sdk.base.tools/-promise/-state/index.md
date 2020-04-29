[kin-android](../../../index.md) / [org.kin.sdk.base.tools](../../index.md) / [Promise](../index.md) / [State](./index.md)

# State

`sealed class State<out T>`

### Types

| Name | Summary |
|---|---|
| [Pending](-pending/index.md) | `class Pending<T> : State<T>` |
| [Rejected](-rejected/index.md) | `data class Rejected<T> : State<T>` |
| [Resolved](-resolved/index.md) | `data class Resolved<T> : State<T>` |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
