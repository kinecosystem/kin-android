package org.kin.sdk.base.viewmodel.di

import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.design.di.Resolver

interface SpendResolver : Resolver {
    fun resolve(
        navigationArgs: PaymentFlowViewModel.NavigationArgs,
        spendNavigator: SpendNavigator
    ): PaymentFlowViewModel
}
