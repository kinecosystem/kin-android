[kin-android](../../index.md) / [org.kin.sdk.base.storage](../index.md) / [KinFileStorage](./index.md)

# KinFileStorage

`class KinFileStorage : `[`Storage`](../-storage/index.md)

### Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | `class Builder` |

### Functions

| Name | Summary |
|---|---|
| [addAccount](add-account.md) | `fun addAccount(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [advanceSequence](advance-sequence.md) | `fun advanceSequence(id: Id): `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`?` |
| [deductFromAccountBalance](deduct-from-account-balance.md) | `fun deductFromAccountBalance(accountId: Id, amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>>` |
| [deleteAllStorage](delete-all-storage.md) | `fun deleteAllStorage(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [getAccount](get-account.md) | `fun getAccount(accountId: Id): `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`?` |
| [getAllAccountIds](get-all-account-ids.md) | `fun getAllAccountIds(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Id>` |
| [getMinFee](get-min-fee.md) | `fun getMinFee(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>>` |
| [getStoredAccount](get-stored-account.md) | `fun getStoredAccount(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>>` |
| [getStoredTransactions](get-stored-transactions.md) | `fun getStoredTransactions(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`?>` |
| [getTransactions](get-transactions.md) | `fun getTransactions(key: Id): `[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`?` |
| [insertNewTransactionInStorage](insert-new-transaction-in-storage.md) | `fun insertNewTransactionInStorage(accountId: Id, newTransaction: `[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [putTransactions](put-transactions.md) | `fun putTransactions(key: Id, transactions: `[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [removeAccount](remove-account.md) | `fun removeAccount(accountId: Id): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeAllTransactions](remove-all-transactions.md) | `fun removeAllTransactions(key: Id): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [setMinFee](set-min-fee.md) | `fun setMinFee(minFee: `[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>>` |
| [storeTransactions](store-transactions.md) | `fun storeTransactions(accountId: Id, transactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [updateAccount](update-account.md) | `fun updateAccount(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [updateAccountInStorage](update-account-in-storage.md) | `fun updateAccountInStorage(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [upsertNewTransactionsInStorage](upsert-new-transactions-in-storage.md) | `fun upsertNewTransactionsInStorage(accountId: Id, newTransactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [upsertOldTransactionsInStorage](upsert-old-transactions-in-storage.md) | `fun upsertOldTransactionsInStorage(accountId: Id, oldTransactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [directoryNameForAllAccounts](directory-name-for-all-accounts.md) | `const val directoryNameForAllAccounts: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [fileNameForAccountInfo](file-name-for-account-info.md) | `const val fileNameForAccountInfo: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [fileNameForConfig](file-name-for-config.md) | `const val fileNameForConfig: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
