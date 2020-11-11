package kin.sdk.internal

import kin.sdk.AccountStatus
import kin.sdk.BackupRestore
import kin.sdk.Balance
import kin.sdk.EventListener
import kin.sdk.KinAccount
import kin.sdk.ListenerRegistration
import kin.sdk.PaymentInfo
import kin.sdk.Transaction
import kin.sdk.TransactionId
import kin.sdk.exception.AccountDeletedException
import kin.sdk.exception.CryptoException
import kin.sdk.exception.OperationFailedException
import kin.utils.Request
import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinAccountContextImpl
import org.kin.sdk.base.ObservationMode
import org.kin.sdk.base.models.AccountSpec
import org.kin.sdk.base.models.AppId
import org.kin.sdk.base.models.ClassicKinMemo
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.MemoSuffix
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.asKinMemo
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.toKinTransaction
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.DecoratedSignature
import java.io.IOException
import java.math.BigDecimal
import java.util.concurrent.Executors


internal class KinAccountImpl(
    private val account: KeyPair,
    private val backupRestore: BackupRestore,
    private val accountContext: KinAccountContext,
    private val kinService: KinService,
    private val networkEnvironment: NetworkEnvironment,
    private val appId: AppId
) : KinAccount {
    private var isDeleted = false
    private val network = networkEnvironment.toNetwork()
    private val backgroundExecutor = Executors.newSingleThreadExecutor()

    override fun getPublicAddress(): String? {
        return if (isDeleted) null else accountContext.accountId.stellarBase32Encode()
    }

    override fun getStringEncodedPrivateKey(): String? {
        return try {
            account.secretSeed
        } catch (t: Throwable) {
            null
        }?.let { String(it) }
    }

    @Throws(OperationFailedException::class)
    override fun buildTransactionSync(
        publicAddress: String,
        amount: BigDecimal,
        fee: Int
    ): Transaction = buildTransactionSync(publicAddress, amount, fee, null)

    @Throws(OperationFailedException::class)
    override fun buildTransactionSync(
        publicAddress: String,
        amount: BigDecimal,
        fee: Int,
        memo: String?
    ): Transaction {
        checkValidAccount()

        return try {
            buildTransactionInternal(publicAddress, amount, fee, memo).sync()
        } catch (e: Exception) {
            throw exceptionCorrectionIfNecessary(e)
        }
    }

    @Throws(OperationFailedException::class)
    override fun sendTransactionSync(transaction: Transaction): TransactionId {
        checkValidAccount()

        return try {
            sendTransactionInternal(transaction).sync()
        } catch (e: Exception) {
            throw exceptionCorrectionIfNecessary(e)
        }
    }

    @Throws(OperationFailedException::class)
    override fun sendWhitelistTransactionSync(whitelist: String): TransactionId {
        checkValidAccount()

        return try {
            sendWhitelistTransactionInternal(whitelist).sync()
        } catch (e: Exception) {
            if (e is IOException) {
                throw OperationFailedException("whitelist transaction data invalid", e)
            } else {
                throw exceptionCorrectionIfNecessary(e)
            }
        }
    }

    @Throws(OperationFailedException::class)
    override fun getBalanceSync(): Balance {
        checkValidAccount()
        return try {
            accountContext.getAccount(true).syncAndMap { balance.toBalance() }
        } catch (e: Exception) {
            throw exceptionCorrectionIfNecessary(e)
        }
    }

    @Throws(OperationFailedException::class)
    override fun getStatusSync(): Int {
        checkValidAccount()

        return try {
            getStatusInternal().sync()
        } catch (e: Exception) {
            throw exceptionCorrectionIfNecessary(e)
        }
    }

    override fun addBalanceListener(listener: EventListener<Balance>): ListenerRegistration {
        return accountContext.observeBalance(ObservationMode.Active)
            .asListenerRegistration(listener) { it.toBalance() }
    }

    override fun addPaymentListener(listener: EventListener<PaymentInfo>): ListenerRegistration {
        return accountContext.observePayments(ObservationMode.ActiveNewOnly) //kinService.streamNewTransactions(accountContext.accountId)
            .asListenerRegistrationToList(listener) {
                it.map { it.toPaymentInfo() }
            }
    }

    override fun addAccountCreationListener(listener: EventListener<Void>): ListenerRegistration {
        accountContext.getAccount()
            .then { listener.onEvent(null) }
        return ListenerRegistration { /* nothing to clean up */ }
    }

    @Throws(CryptoException::class)
    override fun export(passphrase: String): String {
        return backupRestore.exportWallet(account, passphrase)
    }

    fun markAsDeleted() {
        isDeleted = true
        accountContext.clearStorage().resolve()
    }

    @Throws(AccountDeletedException::class)
    private fun checkValidAccount() {
        if (isDeleted) {
            throw AccountDeletedException()
        }
    }

    private fun exceptionCorrectionIfNecessary(e: Exception): Exception {
        return if (e is KinService.FatalError.SDKUpgradeRequired) e
        else OperationFailedException(e)
    }

    override fun buildTransaction(
        publicAddress: String,
        amount: BigDecimal,
        fee: Int
    ): Request<Transaction> {
        return Request(
            buildTransactionInternal(publicAddress, amount, fee, null),
            this::exceptionCorrectionIfNecessary
        )
    }

    override fun buildTransaction(
        publicAddress: String,
        amount: BigDecimal,
        fee: Int,
        memo: String?
    ): Request<Transaction> {
        return Request(
            buildTransactionInternal(publicAddress, amount, fee, memo),
            this::exceptionCorrectionIfNecessary
        )
    }

    override fun sendTransaction(transaction: Transaction): Request<TransactionId> {
        return Request(
            sendTransactionInternal(transaction),
            this::exceptionCorrectionIfNecessary
        )
    }

    override fun sendWhitelistTransaction(whitelist: String): Request<TransactionId> {
        return Request(
            sendWhitelistTransactionInternal(whitelist),
            this::exceptionCorrectionIfNecessary
        )
    }

    override fun getBalance(): Request<Balance> {
        return Request(
            accountContext.getAccount(true).map { it.balance.toBalance() },
            this::exceptionCorrectionIfNecessary
        )
    }

    override fun getStatus(): Request<Int> {
        return Request(
            getStatusInternal(),
            this::exceptionCorrectionIfNecessary
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val account = other as KinAccount
        return if (publicAddress == null || account.publicAddress == null) {
            false
        } else publicAddress == account.publicAddress
    }

    private fun buildTransactionInternal(
        publicAddress: String,
        amount: BigDecimal,
        fee: Int,
        memo: String?
    ): Promise<Transaction> {
        return accountContext.getAccount()
            .flatMap { account ->
                kinService
                    .buildAndSignTransaction(
                        account.key as Key.PrivateKey,
                        account.key.asPublicKey(),
                        (account.status as org.kin.sdk.base.models.KinAccount.Status.Registered).sequence,
                        listOf(
                            KinPaymentItem(
                                KinAmount(amount),
                                KeyPair.fromAccountId(publicAddress).asKinAccountId()
                            )
                        ),
                        buildMemo(memo ?: ""),
                        QuarkAmount(fee.toLong())
                    )
            }
            .map { it.asTransaction(network) }
    }

    private fun sendTransactionInternal(transaction: Transaction): Promise<TransactionId> {
        return Promise.of(transaction.kinTransaction ?: transaction.stellarTransaction!!.toKinTransaction(networkEnvironment))
            .flatMap { sendKinPaymentWithTransaction(it) }
            .map { it.first().id.transactionHash.toTransactionId() }
    }

    private fun buildMemo(suffix: String): KinMemo =
        ClassicKinMemo(appId = appId, memoSuffix = MemoSuffix(suffix)).asKinMemo()

    private fun sendWhitelistTransactionInternal(whitelist: String): Promise<TransactionId> {
        return Promise
            .defer {
                Promise.of(org.kin.stellarfork.Transaction.fromEnvelopeXdr(whitelist, network)
                    .toKinTransaction(networkEnvironment))
            }
            .workOn(backgroundExecutor)
            .flatMap {
                sendTransactionInternal(it.asTransaction(networkEnvironment.toNetwork()))
            }
    }

    private fun sendKinPaymentWithTransaction(
        kinTransaction: KinTransaction
    ): Promise<List<KinPayment>> {
        val payments: List<KinPaymentItem> = kinTransaction.asKinPayments()
            .map {
                KinPaymentItem(it.amount, it.destinationAccountId, Optional.ofNullable(it.invoice))
            }
        val memo: KinMemo = kinTransaction.memo

        val sigs = mutableListOf<DecoratedSignature>()

        try {
            sigs.addAll(org.kin.stellarfork.Transaction.fromEnvelopeXdr(Base64.encodeBase64String(kinTransaction.bytesValue), network).signatures)
        } catch (t: Throwable) {
            // do nothing
        }

        return accountContext.sendKinPayments(payments, memo, AccountSpec.Preferred, AccountSpec.Preferred, sigs, kinTransaction.fee)
    }

    private fun getStatusInternal(): Promise<Int> {
        return accountContext.getAccount()
            .map { AccountStatus.CREATED }
    }
}
