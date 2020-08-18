package org.kin.sdk.demo.viewmodel

import org.kin.sdk.design.viewmodel.tools.ViewModel
import java.math.BigInteger

interface CreateInvoiceViewModel :
    ViewModel<CreateInvoiceViewModel.NavigationArgs, CreateInvoiceViewModel.State> {

    class NavigationArgs

    data class State(
        val title: String,
        val description: String,
        val amount: BigInteger,
        val sku: String,
        val invoiceId: String,
        val lineItems: List<LineItemViewModel>,
        val listItems: List<Any>,
        val canCreateInvoice: Boolean
    )

    interface LineItemViewModel {
        val amount: BigInteger
        val title: String
        val description: String
        val sku: String

        fun onItemTapped()
    }

    interface LineItemAttItemViewModel

    interface AddLineItemViewModel {
        fun onItemTapped()
    }

    fun onTitleUpdated(value: String)

    fun onDescriptionUpdated(value: String)

    fun onAmountUpdated(value: BigInteger)

    fun onSkuUpdated(value: String)

    fun onCreateInvoiceTapped()
}
