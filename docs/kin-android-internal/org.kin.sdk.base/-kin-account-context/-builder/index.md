[kin-android](../../../index.md) / [org.kin.sdk.base](../../index.md) / [KinAccountContext](../index.md) / [Builder](./index.md)

# Builder

`class Builder`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Builder(envBuilder: CompletedBuilder)`<br>`Builder(envBuilder: CompletedBuilder)`<br>`Builder(env: `[`KinEnvironment`](../../-kin-environment/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [createNewAccount](create-new-account.md) | `fun createNewAccount(): NewAccountBuilder` |
| [importExistingPrivateKey](import-existing-private-key.md) | `fun importExistingPrivateKey(privateKey: PrivateKey): ExistingAccountBuilder` |
| [useExistingAccount](use-existing-account.md) | `fun useExistingAccount(accountId: Id): ExistingAccountBuilder` |
| [useExistingAccountReadOnly](use-existing-account-read-only.md) | `fun useExistingAccountReadOnly(accountId: Id): ReadOnlyAccountBuilder` |
