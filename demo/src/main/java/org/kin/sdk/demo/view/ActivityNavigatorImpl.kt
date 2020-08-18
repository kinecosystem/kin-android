package org.kin.sdk.demo.view

import android.app.Activity
import android.content.Intent
import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.CreateInvoiceViewModel
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel
import org.kin.sdk.demo.viewmodel.InvoicesViewModel
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.design.view.ActivityNavigatorBase
import org.kin.sdk.spend.navigation.SpendNavigatorImpl

class ActivityNavigatorImpl(private val launchActivity: Activity) : ActivityNavigatorBase(launchActivity), DemoNavigator {

    private val spendNavigator: SpendNavigator by lazy {
        SpendNavigatorImpl(launchActivity)
    }

    override fun navigateTo(args: HomeViewModel.NavigationArgs) = launchActivity(HomeActivity::class.java, { intent ->
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }) {
        putInt(HomeActivity.BundleKeys.resolverType, args.resolverType.value)
    }

    override fun navigateTo(args: WalletViewModel.NavigationArgs) = launchActivity(WalletActivity::class.java, { intent ->
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }) {
        putInt(WalletActivity.BundleKeys.walletIndex, args.walletIndex)
        putString(WalletActivity.BundleKeys.walletPublicAddress, args.publicAddress)
    }

    override fun navigateTo(args: SendTransactionViewModel.NavigationArgs) = launchActivity(SendTransactionActivity::class.java) {
        putInt(SendTransactionActivity.BundleKeys.walletIndex, args.walletIndex)
        putString(SendTransactionActivity.BundleKeys.walletPublicAddress, args.publicAddress)
    }

    override fun navigateTo(args: TransactionLoadTestingViewModel.NavigationArgs) = launchActivity(TransactionLoadTestingActivity::class.java) {
        putInt(TransactionLoadTestingActivity.BundleKeys.walletIndex, args.walletIndex)
        putString(TransactionLoadTestingActivity.BundleKeys.publicAddress, args.publicAddress)
    }

    override fun navigateTo(args: InvoicesViewModel.NavigationArgs) = launchActivity(InvoicesActivity::class.java) {
        putString(InvoicesActivity.BundleKeys.payerAccountId, args.payerAccountId)
    }

    override fun navigateTo(args: CreateInvoiceViewModel.NavigationArgs) = launchActivity(CreateInvoiceActivity::class.java) {

    }

    override fun navigateTo(args: FullInvoiceViewModel.NavigationArgs) = launchActivity(FullInvoiceActivity::class.java) {
        putString(FullInvoiceActivity.BundleKeys.invoiceId, args.invoiceId)
        putString(FullInvoiceActivity.BundleKeys.payerAccountId, args.payerAccountId)
        putSerializable(FullInvoiceActivity.BundleKeys.amountPaid, args.amountPaid)
        putBoolean(FullInvoiceActivity.BundleKeys.readOnly, args.readOnly)
    }

    override fun navigateToForResult(
        args: PaymentFlowViewModel.NavigationArgs,
        onResult: (PaymentFlowViewModel.Result) -> Unit
    ) {
        spendNavigator.navigateToForResult(args, onResult)
    }
}
