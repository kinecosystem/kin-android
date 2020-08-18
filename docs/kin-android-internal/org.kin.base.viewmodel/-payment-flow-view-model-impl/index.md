[kin-android](../../index.md) / [org.kin.base.viewmodel](../index.md) / [PaymentFlowViewModelImpl](./index.md)

# PaymentFlowViewModelImpl

`class PaymentFlowViewModelImpl : `[`PaymentFlowViewModel`](../-payment-flow-view-model/index.md)`, BaseViewModel<NavigationArgs, State>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `PaymentFlowViewModelImpl(spendNavigator: `[`SpendNavigator`](../../org.kin.base.viewmodel.tools/-spend-navigator/index.md)`, args: NavigationArgs, appInfoRepository: `[`AppInfoRepository`](../../org.kin.sdk.base.repository/-app-info-repository/index.md)`, invoiceRepository: `[`InvoiceRepository`](../../org.kin.sdk.base.repository/-invoice-repository/index.md)`, kinAccountContext: `[`KinAccountContext`](../../org.kin.sdk.base/-kin-account-context/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [getDefaultState](get-default-state.md) | `fun getDefaultState(): State` |
| [onCancelTapped](on-cancel-tapped.md) | `fun onCancelTapped(onCompleted: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onConfirmTapped](on-confirm-tapped.md) | `fun onConfirmTapped(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onStateUpdated](on-state-updated.md) | `fun onStateUpdated(state: State): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
