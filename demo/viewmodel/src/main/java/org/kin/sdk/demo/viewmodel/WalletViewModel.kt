package org.kin.sdk.demo.viewmodel

import org.kin.sdk.design.viewmodel.tools.ViewModel
import java.math.BigDecimal

interface WalletViewModel : ViewModel<WalletViewModel.NavigationArgs, WalletViewModel.State> {

    data class NavigationArgs(val walletIndex: Int, val publicAddress: String)

    enum class WalletStatus {
        Unknown,
        Inactive,
        Active
    }

    data class State(
        val walletHeaderViewModel: WalletHeaderViewModel,
        val walletStatus: WalletStatus,
        val walletActions: List<WalletActionViewModel>,
        val paymentHistoryItems: List<PaymentHistoryItemViewModel>
    )

    data class WalletHeaderViewModel(val publicAddress: String, val balance: BigDecimal?)

    interface PaymentHistoryItemViewModel {
        fun onItemTapped()

        val amount: BigDecimal
        val memo: String
        val sourceWallet: String
        val date: Long
    }

    interface WalletActionViewModel

    interface SendTransactionActionViewModel : WalletActionViewModel {
        fun onItemTapped()
    }

    interface LatencyTestTransactionActionViewModel : WalletActionViewModel {
        fun onItemTapped()
    }

    interface CopyAddressActionViewModel : WalletActionViewModel {
        val publicAddress: String
    }

    interface OnboardActionViewModel : WalletActionViewModel {
        fun onItemTapped(completed: (ex: Throwable?) -> Unit)
    }

    interface FundActionViewModel : WalletActionViewModel {
        fun onItemTapped(completed: (ex: Throwable?) -> Unit)
    }

    interface DeleteWalletActionViewModel : WalletActionViewModel {
        fun onItemTapped(completed: (ex: Throwable?) -> Unit)
    }

    interface ExportWalletActionViewModel : WalletActionViewModel {
        fun onItemTapped(activity: Any)
    }

    interface InvoicesActionItemViewModel : WalletActionViewModel {
        fun onItemTapped()
    }
}
