package org.kin.sdk.demo.view

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import org.kin.sdk.demo.R
import org.kin.sdk.demo.ResolverProvider
import org.kin.sdk.demo.viewmodel.CreateInvoiceViewModel
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.design.view.tools.BaseActivity
import org.kin.sdk.design.view.tools.RecyclerViewTools
import org.kin.sdk.design.view.tools.addIntegerChangedListener
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.build
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.updateItems
import org.kin.sdk.design.view.widget.PrimaryButton
import org.kin.sdk.design.view.widget.internal.ActionListItemView
import org.kin.sdk.design.view.widget.internal.LineItemListItemView
import org.kin.sdk.design.view.widget.internal.StandardEditText
import org.kin.sdk.design.view.widget.internal.VerticalRecyclerView

class CreateInvoiceActivity :
    BaseActivity<CreateInvoiceViewModel, CreateInvoiceViewModel.NavigationArgs, CreateInvoiceViewModel.State, ResolverProvider, DemoNavigator>() {

    override val navigator: DemoNavigator by lazy {
        ActivityNavigatorImpl(this)
    }

    private lateinit var amountEditText: StandardEditText
    private lateinit var titleEditText: StandardEditText
    private lateinit var descriptionEditText: StandardEditText
    private lateinit var skuEditText: StandardEditText
    private lateinit var invoiceIdText: StandardEditText

    private lateinit var createInvoiceButton: PrimaryButton

    private lateinit var items: VerticalRecyclerView

    override fun createViewModel(bundle: Bundle): CreateInvoiceViewModel {
        return resolver.resolver.resolve(CreateInvoiceViewModel.NavigationArgs(), navigator)
    }

    override fun createView(context: Context): ViewGroup {
        val rootLayout = super.createView(context)

        with(LinearLayout(context)) {
            val formLayout = this

            formLayout.orientation = LinearLayout.VERTICAL

            amountEditText = with(StandardEditText(context)) {
                setHintText(getString(R.string.hint_payment_amount))
                innerEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
                innerEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
                innerEditText.setSingleLine()
                addTo(formLayout, bottomMargin = 8.dip)
            }
            titleEditText = with(StandardEditText(context)) {
                setHintText(getString(R.string.hint_title))
                innerEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
                innerEditText.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                innerEditText.setSingleLine()
                addTo(formLayout, bottomMargin = 8.dip)
            }
            descriptionEditText = with(StandardEditText(context)) {
                setHintText(getString(R.string.hint_description))
                innerEditText.maxLines = 3
                innerEditText.isSingleLine = false
                innerEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
                innerEditText.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                addTo(formLayout, bottomMargin = 8.dip)
            }
            skuEditText = with(StandardEditText(context)) {
                setHintText(getString(R.string.hint_sku))
                innerEditText.maxLines = 3
                innerEditText.isSingleLine = false
                innerEditText.imeOptions = EditorInfo.IME_ACTION_GO
                innerEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
                addTo(formLayout, bottomMargin = 8.dip)
            }
            invoiceIdText = with(StandardEditText(context)) {
                visibility = View.GONE
                setHintText(getString(R.string.invoice_id))
                innerEditText.setSingleLine()
                innerEditText.isEnabled = false
                addTo(formLayout, bottomMargin = 8.dip)
            }

            addTo(
                rootLayout,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0f
                ).apply {
                    setMargins(12.dip, 12.dip, 12.dip, 0)
                })
        }

        items = with(
            VerticalRecyclerView(
                context
            )
        ) {
            build {
                layout<ActionListItemView, CreateInvoiceViewModel.AddLineItemViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_add_line_item)
                        view.isAdditive = true
                        view.setOnClickListener {
                            viewModel.onItemTapped()
                            closeKeyboard()
                            titleEditText.clear()
                            descriptionEditText.clear()
                            amountEditText.clear()
                            skuEditText.clear()
                        }
                    }
                }

                layout<LineItemListItemView, CreateInvoiceViewModel.LineItemViewModel> {
                    create(::LineItemListItemView)

                    bind { view, viewModel ->
                        view.title = viewModel.title
                        view.description = viewModel.description
                        view.amount = viewModel.amount.toBigDecimal()
                        view.sku = viewModel.sku
                        view.setOnClickListener { viewModel.onItemTapped() }
                    }
                }
            }

            addTo(rootLayout, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        }

        createInvoiceButton = with(PrimaryButton(context)) {
            setText(R.string.title_create_invoice)
            addTo(rootLayout, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12.dip, 12.dip, 12.dip, 12.dip)
            })
        }

        return rootLayout
    }

    override fun onBindView(viewModel: CreateInvoiceViewModel) {
        createInvoiceButton.setOnClickListener {
            viewModel.onCreateInvoiceTapped()
            finish()
        }

        amountEditText.innerEditText.addIntegerChangedListener { viewModel.onAmountUpdated(it) }
        titleEditText.innerEditText.addTextChangedListener { viewModel.onTitleUpdated(it.toString()) }
        descriptionEditText.innerEditText.addTextChangedListener { viewModel.onDescriptionUpdated(it.toString()) }
        skuEditText.innerEditText.addTextChangedListener { viewModel.onSkuUpdated(it.toString()) }
    }

    private lateinit var lastState: CreateInvoiceViewModel.State

    override fun onStateUpdated(state: CreateInvoiceViewModel.State) {
        items.updateItems(
            listOf(
                *state.listItems.toTypedArray(),
                RecyclerViewTools.header(R.string.title_line_items),
                *state.lineItems.toTypedArray()
            )
        )
        items.smoothScrollToPosition(0)
        createInvoiceButton.isEnabled = state.canCreateInvoice

        invoiceIdText.visibility = if (state.invoiceId.isNotEmpty()) View.VISIBLE else View.GONE
        invoiceIdText.setText(state.invoiceId)
    }
}
