package org.kin.sdk.demo.viewmodel.compat

import android.app.Activity
import kin.backupandrestore.BackupAndRestoreManager
import kin.sdk.KinClient
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.demo.viewmodel.tools.BaseViewModel

class CompatHomeViewModel(
    private val navigator: Navigator,
    args: HomeViewModel.NavigationArgs,
    private val testNetClient: KinClient,
    private val mainNetClient: KinClient
) : HomeViewModel, BaseViewModel<HomeViewModel.NavigationArgs, HomeViewModel.State>(args) {

    private inner class WalletItemViewModel(val index: Int, override val publicAddress: String) : HomeViewModel.WalletItemViewModel {
        override fun onItemTapped() {
            navigator.navigateTo(WalletViewModel.NavigationArgs(index, publicAddress))
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is WalletItemViewModel) return false

            if (index != other.index) return false
            if (publicAddress != other.publicAddress) return false

            return true
        }

        override fun hashCode(): Int {
            var result = index
            result = 31 * result + publicAddress.hashCode()
            return result
        }
    }

    private inner class AddTestNetWalletItemViewModel() : HomeViewModel.AddWalletItemViewModel {
        override fun onItemTapped() {
            testNetClient.addAccount()
            updateAccounts()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AddTestNetWalletItemViewModel) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    private inner class ImportTestNetWalletItemViewModel : HomeViewModel.ImportWalletItemViewModel {
        override fun onItemTapped(activity: Any) {
            BackupAndRestoreManager(activity as Activity, 1, 2).restore(testNetClient)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ImportTestNetWalletItemViewModel) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    private inner class AddMainNetWalletItemViewModel() : HomeViewModel.AddWalletItemViewModel {
        override fun onItemTapped() {
            mainNetClient.addAccount()
            updateAccounts()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AddMainNetWalletItemViewModel) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    private inner class ImportMainNetWalletItemViewModel : HomeViewModel.ImportWalletItemViewModel {
        override fun onItemTapped(activity: Any) {
            BackupAndRestoreManager(activity as Activity, 1, 2).restore(mainNetClient)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ImportMainNetWalletItemViewModel) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    private fun updateAccounts() {
        updateState { previousState ->
            previousState.copy(
                /* mainNetWallets = (0 until mainNetClient.accountCount).map {
                    WalletItemViewModel(it, mainNetClient.getAccount(it).publicAddress!!)
                } + ImportTestNetWalletItemViewModel() + AddMainNetWalletItemViewModel(), */
                testNetWallets = (0 until testNetClient.accountCount).map {
                    WalletItemViewModel(it, testNetClient.getAccount(it).publicAddress!!)
                } + ImportMainNetWalletItemViewModel() + AddTestNetWalletItemViewModel())
        }
    }

    private fun createNewAccountIfNeededAndRefreshAccountList() {
        while (testNetClient.accountCount < 1) {
            testNetClient.addAccount()
        }
        updateAccounts()
    }

    override fun getDefaultState(): HomeViewModel.State = HomeViewModel.State(args.resolverType, emptyList(), emptyList())

    init {
        createNewAccountIfNeededAndRefreshAccountList()
    }

    override fun onCreateWalletTapped() {
        testNetClient.addAccount()
        updateAccounts()
    }

    override fun onUseBaseTapped() {
        navigator.navigateTo(HomeViewModel.NavigationArgs(HomeViewModel.NavigationArgs.ResolverType.Modern))
    }

    override fun onUseBaseCompatTapped() {
        navigator.navigateTo(HomeViewModel.NavigationArgs(HomeViewModel.NavigationArgs.ResolverType.Compat))
    }

    override fun pokeWalletUpdate() {
        createNewAccountIfNeededAndRefreshAccountList()
    }

}
