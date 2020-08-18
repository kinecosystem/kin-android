[kin-android](../../index.md) / [kin.backupandrestore.events](../index.md) / [EventDispatcher](./index.md)

# EventDispatcher

`interface EventDispatcher`

### Annotations

| Name | Summary |
|---|---|
| [EventType](-event-type/index.md) | `class EventType` |

### Properties

| Name | Summary |
|---|---|
| [BACKUP_EVENTS](-b-a-c-k-u-p_-e-v-e-n-t-s.md) | `static val BACKUP_EVENTS: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [EXTRA_KEY_EVENT_ID](-e-x-t-r-a_-k-e-y_-e-v-e-n-t_-i-d.md) | `static val EXTRA_KEY_EVENT_ID: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [EXTRA_KEY_EVENT_TYPE](-e-x-t-r-a_-k-e-y_-e-v-e-n-t_-t-y-p-e.md) | `static val EXTRA_KEY_EVENT_TYPE: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [RESTORE_EVENTS](-r-e-s-t-o-r-e_-e-v-e-n-t-s.md) | `static val RESTORE_EVENTS: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [sendEvent](send-event.md) | `abstract fun sendEvent(eventType: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, eventID: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setActivityResult](set-activity-result.md) | `abstract fun setActivityResult(resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setBackupEvents](set-backup-events.md) | `abstract fun setBackupEvents(backupEvents: `[`BackupEvents`](../../kin.backupandrestore/-backup-events/index.md)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setRestoreEvents](set-restore-events.md) | `abstract fun setRestoreEvents(restoreEvents: `[`RestoreEvents`](../../kin.backupandrestore/-restore-events/index.md)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [unregister](unregister.md) | `abstract fun unregister(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [EventDispatcherImpl](../-event-dispatcher-impl/index.md) | `open class EventDispatcherImpl : `[`EventDispatcher`](./index.md) |
