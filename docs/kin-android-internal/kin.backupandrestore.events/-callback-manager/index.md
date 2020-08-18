[kin-android](../../index.md) / [kin.backupandrestore.events](../index.md) / [CallbackManager](./index.md)

# CallbackManager

`open class CallbackManager`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `CallbackManager(eventDispatcher: `[`EventDispatcher`](../-event-dispatcher/index.md)`)`<br>`CallbackManager(eventDispatcher: `[`EventDispatcher`](../-event-dispatcher/index.md)`, reqCodeBackup: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, reqCodeRestore: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)` |

### Functions

| Name | Summary |
|---|---|
| [onActivityResult](on-activity-result.md) | `open fun onActivityResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendBackupEvent](send-backup-event.md) | `open fun sendBackupEvent(eventCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendBackupSuccessResult](send-backup-success-result.md) | `open fun sendBackupSuccessResult(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendRestoreEvent](send-restore-event.md) | `open fun sendRestoreEvent(eventCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendRestoreSuccessResult](send-restore-success-result.md) | `open fun sendRestoreSuccessResult(publicAddress: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setBackupCallback](set-backup-callback.md) | `open fun setBackupCallback(backupCallback: `[`BackupCallback`](../../kin.backupandrestore/-backup-callback/index.md)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setBackupEvents](set-backup-events.md) | `open fun setBackupEvents(backupEvents: `[`BackupEvents`](../../kin.backupandrestore/-backup-events/index.md)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setCancelledResult](set-cancelled-result.md) | `open fun setCancelledResult(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setInternalRestoreCallback](set-internal-restore-callback.md) | `open fun setInternalRestoreCallback(internalRestoreCallback: `[`InternalRestoreCallback`](../-internal-restore-callback/index.md)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setRestoreEvents](set-restore-events.md) | `open fun setRestoreEvents(restoreEvents: `[`RestoreEvents`](../../kin.backupandrestore/-restore-events/index.md)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [unregisterCallbacksAndEvents](unregister-callbacks-and-events.md) | `open fun unregisterCallbacksAndEvents(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
