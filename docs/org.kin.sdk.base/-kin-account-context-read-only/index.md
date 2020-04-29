[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountContextReadOnly](./index.md)

# KinAccountContextReadOnly

`interface KinAccountContextReadOnly : `[`KinAccountReadOperations`](../-kin-account-read-operations/index.md)`, `[`KinPaymentReadOperations`](../-kin-payment-read-operations/index.md)

### Properties

| Name | Summary |
|---|---|
| [accountId](account-id.md) | `abstract val accountId: Id` |

### Inheritors

| Name | Summary |
|---|---|
| [KinAccountContext](../-kin-account-context/index.md) | `interface KinAccountContext : `[`KinAccountContextReadOnly`](./index.md)`, `[`KinPaymentWriteOperations`](../-kin-payment-write-operations/index.md) |
| [KinAccountContextReadOnlyImpl](../-kin-account-context-read-only-impl/index.md) | Instantiate a [KinAccountContextReadOnlyImpl](../-kin-account-context-read-only-impl/index.md) to operate on a [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) when you only have a [PublicKey](#) Can be used to:`class KinAccountContextReadOnlyImpl : `[`KinAccountContextBase`](../-kin-account-context-base/index.md)`, `[`KinAccountContextReadOnly`](./index.md) |
