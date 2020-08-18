[kin-android](../../index.md) / [org.kin.sdk.base.tools.sha224](../index.md) / [SHA224Digest](./index.md)

# SHA224Digest

`class SHA224Digest : `[`GeneralDigest`](../-general-digest/index.md)`, `[`EncodableDigest`](../-encodable-digest/index.md)

SHA-224 as described in RFC 3874

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Standard constructor`SHA224Digest()`<br>Copy constructor.  This will copy the state of the provided message digest.`SHA224Digest(t: `[`SHA224Digest`](./index.md)`)`<br>State constructor - create a digest initialised with the state of a previous one.`SHA224Digest(encodedState: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [algorithmName](algorithm-name.md) | return the algorithm name`val algorithmName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [digestSize](digest-size.md) | return the size, in bytes, of the digest produced by this message digest.`val digestSize: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [encodedState](encoded-state.md) | Return an encoded byte array for the digest's internal state`val encodedState: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |

### Functions

| Name | Summary |
|---|---|
| [copy](copy.md) | Produce a copy of this object with its configuration and in its current state.`fun copy(): `[`Memoable`](../-memoable/index.md) |
| [doFinal](do-final.md) | close the digest, producing the final digest value. The doFinal call leaves the digest reset.`fun doFinal(out: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, outOff: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [processBlock](process-block.md) | `fun processBlock(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [processLength](process-length.md) | `fun processLength(bitLength: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [processWord](process-word.md) | `fun processWord(inBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, inOff: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [reset](reset.md) | reset the chaining variables`fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Restore a copied object state into this object.`fun reset(other: `[`Memoable`](../-memoable/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [DIGEST_LENGTH](-d-i-g-e-s-t_-l-e-n-g-t-h.md) | `const val DIGEST_LENGTH: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [K](-k.md) | SHA-224 Constants (represent the first 32 bits of the fractional parts of the cube roots of the first sixty-four prime numbers)`val K: `[`IntArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html) |
