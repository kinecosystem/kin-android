package org.kin.sdk.base.network.services

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.models.toSigningKeyPair
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.test
import org.kin.stellarfork.codec.Base64
import kotlin.test.assertTrue

class KinServiceWrapperTest {

    companion object {
        val account = TestUtils.newSigningKinAccount().copy(status = KinAccount.Status.Registered(0))
        val accountPrivateKey = account.toSigningKeyPair().asPrivateKey()
        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )
        val pagingToken = KinTransaction.PagingToken("1234")
    }

    lateinit var v3: KinService
    lateinit var v4: KinService

    lateinit var sut: KinService

    @Before
    fun setUp() {
        v3 = mock {
            on { createAccount(eq(account.id), eq(accountPrivateKey)) } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
            on { getAccount(eq(account.id)) } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
            on { getLatestTransactions(eq(account.id)) } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
            on { getTransactionPage(
                kinAccountId = eq(account.id),
                pagingToken = eq(pagingToken),
                order = eq(KinService.Order.Descending)
            ) } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
            on { getTransaction(eq(expectedKinTransaction.transactionHash)) } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
            on { canWhitelistTransactions() } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
            on { getMinFee() } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
            on { buildAndSignTransaction(eq(account.key as Key.PrivateKey),eq(account.key.asPublicKey()), eq((account.status as KinAccount.Status.Registered).sequence), eq(emptyList()), eq(KinMemo.NONE), eq(QuarkAmount(0))) } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
            on { submitTransaction(eq(expectedKinTransaction)) } doReturn Promise.error(KinService.FatalError.SDKUpgradeRequired)
        }
        v4 = mock {
            on { createAccount(eq(account.id), eq(accountPrivateKey)) } doReturn Promise.of(account)
            on { getAccount(any()) } doReturn Promise.of(account)
            on { getLatestTransactions(eq(account.id)) } doReturn Promise.of(listOf(expectedKinTransaction))
            on { getTransactionPage(
                kinAccountId = eq(account.id),
                pagingToken = eq(pagingToken),
                order = eq(KinService.Order.Descending)
            ) } doReturn Promise.of(listOf(expectedKinTransaction))
            on { getTransaction(eq(expectedKinTransaction.transactionHash)) } doReturn Promise.of(expectedKinTransaction)
            on { canWhitelistTransactions() } doReturn Promise.of(true)
            on { getMinFee() } doReturn Promise.of(KinAmount.ZERO.toQuarks())
            on { buildAndSignTransaction(eq(account.key as Key.PrivateKey),eq(account.key.asPublicKey()), eq((account.status as KinAccount.Status.Registered).sequence), eq(emptyList()), eq(KinMemo.NONE), eq(QuarkAmount(0))) } doReturn Promise.of(expectedKinTransaction)
            on { submitTransaction(eq(expectedKinTransaction)) } doReturn Promise.of(expectedKinTransaction)
        }

        sut = KinServiceWrapper(v3, v4, object : MetaServiceApi {
            override var configuredMinApi: Int = 3
            override fun getMinApiVersion(): Promise<Int> = Promise.of(4)
        })
    }

    @Test
    fun testUpgradeAllWrappedFunctions() {

        sut.createAccount(account.id, accountPrivateKey).test {
            assertTrue { value != null }
        }

        sut.getAccount(account.id).test {
            assertTrue { value != null }
        }

        sut.getLatestTransactions(account.id).test {
            assertTrue { value != null }
        }

        sut.getTransactionPage(
            kinAccountId = account.id,
            pagingToken = pagingToken,
            order = KinService.Order.Descending
        ).test {
            assertTrue { value != null }
        }

        sut.getTransaction(expectedKinTransaction.transactionHash).test {
            assertTrue { value != null }
        }

        sut.canWhitelistTransactions().test {
            assertTrue { value != null }
        }

        sut.getMinFee().test {
            assertTrue { value != null }
        }

        sut.buildAndSignTransaction(account.key as Key.PrivateKey, account.key.asPublicKey(), (account.status as KinAccount.Status.Registered).sequence, emptyList(), KinMemo.NONE, QuarkAmount(0)).test {
            assertTrue { value != null }
        }

        sut.submitTransaction(expectedKinTransaction).test {
            assertTrue { value != null }
        }
    }
}
