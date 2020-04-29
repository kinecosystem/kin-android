[kin-android](../index.md) / [org.kin.sdk.base](./index.md)

## Package org.kin.sdk.base

### Types

| Name | Summary |
|---|---|
| [KinAccountContext](-kin-account-context/index.md) | `interface KinAccountContext : `[`KinAccountContextReadOnly`](-kin-account-context-read-only/index.md)`, `[`KinPaymentWriteOperations`](-kin-payment-write-operations/index.md) |
| [KinAccountContextBase](-kin-account-context-base/index.md) | `abstract class KinAccountContextBase : `[`KinAccountReadOperations`](-kin-account-read-operations/index.md)`, `[`KinPaymentReadOperations`](-kin-payment-read-operations/index.md) |
| [KinAccountContextImpl](-kin-account-context-impl/index.md) | Instantiate a [KinAccountContextImpl](-kin-account-context-impl/index.md) to operate on a [KinAccount](../org.kin.sdk.base.models/-kin-account/index.md) when you have a [PrivateKey](#) Can be used to:`class KinAccountContextImpl : `[`KinAccountContextBase`](-kin-account-context-base/index.md)`, `[`KinAccountContext`](-kin-account-context/index.md) |
| [KinAccountContextReadOnly](-kin-account-context-read-only/index.md) | `interface KinAccountContextReadOnly : `[`KinAccountReadOperations`](-kin-account-read-operations/index.md)`, `[`KinPaymentReadOperations`](-kin-payment-read-operations/index.md) |
| [KinAccountContextReadOnlyImpl](-kin-account-context-read-only-impl/index.md) | Instantiate a [KinAccountContextReadOnlyImpl](-kin-account-context-read-only-impl/index.md) to operate on a [KinAccount](../org.kin.sdk.base.models/-kin-account/index.md) when you only have a [PublicKey](#) Can be used to:`class KinAccountContextReadOnlyImpl : `[`KinAccountContextBase`](-kin-account-context-base/index.md)`, `[`KinAccountContextReadOnly`](-kin-account-context-read-only/index.md) |
| [KinAccountReadOperations](-kin-account-read-operations/index.md) | `interface KinAccountReadOperations : `[`KinAccountReadOperationsAltIdioms`](-kin-account-read-operations-alt-idioms/index.md) |
| [KinAccountReadOperationsAltIdioms](-kin-account-read-operations-alt-idioms/index.md) | `interface KinAccountReadOperationsAltIdioms` |
| [KinEnvironment](-kin-environment/index.md) | `sealed class KinEnvironment` |
| [KinPaymentReadOperations](-kin-payment-read-operations/index.md) | `interface KinPaymentReadOperations : `[`KinPaymentReadOperationsAltIdioms`](-kin-payment-read-operations-alt-idioms/index.md) |
| [KinPaymentReadOperationsAltIdioms](-kin-payment-read-operations-alt-idioms/index.md) | `interface KinPaymentReadOperationsAltIdioms` |
| [KinPaymentWriteOperations](-kin-payment-write-operations/index.md) | `interface KinPaymentWriteOperations : `[`KinPaymentWriteOperationsAltIdioms`](-kin-payment-write-operations-alt-idioms/index.md) |
| [KinPaymentWriteOperationsAltIdioms](-kin-payment-write-operations-alt-idioms/index.md) | `interface KinPaymentWriteOperationsAltIdioms` |
| [ObservationMode](-observation-mode/index.md) | Describes the mode by which updates are presented to an [Observer](../org.kin.sdk.base.tools/-observer/index.md)`sealed class ObservationMode` |
