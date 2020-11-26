package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.stub.StreamObserver
import io.grpc.testing.GrpcCleanupRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.kin.agora.gen.account.v3.AccountGrpc
import org.kin.agora.gen.transaction.v3.TransactionGrpc
import org.kin.agora.gen.transaction.v3.TransactionService
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.latchOperationValueCapture
import org.kin.sdk.base.tools.testValue
import org.kin.stellarfork.codec.Base64
import org.mockito.AdditionalAnswers
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgoraKinTransactionsApiTest {

    companion object {
        val account = KinAccount(
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key,
            balance = KinBalance(KinAmount(5000)),
            status = KinAccount.Status.Registered(12345)
        )
    }

    val grpcCleanup = GrpcCleanupRule()

    @Rule
    fun grpcCleanup(): GrpcCleanupRule = grpcCleanup

    private lateinit var fakeServer: TransactionGrpc.TransactionImplBase

    private val serviceImpl: TransactionGrpc.TransactionImplBase = Mockito.mock(
        TransactionGrpc.TransactionImplBase::class.java,
        AdditionalAnswers.delegatesTo<AccountGrpc.AccountImplBase>(
            // By default the client will receive Status.UNIMPLEMENTED for all RPCs.
            object : TransactionGrpc.TransactionImplBase() {

                override fun submitTransaction(
                    request: TransactionService.SubmitTransactionRequest?,
                    responseObserver: StreamObserver<TransactionService.SubmitTransactionResponse>?
                ) {
                    fakeServer.submitTransaction(request, responseObserver)
                }

                override fun getHistory(
                    request: TransactionService.GetHistoryRequest?,
                    responseObserver: StreamObserver<TransactionService.GetHistoryResponse>?
                ) {
                    fakeServer.getHistory(request, responseObserver)
                }

                override fun getTransaction(
                    request: TransactionService.GetTransactionRequest?,
                    responseObserver: StreamObserver<TransactionService.GetTransactionResponse>?
                ) {
                    fakeServer.getTransaction(request, responseObserver)
                }
            })
    )

    private lateinit var sut: AgoraKinTransactionsApi

    @Before
    fun setUp() {
        // Generate a unique in-process server name.
        val serverName = InProcessServerBuilder.generateName()

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(
            InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start()
        )

        // Create a client channel and register for automatic graceful shutdown.
        val channel = grpcCleanup.register(
            InProcessChannelBuilder.forName(serverName).directExecutor().build()
        )

        sut = AgoraKinTransactionsApi(channel, NetworkEnvironment.KinStellarTestNetKin3)
    }


    @Test
    fun isWhitelistingAvailable() {
        assertTrue { sut.isWhitelistingAvailable }
    }

    @Test
    fun getTransactionMinFee() {
        sut.getTransactionMinFee {
            assertEquals(KinTransactionApi.GetMinFeeForTransactionResponse.Result.Ok, it.result)
            assertEquals(QuarkAmount(100), it.minFee)
        }
    }

    @Test
    fun whitelistTransaction() {
        /**
         * Effectively a no-op, just passing through since white-listing a transaction
         * is done in Agora's submitTransaction operation.
         */

        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        sut.whitelistTransaction(
            KinTransactionWhitelistingApi.WhitelistTransactionRequest("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")
        ) {
            assertTrue(expectedKinTransaction.bytesValue.contentEquals(Base64.decodeBase64(it.base64EncodedWhitelistedTransactionEnvelopeBytes)!!))
        }
    }

    @Test
    fun getTransactionHistory() {
        fakeServer = object : TransactionGrpc.TransactionImplBase() {
            override fun getHistory(
                request: TransactionService.GetHistoryRequest?,
                responseObserver: StreamObserver<TransactionService.GetHistoryResponse>?
            ) {
                responseObserver?.onNext(
                    TransactionService.GetHistoryResponse.newBuilder()
                        .setResult(TransactionService.GetHistoryResponse.Result.OK)
                        .addItems(
                            TransactionService.HistoryItem.newBuilder()
                                .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")))
                                .setEnvelopeXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")))
                                .build()
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        latchOperationValueCapture<KinTransactionApi.GetTransactionHistoryResponse> { capture ->
            sut.getTransactionHistory(KinTransactionApi.GetTransactionHistoryRequest(account.id)) {
                capture(it)
            }
        }.testValue {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transactions!!.first().bytesValue) }
            assertTrue { it.transactions?.first()?.recordType is KinTransaction.RecordType.Historical }
        }
    }

    @Test
    fun getTransaction() {
        fakeServer = object : TransactionGrpc.TransactionImplBase() {
            override fun getTransaction(
                request: TransactionService.GetTransactionRequest?,
                responseObserver: StreamObserver<TransactionService.GetTransactionResponse>?
            ) {
                responseObserver?.onNext(
                    TransactionService.GetTransactionResponse.newBuilder()
                        .setState(TransactionService.GetTransactionResponse.State.SUCCESS)
                        .setItem(
                            TransactionService.HistoryItem.newBuilder()
                                .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")))
                                .setEnvelopeXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")))
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        latchOperationValueCapture<KinTransactionApi.GetTransactionResponse> { capture ->
            sut.getTransaction(KinTransactionApi.GetTransactionRequest(expectedKinTransaction.transactionHash)) {
                capture(it)
            }
        }.testValue {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transaction!!.bytesValue) }
            assertTrue { it.transaction?.recordType is KinTransaction.RecordType.Historical }
        }
    }

    @Test
    fun submitTransaction() {
        fakeServer = object : TransactionGrpc.TransactionImplBase() {
            override fun submitTransaction(
                request: TransactionService.SubmitTransactionRequest?,
                responseObserver: StreamObserver<TransactionService.SubmitTransactionResponse>?
            ) {
                responseObserver?.onNext(
                    TransactionService.SubmitTransactionResponse.newBuilder()
                        .setResult(TransactionService.SubmitTransactionResponse.Result.OK)
                        .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!))
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        latchOperationValueCapture<KinTransactionApi.SubmitTransactionResponse> { capture ->
            sut.submitTransaction(KinTransactionApi.SubmitTransactionRequest(expectedKinTransaction.bytesValue)) {
                capture(it)
            }
        }.testValue {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transaction!!.bytesValue) }
            assertTrue { it.transaction?.recordType is KinTransaction.RecordType.Acknowledged }
        }
    }
}
