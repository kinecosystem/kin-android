[kin-android](../../index.md) / [kin.sdk](../index.md) / [KinClient](./index.md)

# KinClient

`open class KinClient`

An account manager for a [KinAccount].

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | For more details please look at ``[`#KinClient(Context context, Environment environment, String appId, String storeKey)`](-init-.md)`KinClient(context: Context, environment: `[`Environment`](../-environment/index.md)`, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!)`<br>Build KinClient object.`KinClient(context: Context, environment: `[`Environment`](../-environment/index.md)`, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)``KinClient(context: Context!, environment: `[`Environment`](../-environment/index.md)`!, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, backupRestore: `[`BackupRestore`](../-backup-restore/index.md)`!, keyStore: `[`KeyStore`](../-key-store/index.md)`!, storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md)`!, kinEnvironment: `[`KinEnvironment`](../../org.kin.sdk.base/-kin-environment/index.md)`!)`<br>`KinClient(context: Context!, environment: `[`Environment`](../-environment/index.md)`!, appId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, storeKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, backupRestore: `[`BackupRestore`](../-backup-restore/index.md)`!, keyStore: `[`KeyStore`](../-key-store/index.md)`!, storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md)`!)` |

### Functions

| Name | Summary |
|---|---|
| [addAccount](add-account.md) | Creates and adds an account. `open fun addAccount(): `[`KinAccount`](../-kin-account/index.md) |
| [clearAllAccounts](clear-all-accounts.md) | Deletes all accounts.`open fun clearAllAccounts(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [deleteAccount](delete-account.md) | Deletes the account at input index (if it exists)`open fun deleteAccount(index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [getAccount](get-account.md) | Returns an account at input index.`open fun getAccount(index: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`KinAccount`](../-kin-account/index.md)`!` |
| [getAccountByPublicAddress](get-account-by-public-address.md) | Returns an account corresponding to the supplied public address.`open fun getAccountByPublicAddress(accountId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`KinAccount`](../-kin-account/index.md)`!` |
| [getAccountCount](get-account-count.md) | Returns the number of existing accounts`open fun getAccountCount(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [getAppId](get-app-id.md) | `open fun getAppId(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!` |
| [getEnvironment](get-environment.md) | `open fun getEnvironment(): `[`Environment`](../-environment/index.md)`!` |
| [getMinimumFee](get-minimum-fee.md) | Get the current minimum fee that the network charges per operation. This value is expressed in stroops.`open fun getMinimumFee(): `[`Request`](../../kin.utils/-request/index.md)`<`[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`!>!` |
| [getMinimumFeeSync](get-minimum-fee-sync.md) | Get the current minimum fee that the network charges per operation. This value is expressed in stroops. `open fun getMinimumFeeSync(): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [getStoreKey](get-store-key.md) | `open fun getStoreKey(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!` |
| [hasAccount](has-account.md) | `open fun hasAccount(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [importAccount](import-account.md) | Import an account from a JSON-formatted string.`open fun importAccount(exportedJson: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, passphrase: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`KinAccount`](../-kin-account/index.md) |
| [testMigration](test-migration.md) | Used to enable migration to Solana`open static fun testMigration(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
