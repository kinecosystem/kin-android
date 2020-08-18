[kin-android](../../index.md) / [kin.backupandrestore.restore.view](../index.md) / [RestoreView](./index.md)

# RestoreView

`interface RestoreView : `[`BaseView`](../../kin.backupandrestore.base/-base-view.md)

### Functions

| Name | Summary |
|---|---|
| [close](close.md) | `abstract fun close(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [closeKeyboard](close-keyboard.md) | `abstract fun closeKeyboard(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateBack](navigate-back.md) | `abstract fun navigateBack(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToEnterPassword](navigate-to-enter-password.md) | `abstract fun navigateToEnterPassword(keystoreData: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToRestoreCompleted](navigate-to-restore-completed.md) | `abstract fun navigateToRestoreCompleted(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToUpload](navigate-to-upload.md) | `abstract fun navigateToUpload(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showError](show-error.md) | `abstract fun showError(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [RestoreActivity](../-restore-activity/index.md) | `open class RestoreActivity : `[`BaseToolbarActivity`](../../kin.backupandrestore.base/-base-toolbar-activity/index.md)`, `[`RestoreView`](./index.md) |
