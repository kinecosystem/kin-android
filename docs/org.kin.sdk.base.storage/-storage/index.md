[kin-android](../../index.md) / [org.kin.sdk.base.storage](../index.md) / [Storage](./index.md)

# Storage

`interface Storage`

### Functions

| Name | Summary |
|---|---|
| [addAccount](add-account.md) | `abstract fun addAccount(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [advanceSequence](advance-sequence.md) | `abstract fun advanceSequence(id: Id): `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`?` |
| [deductFromAccountBalance](deduct-from-account-balance.md) | `abstract fun deductFromAccountBalance(accountId: Id, amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>>` |
| [deleteAllStorage](delete-all-storage.md) | `abstract fun deleteAllStorage(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [getAccount](get-account.md) | `abstract fun getAccount(accountId: Id): `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`?` |
| [getAllAccountIds](get-all-account-ids.md) | `abstract fun getAllAccountIds(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Id>` |
| [getMinFee](get-min-fee.md) | `abstract fun getMinFee(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>>` |
| [getStoredAccount](get-stored-account.md) | `abstract fun getStoredAccount(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>>` |
| [getStoredTransactions](get-stored-transactions.md) | `abstract fun getStoredTransactions(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`?>` |
| [getTransactions](get-transactions.md) | `abstract fun getTransactions(key: Id): `[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`?` |
| [insertNewTransactionInStorage](insert-new-transaction-in-storage.md) | `abstract fun insertNewTransactionInStorage(accountId: Id, newTransaction: `[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [putTransactions](put-transactions.md) | `abstract fun putTransactions(key: Id, transactions: `[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [removeAccount](remove-account.md) | `abstract fun removeAccount(accountId: Id): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeAllTransactions](remove-all-transactions.md) | `abstract fun removeAllTransactions(key: Id): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [setMinFee](set-min-fee.md) | `abstract fun setMinFee(it: `[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>>` |
| [storeTransactions](store-transactions.md) | `abstract fun storeTransactions(accountId: Id, transactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [updateAccount](update-account.md) | `abstract fun updateAccount(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [updateAccountInStorage](update-account-in-storage.md) | `abstract fun updateAccountInStorage(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [upsertNewTransactionsInStorage](upsert-new-transactions-in-storage.md) | `abstract fun upsertNewTransactionsInStorage(accountId: Id, newTransactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [upsertOldTransactionsInStorage](upsert-old-transactions-in-storage.md) | `abstract fun upsertOldTransactionsInStorage(accountId: Id, oldTransactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinFileStorage](../-kin-file-storage/index.md) | `class KinFileStorage : `[`Storage`](./index.md) |
