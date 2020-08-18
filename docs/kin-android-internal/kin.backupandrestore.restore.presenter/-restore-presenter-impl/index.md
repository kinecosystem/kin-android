[kin-android](../../index.md) / [kin.backupandrestore.restore.presenter](../index.md) / [RestorePresenterImpl](./index.md)

# RestorePresenterImpl

`open class RestorePresenterImpl : `[`BasePresenterImpl`](../../kin.backupandrestore.base/-base-presenter-impl/index.md)`<`[`RestoreView`](../../kin.backupandrestore.restore.view/-restore-view/index.md)`!>, `[`RestorePresenter`](../-restore-presenter/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `RestorePresenterImpl(callbackManager: `[`CallbackManager`](../../kin.backupandrestore.events/-callback-manager/index.md)`!, kinClient: `[`KinClient`](../../kin.sdk/-kin-client/index.md)`!, saveInstanceState: Bundle!)` |

### Properties

| Name | Summary |
|---|---|
| [KEY_ACCOUNT_KEY](-k-e-y_-a-c-c-o-u-n-t_-k-e-y.md) | `static val KEY_ACCOUNT_KEY: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [KEY_PUBLIC_ADDRESS](-k-e-y_-p-u-b-l-i-c_-a-d-d-r-e-s-s.md) | `static val KEY_PUBLIC_ADDRESS: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [closeFlow](close-flow.md) | `open fun closeFlow(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getKinClient](get-kin-client.md) | `open fun getKinClient(): `[`KinClient`](../../kin.sdk/-kin-client/index.md)`!` |
| [navigateToEnterPasswordPage](navigate-to-enter-password-page.md) | `open fun navigateToEnterPasswordPage(accountKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToRestoreCompletedPage](navigate-to-restore-completed-page.md) | `open fun navigateToRestoreCompletedPage(kinAccount: `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onActivityResult](on-activity-result.md) | `open fun onActivityResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onAttach](on-attach.md) | `open fun onAttach(view: `[`RestoreView`](../../kin.backupandrestore.restore.view/-restore-view/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackClicked](on-back-clicked.md) | `open fun onBackClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSaveInstanceState](on-save-instance-state.md) | `open fun onSaveInstanceState(outState: Bundle!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [previousStep](previous-step.md) | `open fun previousStep(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
