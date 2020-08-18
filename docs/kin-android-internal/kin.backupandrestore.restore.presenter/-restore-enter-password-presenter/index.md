[kin-android](../../index.md) / [kin.backupandrestore.restore.presenter](../index.md) / [RestoreEnterPasswordPresenter](./index.md)

# RestoreEnterPasswordPresenter

`interface RestoreEnterPasswordPresenter : BaseChildPresenter<`[`RestoreEnterPasswordView`](../../kin.backupandrestore.restore.view/-restore-enter-password-view/index.md)`!>`

### Functions

| Name | Summary |
|---|---|
| [onPasswordChanged](on-password-changed.md) | `abstract fun onPasswordChanged(password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSaveInstanceState](on-save-instance-state.md) | `abstract fun onSaveInstanceState(outState: Bundle!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [restoreClicked](restore-clicked.md) | `abstract fun restoreClicked(password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [RestoreEnterPasswordPresenterImpl](../-restore-enter-password-presenter-impl/index.md) | `open class RestoreEnterPasswordPresenterImpl : BaseChildPresenterImpl<`[`RestoreEnterPasswordView`](../../kin.backupandrestore.restore.view/-restore-enter-password-view/index.md)`!>, `[`RestoreEnterPasswordPresenter`](./index.md) |
