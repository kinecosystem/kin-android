[kin-android](../../index.md) / [kin.backupandrestore.backup.view](../index.md) / [BackupNavigator](./index.md)

# BackupNavigator

`interface BackupNavigator`

### Annotations

| Name | Summary |
|---|---|
| [Step](-step/index.md) | `class Step` |

### Properties

| Name | Summary |
|---|---|
| [STEP_CLOSE](-s-t-e-p_-c-l-o-s-e.md) | `static val STEP_CLOSE: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [STEP_CREATE_PASSWORD](-s-t-e-p_-c-r-e-a-t-e_-p-a-s-s-w-o-r-d.md) | `static val STEP_CREATE_PASSWORD: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [STEP_SAVE_AND_SHARE](-s-t-e-p_-s-a-v-e_-a-n-d_-s-h-a-r-e.md) | `static val STEP_SAVE_AND_SHARE: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [STEP_START](-s-t-e-p_-s-t-a-r-t.md) | `static val STEP_START: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [STEP_WELL_DONE](-s-t-e-p_-w-e-l-l_-d-o-n-e.md) | `static val STEP_WELL_DONE: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [closeFlow](close-flow.md) | `abstract fun closeFlow(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToCreatePasswordPage](navigate-to-create-password-page.md) | `abstract fun navigateToCreatePasswordPage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToSaveAndSharePage](navigate-to-save-and-share-page.md) | `abstract fun navigateToSaveAndSharePage(accountKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToWellDonePage](navigate-to-well-done-page.md) | `abstract fun navigateToWellDonePage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BackupPresenter](../../kin.backupandrestore.backup.presenter/-backup-presenter/index.md) | `interface BackupPresenter : `[`BasePresenter`](../../kin.backupandrestore.base/-base-presenter/index.md)`<`[`BackupView`](../-backup-view/index.md)`!>, `[`BackupNavigator`](./index.md) |
