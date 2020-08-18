[kin-android](../../index.md) / [kin.backupandrestore.backup.presenter](../index.md) / [BackupPresenterImpl](./index.md)

# BackupPresenterImpl

`open class BackupPresenterImpl : `[`BasePresenterImpl`](../../kin.backupandrestore.base/-base-presenter-impl/index.md)`<`[`BackupView`](../../kin.backupandrestore.backup.view/-backup-view/index.md)`!>, `[`BackupPresenter`](../-backup-presenter/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `BackupPresenterImpl(callbackManager: `[`CallbackManager`](../../kin.backupandrestore.events/-callback-manager/index.md)`, kinAccount: `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`, savedInstanceState: Bundle?)` |

### Properties

| Name | Summary |
|---|---|
| [KEY_ACCOUNT_KEY](-k-e-y_-a-c-c-o-u-n-t_-k-e-y.md) | `static val KEY_ACCOUNT_KEY: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [closeFlow](close-flow.md) | `open fun closeFlow(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getKinAccount](get-kin-account.md) | `open fun getKinAccount(): `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`!` |
| [navigateToCreatePasswordPage](navigate-to-create-password-page.md) | `open fun navigateToCreatePasswordPage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToSaveAndSharePage](navigate-to-save-and-share-page.md) | `open fun navigateToSaveAndSharePage(accountKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [navigateToWellDonePage](navigate-to-well-done-page.md) | `open fun navigateToWellDonePage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onAttach](on-attach.md) | `open fun onAttach(view: `[`BackupView`](../../kin.backupandrestore.backup.view/-backup-view/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackClicked](on-back-clicked.md) | `open fun onBackClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSaveInstanceState](on-save-instance-state.md) | `open fun onSaveInstanceState(outState: Bundle!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setAccountKey](set-account-key.md) | `open fun setAccountKey(accountKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
