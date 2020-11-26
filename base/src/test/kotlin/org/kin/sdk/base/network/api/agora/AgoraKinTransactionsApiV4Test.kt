package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.stub.StreamObserver
import io.grpc.testing.GrpcCleanupRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.kin.agora.gen.account.v4.AccountGrpc
import org.kin.agora.gen.common.v4.Model
import org.kin.agora.gen.transaction.v4.TransactionGrpc
import org.kin.agora.gen.transaction.v4.TransactionService
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.solana.FixedByteArray32
import org.kin.sdk.base.models.solana.Hash
import org.kin.sdk.base.models.solana.TokenProgram
import org.kin.sdk.base.models.solana.Transaction
import org.kin.sdk.base.models.solana.unmarshal
import org.kin.sdk.base.network.api.KinTransactionApiV4
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

class AgoraKinTransactionsApiV4Test {

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

                override fun getServiceConfig(
                    request: TransactionService.GetServiceConfigRequest?,
                    responseObserver: StreamObserver<TransactionService.GetServiceConfigResponse>?
                ) {
                    fakeServer.getServiceConfig(request, responseObserver)
                }

                override fun getRecentBlockhash(
                    request: TransactionService.GetRecentBlockhashRequest?,
                    responseObserver: StreamObserver<TransactionService.GetRecentBlockhashResponse>?
                ) {
                    fakeServer.getRecentBlockhash(request, responseObserver)
                }

                override fun getMinimumBalanceForRentExemption(
                    request: TransactionService.GetMinimumBalanceForRentExemptionRequest?,
                    responseObserver: StreamObserver<TransactionService.GetMinimumBalanceForRentExemptionResponse>?
                ) {
                    fakeServer.getMinimumBalanceForRentExemption(request, responseObserver)
                }

                override fun getMinimumKinVersion(
                    request: TransactionService.GetMinimumKinVersionRequest?,
                    responseObserver: StreamObserver<TransactionService.GetMinimumKinVersionResponse>?
                ) {
                    fakeServer.getMinimumKinVersion(request, responseObserver)
                }
            })
    )

    private lateinit var sut: AgoraKinTransactionsApiV4

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

        sut = AgoraKinTransactionsApiV4(channel, NetworkEnvironment.KinStellarTestNetKin3)
    }

    @Test
    fun getMinRentExemption() {
        fakeServer = object : TransactionGrpc.TransactionImplBase() {
            override fun getMinimumBalanceForRentExemption(
                request: TransactionService.GetMinimumBalanceForRentExemptionRequest?,
                responseObserver: StreamObserver<TransactionService.GetMinimumBalanceForRentExemptionResponse>?
            ) {
                responseObserver?.onNext(
                    TransactionService.GetMinimumBalanceForRentExemptionResponse.newBuilder()
                        .setLamports(200)
                        .build()
                )

                responseObserver?.onCompleted()
            }
        }

        latchOperationValueCapture<KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse> { capture ->
            sut.getMinimumBalanceForRentExemption(
                KinTransactionApiV4.GetMinimumBalanceForRentExemptionRequest(12)
            ) { capture(it) }
        }.testValue {
            assertEquals(200, it.lamports)
        }
    }

    @Test
    fun getServiceConfig() {
        val token = TestUtils.newPublicKey()
        val subsidizer = TestUtils.newPublicKey()
        fakeServer = object : TransactionGrpc.TransactionImplBase() {
            override fun getServiceConfig(
                request: TransactionService.GetServiceConfigRequest?,
                responseObserver: StreamObserver<TransactionService.GetServiceConfigResponse>?
            ) {
                responseObserver?.onNext(
                    TransactionService.GetServiceConfigResponse.newBuilder()
                        .setToken(token.asKinAccountId().toProtoSolanaAccountId())
                        .setTokenProgram(
                            TokenProgram.PROGRAM_KEY.asKinAccountId().toProtoSolanaAccountId()
                        )
                        .setSubsidizerAccount(subsidizer.asKinAccountId().toProtoSolanaAccountId())
                        .build()
                )

                responseObserver?.onCompleted()
            }
        }

        latchOperationValueCapture<KinTransactionApiV4.GetServiceConfigResponse> { capture ->
            sut.getServiceConfig(
                KinTransactionApiV4.GetServiceConfigRequest
            ) { capture(it) }
        }.testValue {
            assertEquals(token.asKinAccountId(), it.token)
            assertEquals(subsidizer.asKinAccountId(), it.subsidizerAccount)
            assertEquals(TokenProgram.PROGRAM_KEY.asKinAccountId(), it.tokenProgram)
        }
    }

    @Test
    fun getMinKinVersion() {
        fakeServer = object : TransactionGrpc.TransactionImplBase() {
            override fun getMinimumKinVersion(
                request: TransactionService.GetMinimumKinVersionRequest?,
                responseObserver: StreamObserver<TransactionService.GetMinimumKinVersionResponse>?
            ) {
                responseObserver?.onNext(
                    TransactionService.GetMinimumKinVersionResponse.newBuilder()
                        .setVersion(4)
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }
        latchOperationValueCapture<KinTransactionApiV4.GetMiniumumKinVersionResponse> { capture ->
            sut.getMinKinVersion(KinTransactionApiV4.GetMiniumumKinVersionRequest) {
                capture(it)
            }
        }.testValue {
            assertEquals(4, it.version)
            assertEquals(KinTransactionApiV4.GetMiniumumKinVersionResponse.Result.Ok, it.result)
        }
    }

    @Test
    fun getRecentBlockhash() {
        fakeServer = object : TransactionGrpc.TransactionImplBase() {
            override fun getRecentBlockhash(
                request: TransactionService.GetRecentBlockhashRequest?,
                responseObserver: StreamObserver<TransactionService.GetRecentBlockhashResponse>?
            ) {
                responseObserver?.onNext(
                    TransactionService.GetRecentBlockhashResponse.newBuilder()
                        .setBlockhash(
                            Model.Blockhash.newBuilder()
                                .setValue(ByteString.copyFrom(FixedByteArray32().byteArray))
                        )
                        .build()
                )

                responseObserver?.onCompleted()
            }
        }

        latchOperationValueCapture<KinTransactionApiV4.GetRecentBlockHashResponse> { capture ->
            sut.getRecentBlockHash(
                KinTransactionApiV4.GetRecentBlockHashRequest
            ) { capture(it) }
        }.testValue {
            assertEquals(Hash(FixedByteArray32()), it.blockHash)
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
                                .setSolanaTransaction(
                                    Model.Transaction.newBuilder()
                                        .setValue(ByteString.copyFrom(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")))
                                )
                                .build()
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        latchOperationValueCapture<KinTransactionApiV4.GetTransactionHistoryResponse> { capture ->
            sut.getTransactionHistory(KinTransactionApiV4.GetTransactionHistoryRequest(account.id)) {
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
                                .setSolanaTransaction(
                                    Model.Transaction.newBuilder()
                                        .setValue(ByteString.copyFrom(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")))
                                )
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        latchOperationValueCapture<KinTransactionApiV4.GetTransactionResponse> { capture ->
            sut.getTransaction(KinTransactionApiV4.GetTransactionRequest(expectedKinTransaction.transactionHash)) {
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
                        .setSignature(
                            Model.TransactionSignature.newBuilder()
                                .setValue(ByteString.copyFrom(ByteArray(64)))
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        latchOperationValueCapture<KinTransactionApiV4.SubmitTransactionResponse> { capture ->
            sut.submitTransaction(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(
                        expectedKinTransaction.bytesValue
                    )
                )
            ) {
                capture(it)
            }
        }.testValue {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transaction!!.bytesValue) }
            assertTrue { it.transaction?.recordType is KinTransaction.RecordType.Acknowledged }
        }
    }
}
