[kin-android](../index.md) / [org.kin.sdk.base.tools](index.md) / [onErrorResumeNext](./on-error-resume-next.md)

# onErrorResumeNext

`fun <T> `[`Promise`](-promise/index.md)`<T>.onErrorResumeNext(resumeNext: (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Promise`](-promise/index.md)`<T>): `[`Promise`](-promise/index.md)`<T>`
`fun <T, ErrorType : `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`> `[`Promise`](-promise/index.md)`<T>.onErrorResumeNext(error: `[`Class`](https://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<ErrorType>, resumeNext: (`[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`) -> `[`Promise`](-promise/index.md)`<T>): `[`Promise`](-promise/index.md)`<T>`