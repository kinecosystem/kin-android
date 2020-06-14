package org.kin.sdk.demo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel

class ActivityNavigatorImpl(private val launchActivity: Activity) : Navigator {

    private fun launchActivity(activityClass: Class<*>, bundleBuilder: Bundle.() -> Unit) = launchActivity<Any>(activityClass, { }, bundleBuilder)

    private fun <T> launchActivity(activityClass: Class<*>, modifyIntent: (Intent) -> Unit, bundleBuilder: Bundle.() -> Unit) {
        val intent = Intent(launchActivity, activityClass)
        val b = Bundle()

        b.bundleBuilder()

        intent.putExtras(b)

        modifyIntent(intent)

        launchActivity.startActivity(intent)
    }

    override fun navigateTo(args: HomeViewModel.NavigationArgs) = launchActivity<Intent>(HomeActivity::class.java, { intent ->
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }) {
        putInt(HomeActivity.BundleKeys.resolverType, args.resolverType.value)
    }

    override fun navigateTo(args: WalletViewModel.NavigationArgs) = launchActivity(WalletActivity::class.java) {
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

    override fun close() {
        launchActivity.finish()
    }
}
