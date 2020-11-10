[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [AccountSpec](./index.md)

# AccountSpec

`sealed class AccountSpec`

A spec for how to interpret a set of accounts.

### Types

| Name | Summary |
|---|---|
| [Exact](-exact.md) | Use the EXACT account address specified and only that. Fail otherwise.`object Exact : `[`AccountSpec`](./index.md) |
| [Preferred](-preferred.md) | PREFER to use the account address I specify, but if that does not exist, resolve the tokenAccounts for this account and use the first one.`object Preferred : `[`AccountSpec`](./index.md) |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
