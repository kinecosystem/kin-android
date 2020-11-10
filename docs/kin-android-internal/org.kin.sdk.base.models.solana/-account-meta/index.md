[kin-android](../../index.md) / [org.kin.sdk.base.models.solana](../index.md) / [AccountMeta](./index.md)

# AccountMeta

`data class AccountMeta : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<`[`AccountMeta`](./index.md)`>`

AccountMeta represents the account information required
for building transactions.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | AccountMeta represents the account information required for building transactions.`AccountMeta(publicKey: PublicKey, isSigner: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, isWritable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, isPayer: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, isProgram: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)` |

### Properties

| Name | Summary |
|---|---|
| [isPayer](is-payer.md) | `val isPayer: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isProgram](is-program.md) | `val isProgram: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isSigner](is-signer.md) | `val isSigner: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isWritable](is-writable.md) | `val isWritable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [publicKey](public-key.md) | `val publicKey: PublicKey` |

### Functions

| Name | Summary |
|---|---|
| [compareTo](compare-to.md) | `fun compareTo(other: `[`AccountMeta`](./index.md)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [newAccountMeta](new-account-meta.md) | `fun newAccountMeta(publicKey: PublicKey, isSigner: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, isPayer: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, isProgram: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`AccountMeta`](./index.md) |
| [newReadonlyAccountMeta](new-readonly-account-meta.md) | `fun newReadonlyAccountMeta(publicKey: PublicKey, isSigner: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, isPayer: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, isProgram: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`AccountMeta`](./index.md) |
