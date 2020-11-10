[kin-android](../../index.md) / [org.kin.sdk.base.tools](../index.md) / [Base58](./index.md)

# Base58

`object Base58`

Base58 is a way to encode Bitcoin addresses (or arbitrary data) as alphanumeric strings.

Note that this is not the same base58 as used by Flickr, which you may find referenced around the Internet.

Satoshi explains: why base-58 instead of standard base-64 encoding?

* Don't want 0OIl characters that look the same in some fonts and
could be used to create visually identical looking account numbers.
* A string with non-alphanumeric characters is not as easily accepted as an account number.
* E-mail usually won't line-break if there's no punctuation to break at.
* Doubleclicking selects the whole number as one word if it's all alphanumeric.

However, note that the encoding/decoding runs in O(n) time, so it is not useful for large data.

The basic idea of the encoding is to treat the data bytes as a large number represented using
base-256 digits, convert the number to be represented using base-58 digits, preserve the exact
number of leading zeros (which are otherwise lost during the mathematical operations on the
numbers), and finally represent the resulting base-58 digits as alphanumeric ASCII characters.

### Exceptions

| Name | Summary |
|---|---|
| [AddressFormatException](-address-format-exception/index.md) | `sealed class AddressFormatException : `[`IllegalArgumentException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-argument-exception/index.html) |

### Functions

| Name | Summary |
|---|---|
| [decode](decode.md) | Decodes the given base58 string into the original data bytes.`fun decode(input: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [decodeChecked](decode-checked.md) | Decodes the given base58 string into the original data bytes, using the checksum in the last 4 bytes of the decoded data to verify that the rest are correct. The checksum is removed from the returned data.`fun decodeChecked(input: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [decodeToBigInteger](decode-to-big-integer.md) | `fun decodeToBigInteger(input: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`BigInteger`](https://docs.oracle.com/javase/6/docs/api/java/math/BigInteger.html) |
| [encode](encode.md) | Encodes the given bytes as a base58 string (no checksum is appended).`fun encode(input: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [encodeChecked](encode-checked.md) | Encodes the given version and bytes as a base58 string. A checksum is appended.`fun encodeChecked(version: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, payload: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [hashTwice](hash-twice.md) | Calculates the SHA-256 hash of the given byte range, and then hashes the resulting hash again.`fun hashTwice(input: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0, length: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = input.size): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
