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
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.base.tools.test
import java.security.Security
import kotlin.test.assertEquals

class HorizonKinAccountContextTest {

    @Rule
    @JvmField
    var tempFolder = TemporaryFolder()

    lateinit var sut: KinAccountContextImpl
    lateinit var sut2: KinAccountContextImpl

    val okHttpClient = OkHttpClient.Builder().build()

    lateinit var lifecycle: DisposeBag

    @Before
    fun setUp() {
        Security.insertProviderAt(Conscrypt.newProvider(), 1);

        lifecycle = DisposeBag()

        val storage = KinFileStorage.Builder(
            tempFolder.root.invariantSeparatorsPath
        )
        sut = KinAccountContext.Builder(
            KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
                .setStorage(storage)
                .build()
        ).createNewAccount()
            .build()

        sut.getAccount().test(timeout = 10) {

        }

        sut2 = KinAccountContext.Builder(
            KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
                .setStorage(storage)
                .build()
        ).createNewAccount()
            .build()

        sut2.getAccount().test(timeout = 10) {

        }

        sut.observeBalance(ObservationMode.Active)
            .add {
                println("---- BALANCE UPDATE --- ${it}")
            }.disposedBy(lifecycle)

        sut.observePayments(ObservationMode.Active).add {
            it.forEach {
                println("---- PAYMENTS UPDATE --- ${it.amount} ")
            }
            println("-----")
        }.disposedBy(lifecycle)
    }

    @Test
    fun sendTransaction() {

        // Send 3 payments from User A -> User B

        sut.sendKinPayments(
            listOf(
                KinPaymentItem(KinAmount(1), sut2.accountId),
                KinPaymentItem(KinAmount(2), sut2.accountId),
                KinPaymentItem(KinAmount(3), sut2.accountId)
            )
        ).test(timeout = 1000) {
            assertEquals(listOf(KinAmount(1), KinAmount(2), KinAmount(3)), value?.map { it.amount })
        }

        // Now send 2 payments From User B -> User A

        sut2.sendKinPayments(
            listOf(
                KinPaymentItem(KinAmount(4), sut.accountId),
                KinPaymentItem(KinAmount(5), sut.accountId)
            )
        ).test(timeout = 1000) {
            assertEquals(listOf(KinAmount(4), KinAmount(5)), value?.map { it.amount })
        }

        val values = mutableListOf<List<String>>()

        sut.observePayments()
            .add {
                println("[USER A] observePayments:")
                val localValues = mutableListOf<String>()
                it.forEach {
                    println(it.amount)
                    localValues += it.amount.toString(0)
                }
                values += localValues
                println("-----")
            }
            .test(2, timeout = 1000) {
                assertEquals(listOf(listOf("3", "2", "1"), listOf("3", "2", "1"), listOf("5", "4", "3", "2", "1")), values)
            }

        println("[USER A] Now Print What's in Storage Now:")

        sut.storage.getStoredTransactions(sut.accountId).map { it!! }.test {
            value?.items?.map { it.asKinPayments().map { it.amount } }
                ?.map {
                    println("storedTxn: $it")
                }
        }

        println("[USER B] Now Print What's in Storage Now:")

        sut2.storage.getStoredTransactions(sut2.accountId).map { it!! }.test {
            value?.items?.map { it.asKinPayments().map { it.amount } }
                ?.map {
                    println("storedTxn: $it")
                }
        }
    }
}
