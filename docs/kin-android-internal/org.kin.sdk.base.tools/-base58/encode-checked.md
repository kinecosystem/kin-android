[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Base58](index.md) / [encodeChecked](./encode-checked.md)

# encodeChecked

`fun encodeChecked(version: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, payload: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)

Encodes the given version and bytes as a base58 string. A checksum is appended.

### Parameters

`version` - the version to encode

`payload` - the bytes to encode, e.g. pubkey hash

**Return**
the base58-encoded string

