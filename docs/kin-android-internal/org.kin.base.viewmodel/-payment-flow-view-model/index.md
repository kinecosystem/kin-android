[kin-android](../../index.md) / [org.kin.base.viewmodel](../index.md) / [PaymentFlowViewModel](./index.md)

# PaymentFlowViewModel

`interface PaymentFlowViewModel : ViewModel<NavigationArgs, State>`

### Types

| Name | Summary |
|---|---|
| [NavigationArgs](-navigation-args/index.md) | `data class NavigationArgs` |
| [Result](-result/index.md) | `sealed class Result` |
| [State](-state/index.md) | `data class State` |

### Functions

| Name | Summary |
|---|---|
| [onCancelTapped](on-cancel-tapped.md) | `abstract fun onCancelTapped(onCompleted: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onConfirmTapped](on-confirm-tapped.md) | `abstract fun onConfirmTapped(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [PaymentFlowViewModelImpl](../-payment-flow-view-model-impl/index.md) | `class PaymentFlowViewModelImpl : `[`PaymentFlowViewModel`](./index.md)`, BaseViewModel<NavigationArgs, State>` |
