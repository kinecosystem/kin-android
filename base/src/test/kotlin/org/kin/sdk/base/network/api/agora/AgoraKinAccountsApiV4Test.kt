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
import org.kin.agora.gen.account.v4.AccountService
import org.kin.agora.gen.common.v4.Model
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.solana.FixedByteArray32
import org.kin.sdk.base.models.solana.Hash
import org.kin.sdk.base.models.solana.SystemProgram
import org.kin.sdk.base.models.solana.TokenProgram
import org.kin.sdk.base.models.solana.Transaction
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toSigningKeyPair
import org.kin.sdk.base.network.api.KinAccountApiV4
import org.kin.sdk.base.network.api.KinAccountCreationApiV4
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

class AgoraKinAccountsApiV4Test {

    companion object {
        val account = KinAccount(
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key,
            balance = KinBalance(KinAmount(5000)),
            status = KinAccount.Status.Registered(0)
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

                override fun resolveTokenAccounts(
                    request: AccountService.ResolveTokenAccountsRequest?,
                    responseObserver: StreamObserver<AccountService.ResolveTokenAccountsResponse>?
                ) {
                    fakeServer.resolveTokenAccounts(request, responseObserver)
                }
            })
    )

    private lateinit var sut: AgoraKinAccountApiV4
    private lateinit var sut2: AgoraKinAccountCreationApiV4

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

        sut =
            AgoraKinAccountApiV4(channel, networkEnvironment = NetworkEnvironment.KinStellarTestNetKin3)
        sut2 = AgoraKinAccountCreationApiV4(channel)
    }

    @Test
    fun createAccount() {

        val account = TestUtils.newSigningKinAccount()
            .copy(balance = KinBalance(KinAmount(5000)), status = KinAccount.Status.Registered(0))

        val subsidizerId =
            TestUtils.fromAccountId("GDXSRIW5MB2FZ3XM6D6RKVVVO65JLQETBRUA6XQ4EYYHKRPHIFNWCVNC").id
        val minRentExemptionInLamports = 555L // would actually be size dependant in real life
        val mintKey = TokenProgram.PROGRAM_KEY
        val recentBlockHash = Hash(FixedByteArray32())

        val subsidizer: Key.PublicKey = subsidizerId.toKeyPair().asPublicKey()
        val accountPub: Key.PublicKey = account.id.toKeyPair().asPublicKey()

        val transaction = Transaction.newTransaction(
            subsidizer,
            SystemProgram.CreateAccount(
                subsidizer = subsidizer,
                address = accountPub,
                owner = TokenProgram.PROGRAM_KEY,
                lamports = minRentExemptionInLamports,
                size = TokenProgram.accountSize
            ).instruction,
            TokenProgram.InitializeAccount(
                account = accountPub,
                mint = mintKey,
                owner = account.key.asPublicKey(),
                TokenProgram.PROGRAM_KEY
            ).instruction
        ).copyAndSetRecentBlockhash(recentBlockHash)
            .copyAndSign(account.toSigningKeyPair().asPrivateKey())

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
                                    Model.SolanaAccountId.newBuilder()
                                        .setValue(ByteString.copyFrom(account.id.value))
                                )
                                .setBalance(500000000)
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        latchOperationValueCapture<KinAccountCreationApiV4.CreateAccountResponse> { capture ->
            sut2.createAccount(KinAccountCreationApiV4.CreateAccountRequest(transaction)) {
                capture(it)
            }
        }.testValue {
            assertEquals(KinAccountCreationApiV4.CreateAccountResponse.Result.Ok, it.result)
            assertEquals(account.copy(key = account.key.asPublicKey()), it.account)
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
                                    Model.SolanaAccountId.newBuilder()
                                        .setValue(ByteString.copyFrom(KinAccount.Id("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").value))
                                )
                                .setBalance(500000000)
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }

        latchOperationValueCapture<KinAccountApiV4.GetAccountResponse> { capture ->
            sut.getAccount(KinAccountApiV4.GetAccountRequest(account.id)) {
                capture(it)
            }
        }.testValue {
            assertEquals(KinAccountApiV4.GetAccountResponse.Result.Ok, it.result)
            assertEquals(account, it.account)
        }
    }

    @Test
    fun resolveTokenAccounts() {
        fakeServer = object : AccountGrpc.AccountImplBase() {
            override fun resolveTokenAccounts(
                request: AccountService.ResolveTokenAccountsRequest?,
                responseObserver: StreamObserver<AccountService.ResolveTokenAccountsResponse>?
            ) {
                responseObserver?.onNext(
                    AccountService.ResolveTokenAccountsResponse.newBuilder()
                        .addAllTokenAccounts(
                            listOf(
                                Model.SolanaAccountId.newBuilder()
                                    .setValue(ByteString.copyFrom(KinAccount.Id("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").value))
                                    .build()
                            )
                        )
                        .build()
                )
                responseObserver?.onCompleted()
            }
        }
        latchOperationValueCapture<KinAccountApiV4.ResolveTokenAccountsResponse> { capture ->
            sut.resolveTokenAcounts(
                KinAccountApiV4.ResolveTokenAccountsRequest(
                    KinAccount.Id("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
                )
            ) { capture(it) }
        }.testValue {
            assertEquals(account.key, it.accounts.first())
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
            assertTrue { value is AgoraKinAccountApiV4.AgoraEvent.UnknownEvent }
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
                                                    Model.SolanaAccountId.newBuilder()
                                                        .setValue(
                                                            ByteString.copyFrom(
                                                                KinAccount.Id(
                                                                    "GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ"
                                                                ).value
                                                            )
                                                        )
                                                )
                                                .setBalance(500000000)
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
                                        .setTransaction(
                                            Model.Transaction.newBuilder()
                                                .setValue(ByteString.copyFrom(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")))
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

        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
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
