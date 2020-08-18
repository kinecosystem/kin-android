[kin-android](../../index.md) / [kin.backupandrestore.events](../index.md) / [BroadcastManagerImpl](./index.md)

# BroadcastManagerImpl

`open class BroadcastManagerImpl : `[`BroadcastManager`](../-broadcast-manager/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BroadcastManagerImpl(activity: Activity)` |

### Functions

| Name | Summary |
|---|---|
| [register](register.md) | `open fun register(listener: Listener, actionName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendEvent](send-event.md) | `open fun sendEvent(data: Intent!, actionName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setActivityResult](set-activity-result.md) | `open fun setActivityResult(resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [unregisterAll](unregister-all.md) | `open fun unregisterAll(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
