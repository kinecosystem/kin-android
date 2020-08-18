[kin-android](../../index.md) / [org.kin.sdk.base.tools.sha224](../index.md) / [GeneralDigest](./index.md)

# GeneralDigest

`abstract class GeneralDigest : `[`ExtendedDigest`](../-extended-digest/index.md)`, `[`Memoable`](../-memoable/index.md)

base implementation of MD4 family style digest as outlined in
"Handbook of Applied Cryptography", pages 344 - 347.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Standard constructor`GeneralDigest()`<br>Copy constructor.  We are using copy constructors in place of the Object.clone() interface as this interface is not supported by J2ME.`GeneralDigest(t: `[`GeneralDigest`](./index.md)`)``GeneralDigest(encodedState: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [byteLength](byte-length.md) | Return the size in bytes of the internal buffer the digest applies it's compression function to.`open val byteLength: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [copyIn](copy-in.md) | `fun copyIn(t: `[`GeneralDigest`](./index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [finish](finish.md) | `fun finish(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [populateState](populate-state.md) | `fun populateState(state: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [processBlock](process-block.md) | `abstract fun processBlock(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [processLength](process-length.md) | `abstract fun processLength(bitLength: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [processWord](process-word.md) | `abstract fun processWord(inBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, inOff: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [reset](reset.md) | reset the digest back to it's initial state.`open fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [update](update.md) | update the message digest with a single byte.`open fun update(inBytes: `[`Byte`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>update the message digest with a block of bytes.`open fun update(inBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, inOff: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, len: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [SHA224Digest](../-s-h-a224-digest/index.md) | SHA-224 as described in RFC 3874`class SHA224Digest : `[`GeneralDigest`](./index.md)`, `[`EncodableDigest`](../-encodable-digest/index.md) |
