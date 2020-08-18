[kin-android](../../index.md) / [kin.backupandrestore.backup.view](../index.md) / [BackupActivity](./index.md)

# BackupActivity

`open class BackupActivity : `[`BaseToolbarActivity`](../../kin.backupandrestore.base/-base-toolbar-activity/index.md)`, `[`BackupView`](../-backup-view/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BackupActivity()` |

### Properties

| Name | Summary |
|---|---|
| [MOVE_TO_SAVE_AND_SHARE](-m-o-v-e_-t-o_-s-a-v-e_-a-n-d_-s-h-a-r-e.md) | `static val MOVE_TO_SAVE_AND_SHARE: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [TAG_CREATE_PASSWORD_PAGE](-t-a-g_-c-r-e-a-t-e_-p-a-s-s-w-o-r-d_-p-a-g-e.md) | `static val TAG_CREATE_PASSWORD_PAGE: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!` |
| [TAG_SAVE_AND_SHARE_PAGE](-t-a-g_-s-a-v-e_-a-n-d_-s-h-a-r-e_-p-a-g-e.md) | `static val TAG_SAVE_AND_SHARE_PAGE: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!` |
| [TAG_WELL_DONE_PAGE](-t-a-g_-w-e-l-l_-d-o-n-e_-p-a-g-e.md) | `static val TAG_WELL_DONE_PAGE: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!` |
| [TOOLBAR_COLOR_ANIM_DURATION](-t-o-o-l-b-a-r_-c-o-l-o-r_-a-n-i-m_-d-u-r-a-t-i-o-n.md) | `static val TOOLBAR_COLOR_ANIM_DURATION: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| Name | Summary |
|---|---|
| [close](close.md) | `open fun close(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getContentLayout](get-content-layout.md) | `open fun getContentLayout(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [moveToCreatePasswordPage](move-to-create-password-page.md) | `open fun moveToCreatePasswordPage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [moveToSaveAndSharePage](move-to-save-and-share-page.md) | `open fun moveToSaveAndSharePage(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [moveToWellDonePage](move-to-well-done-page.md) | `open fun moveToWellDonePage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackButtonClicked](on-back-button-clicked.md) | `open fun onBackButtonClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackPressed](on-back-pressed.md) | `open fun onBackPressed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCreate](on-create.md) | `open fun onCreate(savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSaveInstanceState](on-save-instance-state.md) | `open fun onSaveInstanceState(outState: Bundle): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onStop](on-stop.md) | `open fun onStop(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showError](show-error.md) | `open fun showError(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [startBackupFlow](start-backup-flow.md) | `open fun startBackupFlow(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
