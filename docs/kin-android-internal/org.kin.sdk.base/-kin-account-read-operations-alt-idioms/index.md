[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountReadOperationsAltIdioms](./index.md)

# KinAccountReadOperationsAltIdioms

`interface KinAccountReadOperationsAltIdioms`

### Functions

| Name | Summary |
|---|---|
| [clearStorage](clear-storage.md) | `abstract fun clearStorage(clearCompleteCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getAccount](get-account.md) | `abstract fun getAccount(forceUpdate: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, accountCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [observeBalance](observe-balance.md) | `abstract fun observeBalance(mode: `[`ObservationMode`](../-observation-mode/index.md)` = Passive, balanceListener: `[`ValueListener`](../../org.kin.sdk.base.tools/-value-listener/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinBalance`](../../org.kin.sdk.base.models/-kin-balance/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinAccountReadOperations](../-kin-account-read-operations/index.md) | `interface KinAccountReadOperations : `[`KinAccountReadOperationsAltIdioms`](./index.md) |
