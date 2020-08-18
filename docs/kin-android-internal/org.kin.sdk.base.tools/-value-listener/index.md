[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [ValueListener](./index.md)

# ValueListener

`interface ValueListener<T>`

May call [onNext](on-next.md) or [onError](on-error.md) in a sequence of value updates.
Should not emit onNext updates after an onError event.

### Functions

| Name | Summary |
|---|---|
| [onError](on-error.md) | `abstract fun onError(error: `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onNext](on-next.md) | `abstract fun onNext(value: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
