[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [KinBinaryMemo](./index.md)

# KinBinaryMemo

`data class KinBinaryMemo`

A binary Kin memo format.

### Parameters

`magicByteIndicator` - 2 bits   | &lt; 4

`version` - 3 bits   | &lt; 8

`typeId` - 5 bits   | &lt; 32

`appIdx` - 16 bits  | &lt; 65,536

`foreignKey` - 230 bits | Base64 Encoded String of [230 bits+2 zeros padding](#)

**See Also**

[the](#)

### Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | `data class Builder` |
| [TransferType](-transfer-type/index.md) | `sealed class TransferType` |

### Properties

| Name | Summary |
|---|---|
| [appIdx](app-idx.md) | 16 bits  | &lt; 65,536`val appIdx: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [foreignKey](foreign-key.md) | 230 bits | Base64 Encoded String of [230 bits+2 zeros padding](#)`val foreignKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [foreignKeyBytes](foreign-key-bytes.md) | `val foreignKeyBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [magicByteIndicator](magic-byte-indicator.md) | 2 bits   | &lt; 4`val magicByteIndicator: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [typeId](type-id.md) | 5 bits   | &lt; 32`val typeId: TransferType` |
| [version](version.md) | 3 bits   | &lt; 8`val version: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [encode](encode.md) | Fields below are packed from LSB to MSB order:`fun encode(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [toKinMemo](to-kin-memo.md) | `fun toKinMemo(): `[`KinMemo`](../-kin-memo/index.md) |
| [toString](to-string.md) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [BIT_LENGTH_APP_IDX](-b-i-t_-l-e-n-g-t-h_-a-p-p_-i-d-x.md) | `const val BIT_LENGTH_APP_IDX: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_LENGTH_FOREIGN_KEY](-b-i-t_-l-e-n-g-t-h_-f-o-r-e-i-g-n_-k-e-y.md) | `const val BIT_LENGTH_FOREIGN_KEY: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_LENGTH_MAGIC_BYTE_INDICATOR](-b-i-t_-l-e-n-g-t-h_-m-a-g-i-c_-b-y-t-e_-i-n-d-i-c-a-t-o-r.md) | `const val BIT_LENGTH_MAGIC_BYTE_INDICATOR: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_LENGTH_TYPE_ID](-b-i-t_-l-e-n-g-t-h_-t-y-p-e_-i-d.md) | `const val BIT_LENGTH_TYPE_ID: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_LENGTH_VERSION](-b-i-t_-l-e-n-g-t-h_-v-e-r-s-i-o-n.md) | `const val BIT_LENGTH_VERSION: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_OFFSET_APP_IDX](-b-i-t_-o-f-f-s-e-t_-a-p-p_-i-d-x.md) | `const val BIT_OFFSET_APP_IDX: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_OFFSET_FOREIGN_KEY](-b-i-t_-o-f-f-s-e-t_-f-o-r-e-i-g-n_-k-e-y.md) | `const val BIT_OFFSET_FOREIGN_KEY: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_OFFSET_MAGIC_BYTE_INDICATOR](-b-i-t_-o-f-f-s-e-t_-m-a-g-i-c_-b-y-t-e_-i-n-d-i-c-a-t-o-r.md) | `const val BIT_OFFSET_MAGIC_BYTE_INDICATOR: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_OFFSET_TYPE_ID](-b-i-t_-o-f-f-s-e-t_-t-y-p-e_-i-d.md) | `const val BIT_OFFSET_TYPE_ID: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BIT_OFFSET_VERSION](-b-i-t_-o-f-f-s-e-t_-v-e-r-s-i-o-n.md) | `const val BIT_OFFSET_VERSION: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BYTE_COUNT_FOREIGN_KEY](-b-y-t-e_-c-o-u-n-t_-f-o-r-e-i-g-n_-k-e-y.md) | `val BYTE_COUNT_FOREIGN_KEY: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BYTE_COUNT_LOWER_BYTES](-b-y-t-e_-c-o-u-n-t_-l-o-w-e-r_-b-y-t-e-s.md) | `val BYTE_COUNT_LOWER_BYTES: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BYTE_COUNT_TOTAL](-b-y-t-e_-c-o-u-n-t_-t-o-t-a-l.md) | `val BYTE_COUNT_TOTAL: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [BYTE_OF_FK_START](-b-y-t-e_-o-f_-f-k_-s-t-a-r-t.md) | `val BYTE_OF_FK_START: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [MASK_APP_IDX](-m-a-s-k_-a-p-p_-i-d-x.md) | `const val MASK_APP_IDX: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [MASK_MAGIC_BYTE_INDICATOR](-m-a-s-k_-m-a-g-i-c_-b-y-t-e_-i-n-d-i-c-a-t-o-r.md) | `const val MASK_MAGIC_BYTE_INDICATOR: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [MASK_TYPE_ID](-m-a-s-k_-t-y-p-e_-i-d.md) | `const val MASK_TYPE_ID: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [MASK_VERSION](-m-a-s-k_-v-e-r-s-i-o-n.md) | `const val MASK_VERSION: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [decode](decode.md) | `fun decode(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`KinBinaryMemo`](./index.md) |
