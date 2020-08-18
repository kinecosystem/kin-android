package org.kin.sdk.demo.viewmodel.modern

import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.SHA224Hash
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.repository.KinAccountContextRepository
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.viewmodel.utils.toRenderableInvoice
import org.kin.sdk.demo.di.modern.DemoAppConfig
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.FullInvoiceViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.design.viewmodel.structs.RenderableInvoice
import org.kin.sdk.design.viewmodel.tools.BaseViewModel
import java.math.BigDecimal

class ModernFullInvoiceViewModel(
    private val navigator: DemoNavigator,
    args: FullInvoiceViewModel.NavigationArgs,
    private val invoiceRepository: InvoiceRepository,
    private val kinAccountContextRepository: KinAccountContextRepository
) : FullInvoiceViewModel,
    BaseViewModel<FullInvoiceViewModel.NavigationArgs, FullInvoiceViewModel.State>(args) {

    private val invoice: Promise<Optional<Invoice>> =
        invoiceRepository.invoiceById(Invoice.Id(SHA224Hash(args.invoiceId)))

    private val payerkinAccountContext =
        args.payerAccountId?.let {
            kinAccountContextRepository.getKinAccountContext(
                KinAccount.Id(it)
            )
        }

    init {
        invoice.then {
            it.map { invoice ->
                updateState { it: FullInvoiceViewModel.State ->
                    val renderableInvoice = invoice.toRenderableInvoice(KinAmount(args.amountPaid ?: BigDecimal.ZERO))
                    it.copy(
                        invoice = renderableInvoice,
                        items = renderableInvoice.items.map { LineItemItemViewModel(it) },
                        invoiceBytes = invoice.toProtoBytes()
                    )
                }
            }.orElse {
                // TODO: Shouldn't happen, but show error that the referenced invoice can't be found?
            }
        }

        if (args.payerAccountId != null) {
            payerkinAccountContext?.calculateFee(1)
                ?.then { fee ->
                    updateState {
                        it.copy(
                            invoice = it.invoice?.copy(
                                fee = fee.toKin().value,
                                total = it.invoice?.let { it.total + fee.toKin().value }
                                    ?: BigDecimal.ZERO)
                        )
                    }
                }
        }
    }

    override fun onPurchaseTapped() {
        args.payerAccountId?.let { payerAccountId ->
            navigator.navigateToForResult(
                PaymentFlowViewModel.NavigationArgs(
                    args.invoiceId,
                    payerAccountId,
                    DemoAppConfig.DEMO_APP_IDX.value
                )
            ) {
                when (it) {
                    is PaymentFlowViewModel.Result.Success -> {
                        navigator.navigateTo(
                            WalletViewModel.NavigationArgs(-1, payerAccountId)
                        )
                    }
                    is PaymentFlowViewModel.Result.Failure -> {
                        // Do Nothing
                    }
                }
            }
        }
    }

    override fun getDefaultState(): FullInvoiceViewModel.State =
        FullInvoiceViewModel.State(null, emptyList(), isReadOnly = args.readOnly)

    data class LineItemItemViewModel(
        private val it: RenderableInvoice.RenderableLineItem
    ) : FullInvoiceViewModel.LineItemItemViewModel {
        override val item: RenderableInvoice.RenderableLineItem
            get() = it

        override fun onItemTapped() = Unit
    }
}


