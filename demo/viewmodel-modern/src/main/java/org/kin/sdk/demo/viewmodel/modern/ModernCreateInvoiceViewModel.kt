package org.kin.sdk.demo.viewmodel.modern

import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.SKU
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.demo.viewmodel.CreateInvoiceViewModel
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.design.viewmodel.tools.BaseViewModel
import java.math.BigInteger

class ModernCreateInvoiceViewModel(
    val navigator: DemoNavigator,
    args: CreateInvoiceViewModel.NavigationArgs,
    private val invoiceRepository: InvoiceRepository
) : CreateInvoiceViewModel,
    BaseViewModel<CreateInvoiceViewModel.NavigationArgs, CreateInvoiceViewModel.State>(args) {

    data class ModernLineItemViewModel(val lineItem: LineItem) :
        CreateInvoiceViewModel.LineItemViewModel {
        override val amount: BigInteger
            get() = lineItem.amount.value.toBigInteger()
        override val title: String
            get() = lineItem.title
        override val description: String
            get() = lineItem.description
        override val sku: String
            get() = lineItem.sku?.bytes?.let { String(it) } ?: ""

        override fun onItemTapped() {

        }
    }

    private val modernAddLineItemViewModel = ModernAddLineItemViewModel(::addLineItem)

    private data class ModernAddLineItemViewModel(
        private val addLineItem: () -> Unit
    ) : CreateInvoiceViewModel.AddLineItemViewModel {
        override fun onItemTapped() = addLineItem()
    }

    override fun onTitleUpdated(value: String) {
        updateState {
            it.copy(
                title = value,
                listItems = generateListItems(),
                canCreateInvoice = canCreateInvoice()
            )
        }
    }

    override fun onDescriptionUpdated(value: String) {
        updateState {
            it.copy(
                description = value,
                listItems = generateListItems(),
                canCreateInvoice = canCreateInvoice()
            )
        }
    }

    override fun onAmountUpdated(value: BigInteger) {
        updateState {
            it.copy(
                amount = value,
                listItems = generateListItems(),
                canCreateInvoice = canCreateInvoice()
            )
        }
    }

    override fun onSkuUpdated(value: String) {
        updateState {
            it.copy(
                sku = value,
                listItems = generateListItems(),
                canCreateInvoice = canCreateInvoice()
            )
        }
    }

    override fun onCreateInvoiceTapped() {
        withState {
            invoiceRepository.addInvoice(
                createInvoice(lineItems)
            ).resolve()
        }
    }

    private fun createInvoice(lineItems: List<CreateInvoiceViewModel.LineItemViewModel>): Invoice {
        return Invoice.Builder()
            .addLineItems(lineItems.mapNotNull { (it as? ModernLineItemViewModel)?.lineItem })
            .build()
    }

    override fun getDefaultState(): CreateInvoiceViewModel.State =
        CreateInvoiceViewModel.State(
            "",
            "",
            BigInteger.ZERO,
            "",
            "",
            emptyList(),
            emptyList(),
            false
        )

    private fun canAddLineItem(): Boolean =
        withState { amount != BigInteger.ZERO && title.isNotEmpty() }

    private fun canCreateInvoice(): Boolean =
        withState {
            lineItems.isNotEmpty()
        }.also {
            if (it) {
                updateState {
                    it.copy(invoiceId = createInvoice(it.lineItems).id.invoiceHash.encodedValue)
                }
            }
        }

    private fun addLineItem() = updateState {
        it.copy(
            lineItems = listOf(
                *it.lineItems.toTypedArray(),
                ModernLineItemViewModel(
                    LineItem.Builder(it.title, KinAmount(it.amount.toBigDecimal()))
                        .setDescription(it.description)
                        .apply {
                            if (it.sku.isNotEmpty()) setSKU(SKU(it.sku.toByteArray()))
                        }
                        .build()
                )
            )
        )
    }

    private fun generateListItems(): List<Any> {
        return if (canAddLineItem()) {
            listOf(modernAddLineItemViewModel)
        } else emptyList()
    }
}
