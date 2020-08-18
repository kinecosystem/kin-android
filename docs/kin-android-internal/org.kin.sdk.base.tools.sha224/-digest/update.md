[kin-android](../../index.md) / [org.kin.sdk.base.tools.sha224](../index.md) / [Digest](index.md) / [update](./update.md)

# update

`abstract fun update(inBytes: `[`Byte`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

update the message digest with a single byte.

### Parameters

`in` - the input byte to be entered.`abstract fun update(inBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, inOff: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, len: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

update the message digest with a block of bytes.

### Parameters

`in` - the byte array containing the data.

`inOff` - the offset into the byte array where the data starts.

`len` - the length of the data.