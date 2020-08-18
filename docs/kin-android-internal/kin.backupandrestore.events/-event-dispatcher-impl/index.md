[kin-android](../../index.md) / [kin.backupandrestore.events](../index.md) / [EventDispatcherImpl](./index.md)

# EventDispatcherImpl

`open class EventDispatcherImpl : `[`EventDispatcher`](../-event-dispatcher/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `EventDispatcherImpl(broadcastManager: `[`BroadcastManager`](../-broadcast-manager/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [sendEvent](send-event.md) | `open fun sendEvent(eventType: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, eventID: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setActivityResult](set-activity-result.md) | `open fun setActivityResult(resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setBackupEvents](set-backup-events.md) | `open fun setBackupEvents(backupEvents: `[`BackupEvents`](../../kin.backupandrestore/-backup-events/index.md)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setRestoreEvents](set-restore-events.md) | `open fun setRestoreEvents(restoreEvents: `[`RestoreEvents`](../../kin.backupandrestore/-restore-events/index.md)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [unregister](unregister.md) | `open fun unregister(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
