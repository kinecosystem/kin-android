package org.kin.sdk.demo.view

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import org.kin.sdk.demo.ResolverProvider
import org.kin.sdk.demo.R
import org.kin.sdk.design.view.tools.BaseActivity
import org.kin.sdk.design.view.widget.PrimaryButton
import org.kin.sdk.design.view.widget.internal.StandardDialog
import org.kin.sdk.design.view.widget.internal.StandardEditText
import org.kin.sdk.design.view.tools.addBase32ChangedListener
import org.kin.sdk.design.view.tools.addIntegerChangedListener
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.demo.viewmodel.DemoNavigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel

class SendTransactionActivity : BaseActivity<SendTransactionViewModel, SendTransactionViewModel.NavigationArgs, SendTransactionViewModel.State, ResolverProvider, DemoNavigator>() {
    object BundleKeys {
        const val walletIndex: String = "SendTransactionActivity.WALLET_INDEX"
        const val walletPublicAddress: String = "SendTransactionActivity.WALLET_PUBLIC_ADDRESS"
    }

    override val navigator: DemoNavigator by lazy {
        ActivityNavigatorImpl(this)
    }

    private lateinit var sendButton: PrimaryButton
    private lateinit var memoView: StandardEditText
    private lateinit var feeView: StandardEditText
    private lateinit var amountView: StandardEditText
    private lateinit var publicAddressView: StandardEditText

    override fun createViewModel(bundle: Bundle): SendTransactionViewModel {
        return resolver.resolver.resolve(SendTransactionViewModel.NavigationArgs(bundle.getInt(BundleKeys.walletIndex), bundle.getString(BundleKeys.walletPublicAddress)!!), navigator)
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
            amountView = with(StandardEditText(context)) {
                setHintText(getString(R.string.hint_payment_amount))
                innerEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
                innerEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
                innerEditText.setSingleLine()
                addTo(formLayout, bottomMargin = 8.dip)
            }
            feeView = with(StandardEditText(context)) {
                setHintText(getString(R.string.hint_fee_amount))
                innerEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
                innerEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                innerEditText.setSingleLine()

                addTo(formLayout, bottomMargin = 8.dip)
            }
            memoView = with(StandardEditText(context)) {
                setHintText(getString(R.string.hint_memo))
                innerEditText.maxLines = 4
                innerEditText.imeOptions = EditorInfo.IME_ACTION_GO
                addTo(formLayout, bottomMargin = 8.dip)
            }

            addTo(rootLayout, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f).apply {
                setMargins(12.dip, 12.dip, 12.dip, 12.dip)
            })
        }

        sendButton = with(PrimaryButton(context)) {
            setText(R.string.title_send_transaction)
            addTo(rootLayout, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(12.dip, 12.dip, 12.dip, 12.dip)
            })
        }

        return rootLayout
    }

    override fun onBindView(viewModel: SendTransactionViewModel) {
        sendButton.setOnClickListener {
            showSpinner()

            viewModel.onSendTapped { ex ->
                hideSpinner()

                if (ex != null) {
                    sendButton.post {
                        StandardDialog.error(
                            sendButton.context,
                            title = getString(R.string.title_error),
                            description = getString(R.string.title_send_transaction),
                            error = ex.localizedMessage ?: ex.toString()
                        )
                    }
                } else {
                    finish()
                }
            }
        }

        publicAddressView.innerEditText.addBase32ChangedListener { viewModel.onDestinationAddressUpdated(it) }
        amountView.innerEditText.addIntegerChangedListener { viewModel.onAmountUpdated(it) }
        feeView.innerEditText.addIntegerChangedListener { viewModel.onFeeUpdated(it) }
        memoView.innerEditText.addTextChangedListener { viewModel.onMemoUpdated(it.toString()) }
    }

    override fun onStateUpdated(state: SendTransactionViewModel.State) {
    }
}
