[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinClient](./index.md)

# KinClient

`class KinClient`

An account manager for a [KinAccount](../-kin-account/index.md).

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Build KinClient object.`KinClient(context: Context, environment: `[`Environment`](../-environment/index.md)`, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`<br>For more details please look at [KinClient](./index.md)`KinClient(context: Context, environment: `[`Environment`](../-environment/index.md)`, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [accountCount](account-count.md) | Returns the number of existing accounts`val accountCount: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [appId](app-id.md) | `val appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [environment](environment.md) | `val environment: `[`Environment`](../-environment/index.md) |
| [minimumFee](minimum-fee.md) | Get the current minimum fee that the network charges per operation. This value is expressed in stroops.`val minimumFee: `[`Request`](../../kin.utils/-request/index.md)`<`[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`>` |
| [minimumFeeSync](minimum-fee-sync.md) | Get the current minimum fee that the network charges per operation. This value is expressed in stroops.`val minimumFeeSync: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [storeKey](store-key.md) | `val storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [addAccount](add-account.md) | Creates and adds an account.`fun addAccount(): `[`KinAccount`](../-kin-account/index.md) |
| [clearAllAccounts](clear-all-accounts.md) | Deletes all accounts.`fun clearAllAccounts(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [deleteAccount](delete-account.md) | Deletes the account at input index (if it exists)`fun deleteAccount(index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [getAccount](get-account.md) | Returns an account at input index.`fun getAccount(index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`KinAccount`](../-kin-account/index.md)`?` |
| [getAccountByPublicAddress](get-account-by-public-address.md) | `fun getAccountByPublicAddress(accountId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KinAccount`](../-kin-account/index.md)`?` |
| [hasAccount](has-account.md) | `fun hasAccount(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [importAccount](import-account.md) | Import an account from a JSON-formatted string.`fun importAccount(exportedJson: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, passphrase: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KinAccount`](../-kin-account/index.md) |
