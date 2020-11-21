package org.kin.sdk.base.network.services

import org.kin.sdk.base.models.InvoiceList
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.solana.MemoProgram
import org.kin.sdk.base.models.solana.SystemProgram
import org.kin.sdk.base.models.solana.TokenProgram
import org.kin.sdk.base.models.solana.Transaction
import org.kin.sdk.base.models.solana.marshal
import org.kin.sdk.base.models.solana.unmarshal
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toSigningKeyPair
import org.kin.sdk.base.network.api.KinAccountApiV4
import org.kin.sdk.base.network.api.KinAccountCreationApiV4
import org.kin.sdk.base.network.api.KinStreamingApiV4
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.network.api.agora.sha224Hash
import org.kin.sdk.base.network.api.agora.toProto
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.SolanaKinTransaction
import org.kin.sdk.base.tools.KinLoggerFactory
import org.kin.sdk.base.tools.NetworkOperationsHandler
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.onErrorResumeNextError
import org.kin.sdk.base.tools.queueWork
import org.kin.sdk.base.tools.sha256
import org.kin.sdk.base.tools.toHexString
import org.kin.stellarfork.KeyPair
import java.util.concurrent.TimeUnit

class KinServiceImplV4(
    private val networkEnvironment: NetworkEnvironment,
    private val networkOperationsHandler: NetworkOperationsHandler,
    private val accountApi: KinAccountApiV4,
    private val transactionApi: KinTransactionApiV4,
    private val streamingApi: KinStreamingApiV4,
    private val accountCreationApi: KinAccountCreationApiV4,
    private val logger: KinLoggerFactory,
) : KinService {
    private val log = logger.getLogger(javaClass.simpleName)

    private fun <T> T.requestPrint(): T {
        log.log { "[Request][V4] ===============" }
        log.log { "${this}" }
        log.log { "=============== [V4][Request]" }
        return this
    }

    private fun <T> T.responsePrint(): T {
        log.log { "[Response][V4] ===============" }
        log.log { "${this}" }
        log.log { "=============== [V4][Response]" }
        return this
    }

    private val cache = Cache<String>()

    private fun cachedServiceConfig() =
        cache.resolve(
            "serviceConfig",
            TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES)
        ) {
            networkOperationsHandler.queueWork<KinTransactionApiV4.GetServiceConfigResponse> { resolve ->
                transactionApi.getServiceConfig { resolve(it) }
            }
        }

    private fun cachedRecentBlockHash() =
        cache.resolve(
            "recentBlockHash",
            TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES)
        ) {
            networkOperationsHandler.queueWork<KinTransactionApiV4.GetRecentBlockHashResponse> { resolve ->
                transactionApi.getRecentBlockHash { resolve(it) }
            }
        }

    private fun cachedMinRentExemption() =
        cache.resolve(
            "minRentExemption",
            TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES)
        ) {
            networkOperationsHandler.queueWork<KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse> { resolve ->
                transactionApi.getMinimumBalanceForRentExemption(
                    KinTransactionApiV4.GetMinimumBalanceForRentExemptionRequest(TokenProgram.accountSize)
                ) { resolve(it) }
            }
        }

    override fun createAccount(
        accountId: KinAccount.Id,
        signer: Key.PrivateKey
    ): Promise<KinAccount> {
        return networkOperationsHandler.queueWork { respond, error ->
            if (error is KinService.FatalError.TransientFailure) {
                invalidateBlockhashCache()
            }
            Promise.all(cachedServiceConfig(), cachedRecentBlockHash(), cachedMinRentExemption())
                .onErrorResumeNextError {
                    KinService.FatalError.TransientFailure(
                        RuntimeException("Pre-requisite response failed! $it")
                    )
                }
                .then({ (serviceConfig, recentBlockHash, minRentExemption) ->

                    val tokenAccountSeed = signer.toSigningKeyPair().rawSecretSeed!!.sha256()
                    val tokenAccount = KeyPair.fromSecretSeed(tokenAccountSeed).asPrivateKey()
                    val tokenAccountPub: Key.PublicKey = tokenAccount.asPublicKey()

                    val subsidizer: Key.PublicKey = serviceConfig.subsidizerAccount!!.toKeyPair().asPublicKey()
                    val owner: Key.PublicKey = signer.asPublicKey()
                    val programKey = serviceConfig.tokenProgram!!.toKeyPair().asPublicKey()
                    val mint = serviceConfig.token!!.toKeyPair().asPublicKey()

                    val transaction = Transaction.newTransaction(
                        subsidizer,
                        SystemProgram.CreateAccount(
                            subsidizer = subsidizer,
                            address = tokenAccountPub,
                            owner = programKey,
                            lamports = minRentExemption.lamports!!,
                            size = TokenProgram.accountSize
                        ).instruction,
                        TokenProgram.InitializeAccount(
                            account = tokenAccountPub,
                            mint = mint,
                            owner = owner,
                            programKey = programKey
                        ).instruction,
                        TokenProgram.SetAuthority(
                            account = tokenAccountPub,
                            currentAuthority = owner,
                            newAuthority = subsidizer,
                            authorityType = TokenProgram.AuthorityType.AuthorityCloseAccount,
                            programKey = programKey
                        ).instruction
                    ).copyAndSetRecentBlockhash(recentBlockHash.blockHash!!)
                        .copyAndSign(tokenAccount, signer)

                    log.log { "serviceConfig: $serviceConfig" }
                    log.log { "recentBlockHash: $recentBlockHash" }
                    log.log { "minRentExemption: $minRentExemption" }
                    log.log { "createTransaction: ${transaction.marshal().toHexString()}" }

                    accountCreationApi.createAccount(
                        KinAccountCreationApiV4.CreateAccountRequest(transaction).requestPrint()
                    ) { response ->
                        response.responsePrint()
                        val error: Exception? = when (response.result) {
                            KinAccountCreationApiV4.CreateAccountResponse.Result.Ok -> {
                                if (response.account != null) {
                                    respond(response.account); null
                                } else KinService.FatalError.IllegalResponse
                            }
                            KinAccountCreationApiV4.CreateAccountResponse.Result.Exists -> {
                                if (response.account != null) {
                                    respond(response.account); null
                                } else {
                                    getAccount(accountId).then {
                                        respond(it)
                                    }
                                    null
                                }
                            }
                            KinAccountCreationApiV4.CreateAccountResponse.Result.BadNonce ->
                                KinService.FatalError.TransientFailure(Exception("Bad Nonce: RecentBlockhash invalid"))
                            is KinAccountCreationApiV4.CreateAccountResponse.Result.TransientFailure ->
                                KinService.FatalError.TransientFailure(response.result.error)
                            is KinAccountCreationApiV4.CreateAccountResponse.Result.UndefinedError ->
                                KinService.FatalError.UnexpectedServiceError(response.result.error)
                            KinAccountCreationApiV4.CreateAccountResponse.Result.UpgradeRequiredError ->
                                KinService.FatalError.SDKUpgradeRequired
                            KinAccountCreationApiV4.CreateAccountResponse.Result.PayerRequired ->
                                KinService.FatalError.UnexpectedServiceError(Exception("PayerRequired: no subsidizer set"))
                        }
                        error?.let { respond(it) }
                    }
                }, { respond(it) })
        }
    }

    override fun getAccount(accountId: KinAccount.Id): Promise<KinAccount> {
        return networkOperationsHandler.queueWork { respond ->
            accountApi.getAccount(
                KinAccountApiV4.GetAccountRequest(accountId).requestPrint()
            ) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinAccountApiV4.GetAccountResponse.Result.Ok -> {
                        if (response.account != null) {
                            respond(response.account); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinAccountApiV4.GetAccountResponse.Result.NotFound ->
                        KinService.FatalError.ItemNotFound
                    is KinAccountApiV4.GetAccountResponse.Result.UndefinedError ->
                        KinService.FatalError.UnexpectedServiceError(response.result.error)
                    is KinAccountApiV4.GetAccountResponse.Result.TransientFailure ->
                        KinService.FatalError.TransientFailure(response.result.error)
                    KinAccountApiV4.GetAccountResponse.Result.UpgradeRequiredError ->
                        KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun resolveTokenAccounts(accountId: KinAccount.Id): Promise<List<Key.PublicKey>> {
        val cacheKey = "resolvedAccounts:${accountId.stellarBase32Encode()}"
        val resolve = cache.resolve(cacheKey) {
            networkOperationsHandler.queueWork<List<Key.PublicKey>> { respond ->
                accountApi.resolveTokenAcounts(
                    KinAccountApiV4.ResolveTokenAccountsRequest(accountId).requestPrint()
                ) { response ->
                    response.responsePrint()
                    val error: Exception? = when (response.result) {
                        KinAccountApiV4.ResolveTokenAccountsResponse.Result.Ok -> {
                            respond(response.accounts); null
                        }
                        is KinAccountApiV4.ResolveTokenAccountsResponse.Result.UndefinedError ->
                            KinService.FatalError.UnexpectedServiceError(response.result.error)
                        is KinAccountApiV4.ResolveTokenAccountsResponse.Result.TransientFailure ->
                            KinService.FatalError.TransientFailure(response.result.error)
                        KinAccountApiV4.ResolveTokenAccountsResponse.Result.UpgradeRequiredError ->
                            KinService.FatalError.SDKUpgradeRequired
                    }
                    error?.let { respond(it) }
                }
            }
        }

        return resolve.flatMap {
            if (it.isEmpty()) {
                cache.invalidate(cacheKey)
                resolve
            } else {
                Promise.of(it)
            }
        }
    }

    override fun getLatestTransactions(kinAccountId: KinAccount.Id): Promise<List<KinTransaction>> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransactionHistory(
                KinTransactionApiV4.GetTransactionHistoryRequest(
                    kinAccountId
                ).requestPrint()
            ) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.Ok -> {
                        if (response.transactions != null) {
                            respond(response.transactions); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.NotFound ->
                        KinService.FatalError.ItemNotFound
                    is KinTransactionApiV4.GetTransactionHistoryResponse.Result.UndefinedError ->
                        KinService.FatalError.UnexpectedServiceError(response.result.error)
                    is KinTransactionApiV4.GetTransactionHistoryResponse.Result.TransientFailure ->
                        KinService.FatalError.TransientFailure(response.result.error)
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.UpgradeRequiredError ->
                        KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun getTransactionPage(
        kinAccountId: KinAccount.Id,
        pagingToken: KinTransaction.PagingToken,
        order: KinService.Order
    ): Promise<List<KinTransaction>> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransactionHistory(
                KinTransactionApiV4.GetTransactionHistoryRequest(
                    kinAccountId,
                    pagingToken,
                    when (order) {
                        KinService.Order.Ascending -> KinTransactionApiV4.GetTransactionHistoryRequest.Order.Ascending
                        KinService.Order.Descending -> KinTransactionApiV4.GetTransactionHistoryRequest.Order.Descending
                    }
                ).requestPrint()
            ) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.Ok -> {
                        if (response.transactions != null) {
                            respond(response.transactions); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.NotFound ->
                        KinService.FatalError.ItemNotFound
                    is KinTransactionApiV4.GetTransactionHistoryResponse.Result.UndefinedError ->
                        KinService.FatalError.UnexpectedServiceError(response.result.error)
                    is KinTransactionApiV4.GetTransactionHistoryResponse.Result.TransientFailure ->
                        KinService.FatalError.TransientFailure(response.result.error)
                    KinTransactionApiV4.GetTransactionHistoryResponse.Result.UpgradeRequiredError ->
                        KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond.invoke(it) }
            }
        }
    }

    override fun getTransaction(transactionHash: TransactionHash): Promise<KinTransaction> {
        return networkOperationsHandler.queueWork { respond ->
            transactionApi.getTransaction(
                KinTransactionApiV4.GetTransactionRequest(transactionHash).requestPrint()
            ) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinTransactionApiV4.GetTransactionResponse.Result.Ok -> {
                        if (response.transaction != null) {
                            respond(response.transaction); null
                        } else KinService.FatalError.IllegalResponse
                    }
                    KinTransactionApiV4.GetTransactionResponse.Result.NotFound -> KinService.FatalError.ItemNotFound
                    is KinTransactionApiV4.GetTransactionResponse.Result.UndefinedError ->
                        KinService.FatalError.UnexpectedServiceError(response.result.error)
                    is KinTransactionApiV4.GetTransactionResponse.Result.TransientFailure ->
                        KinService.FatalError.TransientFailure(response.result.error)
                    KinTransactionApiV4.GetTransactionResponse.Result.UpgradeRequiredError -> KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond.invoke(it) }
            }
        }
    }

    override fun canWhitelistTransactions(): Promise<Boolean> = Promise.of(true)

    override fun getMinFee(): Promise<QuarkAmount> = Promise.of(QuarkAmount(0))

    override fun buildAndSignTransaction(
        ownerKey: Key.PrivateKey,
        sourceKey: Key.PublicKey,
        nonce: Long,
        paymentItems: List<KinPaymentItem>,
        memo: KinMemo,
        fee: QuarkAmount
    ): Promise<KinTransaction> {
        log.log { "buildAndSignTransaction: ownerKey:$ownerKey sourceKey:$sourceKey nonce:$nonce paymentItems:$paymentItems memo:$memo fee:$fee" }
        return networkOperationsHandler.queueWork { respond ->
            Promise.all(cachedServiceConfig(), cachedRecentBlockHash())
                .onErrorResumeNextError {
                    KinService.FatalError.TransientFailure(
                        RuntimeException("Pre-requisite response failed! $it")
                    )
                }
                .then({ (serviceConfig, recentBlockHash) ->
                    val ownerAccount = ownerKey.asPublicKey()
                    val subsidizer: Key.PublicKey =
                        serviceConfig.subsidizerAccount!!.toKeyPair().asPublicKey()
                    val programKey = serviceConfig.tokenProgram!!.toKeyPair().asPublicKey()
                    val paymentInstructions = paymentItems.map { paymentItem ->
                        val destinationAccount = paymentItem.destinationAccount.toKeyPair()
                            .asPublicKey()

                        TokenProgram.Transfer(
                            sourceKey,
                            destinationAccount,
                            ownerAccount,
                            paymentItem.amount,
                            programKey = programKey
                        ).instruction
                    }
                    val memoInstruction = if (memo != KinMemo.NONE) {
                        if (memo.type == KinMemo.Type.NoEncoding) {
                            MemoProgram.Base64EncodedMemo.fromBytes(memo.rawValue).instruction
                        } else {
                            MemoProgram.RawMemo(memo.rawValue).instruction
                        }
                    } else null

                    val tx = Transaction.newTransaction(
                        subsidizer,
                        *listOfNotNull(
                            memoInstruction,
                            *paymentInstructions.toTypedArray()
                        ).toTypedArray()
                    ).copyAndSetRecentBlockhash(recentBlockHash.blockHash!!)
                        .copyAndSign(ownerKey)

                    val kinTransaction = SolanaKinTransaction(
                        bytesValue = tx.marshal(),
                        networkEnvironment = networkEnvironment,
                        invoiceList = paymentItems.toInvoiceList()
                    )

                    log.log { "serviceConfig: $serviceConfig" }
                    log.log { "recentBlockHash: $recentBlockHash" }
                    log.log { "ownerAccount: $ownerAccount" }
                    log.log { "sourceOfFundsAccount: $sourceKey" }
                    log.log { "transactionHexString: ${kinTransaction.bytesValue.toHexString()}" }

                    respond(kinTransaction)

                }, { respond(it) })
        }
    }

    override fun submitTransaction(transaction: KinTransaction): Promise<KinTransaction> {
        return networkOperationsHandler.queueWork<KinTransaction> { respond ->
            transactionApi.submitTransaction(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(transaction.bytesValue),
                    transaction.invoiceList
                ).requestPrint()
            ) { response ->
                response.responsePrint()
                val error: Exception? = when (response.result) {
                    KinTransactionApiV4.SubmitTransactionResponse.Result.Ok ->
                        response.transaction?.let { respond(it); null }
                    KinTransactionApiV4.SubmitTransactionResponse.Result.InsufficientFee ->
                        KinService.FatalError.InsufficientFeeInRequest
                    KinTransactionApiV4.SubmitTransactionResponse.Result.BadSequenceNumber ->
                        KinService.FatalError.BadSequenceNumberInRequest
                    KinTransactionApiV4.SubmitTransactionResponse.Result.NoAccount ->
                        KinService.FatalError.UnknownAccountInRequest
                    KinTransactionApiV4.SubmitTransactionResponse.Result.InsufficientBalance ->
                        KinService.FatalError.InsufficientBalanceForSourceAccountInRequest
                    is KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors ->
                        KinService.FatalError.InvoiceErrorsInRequest(response.result.errors.toV3InvoiceErrors())
                    KinTransactionApiV4.SubmitTransactionResponse.Result.WebhookRejected ->
                        KinService.FatalError.WebhookRejectedTransaction
                    is KinTransactionApiV4.SubmitTransactionResponse.Result.UndefinedError ->
                        KinService.FatalError.UnexpectedServiceError(response.result.error)
                    is KinTransactionApiV4.SubmitTransactionResponse.Result.TransientFailure ->
                        KinService.FatalError.TransientFailure(response.result.error)
                    KinTransactionApiV4.SubmitTransactionResponse.Result.UpgradeRequiredError ->
                        KinService.FatalError.SDKUpgradeRequired
                }
                error?.let { respond(it) }
            }
        }
    }

    override fun buildSignAndSubmitTransaction(buildAndSignTransaction: () -> Promise<KinTransaction>): Promise<KinTransaction> {
        return buildAndSignTransaction()
            .flatMap { submitTransaction(it) }
    }

    override fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount> {
        return streamingApi.streamAccount(kinAccountId)
            .add {
                log.log { "streamAccount::Update $it" }
            }
    }

    override fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction> {
        return streamingApi.streamNewTransactions(kinAccountId)
            .add {
                log.log { "streamNewTransactions::Update $it" }
            }
    }

    override fun invalidateBlockhashCache() {
        cache.invalidate("recentBlockHash")
    }

    override val testService: KinTestService = KinTestServiceImplV4(logger, this)

    private fun List<KinPaymentItem>.toInvoiceList(): InvoiceList? =
        with(mapNotNull { it.invoice }.map { it.get() }.filterNotNull()) {
            if (isNotEmpty()) {
                InvoiceList(InvoiceList.Id(toProto().sha224Hash()), this)
            } else null
        }

}

private fun List<KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError>.toV3InvoiceErrors(): List<KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError> =
    map {
        when (it) {
            is KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.ALREADY_PAID -> KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.ALREADY_PAID
            KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.UNKNOWN -> KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.UNKNOWN
            KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.WRONG_DESTINATION -> KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.WRONG_DESTINATION
            KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.SKU_NOT_FOUND -> KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError.SKU_NOT_FOUND
        }
    }
