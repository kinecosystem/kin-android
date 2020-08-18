package org.kin.sdk.spend

import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.TransactionHash
import org.kin.sdk.base.repository.AppInfoRepository
import org.kin.sdk.base.repository.InvoiceRepository

data class SpendController(
    private val kinEnvironment: KinEnvironment.Agora,
    private val navigator: SpendNavigator
) {
    private val invoiceRepository: InvoiceRepository by lazy {
        kinEnvironment.invoiceRepository
    }
    private val appInfoRepository: AppInfoRepository by lazy {
        kinEnvironment.appInfoRepository
    }

    fun confirmPaymentOfInvoice(
        invoice: Invoice,
        payerAccount: KinAccount.Id,
        processingAppInfo: AppInfo = kinEnvironment.appInfoProvider.appInfo,
        onResult: (TransactionHash?, PaymentFlowViewModel.Result.Failure.Reason?) -> Unit
    ) {
        invoiceRepository.addInvoice(invoice)
            .doOnResolved { appInfoRepository.addAppInfo(processingAppInfo) }
            .then({
                navigator.navigateToForResult(
                    PaymentFlowViewModel.NavigationArgs(
                        it.id.invoiceHash.encodedValue,
                        payerAccount.encodeAsString(),
                        processingAppInfo.appIndex.value
                    )
                ) {
                    when (it) {
                        is PaymentFlowViewModel.Result.Success ->
                            onResult(TransactionHash(it.transactionHash), null)
                        is PaymentFlowViewModel.Result.Failure ->
                            onResult(null, it.reason)
                    }
                }
            }, {
                onResult(null, PaymentFlowViewModel.Result.Failure.Reason.UNKNOWN_FAILURE)
            })
    }
}
