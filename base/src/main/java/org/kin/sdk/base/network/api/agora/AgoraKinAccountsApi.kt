package org.kin.sdk.base.network.api.agora

import io.grpc.ManagedChannel
import org.kin.agora.gen.account.v3.AccountGrpc
import org.kin.agora.gen.account.v3.AccountService
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountRequest
import org.kin.sdk.base.network.api.KinAccountApi.GetAccountResponse
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinStreamingApi
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.stellar.models.StellarKinTransaction
import org.kin.sdk.base.tools.ObservableCallback
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.ValueSubject

class AgoraKinAccountsApi(
    managedChannel: ManagedChannel,
    private val networkEnvironment: NetworkEnvironment
) : GrpcApi(managedChannel), KinAccountApi, KinStreamingApi, KinAccountCreationApi {

    private val accountApi = AccountGrpc.newStub(managedChannel)

    override fun createAccount(
        request: KinAccountCreationApi.CreateAccountRequest,
        onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit
    ) = accountApi::createAccount
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.createAccountResponse()
        )

    override fun getAccount(
        request: GetAccountRequest,
        onCompleted: (GetAccountResponse) -> Unit
    ) = accountApi::getAccountInfo
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.getAccountResponse()
        )

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
                            .setAccountId(kinAccountId.toProtoStellarAccountId())
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
                                                StellarKinTransaction(
                                                    event.transactionEvent.envelopeXdr.toByteArray(),
                                                    recordType = KinTransaction.RecordType.Acknowledged(
                                                        System.currentTimeMillis(),
                                                        event.transactionEvent.resultXdr.toByteArray()
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
