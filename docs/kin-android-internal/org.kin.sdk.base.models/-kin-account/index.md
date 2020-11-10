[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [KinAccount](./index.md)

# KinAccount

`data class KinAccount`

### Types

| Name | Summary |
|---|---|
| [Id](-id/index.md) | `data class Id` |
| [Status](-status/index.md) | `sealed class Status` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `KinAccount(key: `[`Key`](../-key/index.md)`, id: Id = Id(key.asPublicKey().value), tokenAccounts: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PublicKey> = emptyList(), balance: `[`KinBalance`](../-kin-balance/index.md)` = KinBalance(), status: Status = Status.Unregistered)` |

### Properties

| Name | Summary |
|---|---|
| [balance](balance.md) | `val balance: `[`KinBalance`](../-kin-balance/index.md) |
| [id](id.md) | `val id: Id` |
| [key](key.md) | `val key: `[`Key`](../-key/index.md) |
| [status](status.md) | `val status: Status` |
| [tokenAccounts](token-accounts.md) | `val tokenAccounts: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PublicKey>` |

### Extension Functions

| Name | Summary |
|---|---|
| [merge](../merge.md) | `fun `[`KinAccount`](./index.md)`.merge(newer: `[`KinAccount`](./index.md)`): `[`KinAccount`](./index.md) |
| [toAccount](../to-account.md) | `fun `[`KinAccount`](./index.md)`.toAccount(): Account` |
| [toSigningKeyPair](../to-signing-key-pair.md) | `fun `[`KinAccount`](./index.md)`.toSigningKeyPair(): KeyPair` |
