[kin-android](../../index.md) / [org.kin.sdk.base.tools.sha224](../index.md) / [EncodableDigest](./index.md)

# EncodableDigest

`interface EncodableDigest`

Encodable digests allow you to download an encoded copy of their internal state. This is useful for the situation where
you need to generate a signature on an external device and it allows for "sign with last round", so a copy of the
internal state of the digest, plus the last few blocks of the message are all that needs to be sent, rather than the
entire message.

### Properties

| Name | Summary |
|---|---|
| [encodedState](encoded-state.md) | Return an encoded byte array for the digest's internal state`abstract val encodedState: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [SHA224Digest](../-s-h-a224-digest/index.md) | SHA-224 as described in RFC 3874`class SHA224Digest : `[`GeneralDigest`](../-general-digest/index.md)`, `[`EncodableDigest`](./index.md) |
