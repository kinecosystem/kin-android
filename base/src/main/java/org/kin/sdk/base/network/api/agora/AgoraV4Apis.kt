package org.kin.sdk.base.network.api.agora

import io.grpc.ManagedChannel
import org.kin.agora.gen.account.v4.AccountGrpc
import org.kin.agora.gen.account.v4.AccountService
import org.kin.agora.gen.transaction.v4.TransactionGrpc
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.network.api.KinAccountApiV4
import org.kin.sdk.base.network.api.KinAccountCreationApiV4
import org.kin.sdk.base.network.api.KinStreamingApiV4
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.SolanaKinTransaction
import org.kin.sdk.base.tools.ObservableCallback
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.ValueSubject
import org.kin.stellarfork.xdr.TransactionResultCode

class AgoraKinAccountCreationApiV4(
    managedChannel: ManagedChannel
) : GrpcApi(managedChannel), KinAccountCreationApiV4 {

    private val accountApi = AccountGrpc.newStub(managedChannel)

    override fun createAccount(
        request: KinAccountCreationApiV4.CreateAccountRequest,
        onCompleted: (KinAccountCreationApiV4.CreateAccountResponse) -> Unit
    ) = accountApi::createAccount
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.createAccountResponse()
        )
}

class AgoraKinAccountApiV4(
    managedChannel: ManagedChannel,
    private val networkEnvironment: NetworkEnvironment
) : GrpcApi(managedChannel), KinAccountApiV4, KinStreamingApiV4 {

    private val accountApi = AccountGrpc.newStub(managedChannel)

    override fun getAccount(
        request: KinAccountApiV4.GetAccountRequest,
        onCompleted: (KinAccountApiV4.GetAccountResponse) -> Unit
    ) {
        return accountApi::getAccountInfo
            .callAsPromisedCallback(
                request.toGrpcRequest(),
                onCompleted.getAccountInfoResponse()
            )
    }

    override fun resolveTokenAcounts(
        request: KinAccountApiV4.ResolveTokenAccountsRequest,
        onCompleted: (KinAccountApiV4.ResolveTokenAccountsResponse) -> Unit
    ) {
        return accountApi::resolveTokenAccounts
            .callAsPromisedCallback(
                request.toGrpcRequest(),
                onCompleted.resolveTokenAccountsResponse()
            )
    }

    sealed class AgoraEvent {
        abstract val event: AccountService.Event

        data class UnknownEvent(
            override val event: AccountService.Event
        ) : AgoraEvent()

        data class AccountUpdate(
            val kinAccount: KinAccount,
            override val event: AccountService.Event
        ) : AgoraEvent()

        data class TransactionUpdate(
            val kinTransaction: KinTransaction,
            override val event: AccountService.Event
        ) : AgoraEvent()
    }

    fun openEventStream(kinAccountId: KinAccount.Id): Observer<AgoraEvent> {
        return ValueSubject<AgoraEvent>()
            .also { subject ->
                val streamHandler = accountApi::getEvents
                    .callAsObservableCallback(
                        AccountService.GetEventsRequest.newBuilder()
                            .setAccountId(kinAccountId.toProtoSolanaAccountId())
                            .build(),
                        ObservableCallback({
                            if (it.result == AccountService.Events.Result.OK) {
                                it.eventsList.forEach { event ->
                                    val agoraEvent: AgoraEvent = (when {
                                        event.hasAccountUpdateEvent() -> {
                                            AgoraEvent.AccountUpdate(
                                                event.accountUpdateEvent.accountInfo.toKinAccount(),
                                                event
                                            )
                                        }
                                        event.hasTransactionEvent() -> {
                                            AgoraEvent.TransactionUpdate(
                                                SolanaKinTransaction(
                                                    event.transactionEvent.transaction.value.toByteArray(),
                                                    recordType = KinTransaction.RecordType.Acknowledged(
                                                        System.currentTimeMillis(),
                                                        TransactionResultCode.txSUCCESS.toResultXdr()
                                                    ),
                                                    networkEnvironment = networkEnvironment
                                                ),
                                                event
                                            )
                                        }
                                        else -> AgoraEvent.UnknownEvent(event)
                                    })
                                    subject.onNext(agoraEvent)
                                }
                            } else {
                                // TODO: should we subject.dispose()?
                            }
                        }, {}, {})
                    )
                subject.doOnDisposed {
                    streamHandler.cancel()
                }
            }
    }

    override fun streamAccount(kinAccountId: KinAccount.Id): Observer<KinAccount> {
        return openEventStream(kinAccountId)
            .filter { (it as? AgoraEvent.AccountUpdate) != null }
            .map { (it as AgoraEvent.AccountUpdate).kinAccount }
    }

    override fun streamNewTransactions(kinAccountId: KinAccount.Id): Observer<KinTransaction> {
        return openEventStream(kinAccountId)
            .filter { (it as? AgoraEvent.TransactionUpdate) != null }
            .map { (it as AgoraEvent.TransactionUpdate).kinTransaction }
    }
}

class AgoraKinTransactionsApiV4(
    managedChannel: ManagedChannel,
    val networkEnvironment: NetworkEnvironment
) : GrpcApi(managedChannel), KinTransactionApiV4 {
    private val transactionApi = TransactionGrpc.newStub(managedChannel)
    override fun getServiceConfig(
        request: KinTransactionApiV4.GetServiceConfigRequest,
        onCompleted: (KinTransactionApiV4.GetServiceConfigResponse) -> Unit
    ) = transactionApi::getServiceConfig
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.createServiceConfigResponse()
        )

    override fun getMinKinVersion(
        request: KinTransactionApiV4.GetMiniumumKinVersionRequest,
        onCompleted: (KinTransactionApiV4.GetMiniumumKinVersionResponse) -> Unit
    ) = transactionApi::getMinimumKinVersion
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.getMinKinVersionResponse()
        )

    override fun getRecentBlockHash(
        request: KinTransactionApiV4.GetRecentBlockHashRequest,
        onCompleted: (KinTransactionApiV4.GetRecentBlockHashResponse) -> Unit
    ) = transactionApi::getRecentBlockhash
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.getRecentBlockHashResponse()
        )

    override fun getMinimumBalanceForRentExemption(
        request: KinTransactionApiV4.GetMinimumBalanceForRentExemptionRequest,
        onCompleted: (KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse) -> Unit
    ) = transactionApi::getMinimumBalanceForRentExemption
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.getMinimumBalanceForRentExemptionResponse()
        )

    override fun getTransaction(
        request: KinTransactionApiV4.GetTransactionRequest,
        onCompleted: (KinTransactionApiV4.GetTransactionResponse) -> Unit
    ) {
        return transactionApi::getTransaction
            .callAsPromisedCallback(
                request.toGrpcRequest(),
                onCompleted.getTransactionResponse(networkEnvironment)
            )
    }

    override fun submitTransaction(
        request: KinTransactionApiV4.SubmitTransactionRequest,
        onCompleted: (KinTransactionApiV4.SubmitTransactionResponse) -> Unit
    ) = transactionApi::submitTransaction
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.submitTransactionResponse(request, networkEnvironment)
        )

    override fun getTransactionHistory(
        request: KinTransactionApiV4.GetTransactionHistoryRequest,
        onCompleted: (KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit
    ) = transactionApi::getHistory
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.getTransactionHistoryResponse(networkEnvironment)
        )

}
