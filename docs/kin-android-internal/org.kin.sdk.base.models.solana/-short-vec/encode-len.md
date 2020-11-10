[kin-android](../../index.md) / [org.kin.sdk.base.models.solana](../index.md) / [ShortVec](index.md) / [encodeLen](./encode-len.md)

# encodeLen

`fun encodeLen(output: `[`ByteArrayOutputStream`](https://docs.oracle.com/javase/6/docs/api/java/io/ByteArrayOutputStream.html)`, length: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

encodeLen encodes the specified [length](encode-len.md#org.kin.sdk.base.models.solana.ShortVec$encodeLen(java.io.ByteArrayOutputStream, kotlin.Int)/length) into the [output](encode-len.md#org.kin.sdk.base.models.solana.ShortVec$encodeLen(java.io.ByteArrayOutputStream, kotlin.Int)/output).

### Parameters

`output` -
* the outputStream the length, of the ShortVec. is to be encoded in

**Return**
* the number of bytes written. If length &gt; UShort.MAX_VALUE.toInt() , a [RuntimeException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-runtime-exception/index.html) is thrown

