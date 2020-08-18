[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountReadOperations](./index.md)

# KinAccountReadOperations

`interface KinAccountReadOperations : `[`KinAccountReadOperationsAltIdioms`](../-kin-account-read-operations-alt-idioms/index.md)

### Functions

| Name | Summary |
|---|---|
| [clearStorage](clear-storage.md) | Deletes the storage associated with the [accountId](#)`abstract fun clearStorage(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [getAccount](get-account.md) | Returns the account info`abstract fun getAccount(forceUpdate: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>`<br>`abstract fun getAccount(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [observeBalance](observe-balance.md) | Returns the current [Balance](#) and listens to future account balance changes.`abstract fun observeBalance(mode: `[`ObservationMode`](../-observation-mode/index.md)` = Passive): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinAccountContextBase](../-kin-account-context-base/index.md) | `abstract class KinAccountContextBase : `[`KinAccountReadOperations`](./index.md)`, `[`KinPaymentReadOperations`](../-kin-payment-read-operations/index.md) |
| [KinAccountContextReadOnly](../-kin-account-context-read-only/index.md) | `interface KinAccountContextReadOnly : `[`KinAccountReadOperations`](./index.md)`, `[`KinPaymentReadOperations`](../-kin-payment-read-operations/index.md) |
