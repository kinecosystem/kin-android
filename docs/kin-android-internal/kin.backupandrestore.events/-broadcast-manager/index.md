[kin-android](../../index.md) / [kin.backupandrestore.events](../index.md) / [BroadcastManager](./index.md)

# BroadcastManager

`interface BroadcastManager`

### Types

| Name | Summary |
|---|---|
| [Listener](-listener/index.md) | `interface Listener` |

### Functions

| Name | Summary |
|---|---|
| [register](register.md) | `abstract fun register(listener: Listener, actionName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendEvent](send-event.md) | `abstract fun sendEvent(data: Intent!, actionName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setActivityResult](set-activity-result.md) | `abstract fun setActivityResult(resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [unregisterAll](unregister-all.md) | `abstract fun unregisterAll(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BroadcastManagerImpl](../-broadcast-manager-impl/index.md) | `open class BroadcastManagerImpl : `[`BroadcastManager`](./index.md) |
