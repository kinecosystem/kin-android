[kin-android](../../index.md) / [org.kin.sdk.base.tools.sha224](../index.md) / [Digest](./index.md)

# Digest

`interface Digest`

interface that a message digest conforms to.

### Properties

| Name | Summary |
|---|---|
| [algorithmName](algorithm-name.md) | return the algorithm name`abstract val algorithmName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [digestSize](digest-size.md) | return the size, in bytes, of the digest produced by this message digest.`abstract val digestSize: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [doFinal](do-final.md) | close the digest, producing the final digest value. The doFinal call leaves the digest reset.`abstract fun doFinal(out: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, outOff: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [reset](reset.md) | reset the digest back to it's initial state.`abstract fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [update](update.md) | update the message digest with a single byte.`abstract fun update(inBytes: `[`Byte`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>update the message digest with a block of bytes.`abstract fun update(inBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, inOff: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, len: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [ExtendedDigest](../-extended-digest/index.md) | `interface ExtendedDigest : `[`Digest`](./index.md) |
