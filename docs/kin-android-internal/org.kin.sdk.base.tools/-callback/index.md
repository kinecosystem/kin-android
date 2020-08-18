[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Callback](./index.md)

# Callback

`interface Callback<T> : `[`Function`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-function/index.html)`<T>`

[onCompleted](on-completed.md) to be called when callback is complete with
*either* a non null value or an error but never both.

### Functions

| Name | Summary |
|---|---|
| [onCompleted](on-completed.md) | `abstract fun onCompleted(value: T? = null, error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`? = null): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
