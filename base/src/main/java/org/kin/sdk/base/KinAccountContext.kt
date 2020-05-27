package org.kin.sdk.base

import org.kin.sdk.base.KinAccountContextImpl.ExistingAccountBuilder
import org.kin.sdk.base.KinAccountContextImpl.NewAccountBuilder
import org.kin.sdk.base.KinAccountContextReadOnlyImpl.ReadOnlyAccountBuilder
import org.kin.sdk.base.ObservationMode.Passive
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.merge
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.Callback
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.ListObserver
import org.kin.sdk.base.tools.ListSubject
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.PromiseQueue
import org.kin.sdk.base.tools.ValueListener
import org.kin.sdk.base.tools.ValueSubject
import org.kin.sdk.base.tools.callback
import org.kin.sdk.base.tools.listen
import org.kin.stellarfork.KeyPair
import java.util.concurrent.CountDownLatch

/**
 * Describes the mode by which updates are
 * presented to an [Observer]
 */
sealed class ObservationMode(val value: Int) {
    /**
     * Updates are only based on local actions
     * or via calling [Observer.requestInvalidation]
     *
     * A current value will always be emitted
     * (which may fault to network) followed
     * by only values as a result of local actions.
     */
    object Passive : ObservationMode(0)

    /**
     * Updates are pushed from the network.
     * Includes all [Passive] updates.
     *
     * Note: Active updates require a persistent network
     * connection to stream data from a remote service.
     */
    object Active : ObservationMode(1)

    /**
     * Exclusively new updates from actions taken after
     * starting to listen to this [Observer].
     *
     * No current value will be emitted.
     */
    object ActiveNewOnly : ObservationMode(2)
}

interface KinAccountReadOperationsAltIdioms {
    /**
     * @see KinAccountReadOperations.getAccount
     */
    fun getAccount(accountCallback: Callback<KinAccount>)

    /**
     * @see KinAccountReadOperations.observeBalance
     */
    fun observeBalance(
        mode: ObservationMode = Passive,
        balanceListener: ValueListener<KinBalance>
    ): Observer<KinBalance>

    /**
     * @see KinAccountReadOperations.clearStorage
     */
    fun clearStorage(clearCompleteCallback: Callback<Boolean>)
}

interface KinAccountReadOperations : KinAccountReadOperationsAltIdioms {
    /**
     * Returns the account info
     * @return a [Promise] containing the [KinAccount] or an error
     */
    fun getAccount(): Promise<KinAccount>

    /**
     * Returns the current [Balance]
     * and listens to future account balance changes.
     *
     * Note: Running with [ObservationMode.Passive] is suggested unless
     * higher data freshness is required.
     *
     * [ObservationMode.Passive] - will return the current balance and any balance updates
     * as a result of actions performed locally.
     * [ObservationMode.Active] - will return the current balance and any balance updates
     * [ObservationMode.ActiveNewOnly] - will *not* return the current balance, but only new
     * updates from now onwards.
     *
     * @param mode will change the frequency of updates according to
     * the rules set in [ObservationMode]
     */
    fun observeBalance(mode: ObservationMode = Passive): Observer<KinBalance>

    /**
     * Deletes the storage associated with the [accountId]
     * @return a [Promise] with true if successful, false otherwise
     */
    fun clearStorage(): Promise<Boolean>
}

interface KinPaymentReadOperationsAltIdioms {
    /**
     * @see KinPaymentReadOperations.observePayments
     */
    fun observePayments(
        mode: ObservationMode = Passive,
        paymentsListener: ValueListener<List<KinPayment>>
    ): ListObserver<KinPayment>

    /**
     * @see KinPaymentReadOperations.getPaymentsForTransactionHash
     */
    fun getPaymentsForTransactionHash(
        transactionHash: TransactionHash,
        paymentsCallback: Callback<List<KinPayment>>
    )
}

interface KinPaymentReadOperations : KinPaymentReadOperationsAltIdioms {
    /**
     * Retrieves the last N [KinPayment]s sent or received by the
     * account and listens for future payments over time.
     *
     * Note: Running with [ObservationMode.Passive] is suggested unless
     * higher data freshness is required.
     *
     * [ObservationMode.Passive] - will return the full recorded history and new [KinTransaction]s
     * as a result of actions performed locally.
     * [ObservationMode.Active] - will return the full recorded history and all
     * new [KinTransaction]s.
     * [ObservationMode.ActiveNewOnly] - will *not* return the recorded history, but only new
     * updates from now onwards.
     *
     * @param mode will change the frequency of updates according to
     * the rules set in [ObservationMode].
     * @return a [ListObserver] to listen to the payment history.
     */
    fun observePayments(mode: ObservationMode = Passive): ListObserver<KinPayment>

    /**
     * Retrieves the [KinPayment]s that were processed in the referred [KinTransaction]
     *
     * @param transactionHash is the referencing hash for a [KinTransaction]
     * that contain a list of the given [KinPayment]s.
     * @return a [Promise] containing the list of [KinPayment]s in the referencing [KinTransaction]
     * or an error
     */
    fun getPaymentsForTransactionHash(transactionHash: TransactionHash): Promise<List<KinPayment>>
}

interface KinPaymentWriteOperationsAltIdioms {
    /**
     * @see KinPaymentWriteOperations.sendKinPayment
     */
    fun sendKinPayment(
        amount: KinAmount,
        destinationAccount: KinAccount.Id,
        memo: KinMemo = KinMemo.NONE,
        paymentCallback: Callback<KinPayment>
    )

    /**
     * @see KinPaymentWriteOperations.sendKinPayments
     */
    fun sendKinPayments(
        payments: List<KinPaymentItem>,
        memo: KinMemo = KinMemo.NONE,
        paymentsCallback: Callback<List<KinPayment>>
    )
}

interface KinPaymentWriteOperations : KinPaymentWriteOperationsAltIdioms {
    /**
     * Send an amount of Kin to a [destinationAccount] to the Kin Blockchain for processing.
     *
     * @param the amount of Kin to be sent
     * @param destinationAccount the account the Kin is to be transferred to
     * @param memo (optional) a memo can be provided to reference what the payment was for.
     * If no memo is desired, then set it to [KinMemo.NONE]
     * @return a [Promise] with the blockchain confirmed [KinPayment]
     */
    fun sendKinPayment(
        amount: KinAmount,
        destinationAccount: KinAccount.Id,
        memo: KinMemo = KinMemo.NONE
    ): Promise<KinPayment>

    /**
     * @see sendKinPayment
     *
     * Sends a batch of payments, each corresponding to [KinPaymentItem] in a
     * single [KinTransaction] to be processed together.
     *
     * Note: If any one payment's data is invalid, all payments will fail.
     *
     * @param memo (optional) a memo can be provided to reference what thes batch of payments
     * were for. If no memo is desired, then set it to [KinMemo.NONE]
     * @return a [Promise] with the blockchain confirmed [KinPayment]s or an error
     */
    fun sendKinPayments(
        payments: List<KinPaymentItem>,
        memo: KinMemo = KinMemo.NONE
    ): Promise<List<KinPayment>>
}

interface KinAccountContextReadOnly : KinAccountReadOperations,
    KinPaymentReadOperations {
    val accountId: KinAccount.Id
}

interface KinAccountContext : KinAccountContextReadOnly, KinPaymentWriteOperations {
    class Builder(private val env: KinEnvironment) {

        constructor(envBuilder: KinEnvironment.Horizon.Builder.CompletedBuilder) : this(envBuilder.build())

        fun createNewAccount() = NewAccountBuilder(env)

        fun importExistingPrivateKey(privateKey: Key.PrivateKey): ExistingAccountBuilder {
            CountDownLatch(1)
                .apply {
                    env.importPrivateKey(privateKey).then { countDown() }
                    await()
                }
            return useExistingAccount(privateKey.asKinAccountId())
        }

        fun useExistingAccount(
            accountId: KinAccount.Id
        ) = ExistingAccountBuilder(env, accountId)

        fun useExistingAccountReadOnly(
            accountId: KinAccount.Id
        ) = ReadOnlyAccountBuilder(env, accountId)
    }
}

/**
 * Instantiate a [KinAccountContextReadOnlyImpl] to operate on a [KinAccount] when you only have a [PublicKey]
 * Can be used to:
 *  - get account data, payment history, and listen to changes over time
 *
 * @property executors defines a set of executors to be used
 * @property service a service used to retrieve all account and payment data
 * @property storage stores all account and payment data. @see [KinFileStorage] for provided implementation.
 * @property accountId denoting the [KinAccount] to get information from
 */
class KinAccountContextReadOnlyImpl private constructor(
    override val executors: ExecutorServices,
    override val service: KinService,
    override val storage: Storage,
    override val accountId: KinAccount.Id
) : KinAccountContextBase(), KinAccountContextReadOnly {

    /**
     * Gives you read-only access to this [KinAccount]
     * @param env full definition of the environment
     * @param accountId denoting the [KinAccount] to get information from
     */
    class ReadOnlyAccountBuilder internal constructor(
        private val env: KinEnvironment,
        private val accountId: KinAccount.Id
    ) {
        fun build(): KinAccountContextReadOnly =
            KinAccountContextReadOnlyImpl(env.executors, env.service, env.storage, accountId)
    }

    override fun maybeFetchAccountDetails(): Promise<KinAccount> = service.getAccount(accountId)

    override fun getAccount(): Promise<KinAccount> {
        return storage.getStoredAccount(accountId)
            .flatMap {
                it.map { storedAccount ->
                    Promise.of(storedAccount)
                }.orElse {
                    maybeFetchAccountDetails()
                }
            }
    }

    // Idiomatic Variants of Primary Functions

    override fun clearStorage(clearCompleteCallback: Callback<Boolean>) =
        clearStorage().callback(clearCompleteCallback)

    override fun getPaymentsForTransactionHash(
        transactionHash: TransactionHash,
        paymentsCallback: Callback<List<KinPayment>>
    ) = getPaymentsForTransactionHash(transactionHash).callback(paymentsCallback)
}

/**
 * Instantiate a [KinAccountContextImpl] to operate on a [KinAccount] when you have a [PrivateKey]
 * Can be used to:
 *  - create an account
 *  - get account data, payment history, and listen to changes over time
 *  - send payments
 *
 * @property executors defines a set of executors to be used
 * @property service a service used to retrieve all account and payment data
 * @property storage stores all account and payment data. @see [KinFileStorage] for provided implementation.
 * @property accountId denoting the [KinAccount] to get information from
 */
class KinAccountContextImpl private constructor(
    override val executors: ExecutorServices,
    override val service: KinService,
    override val storage: Storage,
    override val accountId: KinAccount.Id
) : KinAccountContextBase(), KinAccountContext {

    /**
     * Creates a new [KinAccount]
     * @param env full definition of the environment
     */
    class NewAccountBuilder internal constructor(
        private val env: KinEnvironment
    ) {
        fun build(): KinAccountContextImpl = KinAccountContextImpl(
            env.executors,
            env.service,
            env.storage,
            setupNewAccount(env.storage).id
        )

        private fun setupNewAccount(storage: Storage): KinAccount {
            val newAccount = KinAccount(KeyPair.random().asPrivateKey())
            storage.addAccount(newAccount)
            return newAccount
        }
    }

    /**
     * Let's you access the specified [KinAccount]
     * @param env full definition of the environment
     * @param accountId denoting the [KinAccount] to get information from
     */
    class ExistingAccountBuilder internal constructor(
        private val env: KinEnvironment,
        private val accountId: KinAccount.Id
    ) {
        fun build(): KinAccountContext =
            KinAccountContextImpl(env.executors, env.service, env.storage, accountId)
    }

    private val outgoingTransactions = PromiseQueue<List<KinPayment>>()

    override fun getAccount(): Promise<KinAccount> =
        storage.getStoredAccount(accountId)
            .flatMap {
                it.map { storedAccount ->
                    when (storedAccount.status) {
                        is KinAccount.Status.Unregistered -> registerAccount(storedAccount)
                        is KinAccount.Status.Registered -> Promise.of(storedAccount)
                    }
                }.orElse {
                    maybeFetchAccountDetails()
                }
            }

    private fun registerAccount(account: KinAccount): Promise<KinAccount> =
        service.createAccount(account.id)
            .map {
                val accountToStore = account.merge(it)
                if (!storage.updateAccount(accountToStore)) {
                    throw RuntimeException("Failed to store Account Data!")
                } else accountToStore
            }

    override fun maybeFetchAccountDetails(): Promise<KinAccount> =
        Promise.error(IllegalStateException("Private key missing for account with id: $accountId"))

    override fun sendKinPayment(
        amount: KinAmount,
        destinationAccount: KinAccount.Id,
        memo: KinMemo
    ): Promise<KinPayment> =
        sendKinPayments(listOf(KinPaymentItem(amount, destinationAccount)), memo)
            .map { it.first() }

    override fun sendKinPayments(
        payments: List<KinPaymentItem>,
        memo: KinMemo
    ): Promise<List<KinPayment>> {
        return getAccount()
            .flatMap {
                getFee().flatMap { fee ->
                    service.buildAndSignTransaction(it, payments, memo, fee)
                }
            }
            .flatMap { transaction ->
                outgoingTransactions.queue(Promise.create { resolve, reject ->
                    fun attempt() =
                        service.submitTransaction(transaction)
                            .doOnResolved { storage.advanceSequence(accountId) }
                            .flatMap { storage.insertNewTransactionInStorage(accountId, it) }
                            .map { it.asKinPayments() }
                            .doOnResolved { deductFromAccountBalance(it, transaction.fee) }
                    attempt()
                        .then(
                            resolve,
                            { error ->
                                when (error) {
                                    is KinService.BadSequenceNumberInRequest -> {
                                        service.getAccount(accountId)
                                            .flatMap { storage.updateAccountInStorage(it) }
                                            .then({
                                                attempt()
                                                    .then(resolve, { reject.invoke(it) })
                                            }, { reject.invoke(it) })
                                    }
                                    is KinService.InsufficientFeeInRequest -> {
                                        service.getMinFee()
                                            .flatMap { storage.setMinFee(it) }
                                            .then({
                                                attempt()
                                                    .then(resolve, { reject.invoke(it) })
                                            }, { reject.invoke(it) })
                                    }
                                    else -> {
                                        reject.invoke(error)
                                    }
                                }
                            }
                        )
                })
            }
            .workOn(executors.parallelIO)
    }

    // Idiomatic Variants of Primary Functions

    override fun clearStorage(clearCompleteCallback: Callback<Boolean>) =
        clearStorage().callback(clearCompleteCallback)

    override fun getPaymentsForTransactionHash(
        transactionHash: TransactionHash,
        paymentsCallback: Callback<List<KinPayment>>
    ) = getPaymentsForTransactionHash(transactionHash).callback(paymentsCallback)

    override fun sendKinPayment(
        amount: KinAmount,
        destinationAccount: KinAccount.Id,
        memo: KinMemo,
        paymentCallback: Callback<KinPayment>
    ) = sendKinPayment(amount, destinationAccount, memo).callback(paymentCallback)

    override fun sendKinPayments(
        payments: List<KinPaymentItem>,
        memo: KinMemo,
        paymentsCallback: Callback<List<KinPayment>>
    ) = sendKinPayments(payments, memo).callback(paymentsCallback)
}

abstract class KinAccountContextBase : KinAccountReadOperations, KinPaymentReadOperations {
    abstract val executors: ExecutorServices
    abstract val service: KinService
    abstract val storage: Storage
    abstract val accountId: KinAccount.Id

    private val balanceSubject: ValueSubject<KinBalance> by lazy {
        ValueSubject<KinBalance> {
            executors.parallelIO.submit {
                getAccount()
                    .map { it.balance }
                    .doOnResolved(balanceSubject::onNext)
                    .flatMap { fetchUpdatedBalance() }
                    .resolve()
            }
        }
    }
    private val paymentsSubject: ListSubject<KinPayment> by lazy {
        ListSubject<KinPayment>(
            { requestNextPage().map { it.asKinPayments(true) }.then(paymentsSubject::onNext) },
            { requestPreviousPage().map { it.asKinPayments(true) }.then(paymentsSubject::onNext) },
            {
                executors.parallelIO.submit {
                    storage.getStoredTransactions(accountId)
                        .map { it?.items ?: emptyList() }
                        .map { it.asKinPayments(true) }
                        .doOnResolved(paymentsSubject::onNext)
                        .flatMap { fetchUpdatedTransactionHistory() }
                        .resolve()
                }
            }
        )
    }

    protected abstract fun maybeFetchAccountDetails(): Promise<KinAccount>

    private val lifecycle = DisposeBag()
    private var accountStream: Observer<KinAccount>? = null
    private val streamLock = Any()

    private fun <T> Observer<T>.setupActiveStreamingUpdatesIfNecessary(mode: ObservationMode): Observer<T> {
        when (mode) {
            ObservationMode.ActiveNewOnly,
            ObservationMode.Active -> {
                synchronized(streamLock) {
                    if (accountStream == null) {
                        accountStream = service.streamAccount(accountId).apply {
                            disposedBy(lifecycle)
                                .flatMapPromise { kinAccount ->
                                    storage.updateAccountInStorage(kinAccount)
                                        .map { it.balance }
                                        .doOnResolved {
                                            balanceSubject.onNext(it)
                                            fetchUpdatedTransactionHistory().resolve()
                                        }
                                }.resolve()
                        }

                        doOnDisposed { lifecycle.dispose() }
                    }
                }
            }
        }
        return this
    }

    // Public

    override fun getAccount(accountCallback: Callback<KinAccount>) =
        getAccount().callback(accountCallback)

    override fun observePayments(mode: ObservationMode): ListObserver<KinPayment> {
        return when (mode) {
            Passive -> with(paymentsSubject) { requestInvalidation() } as ListObserver<KinPayment>
            ObservationMode.Active -> with(paymentsSubject) { requestInvalidation() }.setupActiveStreamingUpdatesIfNecessary(
                mode
            ) as ListObserver<KinPayment>
            ObservationMode.ActiveNewOnly -> {
                ListSubject<KinPayment>()
                    .apply {
                        val lifecycle = DisposeBag()
                        service.streamNewTransactions(accountId)
                            .disposedBy(lifecycle)
                            .mapPromise { it.asKinPayments() }
                            .then {
                                onNext(it)
                            }
                        doOnDisposed { lifecycle.dispose() }
                    }
            }
        }
    }

    override fun getPaymentsForTransactionHash(transactionHash: TransactionHash): Promise<List<KinPayment>> =
        service.getTransaction(transactionHash)
            .map { it.asKinPayments() }

    override fun observeBalance(mode: ObservationMode): Observer<KinBalance> =
        with(balanceSubject) {
            setupActiveStreamingUpdatesIfNecessary(mode)
            requestInvalidation()
        }

    override fun clearStorage(): Promise<Boolean> = storage.deleteAllStorage(accountId)


    // Internal

    private fun requestNextPage(): Promise<List<KinTransaction>> {
        return storage.getStoredTransactions(accountId)
            .flatMap {
                if (it?.headPagingToken != null) {
                    service.getTransactionPage(
                        accountId,
                        it.headPagingToken,
                        KinService.Order.Ascending
                    )
                } else {
                    service.getLatestTransactions(accountId)
                }
            }
            .flatMap { storage.upsertNewTransactionsInStorage(accountId, it.reversed()) }
    }

    private fun requestPreviousPage(): Promise<List<KinTransaction>> {
        return storage.getStoredTransactions(accountId)
            .flatMap {
                if (it?.tailPagingToken != null) {
                    service.getTransactionPage(
                        accountId,
                        it.tailPagingToken,
                        KinService.Order.Descending
                    )
                } else {
                    service.getLatestTransactions(accountId)
                }
            }
            .flatMap { storage.upsertOldTransactionsInStorage(accountId, it) }
    }

    protected fun getFee() = service.canWhitelistTransactions()
        .flatMap {
            if (it) Promise.of(KinAmount.ZERO.toQuarks())
            else {
                storage.getMinFee()
                    .flatMap {
                        it.map { Promise.of(it) }
                            .orElse {
                                service.getMinFee()
                                    .doOnResolved { storage.setMinFee(it).resolve() }
                            }
                    }
            }
        }

    protected fun fetchUpdatedTransactionHistory(): Promise<List<KinTransaction>> =
        requestNextPage().map { it }
            .doOnResolved { paymentsSubject.onNext(it.asKinPayments(true)) }

    protected fun fetchUpdatedBalance(): Promise<KinBalance> {
        return service.getAccount(accountId)
            .flatMap { storage.updateAccountInStorage(it) }
            .map { it.balance }
            .doOnResolved { balanceSubject.onNext(it) }
    }

    protected fun deductFromAccountBalance(
        payments: List<KinPayment>,
        transactionFee: QuarkAmount
    ) {
        storage
            .deductFromAccountBalance(
                accountId,
                with(payments) {
                    var totalAmount = transactionFee.toKin()
                    payments.forEach { payment ->
                        totalAmount += (if (!payment.destinationAccountId.equals(payment.sourceAccountId)) payment.amount else KinAmount.ZERO)
                    }
                    totalAmount
                }
            )
            .doOnResolved { it.map { balanceSubject.onNext(it.balance) } }
            .doOnResolved {
                storage.getStoredTransactions(accountId)
                    .map { it?.items ?: emptyList() }
                    .then { paymentsSubject.onNext(it.asKinPayments(true)) }
            }
            .resolve()
    }


    // Idiomatic Variants of Primary Functions

    override fun observeBalance(
        mode: ObservationMode,
        balanceListener: ValueListener<KinBalance>
    ): Observer<KinBalance> = observeBalance(mode).listen(balanceListener)

    override fun observePayments(
        mode: ObservationMode,
        paymentsListener: ValueListener<List<KinPayment>>
    ): ListObserver<KinPayment> = observePayments(mode).listen(paymentsListener)
}

