package org.kin.sdk.demo.view

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import org.kin.sdk.demo.ResolverProvider
import org.kin.sdk.demo.R
import org.kin.sdk.design.view.tools.BaseActivity
import org.kin.sdk.design.view.widget.PrimaryButton
import org.kin.sdk.design.view.widget.internal.PrimaryTextView
import org.kin.sdk.design.view.widget.internal.StandardDialog
import org.kin.sdk.design.view.widget.internal.StandardEditText
import org.kin.sdk.design.view.widget.internal.VerticalRecyclerView
import org.kin.sdk.design.view.tools.addBase32ChangedListener
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.build
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.updateItems
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.design.view.tools.addBase58ChangedListener

class TransactionLoadTestingActivity : BaseActivity<TransactionLoadTestingViewModel, TransactionLoadTestingViewModel.NavigationArgs, TransactionLoadTestingViewModel.State, ResolverProvider, DemoNavigator>() {
    object BundleKeys {
        const val walletIndex: String = "TransactionLoadTestingActivity.WALLET_INDEX"
        const val publicAddress: String = "TransactionLoadTestingActivity.PUBLIC_ADDRESS"
    }

    override val navigator: DemoNavigator by lazy {
        ActivityNavigatorImpl(this)
    }

    private lateinit var startButton: PrimaryButton
    private lateinit var publicAddressView: StandardEditText
    private lateinit var testResults: VerticalRecyclerView

    override fun createViewModel(bundle: Bundle): TransactionLoadTestingViewModel {
        return resolver.resolver.resolve(TransactionLoadTestingViewModel.NavigationArgs(
            bundle.getInt(BundleKeys.walletIndex),
            bundle.getString(BundleKeys.publicAddress)!!
        ), navigator)
    }

    override fun createView(context: Context): ViewGroup {
        val rootLayout = super.createView(context)

        with(LinearLayout(context)) {
            val formLayout = this

            formLayout.orientation = LinearLayout.VERTICAL

            publicAddressView = with(StandardEditText(context)) {
                setHintText(getString(R.string.hint_public_address))
                innerEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
                innerEditText.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                innerEditText.setSingleLine()
                addTo(formLayout, bottomMargin = 8.dip)
            }

            testResults = with(VerticalRecyclerView(context)) {
                build {
                    layout<PrimaryTextView, TransactionLoadTestingViewModel.TestRunViewModel> {
                        create { context ->
                            PrimaryTextView(
                                context
                            ).apply {
                                setPadding(0.dip, 4.dip, 0.dip, 4.dip)
                            }
                        }

                        bind { view, viewModel ->
                            view.text = viewModel.duration.toString()
                        }
                    }
                }

                addTo(formLayout, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
            }

            addTo(rootLayout, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f).apply {
                setMargins(12.dip, 12.dip, 12.dip, 12.dip)
            })
        }

        startButton = with(PrimaryButton(context)) {
            setText(R.string.title_send_transaction)
            addTo(rootLayout, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(12.dip, 12.dip, 12.dip, 12.dip)
            })
        }

        return rootLayout
    }

    override fun onBindView(viewModel: TransactionLoadTestingViewModel) {
        startButton.setOnClickListener {
            showSpinner()

            viewModel.onStartTapped { ex ->
                hideSpinner()

                if (ex != null) {
                    startButton.post {
                        StandardDialog.error(
                            startButton.context,
                            title = getString(R.string.title_error),
                            description = getString(R.string.title_send_transaction),
                            error = ex.localizedMessage ?: ex.toString()
                        )
                    }
                } else {
                    startButton.post {
                        StandardDialog.confirm(
                            startButton.context,
                            title = "Results",//getString(R.string.title_error),
                            description = "p50: ${finalState?.p50}, p95: ${finalState?.p95}, p99: ${finalState?.p99}",//getString(R.string.title_send_transaction),
                            confirmed = {
                                finish()
                            }
                        )
                    }
                }
            }
        }

        publicAddressView.innerEditText.addBase58ChangedListener { viewModel.onDestinationAddressUpdated(it) }
    }

    var finalState: TransactionLoadTestingViewModel.State? = null

    override fun onStateUpdated(state: TransactionLoadTestingViewModel.State) {
        testResults.updateItems(state.testRuns)
        finalState = state
    }
}
