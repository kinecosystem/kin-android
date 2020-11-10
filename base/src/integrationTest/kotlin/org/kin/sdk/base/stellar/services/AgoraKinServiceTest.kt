package org.kin.sdk.base

import io.grpc.ManagedChannel
import io.grpc.netty.NettyChannelBuilder
import okhttp3.OkHttpClient
import org.conscrypt.Conscrypt
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.getNetwork
import org.kin.sdk.base.models.toAccount
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toKinTransaction
import org.kin.sdk.base.models.toSigningKeyPair
import org.kin.sdk.base.network.api.proto.AgoraKinAccountsApi
import org.kin.sdk.base.network.api.proto.AgoraKinTransactionsApi
import org.kin.sdk.base.network.api.FriendBotApi
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.latchOperation
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.stellarfork.AssetTypeNative
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.Memo
import org.kin.stellarfork.PaymentOperation
import org.kin.stellarfork.Transaction
import java.security.Security
import java.util.Arrays
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AgoraKinServiceTest {

    companion object {
        val newPrivateKey = KeyPair.random().asPrivateKey()
        val newSigningAccount = KinAccount(newPrivateKey)

        val DEFAULT_FEE_IN_QUARKS = QuarkAmount(100)
        val DEFAULT_WHITELISTED_FEE_IN_QUARKS = DEFAULT_FEE_IN_QUARKS //QuarkAmount(0)

        var signgingRegisteredAccount: KinAccount? = null
    }

    private lateinit var sutAccountApi: KinAccountApi
    private lateinit var sutTransactionsApi: KinTransactionApi
    private lateinit var sutAccountCreationApi: KinAccountCreationApi
    private lateinit var sutTransactionWhitelistingApi: KinTransactionWhitelistingApi
    private lateinit var friendBotApi: FriendBotApi

    private val networkEnvironment = NetworkEnvironment.KinStellarTestNet

    @Before
    fun setUp() {
        Security.insertProviderAt(Conscrypt.newProvider(), 0);
        val okHttpClient = OkHttpClient.Builder().build()

        AgoraKinAccountsApi(ApiConfig.TestNetAgora.asManagedChannel()).also {
            sutAccountApi = it
            sutAccountCreationApi = it
        }

        friendBotApi = FriendBotApi(okHttpClient)

        AgoraKinTransactionsApi(
            ApiConfig.TestNetAgora.asManagedChannel(),
            networkEnvironment
        ).also {
            sutTransactionsApi = it
            sutTransactionWhitelistingApi = it
        }
    }

    fun ApiConfig.asManagedChannel(): ManagedChannel =
        NettyChannelBuilder.forAddress(networkEndpoint, tlsPort)
            .useTransportSecurity()
            .build()


    @Test
    fun `1 create account success`() {
        var response: KinAccountCreationApi.CreateAccountResponse? = null

        latchOperation(timeoutSeconds = 15) { latch ->
            val request = KinAccountCreationApi.CreateAccountRequest(newSigningAccount.id)
            sutAccountCreationApi.createAccount(request) {
                friendBotApi.fundAccount(request) {
                    response = it
                    latch.countDown()
                }
            }
        }

        assertEquals(KinAccountCreationApi.CreateAccountResponse.Result.Ok, response?.result)
        assertEquals(newSigningAccount.id, response?.account?.id)
        assertNotNull(response?.account?.status as? KinAccount.Status.Registered)

        signgingRegisteredAccount = KinAccount(
            newSigningAccount.key,
            newSigningAccount.id,
            status = response?.account!!.status
        )
    }

    @Test
    fun `2 get min fee success`() {
        var response: KinTransactionApi.GetMinFeeForTransactionResponse? = null

        latchOperation { latch ->
            sutTransactionsApi.getTransactionMinFee {
                response = it
                latch.countDown()
            }
        }

        assertEquals(
            KinTransactionApi.GetMinFeeForTransactionResponse.Result.Ok,
            response?.result
        )
        assertEquals(
            when (sutTransactionsApi) {
                is AgoraKinTransactionsApi -> DEFAULT_WHITELISTED_FEE_IN_QUARKS
                else -> DEFAULT_FEE_IN_QUARKS
            }, response?.minFee
        )
    }

    @Test
    fun `3 test submit transaction success`() {
        var response: KinTransactionApi.SubmitTransactionResponse? = null

        val privateKeyString =
            String(KeyPair.fromSecretSeed(signgingRegisteredAccount!!.key.value).secretSeed)
        println("sender secret: $privateKeyString")
        println("sender public: ${KeyPair.fromSecretSeed(signgingRegisteredAccount!!.key.value).accountId}")

        val sourceKeyPair = signgingRegisteredAccount!!.toSigningKeyPair()
        val destinationKeyPair = signgingRegisteredAccount!!.id.toKeyPair()


        val transaction: KinTransaction = Transaction.Builder(
            signgingRegisteredAccount!!.toAccount(),
            ApiConfig.TestNetHorizon.networkEnv.getNetwork()
        )
            .apply {
                addOperation(
                    PaymentOperation.Builder(
                        destinationKeyPair,
                        AssetTypeNative,
                        KinAmount(25).toString()
                    ).build()
                )
                addFee(DEFAULT_FEE_IN_QUARKS.value.toInt())
                addMemo(Memo.none())
            }
            .build()
            .apply { sign(sourceKeyPair) }
            .toKinTransaction(networkEnvironment)


        latchOperation(timeoutSeconds = 10) { latch ->
            sutTransactionsApi.submitTransaction(
                KinTransactionApi.SubmitTransactionRequest(
                    transaction.bytesValue
                )
            ) {
                response = it
                latch.countDown()
            }
        }

        assertEquals(KinTransactionApi.SubmitTransactionResponse.Result.Ok, response?.result)
        assertTrue(
            Arrays.equals(
                transaction.bytesValue,
                response?.transaction?.bytesValue
            )
        )

    }

    @Test
    fun `4 test fetch transaction history success`() {
        var response: KinTransactionApi.GetTransactionHistoryResponse? = null

        latchOperation(timeoutSeconds = 10) { latch ->
            sutTransactionsApi.getTransactionHistory(
                KinTransactionApi.GetTransactionHistoryRequest(
                    newSigningAccount.id
                )
            ) {
                response = it
                latch.countDown()
            }
        }

        assertEquals(
            KinTransactionApi.GetTransactionHistoryResponse.Result.Ok,
            response?.result
        )
    }

    @Test
    fun `5 test fetch transaction history not found`() {
        val newAccountId = KinAccount(KeyPair.random().asPrivateKey()).id

        var response: KinTransactionApi.GetTransactionHistoryResponse? = null

        latchOperation(timeoutSeconds = 10) { latch ->
            sutTransactionsApi.getTransactionHistory(
                KinTransactionApi.GetTransactionHistoryRequest(newAccountId)
            ) {
                response = it
                latch.countDown()
            }
        }

        assertEquals(
            KinTransactionApi.GetTransactionHistoryResponse.Result.NotFound,
            response?.result
        )
    }


}
