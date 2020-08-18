[kin-android](../../index.md) / [kin.backupandrestore.restore.presenter](../index.md) / [RestorePresenter](./index.md)

# RestorePresenter

`interface RestorePresenter : `[`BasePresenter`](../../kin.backupandrestore.base/-base-presenter/index.md)`<`[`RestoreView`](../../kin.backupandrestore.restore.view/-restore-view/index.md)`!>`

### Functions

| Name | Summary |
|---|---|
| [closeFlow](close-flow.md) | `abstract fun closeFlow(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getKinClient](get-kin-client.md) | `abstract fun getKinClient(): `[`KinClient`](../../kin.sdk/-kin-client/index.md)`!` |
| [navigateToEnterPasswordPage](navigate-to-enter-password-page.md) | `abstract fun navigateToEnterPasswordPage(accountKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToRestoreCompletedPage](navigate-to-restore-completed-page.md) | `abstract fun navigateToRestoreCompletedPage(kinAccount: `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onActivityResult](on-activity-result.md) | `abstract fun onActivityResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSaveInstanceState](on-save-instance-state.md) | `abstract fun onSaveInstanceState(outState: Bundle!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [previousStep](previous-step.md) | `abstract fun previousStep(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [RestorePresenterImpl](../-restore-presenter-impl/index.md) | `open class RestorePresenterImpl : `[`BasePresenterImpl`](../../kin.backupandrestore.base/-base-presenter-impl/index.md)`<`[`RestoreView`](../../kin.backupandrestore.restore.view/-restore-view/index.md)`!>, `[`RestorePresenter`](./index.md) |
