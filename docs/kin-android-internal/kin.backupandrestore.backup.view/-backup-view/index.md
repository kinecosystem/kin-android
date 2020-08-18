[kin-android](../../index.md) / [kin.backupandrestore.backup.view](../index.md) / [BackupView](./index.md)

# BackupView

`interface BackupView : `[`BaseView`](../../kin.backupandrestore.base/-base-view.md)`, `[`KeyboardHandler`](../../kin.backupandrestore.base/-keyboard-handler/index.md)

### Functions

| Name | Summary |
|---|---|
| [close](close.md) | `abstract fun close(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [moveToCreatePasswordPage](move-to-create-password-page.md) | `abstract fun moveToCreatePasswordPage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [moveToSaveAndSharePage](move-to-save-and-share-page.md) | `abstract fun moveToSaveAndSharePage(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [moveToWellDonePage](move-to-well-done-page.md) | `abstract fun moveToWellDonePage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackButtonClicked](on-back-button-clicked.md) | `abstract fun onBackButtonClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showError](show-error.md) | `abstract fun showError(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [startBackupFlow](start-backup-flow.md) | `abstract fun startBackupFlow(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BackupActivity](../-backup-activity/index.md) | `open class BackupActivity : `[`BaseToolbarActivity`](../../kin.backupandrestore.base/-base-toolbar-activity/index.md)`, `[`BackupView`](./index.md) |
