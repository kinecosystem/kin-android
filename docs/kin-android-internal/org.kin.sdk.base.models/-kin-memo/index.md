[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [KinMemo](./index.md)

# KinMemo

`data class KinMemo`

### Types

| Name | Summary |
|---|---|
| [Type](-type/index.md) | `sealed class Type` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Text that will be encoded into charset representation, defaults to UTF8 encoding`KinMemo(textValue: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, charset: `[`Charset`](https://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html)` = Charsets.UTF_8)``KinMemo(rawValue: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, type: Type = Type.NoEncoding)` |

### Properties

| Name | Summary |
|---|---|
| [rawValue](raw-value.md) | `val rawValue: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [type](type.md) | `val type: Type` |

### Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | `fun equals(other: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | `fun hashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [NONE](-n-o-n-e.md) | `val NONE: `[`KinMemo`](./index.md) |

### Extension Functions

| Name | Summary |
|---|---|
| [getAgoraMemo](../get-agora-memo.md) | `fun `[`KinMemo`](./index.md)`.getAgoraMemo(): `[`KinBinaryMemo`](../-kin-binary-memo/index.md)`?` |
