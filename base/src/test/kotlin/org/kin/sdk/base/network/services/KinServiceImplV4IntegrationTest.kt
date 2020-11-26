//package org.kin.sdk.base.network.services
//
//import io.grpc.ManagedChannel
//import io.grpc.netty.NettyChannelBuilder
//import org.conscrypt.Conscrypt
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.kin.agora.gen.airdrop.v4.AirdropGrpc
//import org.kin.agora.gen.airdrop.v4.AirdropService
//import org.kin.agora.gen.common.v4.Model
//import org.kin.sdk.base.KinAccountContext
//import org.kin.sdk.base.KinEnvironment
//import org.kin.sdk.base.models.AccountSpec
//import org.kin.sdk.base.models.AppIdx
//import org.kin.sdk.base.models.AppInfo
//import org.kin.sdk.base.models.AppUserCreds
//import org.kin.sdk.base.models.Key
//import org.kin.sdk.base.models.KinAccount
//import org.kin.sdk.base.models.KinAmount
//import org.kin.sdk.base.models.KinPaymentItem
//import org.kin.sdk.base.models.QuarkAmount
//import org.kin.sdk.base.models.asKinAccountId
//import org.kin.sdk.base.models.toKin
//import org.kin.sdk.base.models.toQuarks
//import org.kin.sdk.base.network.api.KinAccountApiV4
//import org.kin.sdk.base.network.api.KinAccountCreationApiV4
//import org.kin.sdk.base.network.api.KinStreamingApiV4
//import org.kin.sdk.base.network.api.KinTransactionApiV4
//import org.kin.sdk.base.network.api.agora.AgoraKinAccountApiV4
//import org.kin.sdk.base.network.api.agora.AgoraKinAccountCreationApiV4
//import org.kin.sdk.base.network.api.agora.AgoraKinTransactionsApiV4
//import org.kin.sdk.base.network.api.agora.GrpcApi
//import org.kin.sdk.base.network.api.agora.LoggingInterceptor
//import org.kin.sdk.base.network.api.agora.UpgradeApiV4Interceptor
//import org.kin.sdk.base.network.api.agora.UserAgentInterceptor
//import org.kin.sdk.base.network.api.agora.toProtoSolanaAccountId
//import org.kin.sdk.base.stellar.models.ApiConfig
//import org.kin.sdk.base.stellar.models.NetworkEnvironment
//import org.kin.sdk.base.storage.KinFileStorage
//import org.kin.sdk.base.storage.Storage
//import org.kin.sdk.base.tools.Base58
//import org.kin.sdk.base.tools.KinLoggerFactory
//import org.kin.sdk.base.tools.KinTestLoggerFactoryImpl
//import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
//import org.kin.sdk.base.tools.Promise
//import org.kin.sdk.base.tools.PromisedCallback
//import org.kin.sdk.base.tools.latchOperation
//import org.kin.sdk.base.tools.test
//import org.kin.stellarfork.KeyPair
//import java.security.Security
//import java.util.concurrent.Executors
//import kotlin.math.abs
//import kotlin.math.floor
//import kotlin.random.Random
//
//class KinServiceImplV4IntegrationTest {
//
//    val airdropAccount =
////        Key.PublicKey(Base58.decode(  "DemXVWQ9DXYsGFpmjFXxki3PE1i3VoHQtqxXQFx38pmU"))
//    Key.PublicKey(Base58.decode(  "Gd1wVb3ioFZgWGadq5sEoLPQnRNFcpcprNeazY3QsTRf"))
//
//    lateinit var logger: KinLoggerFactory
//    lateinit var storage: Storage
//    lateinit var channel: ManagedChannel
//
//    lateinit var accountCreationApi: KinAccountCreationApiV4
//    lateinit var accountApi: KinAccountApiV4
//    lateinit var transactionApi: KinTransactionApiV4
//    lateinit var streamingApi: KinStreamingApiV4
//
//    lateinit var sut: KinService
//
//    @Before
//    fun setUp() {
//
//        Security.insertProviderAt(Conscrypt.newProvider(), 0);
//
//        val networkEnvironment = ApiConfig.TestNetAgora.networkEnv
//        logger = KinTestLoggerFactoryImpl(true)
//        storage = KinFileStorage("temp", networkEnvironment)
//        channel = ApiConfig.TestNetAgora.asManagedChannel()
//
//        with(AgoraKinAccountApiV4(channel, networkEnvironment)) {
//            accountApi = this
//            streamingApi = this
//        }
//        transactionApi = AgoraKinTransactionsApiV4(channel, networkEnvironment)
//        accountCreationApi = AgoraKinAccountCreationApiV4(channel)
//
//        storage.deleteAllStorage().resolve()
//
//        sut = KinServiceImplV4(
//            networkEnvironment,
//            NetworkOperationsHandlerImpl(logger = logger),
//            accountApi,
//            transactionApi,
//            streamingApi,
//            accountCreationApi,
//            logger
//        )
//    }
//
//    private fun ApiConfig.asManagedChannel(): ManagedChannel =
//        NettyChannelBuilder.forAddress(networkEndpoint, tlsPort)
//            .intercept(
//                *listOf(
//                    UserAgentInterceptor(storage),
//                    LoggingInterceptor(logger)
//                ).toTypedArray()
//            )
//            .useTransportSecurity()
//            .build()
//
//    private fun ApiConfig.asManagedChannelStartUpgrade(): ManagedChannel =
//        NettyChannelBuilder.forAddress(networkEndpoint, tlsPort)
//            .intercept(
//                *listOf(
//                    UpgradeApiV4Interceptor(),
//                    UserAgentInterceptor(storage),
//                    LoggingInterceptor(logger)
//                ).toTypedArray()
//            )
//            .useTransportSecurity()
//            .build()
//
//    data class TestTimes(val p50: Float, val p95: Float, val p99: Float)
//
//    fun runTest(
//        times: Int,
//        test: (onCompleted: (value: Any?) -> Unit) -> Unit
//    ): Promise<TestTimes> {
//        return Promise.create { resolve, reject ->
//            val runs = mutableListOf<Float>()
//
//            for (i in 0 until times) {
//                val startTime = System.nanoTime()
//
//                latchOperation(timeoutSeconds = 9000) { latch ->
//                    test {
//                        latch.countDown()
//
//                        if (it != null) {
//                            val endTime = System.nanoTime()
//                            val totalTime = endTime - startTime
//
//                            runs.add(totalTime / 1000000000.0f)
//
//                            println("Test[$i] ${runs.last()}")
//                        } else {
//                            println("Test[$i] failed! $it")
//                        }
//                    }
//                }
//            }
//
//            val p50 = runs.sortedDescending()
//                .asReversed()[floor(0.50f * runs.count()).toInt()]
//            val p95 = runs.sortedDescending()
//                .asReversed()[floor(0.95f * runs.count()).toInt()]
//            val p99 = runs.sortedDescending()
//                .asReversed()[floor(0.99f * runs.count()).toInt()]
//
//            println("runs: $runs")
//            resolve(TestTimes(p50, p95, p99))
//        }
//    }
//
////    @Test
////    fun speedTest() {
////        speedTestInternal_kinAccountContext()
////    }
////
////    fun speedTestInternal_kinAccountContext() {
////        val context = KinAccountContext.Builder(
////            KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNet)
////                .setMinApiVersion(4)
////                .setManagedChannel(ApiConfig.TestNetAgora.asManagedChannel())
////                .setAppInfoProvider(object : AppInfoProvider {
////                    override val appInfo: AppInfo by lazy {
////                        AppInfo(
////                            AppIdx(0),
////                            airdropAccount.asKinAccountId(),
////                            "TestApp",
////                            0
////                        )
////                    }
////
////                    override fun getPassthroughAppUserCredentials(): AppUserCreds {
////                        return AppUserCreds("abcd", "1212")
////                    }
////                })
////                .setStorage(storage)
////        ).createNewAccount()
////            .build()
////
////        println("Creating Account...")
////        val signer = context.getAccount()
////            .map { it.key as Key.PrivateKey }
////            .test(60) {
////                println("Account Created ${value?.asKinAccountId()}")
////            }
////
////        println("Airdropping 1Kin...")
////        context.service.testService.fundAccount(context.accountId).test(timeout = 60) {
////            println("Airdrop Complete")
//////            sleepFor(5)
////        }
////
////        runTest(10) { onCompleted ->
////            context.sendKinPayments(
////                payments = listOf(
////                    KinPaymentItem(
////                        randomQuarkAmount(1000).toKin(),
////                        airdropAccount.asKinAccountId()
////                    )
////                )
////            ).then({ onCompleted(it) }, { onCompleted(it) })
////        }.test(60) {
////            println("$value")
////        }
////    }
//
//    @After
//    fun cleanup() {
//        storage.deleteAllStorage().resolve()
//    }
//
//    @Test
//    fun testMigration() {
//        val contextV3 = KinAccountContext.Builder(
//            KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
//                .setMinApiVersion(3)
//                .setManagedChannel(ApiConfig.TestNetAgora.asManagedChannel())
//                .setAppInfoProvider(object : AppInfoProvider {
//                    override val appInfo: AppInfo by lazy {
//                        AppInfo(
//                            AppIdx(0),
//                            airdropAccount.asKinAccountId(),
//                            "TestApp",
//                            0
//                        )
//                    }
//
//                    override fun getPassthroughAppUserCredentials(): AppUserCreds {
//                        return AppUserCreds("abcd", "1212")
//                    }
//                })
//                .setStorage(storage)
//        ).createNewAccount()
//            .build()
//
//        println("Creating Account...")
//        val signer = contextV3.getAccount()
//            .map { it.key as Key.PrivateKey }
//            .test(60) {
//                println("Account Created ${value?.asKinAccountId()}")
//            }
//
//        println("Airdropping with FriendBot 10K Kin...")
//        contextV3.service.testService.fundAccount(contextV3.accountId).test(timeout = 60) {
//            if (error == null) {
//                println("Airdrop Complete")
//            } else {
//                println("Airdrio Failed: $error")
//            }
//        }
//
//        val contextV4Upgrade = KinAccountContext.Builder(
//            KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNetKin3)
//                .setMinApiVersion(3)
//                .setManagedChannel(ApiConfig.TestNetAgora.asManagedChannelStartUpgrade())
//                .setAppInfoProvider(object : AppInfoProvider {
//                    override val appInfo: AppInfo by lazy {
//                        AppInfo(
//                            AppIdx(0),
//                            airdropAccount.asKinAccountId(),
//                            "TestApp",
//                            0
//                        )
//                    }
//
//                    override fun getPassthroughAppUserCredentials(): AppUserCreds {
//                        return AppUserCreds("abcd", "1212")
//                    }
//                })
//                .setStorage(storage)
//        ).useExistingAccount(contextV3.accountId)
//            .build()
//
////        contextV4Upgrade.sendKinPayments(
////            payments = listOf(
////                KinPaymentItem(
////                    randomQuarkAmount(100).toKin(),
////                    airdropAccount.asKinAccountId(),
////                )
////            ),
////            destinationAccountSpec = AccountSpec.Exact
////        ).test(60) {
////            println("$value")
////        }
//
//
////        sleepFor(30)
////
////        println("Fetching History...")
////        sut.getLatestTransactions(contextV4Upgrade.accountId).test(timeout = 200) {
////            if (error != null) {
////                println("History Failed: $error")
////            } else {
////                println("Transactions: ${value}")
////            }
////        }
//
//        runTest(10) { onCompleted ->
//            contextV4Upgrade.sendKinPayments(
//                payments = listOf(
//                    KinPaymentItem(
//                        randomQuarkAmount(100).toKin(),
//                        airdropAccount.asKinAccountId(),
//                    )
//                ),
//                destinationAccountSpec = AccountSpec.Exact
//            ).then({ onCompleted(it) }, { onCompleted(it) })
//        }.test(300) {
//            println("$value")
//        }
//    }
//
//
//    fun randomQuarkAmount(lessThan: Long = 100): QuarkAmount =
//        QuarkAmount(abs(Random(System.currentTimeMillis()).nextLong() % lessThan))
//
////    @Test
////    fun speedTestInternal_directService() {
////
////        val signer = Key.PrivateKey.random()
////
////        println("Account Private Seed: ${signer.stellarBase32Encode()}")
////
////        println("Creating Account...")
////        sut.createAccount(signer.asKinAccountId(), signer).test(timeout = 60) {
////            println("Created $value Successfully!")
////        }
//////        sleepFor(5)
////
////        println("Airdropping 1Kin...")
////        airdrop(signer.asKinAccountId()).test(timeout = 30) {
////            println("Airdrop Complete")
//////            sleepFor(5)
////        }
////
////        println("Testing resolveTokenAcounts:")
////        runTest(10) { onCompleted ->
////            accountApi.resolveTokenAcounts(KinAccountApiV4.ResolveTokenAccountsRequest(signer.asKinAccountId())) {
////                onCompleted(it)
////            }
////        }.test {
////            println("$value")
////        }
////
////        println("Testing getMinimumBalanceForRentExemption:")
////        runTest(10) { onCompleted ->
////            transactionApi.getMinimumBalanceForRentExemption(
////                KinTransactionApiV4.GetMinimumBalanceForRentExemptionRequest(
////                    5
////                )
////            ) {
////                onCompleted(it)
////            }
////        }.test {
////            println("$value")
////        }
////
////        println("Testing getRecentBlockHash:")
////        runTest(10) { onCompleted ->
////            transactionApi.getRecentBlockHash {
////                onCompleted(it)
////            }
////        }.test {
////            println("$value")
////        }
////
////        println("Testing getServiceConfig:")
////        runTest(10) { onCompleted ->
////            transactionApi.getServiceConfig {
////                onCompleted(it)
////            }
////        }.test {
////            println("$value")
////        }
//////
//////        println("Testing Just Building & Signing...")
//////        runTest(10) { onCompleted ->
//////            sut.buildAndSignTransaction(
//////                KinAccount(signer),
//////                listOf(KinPaymentItem(KinAmount(1), airdropAccount.asKinAccountId())),
//////                KinMemo.NONE,
//////                QuarkAmount(0)
//////            ).then ({ onCompleted(it) }, { onCompleted(null)} )
//////        }.test {
//////            println("$value")
//////        }
////
////        val executorService = Executors.newSingleThreadExecutor()
////
////        println("Testing Building & Signing & Submitting Transaction...")
////        runTest(10) { onCompleted ->
////            sut.buildAndSignTransaction(
////                signer,
////                signer.asPublicKey(),
////                0,
////                listOf(
////                    KinPaymentItem(
////                        randomQuarkAmount(100).toKin(),
////                        airdropAccount.asKinAccountId()
////                    )
////                ),
////                KinMemo.NONE,
////                QuarkAmount(0)
////            ).flatMap { sut.submitTransaction(it) }
////                .workOn(executorService)
////                .then({ onCompleted(it) }, { onCompleted(it) })
////        }.test(60) {
////            println("$value")
////        }
////    }
//
//    fun sleepFor(seconds: Long) {
//        println("Waiting $seconds Seconds...")
//        Thread.sleep(seconds * 1000)
//    }
//
//    fun airdrop(kinAccountId: KinAccount.Id): Promise<KinAmount> {
//        return Promise.create<KinAmount> { resolve, reject ->
//            object : GrpcApi(channel) {
//                init {
//                    with(AirdropGrpc.newStub(channel)) {
//                        val request = AirdropService.RequestAirdropRequest.newBuilder()
//                            .setAccountId(kinAccountId.toProtoSolanaAccountId())
//                            .setQuarks(KinAmount(9).toQuarks().value)
//                            .setCommitment(Model.Commitment.SINGLE)
//                            .build()
//                        this::requestAirdrop.callAsPromisedCallback(request, PromisedCallback({
//                            resolve(QuarkAmount(request.quarks).toKin())
//                        }, {
//                            reject(it)
//                        }))
//                    }
//                }
//            }
//        }
//    }
//
////    @Test
////    fun integrationTest() {
////
////        val signer = Key.PrivateKey.random()
////        val airdropAccount =
////            Key.PublicKey(Base58.decode("DemXVWQ9DXYsGFpmjFXxki3PE1i3VoHQtqxXQFx38pmU"))
////
////        println("Account Private Seed: ${signer.encode()}")
////
////        println("Creating Account...")
////        sut.createAccount(signer.asKinAccountId(), signer).test(timeout = 15) {
////            println("Created $value Successfully!")
////        }
////
////        // Airdrop
////        Promise.create<KinAmount> { resolve, reject ->
////            object : GrpcApi(channel) {
////                init {
////                    with(AirdropGrpc.newStub(channel)) {
////                        val request = AirdropService.RequestAirdropRequest.newBuilder()
////                            .setAccountId(signer.asKinAccountId().toProtoSolanaAccountId())
////                            .setQuarks(KinAmount(1).toQuarks().value)
////                            .setCommitment(Model.Commitment.SINGLE)
////                            .build()
////                        this::requestAirdrop.callAsPromisedCallback(request, PromisedCallback({
////                            resolve(QuarkAmount(request.quarks).toKin())
////                        }, {
////                            reject(it)
////                        }))
////                    }
////                }
////            }
////        }.test(timeout = 15) {
////            println(
////                "Airdrop Successful! ${value} Kin Airdropped into ${
////                    signer.asPublicKey().encode()
////                }"
////            )
////        }
////
////
//////        Thread.sleep(30000)
//////
////        println("Fetching Updated Account Info...")
////        sut.getAccount(signer.asKinAccountId()).test(timeout = 15) {
////            println("Got Account ${value}")
////        }
////
////        println("Building & Signing...")
////        sut.buildAndSignTransaction(
////            signer,
////            signer.asPublicKey(),
////            0,
////            listOf(KinPaymentItem(QuarkAmount(1).toKin(), airdropAccount.asKinAccountId())),
////            KinMemo.NONE,
////            QuarkAmount(0)
////        )
////            .doOnResolved { println("Sending Transaction... ${it.bytesValue.toHexString()}") }
////            .flatMap { sut.submitTransaction(it) }
////            .test(timeout = 20) {
////                if (error != null) {
////                    println("Transaction Failed: $error")
////                } else {
////                    println("Completed Transaction! $value")
////
////                    println("Fetching Transaction...")
////                    val txnHash = value?.transactionHash
////                    sut.getTransaction(value!!.transactionHash).test {
////                        if (error != null) {
////                            println("Failed to get Transaction: $txnHash")
////                        } else {
////                            println("Got Transaction: $value")
////                        }
////                    }
////                }
////            }
////
////        println("waiting 35s...")
////        Thread.sleep(35000)
////
////        println("Fetching History...")
////        sut.getLatestTransactions(signer.asKinAccountId()).test(timeout = 200) {
////            if (error != null) {
////                println("History Failed: $error")
////            } else {
////                println("Transactions: ${value}")
////            }
////        }
////    }
//}
