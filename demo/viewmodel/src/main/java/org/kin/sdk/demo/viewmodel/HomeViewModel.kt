package org.kin.sdk.demo.viewmodel

import org.kin.sdk.design.viewmodel.tools.ViewModel

interface HomeViewModel : ViewModel<HomeViewModel.NavigationArgs, HomeViewModel.State> {

    data class NavigationArgs(val resolverType: ResolverType = ResolverType.Modern) {
        sealed class ResolverType(val value: Int) {
            object Modern : ResolverType(0)
            object Compat : ResolverType(1)

            companion object {
                fun fromValue(value: Int): ResolverType {
                    return when (value) {
                        1 -> Compat
                        else -> Modern
                    }
                }
            }
        }
    }

    data class State(
        val resolverType: NavigationArgs.ResolverType,
        val otherActions: List<Any>,
        val testNetWallets: List<Any>,
        val mainNetWallets: List<Any>
    )

    interface WalletItemViewModel {
        val publicAddress: String

        fun onItemTapped()
    }

    interface AddWalletItemViewModel {
        fun onItemTapped()
    }

    interface ImportWalletItemViewModel {
        fun onItemTapped( /* this backcast passthrough is gross, but the old interface demands it */ activity: Any)
    }

    interface InvoiceItemViewModel {
        fun onItemTapped()
    }

    fun onCreateWalletTapped()

    fun onUseBaseTapped()
    fun onUseBaseCompatTapped()

    fun pokeWalletUpdate()
}
