package org.kin.sdk.demo.viewmodel

import com.sun.org.apache.xpath.internal.operations.Bool
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel.NavigationArgs
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel.State
import org.kin.sdk.design.viewmodel.structs.RenderableInvoice
import org.kin.sdk.design.viewmodel.tools.ViewModel
import java.math.BigDecimal

interface FullInvoiceViewModel : ViewModel<NavigationArgs, State> {

    data class NavigationArgs(
        val invoiceId: String,
        val payerAccountId: String? = null,
        val amountPaid: BigDecimal? = null,
        val readOnly: Boolean
    )

    data class State(
        val invoice: RenderableInvoice?,
        val items: List<FullInvoiceViewModel.LineItemItemViewModel>,
        val invoiceBytes: ByteArray? = null,
        val isReadOnly: Boolean = false
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is State) return false

            if (invoice != other.invoice) return false
            if (items != other.items) return false
            if (invoiceBytes != null) {
                if (other.invoiceBytes == null) return false
                if (!invoiceBytes.contentEquals(other.invoiceBytes)) return false
            } else if (other.invoiceBytes != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result = invoice?.hashCode() ?: 0
            result = 31 * result + items.hashCode()
            result = 31 * result + (invoiceBytes?.contentHashCode() ?: 0)
            return result
        }
    }

    interface LineItemItemViewModel {
        val item: RenderableInvoice.RenderableLineItem

        fun onItemTapped()
    }

    fun onPurchaseTapped()
}
