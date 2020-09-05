package org.kin.base.viewmodel

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.kin.base.viewmodel.PaymentFlowViewModel.Result
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinBinaryMemo
import org.kin.sdk.base.models.KinMemo
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.SKU
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.repository.AppInfoRepository
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.tools.KinLoggerFactoryImpl
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.toByteArray
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.codec.Hex
import java.math.BigInteger
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class PaymentFlowViewModelImplTest {

    companion object {
        val kinAccount = KinAccount(KeyPair.random().asPrivateKey())
            .copy(
                balance = KinBalance(KinAmount(150))
            )
        val appInfo = AppInfo(
            AppIdx(0),
            KinAccount.Id("GAO47SC3PMCXVWIQLZSKUCFZQ4MLUAEZIPPILUKSFCFQDCHZHGZJDNQ6"),
            "Test App",
            0
        )
        val invoice = Invoice.Builder()
            .addLineItem(
                LineItem.Builder("Start a Chat", KinAmount(50))
                    .setSKU(SKU(UUID.randomUUID().toByteArray()))
                    .build()
            )
            .build()
    }

    private lateinit var spendNavigator: SpendNavigator
    private lateinit var appInfoRepository: AppInfoRepository
    private lateinit var invoiceRepository: InvoiceRepository
    private lateinit var kinAccountContext: KinAccountContext

    lateinit var sut: PaymentFlowViewModelImpl

    @Before
    fun setUp() {
        spendNavigator = mock {

        }
        appInfoRepository = mock {
            on { appInfoByAppIndex(eq(AppIdx(0))) } doReturn Promise.of(Optional.of(appInfo))
        }
        invoiceRepository = mock {
            on { invoiceById(any()) } doReturn Promise.of(Optional.of(invoice))
        }
        kinAccountContext = mock {
            on { getAccount() } doReturn Promise.of(kinAccount)
        }

        sut = PaymentFlowViewModelImpl(
            spendNavigator,
            PaymentFlowViewModel.NavigationArgs("", "", 0),
            appInfoRepository,
            invoiceRepository,
            kinAccountContext,
            KinLoggerFactoryImpl(true)
        )
    }

    @Test
    fun getDefaultState() {
        val countDownLatch = CountDownLatch(2)
        val values = mutableListOf<PaymentFlowViewModel.State>()
        sut.addStateUpdateListener {
            values.add(it)
            countDownLatch.countDown()
        }

        countDownLatch.await(10, TimeUnit.SECONDS)

        assertEquals(
            PaymentFlowViewModel.State(
                null,
                PaymentFlowViewModel.State.Progression.Init
            ),
            values[0]
        )
        assertEquals(
            PaymentFlowViewModel.State(
                0,
                PaymentFlowViewModel.State.Progression.PaymentConfirmation(
                    50.toBigInteger(),
                    "Test App",
                    100.toBigInteger()
                )
            ),
            values[1]
        )
    }

    @Test
    fun getDefaultState_insufficientBalance() {

        kinAccountContext = mock {
            on { getAccount() } doReturn Promise.of(
                kinAccount.copy(
                    balance = KinBalance(KinAmount(25))
                )
            )
        }

        sut = PaymentFlowViewModelImpl(
            spendNavigator,
            PaymentFlowViewModel.NavigationArgs("", "", 0),
            appInfoRepository,
            invoiceRepository,
            kinAccountContext,
            KinLoggerFactoryImpl(true)
        )

        val countDownLatch = CountDownLatch(2)
        val values = mutableListOf<PaymentFlowViewModel.State>()
        sut.addStateUpdateListener {
            values.add(it)
            countDownLatch.countDown()
        }

        countDownLatch.await(10, TimeUnit.SECONDS)

        assertEquals(
            PaymentFlowViewModel.State(
                null,
                PaymentFlowViewModel.State.Progression.Init
            ),
            values[0]
        )
        assertEquals(
            PaymentFlowViewModel.State(
                0,
                PaymentFlowViewModel.State.Progression.PaymentError(
                    Result.Failure.Reason.INSUFFICIENT_BALANCE,
                    25.toBigInteger()
                )
            ),
            values[1]
        )
    }

    @Test
    fun onCancelTapped() {
        val countDownLatch = CountDownLatch(3)
        val values = mutableListOf<PaymentFlowViewModel.State>()
        sut.addStateUpdateListener {
            values.add(it)
            countDownLatch.countDown()

            if (countDownLatch.count == 1L) {
                sut.onCancelTapped { }
            }
        }

        countDownLatch.await(5, TimeUnit.SECONDS)

        assertEquals(
            PaymentFlowViewModel.State(
                0,
                PaymentFlowViewModel.State.Progression.PaymentError(
                    Result.Failure.Reason.CANCELLED,
                    BigInteger.ZERO
                )
            ),
            values[2]
        )
    }

    @Test
    fun onConfirmTapped_success() {
        val countDownLatch = CountDownLatch(4)
        val values = mutableListOf<PaymentFlowViewModel.State>()
        sut.addStateUpdateListener {
            values.add(it)
            countDownLatch.countDown()
        }

        whenever(
            kinAccountContext.payInvoice(
                eq(invoice),
                eq(appInfo.kinAccountId),
                eq(AppIdx(0)),
                eq(KinBinaryMemo.TransferType.Spend)
            )
        ).doReturn(
            Promise.of(
                KinPayment(
                    KinPayment.Id(TransactionHash(Hex.decodeHex("aaff")), 0),
                    KinPayment.Status.Success,
                    kinAccount.id,
                    kinAccount.id,
                    KinAmount(25),
                    QuarkAmount(100),
                    KinMemo.NONE,
                    12345L,
                    invoice
                )
            )
        )

        sut.onConfirmTapped()

        countDownLatch.await(5, TimeUnit.SECONDS)

        println(values)

        assertEquals(
            PaymentFlowViewModel.State.Progression.PaymentProcessing,
            values[2].progression
        )

        assertEquals(
            PaymentFlowViewModel.State.Progression.PaymentSuccess("aaff"),
            values[3].progression
        )
    }

    @Test
    fun testErrorConversionToReasons() {
        assertEquals(
            Result.Failure.Reason.BAD_NETWORK,
            sut.convertErrorToReason(KinService.FatalError.TransientFailure())
        )
        assertEquals(
            Result.Failure.Reason.MISCONFIGURED_REQUEST,
            sut.convertErrorToReason(KinService.FatalError.IllegalRequest())
        )
        assertEquals(
            Result.Failure.Reason.MISCONFIGURED_REQUEST,
            sut.convertErrorToReason(KinService.FatalError.UnknownAccountInRequest)
        )
        assertEquals(
            Result.Failure.Reason.MISCONFIGURED_REQUEST,
            sut.convertErrorToReason(KinService.FatalError.InsufficientFeeInRequest)
        )
        assertEquals(
            Result.Failure.Reason.ALREADY_PURCHASED,
            sut.convertErrorToReason(
                KinService.FatalError.InvoiceErrorsInRequest(
                    listOf(
                        InvoiceErrors.InvoiceError.ALREADY_PAID
                    )
                )
            )
        )
        assertEquals(
            Result.Failure.Reason.MISCONFIGURED_REQUEST,
            sut.convertErrorToReason(
                KinService.FatalError.InvoiceErrorsInRequest(
                    listOf(
                        InvoiceErrors.InvoiceError.SKU_NOT_FOUND
                    )
                )
            )
        )
        assertEquals(
            Result.Failure.Reason.MISCONFIGURED_REQUEST,
            sut.convertErrorToReason(
                KinService.FatalError.InvoiceErrorsInRequest(
                    listOf(
                        InvoiceErrors.InvoiceError.WRONG_DESTINATION
                    )
                )
            )
        )
        assertEquals(
            Result.Failure.Reason.MISCONFIGURED_REQUEST,
            sut.convertErrorToReason(
                KinService.FatalError.InvoiceErrorsInRequest(
                    listOf(
                        InvoiceErrors.InvoiceError.UNKNOWN
                    )
                )
            )
        )
        assertEquals(
            Result.Failure.Reason.INSUFFICIENT_BALANCE,
            sut.convertErrorToReason(KinService.FatalError.InsufficientBalanceForSourceAccountInRequest)
        )
        assertEquals(
            Result.Failure.Reason.DENIED_BY_SERVICE,
            sut.convertErrorToReason(KinService.FatalError.Denied())
        )
        assertEquals(
            Result.Failure.Reason.DENIED_BY_SERVICE,
            sut.convertErrorToReason(KinService.FatalError.WebhookRejectedTransaction)
        )
        assertEquals(
            Result.Failure.Reason.SDK_UPGRADE_REQUIRED,
            sut.convertErrorToReason(KinService.FatalError.SDKUpgradeRequired)
        )
        assertEquals(
            Result.Failure.Reason.UNKNOWN_FAILURE,
            sut.convertErrorToReason(KinService.FatalError.IllegalResponse)
        )
        assertEquals(
            Result.Failure.Reason.UNKNOWN_FAILURE,
            sut.convertErrorToReason(KinService.FatalError.ItemNotFound)
        )
        assertEquals(
            Result.Failure.Reason.UNKNOWN_FAILURE,
            sut.convertErrorToReason(KinService.FatalError.PermanentlyUnavailable)
        )
        assertEquals(
            Result.Failure.Reason.UNKNOWN_FAILURE,
            sut.convertErrorToReason(KinService.FatalError.UnexpectedServiceError())
        )
        assertEquals(
            Result.Failure.Reason.UNKNOWN_FAILURE,
            sut.convertErrorToReason(RuntimeException())
        )
    }

    @Test
    fun testFailureEnum() {
        assertEquals(Result.Failure.Reason.CANCELLED, Result.Failure.Reason.fromValue(0))
        assertEquals(Result.Failure.Reason.ALREADY_PURCHASED, Result.Failure.Reason.fromValue(1))
        assertEquals(Result.Failure.Reason.UNKNOWN_FAILURE, Result.Failure.Reason.fromValue(2))
        assertEquals(Result.Failure.Reason.UNKNOWN_INVOICE, Result.Failure.Reason.fromValue(3))
        assertEquals(
            Result.Failure.Reason.UNKNOWN_PAYER_ACCOUNT,
            Result.Failure.Reason.fromValue(4)
        )
        assertEquals(Result.Failure.Reason.INSUFFICIENT_BALANCE, Result.Failure.Reason.fromValue(5))
        assertEquals(
            Result.Failure.Reason.MISCONFIGURED_REQUEST,
            Result.Failure.Reason.fromValue(6)
        )
        assertEquals(Result.Failure.Reason.DENIED_BY_SERVICE, Result.Failure.Reason.fromValue(7))
        assertEquals(Result.Failure.Reason.SDK_UPGRADE_REQUIRED, Result.Failure.Reason.fromValue(8))
        assertEquals(Result.Failure.Reason.BAD_NETWORK, Result.Failure.Reason.fromValue(9))
    }

    @After
    fun cleanup() {
        sut.removeAllListeners()
    }
}
