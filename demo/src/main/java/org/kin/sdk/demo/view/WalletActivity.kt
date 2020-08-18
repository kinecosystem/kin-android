package org.kin.sdk.demo.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import org.kin.sdk.demo.ResolverProvider
import org.kin.sdk.demo.R
import org.kin.sdk.design.view.widget.internal.ActionListItemView
import org.kin.sdk.demo.view.custom.HistoryListItemView
import org.kin.sdk.design.view.widget.internal.StandardDialog
import org.kin.sdk.design.view.widget.internal.VerticalRecyclerView
import org.kin.sdk.demo.view.custom.WalletStatusHeaderView
import org.kin.sdk.design.view.tools.BaseActivity
import org.kin.sdk.design.view.tools.RecyclerViewTools
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.build
import org.kin.sdk.design.view.tools.updateItems
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.WalletViewModel

class WalletActivity :
    BaseActivity<WalletViewModel, WalletViewModel.NavigationArgs, WalletViewModel.State, ResolverProvider, DemoNavigator>() {
    object BundleKeys {
        const val walletIndex: String = "WalletActivity.WALLET_INDEX"
        const val networkIndex: String = "WalletActivity.NETWORK_INDEX"
        const val walletPublicAddress: String = "SendTransactionActivity.WALLET_PUBLIC_ADDRESS"
    }

    override val navigator: DemoNavigator by lazy {
        ActivityNavigatorImpl(this)
    }

    private lateinit var items: VerticalRecyclerView

    override fun createViewModel(bundle: Bundle): WalletViewModel {
        return resolver.resolver.resolve(
            WalletViewModel.NavigationArgs(
                bundle.getInt(BundleKeys.walletIndex),
                bundle.getString(SendTransactionActivity.BundleKeys.walletPublicAddress)!!
            ), navigator
        )
    }

    override fun createView(context: Context): ViewGroup {
        val rootLayout = super.createView(context)

        title = getString(R.string.title_wallet_screen, "TestNet")

        items = with(VerticalRecyclerView(context)) {
            build {
                layout<WalletStatusHeaderView, WalletViewModel.WalletHeaderViewModel> {
                    create(::WalletStatusHeaderView)

                    bind { view, viewModel ->
                        view.address = viewModel.publicAddress
                        view.balance = viewModel.balance
                    }
                }

                layout<ActionListItemView, WalletViewModel.CopyAddressActionViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_copy_address)
                        view.setOnClickListener {
                            val addressData = ClipData.newPlainText(
                                "Kin Public Address",
                                viewModel.publicAddress
                            )
                            val clipboard =
                                (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager)

                            clipboard.setPrimaryClip(addressData)

                            Toast.makeText(context, R.string.toast_copied, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                layout<ActionListItemView, WalletViewModel.ExportWalletActionViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_export_wallet)
                        view.description = getString(R.string.description_export_wallet)
                        view.setOnClickListener { viewModel.onItemTapped(this@WalletActivity) }
                    }
                }

                layout<ActionListItemView, WalletViewModel.DeleteWalletActionViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_delete_wallet)
                        view.isDestructive = true
                        view.setOnClickListener {
                            StandardDialog.confirm(
                                context,
                                title = getString(R.string.title_confirm_delete),
                                description = getString(R.string.description_confirm_delete)
                            ) {
                                if (it) {
                                    showSpinner()
                                    viewModel.onItemTapped {
                                        hideSpinner()
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }

                layout<ActionListItemView, WalletViewModel.OnboardActionViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_onboard_wallet)
                        view.description = getString(R.string.description_onboard_wallet)
                        view.setOnClickListener {
                            showSpinner()

                            viewModel.onItemTapped { ex ->
                                hideSpinner()

                                if (ex != null) {
                                    view.post {
                                        StandardDialog.error(
                                            context,
                                            title = getString(R.string.title_error),
                                            description = getString(R.string.title_onboard_wallet),
                                            error = ex.localizedMessage ?: ex.toString()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                layout<ActionListItemView, WalletViewModel.FundActionViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_fund_wallet)
                        view.description = getString(R.string.description_fund_wallet)
                        view.setOnClickListener {
                            showSpinner()

                            viewModel.onItemTapped { ex ->
                                hideSpinner()

                                if (ex != null) {
                                    view.post {
                                        StandardDialog.error(
                                            context,
                                            title = getString(R.string.title_error),
                                            description = getString(R.string.title_fund_wallet),
                                            error = ex.localizedMessage ?: ex.toString()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                layout<ActionListItemView, WalletViewModel.SendTransactionActionViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_send_transaction)
                        view.description = getString(R.string.description_send_transaction)
                        view.setOnClickListener { viewModel.onItemTapped() }
                    }
                }

                layout<ActionListItemView, WalletViewModel.LatencyTestTransactionActionViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_test_transaction_latency)
                        view.description = getString(R.string.description_test_transaction_latency)
                        view.setOnClickListener { viewModel.onItemTapped() }
                    }
                }

                layout<ActionListItemView, WalletViewModel.ExportWalletActionViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_export_kin)
                        view.description = getString(R.string.description_export_kin)
                        view.setOnClickListener { viewModel.onItemTapped(this@WalletActivity) }
                    }
                }

                layout<HistoryListItemView, WalletViewModel.PaymentHistoryItemViewModel> {
                    create(::HistoryListItemView)

                    bind { view, viewModel ->
                        view.address = viewModel.sourceWallet
                        view.memo = viewModel.memo
                        view.amount = viewModel.amount
                        view.setOnClickListener { viewModel.onItemTapped() }
                    }
                }

                layout<ActionListItemView, WalletViewModel.InvoicesActionItemViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_invoices)
                        view.description = getString(R.string.description_invoices)
                        view.setOnClickListener { viewModel.onItemTapped() }
                    }
                }
            }

            addTo(rootLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        }

        return rootLayout
    }

    override fun onBindView(viewModel: WalletViewModel) {
    }

    override fun onStateUpdated(state: WalletViewModel.State) {
        val historyItems = listOf(RecyclerViewTools.header(R.string.title_history)) +
                if (state.paymentHistoryItems.isNotEmpty()) {
                    state.paymentHistoryItems
                } else {
                    listOf(RecyclerViewTools.placeholder(R.string.placeholder_no_history))
                }

        items.updateItems(listOf(state.walletHeaderViewModel) + state.walletActions + historyItems)

        when (state.walletStatus) {
            WalletViewModel.WalletStatus.Unknown -> showSpinner()
            else -> hideSpinner()
        }
    }
}
