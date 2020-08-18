package org.kin.sdk.base.stellar.services

import okhttp3.OkHttpClient
import org.conscrypt.Conscrypt
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinAccountContextImpl
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.ObservationMode
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinPaymentItem
import org.kin.sdk.base.models.asKinPayments
import org.kin.sdk.base.models.AgoraMemo
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.base.tools.test
import java.security.Security
import kotlin.test.assertEquals
import org.kin.sdk.base.tools.intToByteArray
import org.kin.sdk.base.tools.toByteArray
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.kin.sdk.base.network.api.FriendBotApi
import org.kin.sdk.base.tools.latchOperation
import org.kin.sdk.base.network.api.KinAccountCreationApi

class AgoraKinAccountContextTest {

    @Rule
    @JvmField
    var tempFolder = TemporaryFolder()

    lateinit var sut: KinAccountContextImpl
    lateinit var sut2: KinAccountContextImpl

    val okHttpClient = OkHttpClient.Builder().build()
    private lateinit var friendBotApi: FriendBotApi

    lateinit var lifecycle: DisposeBag

    @Before
    fun setUp() {
        Security.insertProviderAt(Conscrypt.newProvider(), 0);

        lifecycle = DisposeBag()

        friendBotApi = FriendBotApi(okHttpClient)

        val storage = KinFileStorage.Builder(
            tempFolder.root.invariantSeparatorsPath
        )
        sut = KinAccountContext.Builder(
            KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNet)
                .setStorage(storage)
                .build()
        ).createNewAccount()
            .build()

        latchOperation(timeoutSeconds = 10) { latch ->
            sut.getAccount().test(timeout = 10) {
                val request = KinAccountCreationApi.CreateAccountRequest(value!!.id)
                friendBotApi.fundAccount(request) {
                    latch.countDown()
                }
            }
        }


        sut2 = KinAccountContext.Builder(
            KinEnvironment.Agora.Builder(NetworkEnvironment.KinStellarTestNet)
                .setStorage(storage)
                .build()
        ).createNewAccount()
            .build()

        sut2.getAccount().test(timeout = 10) {

        }
    }

    @Test
    fun sendTransaction_withAgoraMemo() {

        sut.sendKinPayment(
            KinAmount(1),
            sut2.accountId,
            AgoraMemo.Builder(AgoraMemo.TEST_APP_ID)
                .setTransactionType(AgoraMemo.TransactionType.Spend)
                .setForeignKey(UUID.randomUUID().toByteArray() + UUID.randomUUID().toByteArray())
                .build()
                .toKinMemo()

        ).test(timeout = 1000) {
            assertEquals(KinAmount(1), value?.amount)
        }
    }
}
