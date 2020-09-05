package org.kin.sdk.base.viewmodel.di

import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.base.viewmodel.PaymentFlowViewModelImpl
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.repository.AppInfoRepository
import org.kin.sdk.base.repository.InMemoryKinAccountContextRepositoryImpl
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.repository.KinAccountContextRepository

class SpendResolverImpl constructor(
    private val kinEnvironment: KinEnvironment.Agora,
    private val invoiceRepository: InvoiceRepository = kinEnvironment.invoiceRepository,
    private val appInfoRepository: AppInfoRepository = kinEnvironment.appInfoRepository,
    private val kinAccountContextRepository: KinAccountContextRepository
    = InMemoryKinAccountContextRepositoryImpl(kinEnvironment),
) : SpendResolver {

    override fun resolve(
        navigationArgs: PaymentFlowViewModel.NavigationArgs,
        spendNavigator: SpendNavigator,
    ): PaymentFlowViewModel {
        val kinAccountContext =
            kinAccountContextRepository
                .getKinAccountContext(
                    KinAccount.Id(navigationArgs.payerAccountId)
                )
                ?: throw RuntimeException("Missing Kin Account specified in PaymentFlowViewModel.NavigationArgs")

        return PaymentFlowViewModelImpl(
            spendNavigator,
            navigationArgs,
            appInfoRepository,
            invoiceRepository,
            kinAccountContext,
            kinEnvironment.logger
        )
    }
}
