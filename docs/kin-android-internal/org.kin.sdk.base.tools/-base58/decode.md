[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Base58](index.md) / [decode](./decode.md)

# decode

`fun decode(input: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)

Decodes the given base58 string into the original data bytes.

### Parameters

`input` - the base58-encoded string to decode

### Exceptions

`AddressFormatException` - if the given string is not a valid base58 string

**Return**
the decoded data bytes

