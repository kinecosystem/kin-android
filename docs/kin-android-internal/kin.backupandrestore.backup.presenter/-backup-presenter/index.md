[kin-android](../../index.md) / [kin.backupandrestore.backup.presenter](../index.md) / [BackupPresenter](./index.md)

# BackupPresenter

`interface BackupPresenter : `[`BasePresenter`](../../kin.backupandrestore.base/-base-presenter/index.md)`<`[`BackupView`](../../kin.backupandrestore.backup.view/-backup-view/index.md)`!>, `[`BackupNavigator`](../../kin.backupandrestore.backup.view/-backup-navigator/index.md)

### Functions

| Name | Summary |
|---|---|
| [getKinAccount](get-kin-account.md) | `abstract fun getKinAccount(): `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`!` |
| [onSaveInstanceState](on-save-instance-state.md) | `abstract fun onSaveInstanceState(outState: Bundle!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setAccountKey](set-account-key.md) | `abstract fun setAccountKey(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BackupPresenterImpl](../-backup-presenter-impl/index.md) | `open class BackupPresenterImpl : `[`BasePresenterImpl`](../../kin.backupandrestore.base/-base-presenter-impl/index.md)`<`[`BackupView`](../../kin.backupandrestore.backup.view/-backup-view/index.md)`!>, `[`BackupPresenter`](./index.md) |
