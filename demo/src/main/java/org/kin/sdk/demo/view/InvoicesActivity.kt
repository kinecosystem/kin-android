package org.kin.sdk.demo.view

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import org.kin.sdk.demo.ResolverProvider
import org.kin.sdk.demo.R
import org.kin.sdk.design.view.widget.internal.ActionListItemView
import org.kin.sdk.design.view.widget.internal.InvoiceListItemView
import org.kin.sdk.design.view.widget.internal.VerticalRecyclerView
import org.kin.sdk.design.view.tools.BaseActivity
import org.kin.sdk.design.view.tools.RecyclerViewTools
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.build
import org.kin.sdk.design.view.tools.updateItems
import org.kin.sdk.demo.viewmodel.InvoicesViewModel
import org.kin.sdk.demo.viewmodel.DemoNavigator

class InvoicesActivity :
    BaseActivity<InvoicesViewModel, InvoicesViewModel.NavigationArgs, InvoicesViewModel.State, ResolverProvider, DemoNavigator>() {
    object BundleKeys {
        const val payerAccountId: String = "InvoicesActivity.PAYER_ACCOUNT_ID"
    }

    override val navigator: DemoNavigator by lazy {
        ActivityNavigatorImpl(this)
    }

    private lateinit var items: VerticalRecyclerView

    override fun createViewModel(bundle: Bundle): InvoicesViewModel {
        return resolver.resolver.resolve(
            InvoicesViewModel.NavigationArgs(bundle.getString(BundleKeys.payerAccountId)),
            navigator
        )
    }

    override fun createView(context: Context): ViewGroup {
        val rootLayout = super.createView(context)

        items = with(VerticalRecyclerView(context)) {
            build {

                layout<ActionListItemView, InvoicesViewModel.CreateInvoiceItemViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_create_invoice)
                        view.isAdditive = true
                        view.setOnClickListener {
                            viewModel.onItemTapped()
                            closeKeyboard()
                        }
                    }
                }

                layout<InvoiceListItemView, InvoicesViewModel.InvoiceItemViewModel> {
                    create(::InvoiceListItemView)

                    bind { view, viewModel ->
                        view.title =
                            if (viewModel.itemCount > 1) "${viewModel.firstItemTitle} & ${viewModel.itemCount - 1} more items" else viewModel.firstItemTitle
                        view.description = viewModel.invoiceId
                        view.amount = viewModel.amount.toBigDecimal()
                        view.setOnClickListener { viewModel.onItemTapped() }
                    }
                }
            }

            addTo(rootLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        }

        return rootLayout
    }

    override fun onBindView(viewModel: InvoicesViewModel) {

    }

    override fun onStateUpdated(state: InvoicesViewModel.State) {
        items.updateItems(
            listOf(
                RecyclerViewTools.header(R.string.title_invoices),
                *state.invoices.toTypedArray(),
                *state.actions.toTypedArray()
            )
        )
    }
}
