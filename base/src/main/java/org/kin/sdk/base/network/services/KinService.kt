package org.kin.sdk.base.network.services

import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.KinTokenAccountInfo
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.solana.Instruction
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.network.api.KinTransactionApiV4.SubmitTransactionResponse
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Promise
import java.io.IOException

interface KinService {

    /**
     * Creates a [KinAccount] and activates it on the network.
     * @param signer only ever used to sign a request, never transmitted
     */
    fun createAccount(accountId: KinAccount.Id, signer: Key.PrivateKey, appIndex: AppIdx): Promise<KinAccount>

    fun getAccount(accountId: KinAccount.Id): Promise<KinAccount>

    fun resolveTokenAccounts(accountId: KinAccount.Id): Promise<List<KinTokenAccountInfo>>

    fun mergeTokenAccounts(
        accountId: KinAccount.Id,
        signer: Key.PrivateKey,
        appIndex: AppIdx,
        shouldCreateAssociatedAccount: Boolean = true
    ): Promise<List<KinTokenAccountInfo>>

    fun getLatestTransactions(kinAccountId: KinAccount.Id): Promise<List<KinTransaction>>

    fun getTransactionPage(
        kinAccountId: KinAccount.Id,
        pagingToken: KinTransaction.PagingToken,
        order: Order = Order.Descending
    ): Promise<List<KinTransaction>>

    fun getTransaction(transactionHash: TransactionHash): Promise<KinTransaction>

    fun buildAndSignTransaction(
        ownerKey: Key.PrivateKey,
        sourceKey: Key.PublicKey,
        paymentItems: List<KinPaymentItem>,
        memo: KinMemo,
        createAccountInstructions: List<Instruction> = emptyList(),
        signers: List<Key.PrivateKey> = emptyList()
    ): Promise<KinTransaction>

    fun submitTransaction(transaction: KinTransaction): Promise<KinTransaction>

    fun buildSignAndSubmitTransaction(
        buildAndSignTransaction: () -> Promise<KinTransaction>
    ): Promise<KinTransaction>

    fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount>

    fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction>

    fun invalidateBlockhashCache()

    sealed class Order(val value: Int) {
        object Ascending : Order(0)
        object Descending : Order(1)
    }

    sealed class FatalError(reason: Throwable) : RuntimeException(reason) {

        open class TransientFailure(reason: Throwable? = null) :
            FatalError(IOException("The request was retried until limit was exceeded", reason))

        open class UnexpectedServiceError(reason: Throwable) :
            FatalError(reason) //(IOException("There was an unexpected service error", reason))

        open class Denied(reason: Throwable? = null) :
            FatalError(reason ?: Exception("This action was not allowed"))

        open class IllegalRequest(reason: Throwable? = null) :
            FatalError(IllegalStateException("Malformed request", reason))

        object IllegalResponse :
            FatalError(IllegalStateException("Malformed response from server"))

        object ItemNotFound :
            FatalError(NoSuchElementException("The requested item was not found"))

        object PermanentlyUnavailable :
            FatalError(Exception("This operation is not supported under this configuration"))

        object UnknownAccountInRequest :
            FatalError(IllegalStateException("Unknown Account"))

        object BadBlockhashInRequest :
            FatalError(IllegalArgumentException("Bad Blockhash in Transaction"))

        object InsufficientFeeInRequest :
            FatalError(IllegalArgumentException("Insufficient Fee"))

        object InsufficientBalanceForSourceAccountInRequest :
            FatalError(IllegalStateException("Insufficient Balance"))

        object WebhookRejectedTransaction :
            FatalError(Exception("This transaction was rejected by the configured webhook without a reason"))

        data class InvoiceErrorsInRequest(val invoiceErrors: List<KinTransactionApiV4.InvoiceError>) :
            FatalError(IllegalArgumentException("Invoice Errors"))

        /**
         * It is expected that this error is handled gracefully by notifying users
         * to upgrade to a newer version of the software that should contain a more
         * recent version of this SDK.
         */
        object SDKUpgradeRequired :
            FatalError(Exception("Please upgrade to a newer version of the SDK"))
    }

    /**
     * WARNING: This *ONLY* works in test environments.
     */
    val testService: KinTestService
    fun createTokenAccountForDestinationOwner(owner: Key.PublicKey): Promise<Pair<List<Instruction>, Key.PrivateKey>>
}

/**
 * WARNING: This *ONLY* works in test environments.
 */
interface KinTestService {
    /**
     * Funds an account with a set amount of test Kin.
     */
    fun fundAccount(accountId: KinAccount.Id): Promise<KinAccount>
}

