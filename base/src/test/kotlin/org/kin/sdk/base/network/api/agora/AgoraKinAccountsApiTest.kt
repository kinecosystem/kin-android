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
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.common.v3.Model
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.latchOperationValueCapture
import org.kin.sdk.base.tools.test
import org.kin.sdk.base.tools.testValue
import org.kin.stellarfork.codec.Base64
import org.mockito.AdditionalAnswers.delegatesTo
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgoraKinAccountsApiTest {

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

    private lateinit var fakeServer: AccountGrpc.AccountImplBase

    private val serviceImpl: AccountGrpc.AccountImplBase = mock(
        AccountGrpc.AccountImplBase::class.java, delegatesTo<AccountGrpc.AccountImplBase>(
            // By default the client will receive Status.UNIMPLEMENTED for all RPCs.
            object : AccountGrpc.AccountImplBase() {
                override fun createAccount(
                    request: AccountService.CreateAccountRequest?,
                    responseObserver: StreamObserver<AccountService.CreateAccountResponse>?
                ) {
                    fakeServer.createAccount(request, responseObserver)
                }

                override fun getAccountInfo(
                    request: AccountService.GetAccountInfoRequest?,
                    responseObserver: StreamObserver<AccountService.GetAccountInfoResponse>?
                ) {
                    fakeServer.getAccountInfo(request, responseObserver)
                }

                override fun getEvents(
                    request: AccountService.GetEventsRequest?,
                    responseObserver: StreamObserver<AccountService.Events>?
                ) {
                    fakeServer.getEvents(request, responseObserver)
                }
            })
    )

    private lateinit var sut: AgoraKinAccountsApi

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

        sut = AgoraKinAccountsApi(channel, networkEnvironment = NetworkEnvironment.KinStellarTestNetKin3)
    }

    @Test
    fun createAccount() {
        fakeServer = object : AccountGrpc.AccountImplBase() {
            override fun createAccount(
                request: AccountService.CreateAccountRequest?,
                responseObserver: StreamObserver<AccountService.CreateAccountResponse>?
            ) {
                responseObserver?.onNext(
                    AccountService.CreateAccountResponse.newBuilder()
                        .setResult(AccountService.CreateAccountResponse.Result.OK)
                        .setAccountInfo(
                            AccountService.AccountInfo.newBuilder()
                                .setAccountId(
                                    Model.StellarAccountId.newBuilder()
                                        .setValue("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
                                )
                                .setBalance(500000000)
                                .setSequenceNumber(12345)
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        latchOperationValueCapture<KinAccountCreationApi.CreateAccountResponse> { capture ->
            sut.createAccount(KinAccountCreationApi.CreateAccountRequest(account.id)) {
                capture(it)
            }
        }.testValue {
            assertEquals(KinAccountCreationApi.CreateAccountResponse.Result.Ok, it.result)
            assertEquals(account, it.account)
        }
    }

    @Test
    fun getAccount() {
        fakeServer = object : AccountGrpc.AccountImplBase() {
            override fun getAccountInfo(
                request: AccountService.GetAccountInfoRequest?,
                responseObserver: StreamObserver<AccountService.GetAccountInfoResponse>?
            ) {
                responseObserver?.onNext(
                    AccountService.GetAccountInfoResponse.newBuilder()
                        .setResult(AccountService.GetAccountInfoResponse.Result.OK)
                        .setAccountInfo(
                            AccountService.AccountInfo.newBuilder()
                                .setAccountId(
                                    Model.StellarAccountId.newBuilder()
                                        .setValue("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
                                )
                                .setBalance(500000000)
                                .setSequenceNumber(12345)
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        latchOperationValueCapture<KinAccountApi.GetAccountResponse> { capture ->
            sut.getAccount(KinAccountApi.GetAccountRequest(account.id)) {
                capture(it)
            }
        }.testValue {
            assertEquals(KinAccountApi.GetAccountResponse.Result.Ok, it.result)
            assertEquals(account, it.account)
        }
    }

    @Test
    fun streamUnknownEvent() {
        fakeServer = object : AccountGrpc.AccountImplBase() {
            override fun getEvents(
                request: AccountService.GetEventsRequest?,
                responseObserver: StreamObserver<AccountService.Events>?
            ) {
                responseObserver?.onNext(
                    AccountService.Events.newBuilder()
                        .addEvents(AccountService.Event.newBuilder().build())
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        val disposeBag = DisposeBag()

        val o = sut.openEventStream(account.id).test {
            assertTrue { value is AgoraKinAccountsApi.AgoraEvent.UnknownEvent }
        }.disposedBy(disposeBag)

        disposeBag.dispose()

        assertEquals(0, o.listenerCount())
    }

    @Test
    fun streamAccount() {
        fakeServer = object : AccountGrpc.AccountImplBase() {
            override fun getEvents(
                request: AccountService.GetEventsRequest?,
                responseObserver: StreamObserver<AccountService.Events>?
            ) {
                responseObserver?.onNext(
                    AccountService.Events.newBuilder()
                        .addEvents(
                            AccountService.Event.newBuilder()
                                .setAccountUpdateEvent(
                                    AccountService.AccountUpdateEvent.newBuilder()
                                        .setAccountInfo(
                                            AccountService.AccountInfo.newBuilder()
                                                .setAccountId(
                                                    Model.StellarAccountId.newBuilder()
                                                        .setValue("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
                                                )
                                                .setBalance(500000000)
                                                .setSequenceNumber(12345)
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        sut.streamAccount(account.id).test {
            assertEquals(account, value!!)
        }
    }

    @Test
    fun streamNewTransactions() {
        fakeServer = object : AccountGrpc.AccountImplBase() {
            override fun getEvents(
                request: AccountService.GetEventsRequest?,
                responseObserver: StreamObserver<AccountService.Events>?
            ) {
                responseObserver?.onNext(
                    AccountService.Events.newBuilder()
                        .addEvents(
                            AccountService.Event.newBuilder()
                                .setTransactionEvent(
                                    AccountService.TransactionEvent.newBuilder()
                                        .setEnvelopeXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")))
                                        .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")))
                                        .build()
                                )
                                .build()
                        )
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

        sut.streamNewTransactions(account.id).test {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(value!!.bytesValue) }
            assertTrue { value!!.recordType is KinTransaction.RecordType.Acknowledged }
        }
    }
}
