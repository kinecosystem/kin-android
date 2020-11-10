[kin-android](../../index.md) / [org.kin.sdk.base.models.solana](../index.md) / [ShortVec](./index.md)

# ShortVec

`object ShortVec`

### Functions

| Name | Summary |
|---|---|
| [decodeLen](decode-len.md) | decodeLen decodes a ShortVec encoded [length](#) from the [input](decode-len.md#org.kin.sdk.base.models.solana.ShortVec$decodeLen(java.io.ByteArrayInputStream)/input).`fun decodeLen(input: `[`ByteArrayInputStream`](https://docs.oracle.com/javase/6/docs/api/java/io/ByteArrayInputStream.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [decodeShortVecOf](decode-short-vec-of.md) | `fun <T> decodeShortVecOf(input: `[`ByteArrayInputStream`](https://docs.oracle.com/javase/6/docs/api/java/io/ByteArrayInputStream.html)`, sizeOfElement: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, newInstance: (`[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`) -> T? = { null }): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>` |
| [encodeLen](encode-len.md) | encodeLen encodes the specified [length](encode-len.md#org.kin.sdk.base.models.solana.ShortVec$encodeLen(java.io.ByteArrayOutputStream, kotlin.Int)/length) into the [output](encode-len.md#org.kin.sdk.base.models.solana.ShortVec$encodeLen(java.io.ByteArrayOutputStream, kotlin.Int)/output).`fun encodeLen(output: `[`ByteArrayOutputStream`](https://docs.oracle.com/javase/6/docs/api/java/io/ByteArrayOutputStream.html)`, length: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [encodeShortVecOf](encode-short-vec-of.md) | `fun <T> encodeShortVecOf(output: `[`ByteArrayOutputStream`](https://docs.oracle.com/javase/6/docs/api/java/io/ByteArrayOutputStream.html)`, elements: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>, byteTransform: (T) -> `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
