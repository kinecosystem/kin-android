[kin-android](../../index.md) / [org.kin.sdk.base.models.solana](../index.md) / [Message](./index.md)

# Message

`data class Message`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Message(header: `[`Header`](../-header/index.md)`, accounts: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PublicKey>, instructions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`CompiledInstruction`](../-compiled-instruction/index.md)`>, recentBlockhash: `[`Hash`](../-hash/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [accounts](accounts.md) | `val accounts: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PublicKey>` |
| [header](header.md) | `val header: `[`Header`](../-header/index.md) |
| [instructions](instructions.md) | `val instructions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`CompiledInstruction`](../-compiled-instruction/index.md)`>` |
| [recentBlockhash](recent-blockhash.md) | `val recentBlockhash: `[`Hash`](../-hash/index.md) |

### Extension Functions

| Name | Summary |
|---|---|
| [marshal](../marshal.md) | `fun `[`Message`](./index.md)`.marshal(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |

### Companion Object Extension Functions

| Name | Summary |
|---|---|
| [unmarshal](../unmarshal.md) | `fun Message.Companion.unmarshal(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`Message`](./index.md) |
