[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Base58](index.md) / [hashTwice](./hash-twice.md)

# hashTwice

`@JvmOverloads fun hashTwice(input: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0, length: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = input.size): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)

Calculates the SHA-256 hash of the given byte range,
and then hashes the resulting hash again.

### Parameters

`input` - the array containing the bytes to hash

`offset` - the offset within the array of the bytes to hash

`length` - the number of bytes to hash

**Return**
the double-hash (in big-endian order)

