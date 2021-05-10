package org.kin.sdk.demo.view

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import org.kin.sdk.demo.R
import org.kin.sdk.demo.ResolverProvider
import org.kin.sdk.demo.view.custom.WalletListItemView
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.HomeViewModel.NavigationArgs.ResolverType.Compat
import org.kin.sdk.demo.viewmodel.HomeViewModel.NavigationArgs.ResolverType.Modern
import org.kin.sdk.design.view.tools.BaseActivity
import org.kin.sdk.design.view.tools.RecyclerViewTools
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.build
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.updateItems
import org.kin.sdk.design.view.widget.PrimaryButton
import org.kin.sdk.design.view.widget.internal.ActionListItemView
import org.kin.sdk.design.view.widget.internal.VerticalRecyclerView

class HomeActivity :
    BaseActivity<HomeViewModel, HomeViewModel.NavigationArgs, HomeViewModel.State, ResolverProvider, DemoNavigator>() {

    object BundleKeys {
        const val resolverType: String = "HomeActivity.RESOLVER_TYPE"
    }

    override val navigator: DemoNavigator by lazy {
        ActivityNavigatorImpl(this)
    }

    private lateinit var walletItems: RecyclerView

    override fun createViewModel(bundle: Bundle): HomeViewModel {
        val resolverType =
            HomeViewModel.NavigationArgs.ResolverType.fromValue(bundle.getInt(BundleKeys.resolverType))
        when (resolverType) {
            Modern -> resolver.resolver = resolver.modernResolver
        }
        return resolver.resolver.resolve(HomeViewModel.NavigationArgs(resolverType), navigator)
    }

    override fun createView(context: Context): ViewGroup {
        val rootLayout = super.createView(context)

        setTitle(R.string.title_home_screen)

        walletItems = with(VerticalRecyclerView(context)) {
            build {
                layout<WalletListItemView, HomeViewModel.WalletItemViewModel> {
                    create(::WalletListItemView)

                    bind { view, viewModel ->
                        view.address = viewModel.publicAddress
                        view.setOnClickListener { viewModel.onItemTapped() }
                    }
                }

                layout<ActionListItemView, HomeViewModel.AddWalletItemViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.add_new_wallet)
                        view.isAdditive = true
                        view.setOnClickListener {
                            viewModel.onItemTapped()
                        }
                    }
                }

                layout<ActionListItemView, HomeViewModel.ImportWalletItemViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_import_kin)
                        view.description = getString(R.string.description_import_kin)
                        view.setOnClickListener { viewModel.onItemTapped(this@HomeActivity) }
                    }
                }

                layout<ActionListItemView, HomeViewModel.InvoiceItemViewModel> {
                    create(::ActionListItemView)

                    bind { view, viewModel ->
                        view.title = getString(R.string.title_invoices)
                        view.description = getString(R.string.description_invoices)
                        view.setOnClickListener { viewModel.onItemTapped() }
                    }
                }
            }

            addTo(rootLayout, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
        }

        return rootLayout
    }

    override fun onResume() {
        super.onResume()

        viewModel.pokeWalletUpdate()
    }

    override fun onBindView(viewModel: HomeViewModel) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onStateUpdated(state: HomeViewModel.State) {
        val testWallets = if (state.testNetWallets.isNotEmpty()) {
            listOf(
                RecyclerViewTools.header(R.string.title_test_network),
                *state.testNetWallets.toTypedArray()
            )
        } else {
            emptyList()
        }

        val mainWallets = if (state.mainNetWallets.isNotEmpty()) {
            listOf(
                RecyclerViewTools.header(R.string.title_main_network),
                *state.mainNetWallets.toTypedArray()
            )
        } else {
            emptyList()
        }

        val allWallets = testWallets + mainWallets

        walletItems.updateItems(
            if (allWallets.isNotEmpty()) {
                state.otherActions + allWallets
            } else {
                listOf(RecyclerViewTools.placeholder(R.string.placeholder_no_wallets))
            }
        )
    }
}
