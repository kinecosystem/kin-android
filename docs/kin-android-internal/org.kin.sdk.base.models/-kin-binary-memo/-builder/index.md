[kin-android](../../../index.md) / [org.kin.sdk.base.models](../../index.md) / [KinBinaryMemo](../index.md) / [Builder](./index.md)

# Builder

`data class Builder`

### Exceptions

| Name | Summary |
|---|---|
| [KinBinaryMemoFormatException](-kin-binary-memo-format-exception/index.md) | `data class KinBinaryMemoFormatException : `[`RuntimeException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-runtime-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Builder(appIdx: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, magicByteIndicator: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0x1, version: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0)` |

### Properties

| Name | Summary |
|---|---|
| [appIdx](app-idx.md) | `val appIdx: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [magicByteIndicator](magic-byte-indicator.md) | `val magicByteIndicator: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [version](version.md) | `val version: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [build](build.md) | `fun build(): `[`KinBinaryMemo`](../index.md) |
| [setForeignKey](set-foreign-key.md) | `fun setForeignKey(foreignKeyBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): Builder` |
| [setTranferType](set-tranfer-type.md) | `fun setTranferType(typeId: TransferType): Builder` |
