package org.kin.sdk.demo.viewmodel.modern

import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.tools.DisposeBag
import org.kin.sdk.demo.viewmodel.CreateInvoiceViewModel
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel
import org.kin.sdk.demo.viewmodel.InvoicesViewModel
import org.kin.sdk.demo.viewmodel.InvoicesViewModel.InvoiceItemViewModel
import org.kin.sdk.demo.viewmodel.InvoicesViewModel.NavigationArgs
import org.kin.sdk.demo.viewmodel.InvoicesViewModel.State
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.design.viewmodel.tools.BaseViewModel
import java.math.BigInteger

class ModernInvoicesViewModel(
    private val navigator: DemoNavigator,
    args: NavigationArgs,
    private val invoiceRepository: InvoiceRepository
) : InvoicesViewModel, BaseViewModel<NavigationArgs, State>(args) {

    private val disposeBag = DisposeBag()
    private val invoices = invoiceRepository.allInvoices()
        .add { invoices ->
            updateState {
                it.copy(
                    invoices = invoices.map { invoice ->
                        ModernInvoiceItemViewModel(invoice)
                    }
                )
            }
        }.disposedBy(disposeBag)

    private inner class ModernInvoiceItemViewModel(
        val invoice: Invoice
    ) : InvoiceItemViewModel {
        override val firstItemTitle: String
            get() = invoice.lineItems.first().title
        override val invoiceId: String
            get() = invoice.id.invoiceHash.encodedValue
        override val itemCount: Int
            get() = invoice.lineItems.size
        override val amount: BigInteger
            get() = invoice.total.value.toBigInteger()

        override fun onItemTapped() = navigateToViewInvoice(invoice.id)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ModernInvoiceItemViewModel) return false

            if (invoice != other.invoice) return false

            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    data class ModernCreateInvoiceItemViewModel(val navigator: DemoNavigator) :
        InvoicesViewModel.CreateInvoiceItemViewModel {
        override fun onItemTapped() = navigator.navigateTo(CreateInvoiceViewModel.NavigationArgs())
    }

    fun navigateToViewInvoice(invoiceId: Invoice.Id) =
        navigator.navigateTo(FullInvoiceViewModel.NavigationArgs(invoiceId.invoiceHash.encodedValue, args.payerAccountId, null, false))

    override fun getDefaultState(): State =
        State(listOf(ModernCreateInvoiceItemViewModel(navigator)), emptyList())

    override fun cleanup() {
        super.cleanup()
        disposeBag.dispose()
    }
}
