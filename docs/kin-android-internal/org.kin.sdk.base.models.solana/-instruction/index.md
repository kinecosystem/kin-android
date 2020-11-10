[kin-android](../../index.md) / [org.kin.sdk.base.models.solana](../index.md) / [Instruction](./index.md)

# Instruction

`data class Instruction`

Instruction represents a transaction instruction.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Instruction represents a transaction instruction.`Instruction(program: PublicKey, accounts: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`AccountMeta`](../-account-meta/index.md)`>, data: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [accounts](accounts.md) | `val accounts: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`AccountMeta`](../-account-meta/index.md)`>` |
| [data](data.md) | `val data: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [program](program.md) | `val program: PublicKey` |

### Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | `fun equals(other: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | `fun hashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [newInstruction](new-instruction.md) | `fun newInstruction(program: PublicKey, data: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, vararg accounts: `[`AccountMeta`](../-account-meta/index.md)`): `[`Instruction`](./index.md) |
