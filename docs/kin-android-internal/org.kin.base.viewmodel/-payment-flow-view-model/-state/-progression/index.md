[kin-android](../../../../index.md) / [org.kin.base.viewmodel](../../../index.md) / [PaymentFlowViewModel](../../index.md) / [State](../index.md) / [Progression](./index.md)

# Progression

`sealed class Progression`

### Types

| Name | Summary |
|---|---|
| [Init](-init.md) | `object Init : Progression` |
| [PaymentConfirmation](-payment-confirmation/index.md) | `data class PaymentConfirmation : Progression` |
| [PaymentError](-payment-error/index.md) | `data class PaymentError : Progression` |
| [PaymentProcessing](-payment-processing.md) | `object PaymentProcessing : Progression` |
| [PaymentSuccess](-payment-success/index.md) | `data class PaymentSuccess : Progression` |

### Properties

| Name | Summary |
|---|---|
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
