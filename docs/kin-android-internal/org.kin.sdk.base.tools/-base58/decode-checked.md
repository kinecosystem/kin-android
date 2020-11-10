[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Base58](index.md) / [decodeChecked](./decode-checked.md)

# decodeChecked

`fun decodeChecked(input: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)

Decodes the given base58 string into the original data bytes, using the checksum in the
last 4 bytes of the decoded data to verify that the rest are correct. The checksum is
removed from the returned data.

### Parameters

`input` - the base58-encoded string to decode (which should include the checksum)

### Exceptions

`AddressFormatException` - if the input is not base 58 or the checksum does not validate.