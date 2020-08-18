[kin-android](../index.md) / [org.kin.sdk.base.tools.sha224](./index.md)

## Package org.kin.sdk.base.tools.sha224

### Types

| Name | Summary |
|---|---|
| [Digest](-digest/index.md) | interface that a message digest conforms to.`interface Digest` |
| [EncodableDigest](-encodable-digest/index.md) | Encodable digests allow you to download an encoded copy of their internal state. This is useful for the situation where you need to generate a signature on an external device and it allows for "sign with last round", so a copy of the internal state of the digest, plus the last few blocks of the message are all that needs to be sent, rather than the entire message.`interface EncodableDigest` |
| [ExtendedDigest](-extended-digest/index.md) | `interface ExtendedDigest : `[`Digest`](-digest/index.md) |
| [GeneralDigest](-general-digest/index.md) | base implementation of MD4 family style digest as outlined in "Handbook of Applied Cryptography", pages 344 - 347.`abstract class GeneralDigest : `[`ExtendedDigest`](-extended-digest/index.md)`, `[`Memoable`](-memoable/index.md) |
| [Memoable](-memoable/index.md) | Interface for Memoable objects. Memoable objects allow the taking of a snapshot of their internal state via the copy() method and then reseting the object back to that state later using the reset() method.`interface Memoable` |
| [Pack](-pack/index.md) | Utility methods for converting byte arrays into ints and longs, and back again.`object Pack` |
| [SHA224Digest](-s-h-a224-digest/index.md) | SHA-224 as described in RFC 3874`class SHA224Digest : `[`GeneralDigest`](-general-digest/index.md)`, `[`EncodableDigest`](-encodable-digest/index.md) |
