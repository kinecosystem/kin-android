package org.kin.sdk.demo.view

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import org.kin.sdk.demo.R
import org.kin.sdk.demo.view.custom.ActionListItemView
import org.kin.sdk.demo.view.custom.PrimaryButton
import org.kin.sdk.demo.view.custom.VerticalRecyclerView
import org.kin.sdk.demo.view.custom.WalletListItemView
import org.kin.sdk.demo.view.tools.BaseActivity
import org.kin.sdk.demo.view.tools.RecyclerViewTools
import org.kin.sdk.demo.view.tools.addTo
import org.kin.sdk.demo.view.tools.build
import org.kin.sdk.demo.view.tools.dip
import org.kin.sdk.demo.view.tools.updateItems
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.HomeViewModel.NavigationArgs.ResolverType.Compat
import org.kin.sdk.demo.viewmodel.HomeViewModel.NavigationArgs.ResolverType.Modern

class HomeActivity :
    BaseActivity<HomeViewModel, HomeViewModel.NavigationArgs, HomeViewModel.State>() {

    object BundleKeys {
        const val resolverType: String = "HomeActivity.RESOLVER_TYPE"
    }

    private lateinit var modernButton: PrimaryButton
    private lateinit var compatButton: PrimaryButton
    private lateinit var walletItems: RecyclerView

    override fun createViewModel(bundle: Bundle): HomeViewModel {
        val resolverType =
            HomeViewModel.NavigationArgs.ResolverType.fromValue(bundle.getInt(BundleKeys.resolverType))
        when (resolverType) {
            Modern -> applicationResolver.resolver = applicationResolver.modernResolver
            Compat -> applicationResolver.resolver = applicationResolver.compatResolver
        }
        return resolver.resolve(HomeViewModel.NavigationArgs(resolverType), navigator)
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
            }

            addTo(rootLayout, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
        }

        modernButton = with(PrimaryButton(context)) {
            text = context.getString(R.string.use_base_lib)
            addTo(rootLayout, leftMargin = 16.dip, rightMargin = 16.dip, bottomMargin = 16.dip)
        }

        compatButton = with(PrimaryButton(context)) {
            text = context.getString(R.string.use_base_compat_lib)
            addTo(rootLayout, leftMargin = 16.dip, rightMargin = 16.dip, bottomMargin = 16.dip)
        }

        return rootLayout
    }

    override fun onResume() {
        super.onResume()

        viewModel.pokeWalletUpdate()
    }

    override fun onBindView(viewModel: HomeViewModel) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        modernButton.setOnClickListener {
            viewModel.onUseBaseTapped()
        }
        compatButton.setOnClickListener {
            viewModel.onUseBaseCompatTapped()
        }
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
                allWallets
            } else {
                listOf(RecyclerViewTools.placeholder(R.string.placeholder_no_wallets))
            }
        )

        when(state.resolverType) {
            Modern -> {
                modernButton.isEnabled = false
                compatButton.isEnabled = true
            }
            Compat -> {
                modernButton.isEnabled = true
                compatButton.isEnabled = false
            }
        }
    }
}
