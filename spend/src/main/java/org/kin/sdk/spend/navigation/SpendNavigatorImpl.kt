package org.kin.sdk.spend.navigation

import android.app.Activity
import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.base.viewmodel.PaymentFlowViewModel.Result.Failure.Reason
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.design.view.ActivityNavigatorBase
import org.kin.sdk.spend.view.PaymentFlowActivity

class SpendNavigatorImpl(
    private val launchActivity: Activity
) : ActivityNavigatorBase(launchActivity), SpendNavigator {

    override fun navigateToForResult(
        args: PaymentFlowViewModel.NavigationArgs,
        onResult: (PaymentFlowViewModel.Result) -> Unit
    ) = launchActivityForResult(PaymentFlowActivity::class.java, {}, {
        putString(PaymentFlowActivity.BundleKeys.invoiceId, args.invoiceId)
        putInt(PaymentFlowActivity.BundleKeys.processingAppIdx, args.processingAppIdx)
        putString(PaymentFlowActivity.BundleKeys.payerAccountId, args.payerAccountId)
    }, { _, data ->

        val transactionHash =
            data?.getStringExtra(PaymentFlowActivity.BundleKeys.resultTransactionHash)

        if (transactionHash != null) {
            onResult(PaymentFlowViewModel.Result.Success(transactionHash))
        } else {
            data?.getIntExtra(
                PaymentFlowActivity.BundleKeys.resultFailureType,
                Reason.UNKNOWN_FAILURE.value
            )?.let { failureType ->
                onResult(PaymentFlowViewModel.Result.Failure(Reason.fromValue(failureType)))
            }
        }
    })

    override fun close() {
        launchActivity.finish()
    }
}
