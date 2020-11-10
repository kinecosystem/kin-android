package org.kin.sdk.base.storage

import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.KinTransactions
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise

interface Storage {
    fun addAccount(account: KinAccount): Boolean
    fun updateAccount(account: KinAccount): Boolean
    fun removeAccount(accountId: KinAccount.Id): Boolean
    fun getAccount(accountId: KinAccount.Id): KinAccount?

    fun advanceSequence(id: KinAccount.Id): KinAccount?

    fun getAllAccountIds(): List<KinAccount.Id>

    fun putTransactions(key: KinAccount.Id, transactions: KinTransactions)
    fun removeAllTransactions(key: KinAccount.Id): Boolean

    fun removeAllInvoices(account: KinAccount.Id): Boolean

    fun removeServiceConfig(): Boolean

    fun getTransactions(key: KinAccount.Id): KinTransactions?

    fun getOrCreateCID(): String


    // TODO: Replace all sync functions with Promises

    fun getStoredTransactions(accountId: KinAccount.Id): Promise<KinTransactions?>

    fun storeTransactions(
        accountId: KinAccount.Id,
        transactions: List<KinTransaction>
    ): Promise<List<KinTransaction>>

    fun upsertNewTransactionsInStorage(
        accountId: KinAccount.Id,
        newTransactions: List<KinTransaction>
    ): Promise<List<KinTransaction>>

    fun upsertOldTransactionsInStorage(
        accountId: KinAccount.Id,
        oldTransactions: List<KinTransaction>
    ): Promise<List<KinTransaction>>

    fun insertNewTransactionInStorage(
        accountId: KinAccount.Id,
        newTransaction: KinTransaction
    ): Promise<List<KinTransaction>>

    fun addInvoiceLists(
        accountId: KinAccount.Id,
        invoiceLists: List<InvoiceList>
    ): Promise<List<InvoiceList>>

    fun getInvoiceListsMapForAccountId(
        account: KinAccount.Id
    ): Promise<Map<InvoiceList.Id, InvoiceList>>

    fun getStoredAccount(accountId: KinAccount.Id): Promise<Optional<KinAccount>>

    fun updateAccountInStorage(account: KinAccount): Promise<KinAccount>

    fun updateAccountBalance(
        accountId: KinAccount.Id,
        balance: KinBalance
    ): Promise<Optional<KinAccount>>

    fun setMinFee(minFee: QuarkAmount): Promise<Optional<QuarkAmount>>

    fun getMinFee(): Promise<Optional<QuarkAmount>>

    fun deleteAllStorage(accountId: KinAccount.Id): Promise<Boolean>

    fun deleteAllStorage(): Promise<Boolean>

    fun setMinApiVersion(apiVersion: Int): Promise<Int>

    fun getMinApiVersion(): Promise<Optional<Int>>
}
