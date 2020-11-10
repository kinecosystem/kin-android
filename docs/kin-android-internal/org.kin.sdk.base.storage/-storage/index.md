[kin-android](../../index.md) / [org.kin.sdk.base.storage](../index.md) / [Storage](./index.md)

# Storage

`interface Storage`

### Functions

| Name | Summary |
|---|---|
| [addAccount](add-account.md) | `abstract fun addAccount(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [addInvoiceLists](add-invoice-lists.md) | `abstract fun addInvoiceLists(accountId: Id, invoiceLists: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`InvoiceList`](../../org.kin.sdk.base.models/-invoice-list/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`InvoiceList`](../../org.kin.sdk.base.models/-invoice-list/index.md)`>>` |
| [advanceSequence](advance-sequence.md) | `abstract fun advanceSequence(id: Id): `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`?` |
| [deleteAllStorage](delete-all-storage.md) | `abstract fun deleteAllStorage(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>`<br>`abstract fun deleteAllStorage(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [getAccount](get-account.md) | `abstract fun getAccount(accountId: Id): `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`?` |
| [getAllAccountIds](get-all-account-ids.md) | `abstract fun getAllAccountIds(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Id>` |
| [getInvoiceListsMapForAccountId](get-invoice-lists-map-for-account-id.md) | `abstract fun getInvoiceListsMapForAccountId(account: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<Id, `[`InvoiceList`](../../org.kin.sdk.base.models/-invoice-list/index.md)`>>` |
| [getMinApiVersion](get-min-api-version.md) | `abstract fun getMinApiVersion(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>>` |
| [getMinFee](get-min-fee.md) | `abstract fun getMinFee(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>>` |
| [getOrCreateCID](get-or-create-c-i-d.md) | `abstract fun getOrCreateCID(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [getStoredAccount](get-stored-account.md) | `abstract fun getStoredAccount(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>>` |
| [getStoredTransactions](get-stored-transactions.md) | `abstract fun getStoredTransactions(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`?>` |
| [getTransactions](get-transactions.md) | `abstract fun getTransactions(key: Id): `[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`?` |
| [insertNewTransactionInStorage](insert-new-transaction-in-storage.md) | `abstract fun insertNewTransactionInStorage(accountId: Id, newTransaction: `[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [putTransactions](put-transactions.md) | `abstract fun putTransactions(key: Id, transactions: `[`KinTransactions`](../../org.kin.sdk.base.stellar.models/-kin-transactions/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [removeAccount](remove-account.md) | `abstract fun removeAccount(accountId: Id): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeAllInvoices](remove-all-invoices.md) | `abstract fun removeAllInvoices(account: Id): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeAllTransactions](remove-all-transactions.md) | `abstract fun removeAllTransactions(key: Id): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeServiceConfig](remove-service-config.md) | `abstract fun removeServiceConfig(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [setMinApiVersion](set-min-api-version.md) | `abstract fun setMinApiVersion(apiVersion: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`>` |
| [setMinFee](set-min-fee.md) | `abstract fun setMinFee(minFee: `[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>>` |
| [storeTransactions](store-transactions.md) | `abstract fun storeTransactions(accountId: Id, transactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [updateAccount](update-account.md) | `abstract fun updateAccount(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [updateAccountBalance](update-account-balance.md) | `abstract fun updateAccountBalance(accountId: Id, balance: `[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>>` |
| [updateAccountInStorage](update-account-in-storage.md) | `abstract fun updateAccountInStorage(account: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [upsertNewTransactionsInStorage](upsert-new-transactions-in-storage.md) | `abstract fun upsertNewTransactionsInStorage(accountId: Id, newTransactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [upsertOldTransactionsInStorage](upsert-old-transactions-in-storage.md) | `abstract fun upsertOldTransactionsInStorage(accountId: Id, oldTransactions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinFileStorage](../-kin-file-storage/index.md) | `class KinFileStorage : `[`Storage`](./index.md) |
