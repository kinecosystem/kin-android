package org.kin.sdk.base

import org.kin.sdk.base.KinAccountContextImpl.ExistingAccountBuilder
import org.kin.sdk.base.KinAccountContextImpl.NewAccountBuilder
import org.kin.sdk.base.KinAccountContextReadOnlyImpl.ReadOnlyAccountBuilder
import org.kin.sdk.base.ObservationMode.Passive
import org.kin.sdk.base.models.AccountSpec
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinBinaryMemo
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.getNetwork
import org.kin.sdk.base.models.merge
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.network.api.agora.sha224Hash
import org.kin.sdk.base.network.api.agora.toProto
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.network.services.KinServiceWrapper
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.BackoffStrategy
import org.kin.sdk.base.tools.Callback
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.base.tools.ExecutorServices
import org.kin.sdk.base.tools.KinLogger
import org.kin.sdk.base.tools.KinLoggerFactory
import org.kin.sdk.base.tools.ListObserver
import org.kin.sdk.base.tools.ListSubject
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.PromiseQueue
import org.kin.sdk.base.tools.ValueListener
import org.kin.sdk.base.tools.ValueSubject
import org.kin.sdk.base.tools.callback
import org.kin.sdk.base.tools.listen
import org.kin.sdk.base.tools.onErrorResumeNext
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.DecoratedSignature
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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
    fun getAccount(forceUpdate: Boolean = false, accountCallback: Callback<KinAccount>)

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
     * @param forceUpdate - forces an update from the network
     */
    fun getAccount(forceUpdate: Boolean = false): Promise<KinAccount>

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

    fun calculateFee(numberOfOperations: Int): Promise<QuarkAmount>

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
        invoice: Optional<Invoice> = Optional.empty(),
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

    val appInfoProvider: AppInfoProvider?

    fun payInvoice(
        invoice: Invoice,
        destinationAccount: KinAccount.Id,
        processingAppIdx: AppIdx = appInfoProvider?.appInfo?.appIndex
            ?: throw RuntimeException("Need to specify an AppIdx"),
        type: KinBinaryMemo.TransferType = KinBinaryMemo.TransferType.Spend
    ): Promise<KinPayment>

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
        memo: KinMemo = KinMemo.NONE,
        invoice: Optional<Invoice> = Optional.empty()
    ): Promise<KinPayment>

    /**
     * @see sendKinPayment
     *
     * Sends a batch of payments, each corresponding to [KinPaymentItem] in a
     * single [KinTransaction] to be processed together.
     *
     * Note: If any one payment's data is invalid, all payments will fail.
     *
     * @param payments - a representation of the payments to send as a batch
     * @param memo (optional) a memo can be provided to reference what thes batch of payments
     * @param sourceAccountSpec - a spec of how to interpret the source of funds
     * @param destinationAccountSpec - a spec of how to interpret the source of funds
     * were for. If no memo is desired, then set it to [KinMemo.NONE]
     * @return a [Promise] with the blockchain confirmed [KinPayment]s or an error
     */
    fun sendKinPayments(
        payments: List<KinPaymentItem>,
        memo: KinMemo = KinMemo.NONE,
        sourceAccountSpec: AccountSpec = AccountSpec.Preferred,
        destinationAccountSpec: AccountSpec = AccountSpec.Preferred,
    ): Promise<List<KinPayment>>

    /**
     * This is a total hack, won't exist forever, don't use this, to support base-compat ONLY
     *
     * This is not meant for other external consumption. Use at your own risk.
     */
    @Deprecated("Don't use this version of sendKinPayments")
    fun sendKinPayments(
        payments: List<KinPaymentItem>,
        memo: KinMemo,
        sourceAccountSpec: AccountSpec,
        destinationAccountSpec: AccountSpec,
        additionalSignatures: List<DecoratedSignature>,
        feeOverride: QuarkAmount? = null
    ) : Promise<List<KinPayment>>

    /**
     * Directly sends a [KinTransaction].
     * Currently only exposed to support the kin-android:base-compat library
     *
     * This is not meant for other external consumption. Use at your own risk.
     *
     * Payments should instead be sent with [sendKinPayment] or [sendKinPayments] functions.
     */
    fun sendKinTransaction(
        buildTransaction: () -> Promise<KinTransaction>
    ): Promise<List<KinPayment>>
}

interface KinAccountContextReadOnly : KinAccountReadOperations,
    KinPaymentReadOperations {
    val accountId: KinAccount.Id
}

interface KinAccountContext : KinAccountContextReadOnly, KinPaymentWriteOperations {
    class Builder(private val env: KinEnvironment) {

        constructor(envBuilder: KinEnvironment.Horizon.Builder.CompletedBuilder) : this(envBuilder.build())

        constructor(envBuilder: KinEnvironment.Agora.Builder.CompletedBuilder) : this(envBuilder.build())

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
    override val accountId: KinAccount.Id,
    override val logger: KinLoggerFactory
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
            KinAccountContextReadOnlyImpl(
                env.executors,
                env.service,
                env.storage,
                accountId,
                env.logger
            )
    }

    override fun getAccount(forceUpdate: Boolean): Promise<KinAccount> {
        log.log("getAccount")
        return storage.getStoredAccount(accountId)
            .flatMap {
                it.map { storedAccount ->
                    if (!forceUpdate) Promise.of(storedAccount)
                    else maybeFetchAccountDetails()
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
    override val accountId: KinAccount.Id,
    override val appInfoProvider: AppInfoProvider?,
    override val logger: KinLoggerFactory
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
            setupNewAccount(env.storage).id,
            (env as? KinEnvironment.Agora)?.appInfoProvider,
            env.logger
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
            KinAccountContextImpl(
                env.executors,
                env.service,
                env.storage,
                accountId,
                (env as? KinEnvironment.Agora)?.appInfoProvider,
                env.logger
            )
    }

    private val outgoingTransactions = PromiseQueue<List<KinPayment>>()

    override fun getAccount(forceUpdate: Boolean): Promise<KinAccount> {
        log.log("getAccount")
        return storage.getStoredAccount(accountId)
            .flatMap {
                it.map { storedAccount ->
                    when (storedAccount.status) {
                        is KinAccount.Status.Unregistered -> {
                            Promise.create { resolve, reject ->
                                registerAccount(storedAccount)
                                    .then(
                                        resolve,
                                        { maybeFetchAccountDetails().then(resolve, reject) }
                                    )
                            }
                        }
                        is KinAccount.Status.Registered -> {
                            if (!forceUpdate) Promise.of(storedAccount)
                            else maybeFetchAccountDetails()
                        }
                    }
                }.orElse {
                    Promise.error(IllegalStateException("Private key missing for account with id: $accountId"))
                }
            }
    }

    private fun registerAccount(account: KinAccount): Promise<KinAccount> =
        service.createAccount(account.id, account.key as Key.PrivateKey)
            .map {
                val accountToStore = account.merge(it)
                if (!storage.updateAccount(accountToStore)) {
                    throw RuntimeException("Failed to store Account Data!")
                } else accountToStore
            }

    override fun payInvoice(
        invoice: Invoice,
        destinationAccount: KinAccount.Id,
        processingAppIdx: AppIdx,
        type: KinBinaryMemo.TransferType
    ): Promise<KinPayment> {
        log.log(::payInvoice.name)
        return sendKinPayment(
            invoice.total,
            destinationAccount,
            KinBinaryMemo.Builder(processingAppIdx.value)
                .setForeignKey(listOf(invoice).toProto().sha224Hash().decode())
                .setTranferType(type)
                .build()
                .toKinMemo(),
            Optional.of(invoice)
        )
    }

    override fun sendKinPayment(
        amount: KinAmount,
        destinationAccount: KinAccount.Id,
        memo: KinMemo,
        invoice: Optional<Invoice>
    ): Promise<KinPayment> {
        log.log("sendKinPayment")
        return sendKinPayments(listOf(KinPaymentItem(amount, destinationAccount, invoice)), memo)
            .map { it.first() }
    }

    override fun sendKinTransaction(
        buildTransaction: () -> Promise<KinTransaction>
    ): Promise<List<KinPayment>> {
        var buildConsumed = false
        log.log(::sendKinTransaction.name)
        return outgoingTransactions.queue(
            buildTransaction()
                .flatMap { transaction ->
                    computeExpectedNewBalance(transaction)
                        .flatMap { expectedNewBalance ->
                            service
                                .buildSignAndSubmitTransaction {
                                    if (!buildConsumed) {
                                        buildConsumed = true
                                        Promise.of(transaction)
                                    } else {
                                        buildTransaction()
                                    }
                                }
                                .doOnResolved { storage.advanceSequence(accountId) }
                                .flatMap { submittedTransaction ->
                                    storage
                                        .insertNewTransactionInStorage(
                                            accountId,
                                            submittedTransaction
                                        )
                                        .map { submittedTransaction.asKinPayments() }
                                }
                                .doOnResolved {
                                    // If we have an active stream then we rely on that update for balance changes
                                    if (accountStream == null) {
                                        storeAndNotifyOfBalanceUpdate(expectedNewBalance)
                                    }
                                }
                        }
                }
        )
    }

    private data class SourceAccountSigningData(
        val nonce: Long,
        val ownerKey: Key.PrivateKey,
        val sourceKey: Key.PublicKey
    )

    override fun sendKinPayments(
        payments: List<KinPaymentItem>,
        memo: KinMemo,
        sourceAccountSpec: AccountSpec,
        destinationAccountSpec: AccountSpec,
    ): Promise<List<KinPayment>> =
        sendKinPayments(payments, memo, sourceAccountSpec, destinationAccountSpec, emptyList())

    /**
     * This is a total hack, won't exist forever, don't use this
     */
    override fun sendKinPayments(
        payments: List<KinPaymentItem>,
        memo: KinMemo,
        sourceAccountSpec: AccountSpec,
        destinationAccountSpec: AccountSpec,
        signaturesOverride: List<DecoratedSignature>,
        feeOverride: QuarkAmount?
    ): Promise<List<KinPayment>> {
        log.log("sendKinPayments")
        val MAX_ATTEMPTS = 6
        val FIXED_ATTEMPTS = 2
        var attemptCount = 0
        val invalidAccountErrorRetryStrategy =
            BackoffStrategy.combine(BackoffStrategy.Fixed(
                after = 3000,
                maxAttempts = FIXED_ATTEMPTS
            ), BackoffStrategy.ExponentialIncrease(
                initial = 275,
                multiplier = 2.0,
                jitter = 1.0,
                maxAttempts = MAX_ATTEMPTS - FIXED_ATTEMPTS,
                maximumWaitTime = 60000
            ))

        fun buildAttempt(error: Throwable? = null): Promise<KinTransaction> {
            if (error == null) {
                // Resetting attempts (likely KinService api upgrade)
                attemptCount = 0
                invalidAccountErrorRetryStrategy.reset()
            }
            val sourceAccountPromise = getAccount()
                .flatMap { account ->
                    println("account.accounts.isEmpty(): ${account.tokenAccounts.isEmpty()}")
                    if ((attemptCount == 0 && account.tokenAccounts.isEmpty()) || sourceAccountSpec == AccountSpec.Exact) {
                        Promise.of(
                            SourceAccountSigningData(
                                (account.status as? KinAccount.Status.Registered)?.sequence ?: 0,
                                account.key as Key.PrivateKey,
                                account.key.asPublicKey()
                            )
                        )
                    } else {
                        service.resolveTokenAccounts(accountId)
                            .flatMap {
                                storage.updateAccountInStorage(account.copy(tokenAccounts = it))
                            }.map { resolvedAccount ->
                                SourceAccountSigningData(
                                    (resolvedAccount.status as? KinAccount.Status.Registered)?.sequence
                                        ?: 0,
                                    resolvedAccount.key as Key.PrivateKey,
                                    resolvedAccount.tokenAccounts.firstOrNull()
                                        ?: resolvedAccount.key.asPublicKey()
                                )
                            }
                    }
                }

            val paymentItemsPromise =
                if (attemptCount == 0 || destinationAccountSpec == AccountSpec.Exact) {
                    Promise.of(payments)
                } else {
                    Promise.allAny(
                        *payments.map { paymentItem ->
                            service.resolveTokenAccounts(paymentItem.destinationAccount)
                                .map {
                                    paymentItem.copy(
                                        destinationAccount = it.first().asKinAccountId()
                                    )
                                }
                                .onErrorResumeNext { Promise.of(paymentItem) }
                        }.toTypedArray()
                    )
                }

            return sourceAccountPromise.flatMap { accountData ->
                paymentItemsPromise.flatMap {
                    attemptCount++
                    calculateFee(payments.size).flatMap { fee ->
                        service.buildAndSignTransaction(
                            accountData.ownerKey,
                            accountData.sourceKey,
                            accountData.nonce,
                            it,
                            memo,
                            feeOverride ?: fee
                        ).map {
                            if (it is StellarKinTransaction) {
                                val tx = org.kin.stellarfork.Transaction.fromEnvelopeXdr(Base64.encodeBase64String(it.bytesValue), it.networkEnvironment.getNetwork())
                                if (signaturesOverride.isNotEmpty()) {
                                    tx.signatures = signaturesOverride
                                }

                                it.copy(bytesValue = Base64.decodeBase64(tx.toEnvelopeXdrBase64())!!)
                            } else it
                        }
                    }
                }
            }
        }

        fun attempt(error: Throwable? = null): Promise<List<KinPayment>> {
            log.log { "attempt: $attemptCount" }
            return if (attemptCount >= MAX_ATTEMPTS) {
                Promise.error(error!!)
            } else {
                Promise
                    .defer { sendKinTransaction { buildAttempt(error) } }
                    .workOn(executors.parallelIO)
                    .onErrorResumeNext { error ->
                        when (error) {
                            is KinService.FatalError.BadSequenceNumberInRequest -> {
                                if ((service as KinServiceWrapper).metaServiceApi.configuredMinApi == 4) {
                                    service.invalidateBlockhashCache()
                                    attempt(error)
                                } else {
                                    getAccount(true)
                                        .flatMap { storage.updateAccountInStorage(it) }
                                        .flatMap { attempt(error) }
                                }
                            }
                            is KinService.FatalError.InsufficientFeeInRequest -> {
                                service.getMinFee()
                                    .flatMap { storage.setMinFee(it) }
                                    .flatMap { attempt(error) }
                            }
                            is KinService.FatalError.UnknownAccountInRequest -> {
                                val delay = invalidAccountErrorRetryStrategy.nextDelay()
                                log.log("waiting $delay ms...")
                                Thread.sleep(delay)
                                attempt(error)
                            }
                            else -> Promise.error(error)
                        }
                    }
            }
        }

        return attempt()
    }

    // Idiomatic Variants of Primary Functions

    override fun clearStorage(clearCompleteCallback: Callback<Boolean>) {
        log.log("clearStorage")
        clearStorage().callback(clearCompleteCallback)
    }

    override fun getPaymentsForTransactionHash(
        transactionHash: TransactionHash,
        paymentsCallback: Callback<List<KinPayment>>
    ) = getPaymentsForTransactionHash(transactionHash).callback(paymentsCallback)

    override fun sendKinPayment(
        amount: KinAmount,
        destinationAccount: KinAccount.Id,
        memo: KinMemo,
        invoice: Optional<Invoice>,
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
    abstract val logger: KinLoggerFactory
    internal val log: KinLogger by lazy {
        logger.getLogger(javaClass.simpleName)
    }

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

    override fun getAccount(): Promise<KinAccount> = getAccount(false)

    fun maybeFetchAccountDetails(): Promise<KinAccount> =
        service.getAccount(accountId)
            .flatMap { storage.updateAccountInStorage(it) }
            .onErrorResumeNext(KinService.FatalError.ItemNotFound.javaClass) {
                service.resolveTokenAccounts(accountId)
                    .flatMap { accounts ->
                        val maybeResolvedAccountId =
                            accounts.firstOrNull()?.asKinAccountId() ?: accountId
                        service.getAccount(maybeResolvedAccountId)
                            .map {
                                if (maybeResolvedAccountId != accountId) {
                                    // b/c we want to update our on hand account with the resolved accountInfo details on solana
                                    it.copy(
                                        id = accountId,
                                        key = Key.PublicKey(accountId.value),
                                        tokenAccounts = accounts,
                                    )
                                } else it
                            }
                            .flatMap { storage.updateAccountInStorage(it) }
                    }
            }

    private val lifecycle = DisposeBag()
    internal var accountStream: Observer<KinAccount>? = null
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
                                        .doOnResolved { balance ->
                                            // Yea...this 5s delay is gross but reads aren't
                                            // deterministic with the account update events so
                                            // instead of polling (worse), we delay for a
                                            // 'best effort' history update.
                                            // TODO: Maybe we can do better with a future event for history updates.
                                            Promise.defer { fetchUpdatedTransactionHistory() }
                                                .doOnResolved { balanceSubject.onNext(balance) }
                                                .resolveIn(5, TimeUnit.SECONDS)
                                        }
                                }.resolve()
                        }

                        doOnDisposed {
                            lifecycle.dispose()
                            accountStream = null
                        }
                    }
                }
            }
        }
        return this
    }

    // Public

    override fun getAccount(forceUpdate: Boolean, accountCallback: Callback<KinAccount>) =
        getAccount(forceUpdate).callback(accountCallback)

    override fun observePayments(mode: ObservationMode): ListObserver<KinPayment> {
        return when (mode) {
            Passive -> paymentsSubject.apply { requestInvalidation() }
            ObservationMode.Active -> paymentsSubject.apply {
                requestInvalidation()
                setupActiveStreamingUpdatesIfNecessary(mode)
            }
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

    override fun getPaymentsForTransactionHash(transactionHash: TransactionHash): Promise<List<KinPayment>> {
        log.log("getPaymentsForTransactionHash")
        return service.getTransaction(transactionHash)
            .map { it.asKinPayments() }
    }

    override fun observeBalance(mode: ObservationMode): Observer<KinBalance> {
        log.log("observeBalance")
        return with(balanceSubject) {
            setupActiveStreamingUpdatesIfNecessary(mode)
            requestInvalidation()
        }
    }

    override fun clearStorage(): Promise<Boolean> {
        log.log("clearStorage")
        return storage.deleteAllStorage(accountId)
    }


    override fun calculateFee(numberOfOperations: Int): Promise<QuarkAmount> =
        service.canWhitelistTransactions()
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
                        }.map { QuarkAmount(it.value * numberOfOperations) }
                }
            }

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

    protected fun fetchUpdatedTransactionHistory(): Promise<List<KinTransaction>> =
        requestNextPage().map { it }
            .doOnResolved { paymentsSubject.onNext(it.asKinPayments(true)) }

    protected fun fetchUpdatedBalance(): Promise<KinBalance> {
        return getAccount(true)
            .flatMap { storage.updateAccountInStorage(it) }
            .map { it.balance }
            .doOnResolved { balanceSubject.onNext(it) }
    }

    protected fun computeExpectedNewBalance(transaction: KinTransaction): Promise<KinBalance> {
        return storage.getStoredAccount(accountId)
            .map {
                val amountToDeduct = with(transaction.asKinPayments()) {
                    var totalAmount = transaction.fee.toKin()
                    filter {
                        it.destinationAccountId != accountId
                    }.forEach { payment ->
                        totalAmount += (if (!payment.destinationAccountId.equals(payment.sourceAccountId)) payment.amount else KinAmount.ZERO)
                    }
                    totalAmount
                }
                it.map {
                    val newAmount = with(it.balance.amount - amountToDeduct) {
                        if (this.value < BigDecimal.ZERO) KinAmount.ZERO else this
                    }
                    it.balance.copy(
                        amount = newAmount,
                        pendingAmount = newAmount
                    )
                }.orElse(KinBalance())
            }
    }

    protected fun storeAndNotifyOfBalanceUpdate(newBalance: KinBalance) {
        storage.updateAccountBalance(accountId, newBalance)
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

