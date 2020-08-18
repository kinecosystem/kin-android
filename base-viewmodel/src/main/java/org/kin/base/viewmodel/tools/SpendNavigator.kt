package org.kin.base.viewmodel.tools

import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.sdk.design.viewmodel.Navigator

interface SpendNavigator : Navigator {
    fun navigateToForResult(
        args: PaymentFlowViewModel.NavigationArgs,
        onResult: (PaymentFlowViewModel.Result) -> Unit
    )
}
