[kin-android](../index.md) / [org.kin.sdk.base.models.solana](./index.md)

## Package org.kin.sdk.base.models.solana

### Types

| Name | Summary |
|---|---|
| [AccountMeta](-account-meta/index.md) | AccountMeta represents the account information required for building transactions.`data class AccountMeta : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<`[`AccountMeta`](-account-meta/index.md)`>` |
| [CompiledInstruction](-compiled-instruction/index.md) | `data class CompiledInstruction` |
| [FixedByteArray](-fixed-byte-array/index.md) | `abstract class FixedByteArray` |
| [FixedByteArray32](-fixed-byte-array32/index.md) | `class FixedByteArray32 : `[`FixedByteArray`](-fixed-byte-array/index.md) |
| [FixedByteArray64](-fixed-byte-array64/index.md) | `class FixedByteArray64 : `[`FixedByteArray`](-fixed-byte-array/index.md) |
| [Hash](-hash/index.md) | `data class Hash` |
| [Header](-header/index.md) | `data class Header` |
| [Instruction](-instruction/index.md) | Instruction represents a transaction instruction.`data class Instruction` |
| [MemoProgram](-memo-program/index.md) | `object MemoProgram` |
| [Message](-message/index.md) | `data class Message` |
| [ShortVec](-short-vec/index.md) | `object ShortVec` |
| [Signature](-signature/index.md) | `data class Signature` |
| [SystemProgram](-system-program/index.md) | `object SystemProgram` |
| [TokenProgram](-token-program/index.md) | `object TokenProgram` |
| [Transaction](-transaction/index.md) | `data class Transaction` |

### Extensions for External Classes

| Name | Summary |
|---|---|
| [java.io.ByteArrayInputStream](java.io.-byte-array-input-stream/index.md) |  |
| [kotlin.ByteArray](kotlin.-byte-array/index.md) |  |
| [kotlin.collections.List](kotlin.collections.-list/index.md) |  |

### Functions

| Name | Summary |
|---|---|
| [contentEquals](content-equals.md) | `infix fun `[`FixedByteArray`](-fixed-byte-array/index.md)`?.contentEquals(other: `[`FixedByteArray`](-fixed-byte-array/index.md)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [contentHashCode](content-hash-code.md) | `fun `[`FixedByteArray`](-fixed-byte-array/index.md)`?.contentHashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [marshal](marshal.md) | `fun `[`Transaction`](-transaction/index.md)`.marshal(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)<br>`fun `[`Signature`](-signature/index.md)`.marshal(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)<br>`fun `[`CompiledInstruction`](-compiled-instruction/index.md)`.marshal(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)<br>`fun PublicKey.marshal(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)<br>`fun `[`Hash`](-hash/index.md)`.marshal(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)<br>`fun `[`Message`](-message/index.md)`.marshal(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [wrapError](wrap-error.md) | `fun <T> wrapError(msg: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, wrapped: () -> T): T` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [unmarshal](unmarshal.md) | `fun Signature.Companion.unmarshal(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`Signature`](-signature/index.md)<br>`fun Transaction.Companion.unmarshal(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`Transaction`](-transaction/index.md)<br>`fun Message.Companion.unmarshal(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`Message`](-message/index.md) |
