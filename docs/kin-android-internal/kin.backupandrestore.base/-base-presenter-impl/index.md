[kin-android](../../index.md) / [kin.backupandrestore.base](../index.md) / [BasePresenterImpl](./index.md)

# BasePresenterImpl

`abstract class BasePresenterImpl<T : `[`BaseView`](../-base-view.md)`!> : `[`BasePresenter`](../-base-presenter/index.md)`<T>`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BasePresenterImpl()` |

### Properties

| Name | Summary |
|---|---|
| [view](view.md) | `var view: T` |

### Functions

| Name | Summary |
|---|---|
| [getView](get-view.md) | `open fun getView(): T` |
| [onAttach](on-attach.md) | `open fun onAttach(view: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onDetach](on-detach.md) | `open fun onDetach(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [BackupInfoPresenterImpl](../../kin.backupandrestore.backup.presenter/-backup-info-presenter-impl/index.md) | `open class BackupInfoPresenterImpl : `[`BasePresenterImpl`](./index.md)`<`[`BaseView`](../-base-view.md)`!>, `[`BackupInfoPresenter`](../../kin.backupandrestore.backup.presenter/-backup-info-presenter/index.md) |
| [BackupPresenterImpl](../../kin.backupandrestore.backup.presenter/-backup-presenter-impl/index.md) | `open class BackupPresenterImpl : `[`BasePresenterImpl`](./index.md)`<`[`BackupView`](../../kin.backupandrestore.backup.view/-backup-view/index.md)`!>, `[`BackupPresenter`](../../kin.backupandrestore.backup.presenter/-backup-presenter/index.md) |
| [CreatePasswordPresenterImpl](../../kin.backupandrestore.backup.presenter/-create-password-presenter-impl/index.md) | `open class CreatePasswordPresenterImpl : `[`BasePresenterImpl`](./index.md)`<`[`CreatePasswordView`](../../kin.backupandrestore.backup.view/-create-password-view/index.md)`!>, `[`CreatePasswordPresenter`](../../kin.backupandrestore.backup.presenter/-create-password-presenter/index.md) |
| [RestorePresenterImpl](../../kin.backupandrestore.restore.presenter/-restore-presenter-impl/index.md) | `open class RestorePresenterImpl : `[`BasePresenterImpl`](./index.md)`<`[`RestoreView`](../../kin.backupandrestore.restore.view/-restore-view/index.md)`!>, `[`RestorePresenter`](../../kin.backupandrestore.restore.presenter/-restore-presenter/index.md) |
| [SaveAndSharePresenterImpl](../../kin.backupandrestore.backup.presenter/-save-and-share-presenter-impl/index.md) | `open class SaveAndSharePresenterImpl : `[`BasePresenterImpl`](./index.md)`<`[`SaveAndShareView`](../../kin.backupandrestore.backup.view/-save-and-share-view/index.md)`!>, `[`SaveAndSharePresenter`](../../kin.backupandrestore.backup.presenter/-save-and-share-presenter/index.md) |
