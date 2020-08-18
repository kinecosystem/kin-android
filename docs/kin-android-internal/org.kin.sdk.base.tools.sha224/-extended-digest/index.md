[kin-android](../../index.md) / [org.kin.sdk.base.tools.sha224](../index.md) / [ExtendedDigest](./index.md)

# ExtendedDigest

`interface ExtendedDigest : `[`Digest`](../-digest/index.md)

### Properties

| Name | Summary |
|---|---|
| [byteLength](byte-length.md) | Return the size in bytes of the internal buffer the digest applies it's compression function to.`abstract val byteLength: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [GeneralDigest](../-general-digest/index.md) | base implementation of MD4 family style digest as outlined in "Handbook of Applied Cryptography", pages 344 - 347.`abstract class GeneralDigest : `[`ExtendedDigest`](./index.md)`, `[`Memoable`](../-memoable/index.md) |
