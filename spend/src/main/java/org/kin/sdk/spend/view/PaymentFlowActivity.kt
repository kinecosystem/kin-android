package org.kin.sdk.spend.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.kin.spend.R
import org.kin.base.viewmodel.PaymentFlowViewModel
import org.kin.base.viewmodel.PaymentFlowViewModel.Result.Failure.Reason
import org.kin.base.viewmodel.PaymentFlowViewModel.State.Progression
import org.kin.base.viewmodel.tools.SpendNavigator
import org.kin.sdk.base.viewmodel.di.MetaResolver
import org.kin.sdk.design.view.tools.InvisibleBaseActivity
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.fadeInViews
import org.kin.sdk.design.view.tools.fadeOutViews
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.tools.resolveDrawable
import org.kin.sdk.design.view.tools.tint
import org.kin.sdk.design.view.widget.KinAmountView
import org.kin.sdk.design.view.widget.StandardButton
import org.kin.sdk.design.view.widget.internal.RoundedBottomSheetDialogFragment
import org.kin.sdk.design.view.widget.internal.SecondaryTextView
import org.kin.sdk.spend.navigation.SpendNavigatorImpl
import org.kin.sdk.spend.view.PaymentFlowActivity.BundleKeys.toNavArgs
import java.util.Timer
import kotlin.concurrent.schedule

class PaymentFlowActivity :
    InvisibleBaseActivity<PaymentFlowViewModel, PaymentFlowViewModel.NavigationArgs, PaymentFlowViewModel.State, MetaResolver, SpendNavigatorImpl>() {

    object BundleKeys {
        // Nav Params
        const val invoiceId: String = "PaymentFlowActivity.INVOICE_ID"
        const val payerAccountId: String = "PaymentFlowActivity.PAYER_ACCOUNT_ID"
        const val processingAppIdx: String = "PaymentFlowActivity.PROCESSING_APP_IDX"

        // Result Extras
        const val resultTransactionHash: String = "PaymentFlowActivity.TRANSACTION_HASH"
        const val resultFailureType: String = "PaymentFlowActivity.FAILURE_TYPE"

        fun Bundle.toNavArgs(): PaymentFlowViewModel.NavigationArgs =
            PaymentFlowViewModel.NavigationArgs(
                getString(invoiceId)!!,
                getString(payerAccountId)!!,
                getInt(processingAppIdx)
            )
    }

    override val navigator: SpendNavigatorImpl = SpendNavigatorImpl(this)

    private lateinit var paymentFlowFragment: PaymentFlowFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentFlowFragment = PaymentFlowFragment(navigator)
            .apply {
                arguments = intent.extras ?: Bundle()
            }
        paymentFlowFragment.show(supportFragmentManager, "")
    }

    override fun createViewModel(bundle: Bundle): PaymentFlowViewModel {
        return resolver.spendResolver.resolve(bundle.toNavArgs(), navigator)
    }

    override fun onBindView(viewModel: PaymentFlowViewModel) {
        paymentFlowFragment.onBindView(viewModel)
    }

    override fun onStateUpdated(state: PaymentFlowViewModel.State) {
        paymentFlowFragment.onStateUpdated(state)
    }
}

class PaymentFlowFragment(private val spendNavigator: SpendNavigator) :
    RoundedBottomSheetDialogFragment() {

    private fun createView(context: Context): FrameLayout {
        val rootLayout = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        return rootLayout
    }

    lateinit var rootLayout: FrameLayout

    lateinit var headerLayout: LinearLayout
    lateinit var cancelButton: StandardButton

    lateinit var paymentConfirmationView: LinearLayout
    lateinit var paymentConfirmationProcessingAppImageView: AppCompatImageView
    lateinit var kinAmountView: KinAmountView
    lateinit var confirmationTitleView: SecondaryTextView
    lateinit var confirmButton: StandardButton

    lateinit var paymentProcessingView: LinearLayout

    lateinit var paymentSuccessView: LinearLayout

    lateinit var paymentErrorView: LinearLayout
    lateinit var errorReasonTextView: SecondaryTextView
    lateinit var errorActionButton: StandardButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        with(createView(inflater.context)) {
            rootLayout = this
        }

        addHeaderView()
        addPaymentConfirmationView()
        addPaymentProcessingView()
        addPaymentSuccessView()
        addPaymentErrorView()

        isCancelable = false

        return rootLayout
    }

    fun onBindView(viewModel: PaymentFlowViewModel) {
        cancelButton.setOnClickListener {
            viewModel.onCancelTapped {
                activity?.setResult(
                    Activity.RESULT_CANCELED,
                    Intent().apply {
                        putExtra(
                            PaymentFlowActivity.BundleKeys.resultFailureType,
                            Reason.CANCELLED.value
                        )
                    })
                spendNavigator.close()
            }
        }

        confirmButton.setOnClickListener {
            viewModel.onConfirmTapped()
        }
    }

    fun onStateUpdated(state: PaymentFlowViewModel.State) {
        rootLayout.post {
            state.appIconId?.let {
                paymentConfirmationProcessingAppImageView.apply {
                    setImageDrawable(context.resolveDrawable(it))
                }
            }

            when (state.progression) {
                Progression.Init -> Unit
                is Progression.PaymentConfirmation -> {
                    (state.progression as? Progression.PaymentConfirmation)?.let {
                        confirmationTitleView.text = String.format(
                            getString(R.string.kin_sdk_description_payment_flow_confirm_kin),
                            it.amount,
                            it.appName,
                            it.newBalanceAfter
                        )
                        kinAmountView.amount = it.amount.toBigDecimal()
                    }
                    showPaymentConfirmationView()
                }
                is Progression.PaymentProcessing -> showPaymentProcessingView()
                is Progression.PaymentSuccess -> {
                    (state.progression as? Progression.PaymentSuccess)?.let {
                        activity?.setResult(
                            Activity.RESULT_OK,
                            Intent().apply {
                                putExtra(
                                    PaymentFlowActivity.BundleKeys.resultTransactionHash,
                                    it.transactionHash
                                )
                            })
                    }
                    showPaymentSuccessView()
                    Timer().schedule(600) {
                        spendNavigator.close()
                    }
                }
                is Progression.PaymentError -> {
                    (state.progression as? Progression.PaymentError)?.let {
                        activity?.setResult(
                            Activity.RESULT_CANCELED,
                            Intent().apply {
                                putExtra(
                                    PaymentFlowActivity.BundleKeys.resultFailureType,
                                    it.reason.value
                                )
                            })


                        if (it.reason != Reason.CANCELLED) {
                            val errorString = when (it.reason) {
                                Reason.CANCELLED -> getString(R.string.kin_sdk_error_reason_payment_cancelled)
                                Reason.ALREADY_PURCHASED -> getString(R.string.kin_sdk_error_reason_already_purchased)
                                Reason.INSUFFICIENT_BALANCE -> String.format(
                                    getString(R.string.kin_sdk_error_reason_insufficient_balance),
                                    it.balance
                                )
                                Reason.UNKNOWN_PAYER_ACCOUNT -> getString(R.string.kin_sdk_error_reason_unknown_payer_account)
                                Reason.UNKNOWN_INVOICE -> getString(R.string.kin_sdk_error_reason_unknown_invoice)
                                Reason.MISCONFIGURED_REQUEST -> getString(R.string.kin_sdk_error_reason_misconfigured_request)
                                Reason.DENIED_BY_SERVICE -> getString(R.string.kin_sdk_error_reason_denied_by_service)
                                Reason.SDK_UPGRADE_REQUIRED -> getString(R.string.kin_sdk_error_reason_sdk_upgrade_required)
                                Reason.BAD_NETWORK -> getString(R.string.kin_sdk_error_reason_bad_network)
                                Reason.UNKNOWN_FAILURE -> getString(R.string.kin_sdk_error_reason_unknown)
                            }
                            showPaymentErrorView(errorString)
                        }
                    }
                }
            }
        }
    }

    fun addHeaderView() {
        with(LinearLayout(context)) {
            headerLayout = this

            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            visibility = View.GONE

            cancelButton = with(StandardButton(context!!)) {
                text = getString(R.string.kin_sdk_button_cancel)
                type = StandardButton.Type.TYPE_INLINE
                addTo(
                    headerLayout, LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }

            addTo(
                rootLayout,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    fun showPaymentConfirmationView() {
        fadeInViews(
            headerLayout,
            paymentConfirmationView
        )
        fadeOutViews(
            paymentProcessingView,
            paymentSuccessView,
            paymentErrorView
        )
    }

    fun addPaymentConfirmationView() {
        paymentConfirmationView = with(LinearLayout(context)) {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.GONE

            addTo(
                rootLayout,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        paymentConfirmationProcessingAppImageView = with(AppCompatImageView(context!!)) {
            addTo(
                paymentConfirmationView,
                LinearLayout.LayoutParams(
                    60.dip,
                    60.dip
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(8.dip, 58.dip - headerLayout.height, 8.dip, 0.dip)
                }
            )
        }

        kinAmountView = with(KinAmountView(context!!)) {
            textSize = 35f
            addTo(
                paymentConfirmationView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(8.dip, 16.dip, 8.dip, 0.dip)
                }
            )
        }

        confirmationTitleView = with(SecondaryTextView(context!!)) {
            gravity = Gravity.CENTER

            addTo(
                paymentConfirmationView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_VERTICAL
                    setMargins(48.dip, 10.dip, 48.dip, 0.dip)
                }
            )
        }

        confirmButton = with(StandardButton(context!!)) {
            text = getString(R.string.kin_sdk_title_confirm)
            type = StandardButton.Type.TYPE_POSITIVE
            setOnClickListener {
                showPaymentProcessingView()
            }
            addTo(
                paymentConfirmationView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(16.dip, 20.dip, 16.dip, 58.dip)
                }
            )
        }
    }

    fun showPaymentProcessingView() {
        fadeInViews(paymentProcessingView)
        fadeOutViews(
            headerLayout,
            paymentConfirmationView,
            paymentSuccessView,
            paymentErrorView
        )
    }

    fun addPaymentProcessingView() {
        paymentProcessingView = with(LinearLayout(context)) {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.GONE

            addTo(
                rootLayout,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        with(ProgressBar(context!!)) {
            val accentColor = context.resolveColor(R.color.kin_sdk_purple_dark)
            with(indeterminateDrawable) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        accentColor,
                        BlendModeCompat.SRC_ATOP
                    )
                } else {
                    @Suppress("DEPRECATION")
                    setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY)
                }
            }
            isIndeterminate = true

            addTo(
                paymentProcessingView,
                LinearLayout.LayoutParams(
                    64.dip,
                    64.dip
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(48.dip, 8.dip, 48.dip, 16.dip)
                }
            )
        }

        with(SecondaryTextView(context!!)) {
            text = getString(R.string.kin_sdk_description_payment_flow_processing)
            gravity = Gravity.CENTER

            addTo(
                paymentProcessingView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(48.dip, 0, 48.dip, 8.dip)
                }
            )
        }
    }

    fun showPaymentSuccessView() {
        fadeInViews(paymentSuccessView)
        fadeOutViews(
            headerLayout,
            paymentConfirmationView,
            paymentProcessingView,
            paymentErrorView
        )
    }

    fun addPaymentSuccessView() {
        paymentSuccessView = with(LinearLayout(context)) {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.GONE

            addTo(
                rootLayout,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        with(AppCompatImageView(context!!)) {
            setImageDrawable(
                context.resolveDrawable(R.drawable.ic_check_circle_outline_24px)
                    ?.tint(context.resolveColor(R.color.kin_sdk_green))
            )

            addTo(
                paymentSuccessView,
                LinearLayout.LayoutParams(
                    64.dip,
                    64.dip
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(8.dip, 8.dip, 8.dip, 16.dip)
                }
            )
        }

        with(SecondaryTextView(context!!)) {
            text = getString(R.string.kin_sdk_description_confirmed)
            gravity = Gravity.CENTER

            addTo(
                paymentSuccessView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(48.dip, 0, 48.dip, 8.dip)
                }
            )
        }
    }

    fun showPaymentErrorView(reasonString: String) {
        errorReasonTextView.text = reasonString

        fadeInViews(paymentErrorView)
        fadeOutViews(
            headerLayout,
            paymentConfirmationView,
            paymentProcessingView,
            paymentSuccessView
        )
    }

    fun addPaymentErrorView() {
        paymentErrorView = with(LinearLayout(context)) {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.GONE

            addTo(
                rootLayout,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        with(AppCompatImageView(context!!)) {
            setImageDrawable(
                context.resolveDrawable(R.drawable.ic_error_outline_24px)
                    ?.tint(context.resolveColor(R.color.kin_sdk_red))
            )
            addTo(
                paymentErrorView,
                LinearLayout.LayoutParams(
                    64.dip,
                    64.dip
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(48.dip, 8.dip, 48.dip, 16.dip)
                }
            )
        }

        errorReasonTextView = with(SecondaryTextView(context!!)) {
            text = getString(R.string.kin_sdk_error_reason_unknown)
            gravity = Gravity.CENTER

            addTo(
                paymentErrorView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(48.dip, 0, 48.dip, 8.dip)
                }
            )
        }

        errorActionButton = with(StandardButton(context!!)) {
            text = getString(R.string.kin_sdk_title_close)
            type = StandardButton.Type.TYPE_NEGATIVE
            setOnClickListener { spendNavigator.close() }
            addTo(
                paymentErrorView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    setMargins(16.dip, 24.dip, 16.dip, 16.dip)
                }
            )
        }
    }


    override fun onPause() {
        super.onPause()
        spendNavigator.close()
    }
}
