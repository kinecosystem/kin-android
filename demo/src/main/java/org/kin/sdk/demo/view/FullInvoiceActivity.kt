package org.kin.sdk.demo.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.kin.sdk.demo.R
import org.kin.sdk.demo.ResolverProvider
import org.kin.sdk.demo.view.FullInvoiceActivity.BundleKeys.toNavArgs
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel
import org.kin.sdk.design.view.tools.BaseActivity
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.widget.InvoiceRenderer
import org.kin.sdk.design.view.widget.PrimaryButton
import java.math.BigDecimal

class FullInvoiceActivity :
    BaseActivity<FullInvoiceViewModel, FullInvoiceViewModel.NavigationArgs, FullInvoiceViewModel.State, ResolverProvider, DemoNavigator>() {

    object BundleKeys {
        const val invoiceId: String = "FullInvoiceActivity.INVOICE_ID"
        const val payerAccountId: String = "FullInvoiceActivity.PAYER_ACCOUNT_ID"
        const val amountPaid: String = "FullInvoiceActivity.AMOUNT_PAID"
        const val readOnly: String = "FullInvoiceActivity.READ_ONLY"

        fun Bundle.toNavArgs(): FullInvoiceViewModel.NavigationArgs =
            FullInvoiceViewModel.NavigationArgs(
                getString(BundleKeys.invoiceId)!!,
                getString(BundleKeys.payerAccountId),
                getSerializable(BundleKeys.amountPaid) as? BigDecimal?,
                getBoolean(BundleKeys.readOnly, false)
            )
    }

    override val navigator: DemoNavigator by lazy {
        ActivityNavigatorImpl(this)
    }

    private lateinit var purchaseButton: PrimaryButton

    private lateinit var invoiceRenderer: InvoiceRenderer

    override fun createViewModel(bundle: Bundle): FullInvoiceViewModel {
        return resolver.resolver.resolve(bundle.toNavArgs(), navigator)
    }

    override fun createView(context: Context): ViewGroup {
        val rootLayout = super.createView(context)

        invoiceRenderer = with(InvoiceRenderer(context)) {
            addTo(
                rootLayout, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        purchaseButton = with(PrimaryButton(context)) {
            setText(R.string.title_pay_now)
            addTo(invoiceRenderer.totalsCard, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16.dip, 0.dip, 16.dip, 24.dip)
            })
        }

        return rootLayout;
    }

    override fun onBindView(viewModel: FullInvoiceViewModel) {
        purchaseButton.setOnClickListener {
            viewModel.onPurchaseTapped()
        }
    }

    override fun onStateUpdated(state: FullInvoiceViewModel.State) {
        invoiceRenderer.invoice = state.invoice
        purchaseButton.visibility = when (state.isReadOnly) {
            true -> View.GONE
            false -> View.VISIBLE
        }
    }
}
