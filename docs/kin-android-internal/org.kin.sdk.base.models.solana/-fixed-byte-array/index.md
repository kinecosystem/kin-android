[kin-android](../../index.md) / [org.kin.sdk.base.models.solana](../index.md) / [FixedByteArray](./index.md)

# FixedByteArray

`abstract class FixedByteArray`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `FixedByteArray(byteArray: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [byteArray](byte-array.md) | `val byteArray: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [size](size.md) | `abstract val size: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [check](check.md) | `fun check(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [equals](equals.md) | `open fun equals(other: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [get](get.md) | `operator fun get(i: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Byte`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte/index.html) |
| [hashCode](hash-code.md) | `open fun hashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [set](set.md) | `operator fun set(i: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, b: `[`Byte`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Extension Functions

| Name | Summary |
|---|---|
| [contentEquals](../content-equals.md) | `infix fun `[`FixedByteArray`](./index.md)`?.contentEquals(other: `[`FixedByteArray`](./index.md)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [contentHashCode](../content-hash-code.md) | `fun `[`FixedByteArray`](./index.md)`?.contentHashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [FixedByteArray32](../-fixed-byte-array32/index.md) | `class FixedByteArray32 : `[`FixedByteArray`](./index.md) |
| [FixedByteArray64](../-fixed-byte-array64/index.md) | `class FixedByteArray64 : `[`FixedByteArray`](./index.md) |
