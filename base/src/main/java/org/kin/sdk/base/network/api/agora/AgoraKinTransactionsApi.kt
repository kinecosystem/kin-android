package org.kin.sdk.base.network.api.agora

import io.grpc.ManagedChannel
import org.kin.agora.gen.transaction.v3.TransactionGrpc
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionApi.GetMinFeeForTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryRequest
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionHistoryResponse
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionApi.GetTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionRequest
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi.WhitelistTransactionResponse
import org.kin.sdk.base.stellar.models.NetworkEnvironment

class AgoraKinTransactionsApi(
    managedChannel: ManagedChannel,
    private val networkEnvironment: NetworkEnvironment
) : GrpcApi(managedChannel), KinTransactionApi,
    KinTransactionWhitelistingApi {
    override val isWhitelistingAvailable: Boolean = true

    private val transactionApi = TransactionGrpc.newStub(managedChannel)

    override fun getTransactionHistory(
        request: GetTransactionHistoryRequest,
        onCompleted: (GetTransactionHistoryResponse) -> Unit
    ) = transactionApi::getHistory
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.getTransactionHistoryResponse(networkEnvironment)
        )

    override fun getTransaction(
        request: GetTransactionRequest,
        onCompleted: (GetTransactionResponse) -> Unit
    ) = transactionApi::getTransaction
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.getTransactionResponse(networkEnvironment)
        )

    override fun getTransactionMinFee(onCompleted: (GetMinFeeForTransactionResponse) -> Unit) {
        onCompleted(
            GetMinFeeForTransactionResponse(
                GetMinFeeForTransactionResponse.Result.Ok,
                QuarkAmount(100) //TODO: we need an rpc to fetch this from Agora
            )
        )
    }

    override fun whitelistTransaction(
        request: KinTransactionWhitelistingApi.WhitelistTransactionRequest,
        onCompleted: (WhitelistTransactionResponse) -> Unit
    ) {
        /**
         * Effectively a no-op, just passing through since white-listing a transaction
         * is done in Agora's submitTransaction operation.
         */
        onCompleted(
            WhitelistTransactionResponse(
                WhitelistTransactionResponse.Result.Ok,
                request.base64EncodedTransactionEnvelopeBytes
            )
        )
    }

    override fun submitTransaction(
        request: SubmitTransactionRequest,
        onCompleted: (SubmitTransactionResponse) -> Unit
    ) = transactionApi::submitTransaction
        .callAsPromisedCallback(
            request.toGrpcRequest(),
            onCompleted.submitTransactionResponse(request, networkEnvironment)
        )
}
