[kin-android](../../index.md) / [kin.utils](../index.md) / [ResultCallback](./index.md)

# ResultCallback

`interface ResultCallback<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`!>`

### Functions

| Name | Summary |
|---|---|
| [onError](on-error.md) | Method will be called when operation has failed`abstract fun onError(e: `[`Exception`](https://docs.oracle.com/javase/6/docs/api/java/lang/Exception.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onResult](on-result.md) | Method will be called when operation has completed successfully`abstract fun onResult(result: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
