[kin-android](../../index.md) / [kin.backupandrestore.base](../index.md) / [BasePresenter](./index.md)

# BasePresenter

`interface BasePresenter<T : `[`BaseView`](../-base-view.md)`!>`

### Functions

| Name | Summary |
|---|---|
| [getView](get-view.md) | `abstract fun getView(): T` |
| [onAttach](on-attach.md) | `abstract fun onAttach(view: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackClicked](on-back-clicked.md) | `abstract fun onBackClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onDetach](on-detach.md) | `abstract fun onDetach(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BackupInfoPresenter](../../kin.backupandrestore.backup.presenter/-backup-info-presenter/index.md) | `interface BackupInfoPresenter : `[`BasePresenter`](./index.md)`<`[`BaseView`](../-base-view.md)`!>` |
| [BackupPresenter](../../kin.backupandrestore.backup.presenter/-backup-presenter/index.md) | `interface BackupPresenter : `[`BasePresenter`](./index.md)`<`[`BackupView`](../../kin.backupandrestore.backup.view/-backup-view/index.md)`!>, `[`BackupNavigator`](../../kin.backupandrestore.backup.view/-backup-navigator/index.md) |
| [BasePresenterImpl](../-base-presenter-impl/index.md) | `abstract class BasePresenterImpl<T : `[`BaseView`](../-base-view.md)`!> : `[`BasePresenter`](./index.md)`<T>` |
| [CreatePasswordPresenter](../../kin.backupandrestore.backup.presenter/-create-password-presenter/index.md) | `interface CreatePasswordPresenter : `[`BasePresenter`](./index.md)`<`[`CreatePasswordView`](../../kin.backupandrestore.backup.view/-create-password-view/index.md)`!>` |
| [RestorePresenter](../../kin.backupandrestore.restore.presenter/-restore-presenter/index.md) | `interface RestorePresenter : `[`BasePresenter`](./index.md)`<`[`RestoreView`](../../kin.backupandrestore.restore.view/-restore-view/index.md)`!>` |
| [SaveAndSharePresenter](../../kin.backupandrestore.backup.presenter/-save-and-share-presenter/index.md) | `interface SaveAndSharePresenter : `[`BasePresenter`](./index.md)`<`[`SaveAndShareView`](../../kin.backupandrestore.backup.view/-save-and-share-view/index.md)`!>` |
