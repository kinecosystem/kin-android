[kin-android](../../index.md) / [org.kin.sdk.spend.view](../index.md) / [PaymentFlowActivity](./index.md)

# PaymentFlowActivity

`class PaymentFlowActivity : InvisibleBaseActivity<`[`PaymentFlowViewModel`](../../org.kin.base.viewmodel/-payment-flow-view-model/index.md)`, NavigationArgs, State, `[`MetaResolver`](../../org.kin.sdk.base.viewmodel.di/-meta-resolver/index.md)`, `[`SpendNavigatorImpl`](../../org.kin.sdk.spend.navigation/-spend-navigator-impl/index.md)`>`

### Types

| Name | Summary |
|---|---|
| [BundleKeys](-bundle-keys/index.md) | `object BundleKeys` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `PaymentFlowActivity()` |

### Properties

| Name | Summary |
|---|---|
| [navigator](navigator.md) | `val navigator: `[`SpendNavigatorImpl`](../../org.kin.sdk.spend.navigation/-spend-navigator-impl/index.md) |

### Functions

| Name | Summary |
|---|---|
| [createViewModel](create-view-model.md) | `fun createViewModel(bundle: Bundle): `[`PaymentFlowViewModel`](../../org.kin.base.viewmodel/-payment-flow-view-model/index.md) |
| [onBindView](on-bind-view.md) | `fun onBindView(viewModel: `[`PaymentFlowViewModel`](../../org.kin.base.viewmodel/-payment-flow-view-model/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreate](on-create.md) | `fun onCreate(savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onStateUpdated](on-state-updated.md) | `fun onStateUpdated(state: State): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
