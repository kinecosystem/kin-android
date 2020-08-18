package org.kin.sdk.demo.viewmodel

import org.kin.sdk.design.viewmodel.tools.ViewModel
import java.math.BigInteger

interface InvoicesViewModel : ViewModel<InvoicesViewModel.NavigationArgs, InvoicesViewModel.State> {

    data class NavigationArgs(val payerAccountId: String? = null)

    data class State(val actions: List<Any>, val invoices: List<InvoiceItemViewModel>)

    interface InvoiceItemViewModel {
        val firstItemTitle: String
        val invoiceId: String
        val itemCount: Int
        val amount: BigInteger

        fun onItemTapped()
    }

    interface CreateInvoiceItemViewModel {
        fun onItemTapped()
    }
}
