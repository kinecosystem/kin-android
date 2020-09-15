package org.kin.base.viewmodel

import org.kin.base.viewmodel.PaymentFlowViewModel.Result
import org.kin.base.viewmodel.PaymentFlowViewModel.State.Progression
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.SHA224Hash
import org.kin.sdk.base.network.api.KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors.InvoiceError
import org.kin.sdk.base.network.services.KinService
import org.kin.sdk.base.repository.AppInfoRepository
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.tools.KinLogger
import org.kin.sdk.base.tools.KinLoggerFactory
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.design.viewmodel.tools.BaseViewModel
import java.math.BigInteger
import java.util.concurrent.Executors

class PaymentFlowViewModelImpl(
    private val spendNavigator: SpendNavigator,
    args: PaymentFlowViewModel.NavigationArgs,
    private val appInfoRepository: AppInfoRepository,
    private val invoiceRepository: InvoiceRepository,
    private val kinAccountContext: KinAccountContext,
    private val logger: KinLoggerFactory
) : PaymentFlowViewModel,
    BaseViewModel<PaymentFlowViewModel.NavigationArgs, PaymentFlowViewModel.State>(args) {

    private val processingAppIdx = AppIdx(args.processingAppIdx)
    private val executor = Executors.newSingleThreadExecutor()
    private val log: KinLogger by lazy {
        logger.getLogger(javaClass.simpleName)
    }

    init {
        log.log("Navigated to: ${javaClass.simpleName}")
    }

    private fun setup() {
        appInfoRepository.appInfoByAppIndex(AppIdx(args.processingAppIdx)).then { appInfo ->
            invoiceRepository.invoiceById(Invoice.Id(SHA224Hash(args.invoiceId)))
                .workOn(executor)
                .then { invoice ->
                    kinAccountContext.getAccount().then { account ->
                        updateState {
                            it.copy(
                                appIconId = appInfo.get()?.appIconResourceId,
                                progression = if (invoice.isPresent && appInfo.isPresent) {
                                    val newBalance =
                                        (account.balance.amount - invoice.get()!!.total).value.toBigInteger()
                                    if (newBalance >= BigInteger.ZERO) {
                                        Progression.PaymentConfirmation(
                                            invoice.get()!!.total.value.toBigInteger(),
                                            appInfo.get()!!.appName,
                                            newBalance
                                        )
                                    } else {
                                        Progression.PaymentError(
                                            Result.Failure.Reason.INSUFFICIENT_BALANCE,
                                            account.balance.amount.value.toBigInteger()
                                        )
                                    }
                                } else Progression.PaymentConfirmation(
                                    BigInteger.ZERO,
                                    "",
                                    BigInteger.ZERO
                                )
                            )
                        }
                    }
                }
        }
    }

    override fun getDefaultState(): PaymentFlowViewModel.State =
        PaymentFlowViewModel.State(null, Progression.Init)

    override fun onStateUpdated(state: PaymentFlowViewModel.State) {
        log.log("${::onStateUpdated.name}:$state")
        if (state.progression == Progression.Init) {
            setup()
        }
    }

    override fun onCancelTapped(onCompleted: () -> Unit) {
        log.log(::onCancelTapped.name)
        updateState {
            it.copy(
                progression = Progression.PaymentError(
                    Result.Failure.Reason.CANCELLED,
                    BigInteger.ZERO
                )
            )
        }
        onCompleted()
    }

    override fun onConfirmTapped() {
        log.log(::onConfirmTapped.name)
        updateState {
            it.copy(progression = Progression.PaymentProcessing)
        }

        invoiceRepository.invoiceById(Invoice.Id(SHA224Hash(args.invoiceId)))
            .flatMap {
                it.map { invoice ->
                    appInfoRepository.appInfoByAppIndex(processingAppIdx).flatMap { appInfo ->
                        kinAccountContext.payInvoice(
                            invoice,
                            appInfo.get()!!.kinAccountId,
                            processingAppIdx
                        )
                    }
                }.orElse { Promise.error(RuntimeException("Missing Invoice")) }
            }
            .then({ payment ->
                updateState {
                    it.copy(progression = Progression.PaymentSuccess(payment.id.transactionHash.toString()))
                }
            }, { error ->
                kinAccountContext.getAccount().then { account ->
                    updateState {
                        it.copy(
                            progression = Progression.PaymentError(
                                convertErrorToReason(error),
                                account.balance.amount.value.toBigInteger()
                            )
                        )
                    }
                }
            })
    }

    internal fun convertErrorToReason(error: Throwable): Result.Failure.Reason {
        return if (error is KinService.FatalError) {
            @Suppress("USELESS_CAST")
            when (error as KinService.FatalError) {
                is KinService.FatalError.TransientFailure ->
                    Result.Failure.Reason.BAD_NETWORK
                is KinService.FatalError.IllegalRequest,
                KinService.FatalError.UnknownAccountInRequest,
                KinService.FatalError.BadSequenceNumberInRequest,
                KinService.FatalError.InsufficientFeeInRequest ->
                    Result.Failure.Reason.MISCONFIGURED_REQUEST
                is KinService.FatalError.InvoiceErrorsInRequest -> {
                    val invoiceErrorsInRequest =
                        (error as KinService.FatalError.InvoiceErrorsInRequest)
                    if (invoiceErrorsInRequest.invoiceErrors.contains(InvoiceError.ALREADY_PAID)) {
                        Result.Failure.Reason.ALREADY_PURCHASED
                    } else {
                        Result.Failure.Reason.MISCONFIGURED_REQUEST
                    }
                }
                KinService.FatalError.InsufficientBalanceForSourceAccountInRequest ->
                    Result.Failure.Reason.INSUFFICIENT_BALANCE

                is KinService.FatalError.Denied,
                KinService.FatalError.WebhookRejectedTransaction ->
                    Result.Failure.Reason.DENIED_BY_SERVICE
                KinService.FatalError.SDKUpgradeRequired ->
                    Result.Failure.Reason.SDK_UPGRADE_REQUIRED
                KinService.FatalError.IllegalResponse,
                KinService.FatalError.ItemNotFound,
                KinService.FatalError.PermanentlyUnavailable,
                is KinService.FatalError.UnexpectedServiceError ->
                    Result.Failure.Reason.UNKNOWN_FAILURE
            }
        } else {
            Result.Failure.Reason.UNKNOWN_FAILURE
        }
    }
}
