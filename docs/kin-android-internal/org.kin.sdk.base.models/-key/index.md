[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [Key](./index.md)

# Key

`sealed class Key`

### Types

| Name | Summary |
|---|---|
| [PrivateKey](-private-key/index.md) | `data class PrivateKey : `[`Key`](./index.md) |
| [PublicKey](-public-key/index.md) | `data class PublicKey : `[`Key`](./index.md) |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `abstract val value: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |

### Extension Functions

| Name | Summary |
|---|---|
| [asKinAccountId](../as-kin-account-id.md) | `fun `[`Key`](./index.md)`.asKinAccountId(): Id` |
| [asPublicKey](../as-public-key.md) | `fun `[`Key`](./index.md)`.asPublicKey(): PublicKey` |
