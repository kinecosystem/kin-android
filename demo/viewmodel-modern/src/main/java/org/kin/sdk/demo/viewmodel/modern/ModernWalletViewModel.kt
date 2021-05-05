package org.kin.sdk.demo.viewmodel.modern

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinAccountContextImpl
import org.kin.sdk.base.ObservationMode
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.getAgoraMemo
import org.kin.sdk.base.tools.ListObserver
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.demo.viewmodel.BackupViewModel
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel
import org.kin.sdk.demo.viewmodel.InvoicesViewModel
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.design.viewmodel.tools.BaseViewModel
import java.math.BigDecimal

class ModernWalletViewModel(
    private val navigator: DemoNavigator,
    args: WalletViewModel.NavigationArgs,
    private val kinAccountContext: KinAccountContext
) : WalletViewModel, BaseViewModel<WalletViewModel.NavigationArgs, WalletViewModel.State>(args) {

    private data class SendTransactionActionViewModel(
        val navigator: DemoNavigator,
        val args: WalletViewModel.NavigationArgs
    ) :
        WalletViewModel.SendTransactionActionViewModel {
        override fun onItemTapped() = navigator.navigateTo(
            SendTransactionViewModel.NavigationArgs(
                args.walletIndex,
                args.publicAddress
            )
        )
    }

    private data class LatencyTestTransactionActionViewModel(
        val navigator: DemoNavigator,
        val args: WalletViewModel.NavigationArgs
    ) :
        WalletViewModel.LatencyTestTransactionActionViewModel {
        override fun onItemTapped() = navigator.navigateTo(
            TransactionLoadTestingViewModel.NavigationArgs(
                args.walletIndex,
                args.publicAddress
            )
        )
    }

    private data class CopyAddressActionViewModel(val kinAccountContext: KinAccountContext) :
        WalletViewModel.CopyAddressActionViewModel {
        override val publicAddress: String
            get() = kinAccountContext.accountId.stellarBase32Encode()
    }

    private inner class FundActionViewModel : WalletViewModel.FundActionViewModel {
        override fun onItemTapped(completed: (ex: Throwable?) -> Unit) {
            (kinAccountContext as KinAccountContextImpl).service
                .testService
                .fundAccount(kinAccountContext.accountId)
                .then({ completed(null) }, completed)
        }
    }

    private inner class ExportWalletActionViewModel : WalletViewModel.ExportWalletActionViewModel {
        override fun onItemTapped(activity: Any) {
            navigator.navigateTo(BackupViewModel.NavigationArgs(args.publicAddress))
        }
    }

    private data class DeleteWalletActionViewModel(val kinAccountContext: KinAccountContext) :
        WalletViewModel.DeleteWalletActionViewModel {
        override fun onItemTapped(completed: (ex: Throwable?) -> Unit) =
            kinAccountContext.clearStorage().then({ completed(null) }, completed)
    }

    private data class InvoicesActionActionViewModel(
        val navigator: DemoNavigator,
        val payerAccountId: String
    ) : WalletViewModel.InvoicesActionItemViewModel {
        override fun onItemTapped() =
            navigator.navigateTo(InvoicesViewModel.NavigationArgs(payerAccountId))
    }

    override fun getDefaultState(): WalletViewModel.State = WalletViewModel.State(
        WalletViewModel.WalletHeaderViewModel(
            kinAccountContext.accountId.stellarBase32Encode(),
        null
        ),
        WalletViewModel.WalletStatus.Unknown,
        listOf(
            CopyAddressActionViewModel(kinAccountContext),
            ExportWalletActionViewModel(),
            SendTransactionActionViewModel(navigator, args),
            InvoicesActionActionViewModel(navigator, kinAccountContext.accountId.stellarBase32Encode()),
            LatencyTestTransactionActionViewModel(navigator, args),
            FundActionViewModel(),
            DeleteWalletActionViewModel(kinAccountContext)
        ),
        emptyList()
    )

    private data class PaymentHistoryItemViewModel(
        override val amount: BigDecimal,
        override val memo: String,
        override val sourceWallet: String,
        override val date: Long,
        val payment: KinPayment,
        val navigator: DemoNavigator
    ) : WalletViewModel.PaymentHistoryItemViewModel {
        override fun onItemTapped() {
            payment.invoice?.let {
                navigator.navigateTo(
                    FullInvoiceViewModel.NavigationArgs(
                        it.id.invoiceHash.encodedValue,
                        null,
                        payment.amount.value,
                        true
                    )
                )
            }
        }
    }

    private val listeners = mutableListOf<Observer<*>>()

    init {
        kinAccountContext.getAccount().then {
            listeners.add(observeBalance())
            listeners.add(observePayments())
        }
    }

    private fun observeBalance(): Observer<KinBalance> {
        return kinAccountContext.observeBalance(ObservationMode.Active)
            .add { balance ->
                updateState { previousState ->
                    previousState.copy(
                        walletHeaderViewModel = previousState.walletHeaderViewModel.copy(balance = balance.amount.value),
                        walletStatus = if (balance.amount != KinAmount.ZERO) WalletViewModel.WalletStatus.Active else WalletViewModel.WalletStatus.Inactive
                    )
                }
            }
    }

    private fun observePayments(): ListObserver<KinPayment> {
        return kinAccountContext.observePayments()
            .add { payments ->
                updateState { previousState ->
                    previousState.copy(paymentHistoryItems = payments.map {
                        val destination = it.destinationAccountId.stellarBase32Encode()
                        val source = it.sourceAccountId.stellarBase32Encode()
                        val incoming = destination == kinAccountContext.accountId.stellarBase32Encode()
                        val amount = if (incoming) it.amount.value else it.amount.value.negate()
                        val address = if (incoming) source else destination
                        val memo = if (it.invoice != null) {
                            if (it.invoice!!.lineItems.size > 1) {
                                "${it.invoice!!.lineItems.first().title} +${it.invoice!!.lineItems.size - 1}"
                            } else {
                                it.invoice!!.lineItems.first().title
                            }
                        } else {
                            it.memo.getAgoraMemo()?.let { toString() } ?: String(it.memo.rawValue)
                        }

                        PaymentHistoryItemViewModel(
                            amount,
                            memo,
                            address,
                            0L,
                            it,
                            navigator
                        )
                    })
                }
            }
    }

    override fun cleanup() {
        listeners.forEach(Observer<*>::dispose)
    }
}
