[kin-android](../../../index.md) / [org.kin.sdk.base.models](../../index.md) / [KinBinaryMemo](../index.md) / [TransferType](./index.md)

# TransferType

`sealed class TransferType`

### Types

| Name | Summary |
|---|---|
| [Earn](-earn.md) | Use when transferring Kin to a user for some performed action.`object Earn : TransferType` |
| [P2P](-p2-p.md) | Use when transferring Kin where it does not constitute an [Earn](-earn.md) or [Spend](-spend.md)`object P2P : TransferType` |
| [Spend](-spend.md) | Use when transferring Kin due to purchasing something.`object Spend : TransferType` |
| [Unknown](-unknown.md) | An unclassified transfer of Kin.`object Unknown : TransferType` |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [fromValue](from-value.md) | `fun fromValue(value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): TransferType` |
