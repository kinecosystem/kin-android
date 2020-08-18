[kin-android](../../index.md) / [org.kin.sdk.spend.view](../index.md) / [PaymentFlowFragment](./index.md)

# PaymentFlowFragment

`class PaymentFlowFragment : RoundedBottomSheetDialogFragment`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `PaymentFlowFragment(spendNavigator: `[`SpendNavigator`](../../org.kin.base.viewmodel.tools/-spend-navigator/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [cancelButton](cancel-button.md) | `lateinit var cancelButton: StandardButton` |
| [confirmationTitleView](confirmation-title-view.md) | `lateinit var confirmationTitleView: SecondaryTextView` |
| [confirmButton](confirm-button.md) | `lateinit var confirmButton: StandardButton` |
| [errorActionButton](error-action-button.md) | `lateinit var errorActionButton: StandardButton` |
| [errorReasonTextView](error-reason-text-view.md) | `lateinit var errorReasonTextView: SecondaryTextView` |
| [headerLayout](header-layout.md) | `lateinit var headerLayout: LinearLayout` |
| [kinAmountView](kin-amount-view.md) | `lateinit var kinAmountView: KinAmountView` |
| [paymentConfirmationProcessingAppImageView](payment-confirmation-processing-app-image-view.md) | `lateinit var paymentConfirmationProcessingAppImageView: AppCompatImageView` |
| [paymentConfirmationView](payment-confirmation-view.md) | `lateinit var paymentConfirmationView: LinearLayout` |
| [paymentErrorView](payment-error-view.md) | `lateinit var paymentErrorView: LinearLayout` |
| [paymentProcessingView](payment-processing-view.md) | `lateinit var paymentProcessingView: LinearLayout` |
| [paymentSuccessView](payment-success-view.md) | `lateinit var paymentSuccessView: LinearLayout` |
| [rootLayout](root-layout.md) | `lateinit var rootLayout: FrameLayout` |

### Functions

| Name | Summary |
|---|---|
| [addHeaderView](add-header-view.md) | `fun addHeaderView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [addPaymentConfirmationView](add-payment-confirmation-view.md) | `fun addPaymentConfirmationView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [addPaymentErrorView](add-payment-error-view.md) | `fun addPaymentErrorView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [addPaymentProcessingView](add-payment-processing-view.md) | `fun addPaymentProcessingView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [addPaymentSuccessView](add-payment-success-view.md) | `fun addPaymentSuccessView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBindView](on-bind-view.md) | `fun onBindView(viewModel: `[`PaymentFlowViewModel`](../../org.kin.base.viewmodel/-payment-flow-view-model/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreateView](on-create-view.md) | `fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?` |
| [onPause](on-pause.md) | `fun onPause(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onStateUpdated](on-state-updated.md) | `fun onStateUpdated(state: State): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showPaymentConfirmationView](show-payment-confirmation-view.md) | `fun showPaymentConfirmationView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showPaymentErrorView](show-payment-error-view.md) | `fun showPaymentErrorView(reasonString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showPaymentProcessingView](show-payment-processing-view.md) | `fun showPaymentProcessingView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showPaymentSuccessView](show-payment-success-view.md) | `fun showPaymentSuccessView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
