[kin-android](../../index.md) / [kin.backupandrestore.backup.presenter](../index.md) / [CreatePasswordPresenterImpl](./index.md)

# CreatePasswordPresenterImpl

`open class CreatePasswordPresenterImpl : `[`BasePresenterImpl`](../../kin.backupandrestore.base/-base-presenter-impl/index.md)`<`[`CreatePasswordView`](../../kin.backupandrestore.backup.view/-create-password-view/index.md)`!>, `[`CreatePasswordPresenter`](../-create-password-presenter/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `CreatePasswordPresenterImpl(callbackManager: `[`CallbackManager`](../../kin.backupandrestore.events/-callback-manager/index.md)`, backupNavigator: `[`BackupNavigator`](../../kin.backupandrestore.backup.view/-backup-navigator/index.md)`, kinAccount: `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [checkAllCompleted](check-all-completed.md) | `open fun checkAllCompleted(password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, otherPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [iUnderstandChecked](i-understand-checked.md) | `open fun iUnderstandChecked(isChecked: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, enterPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, confirmPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [nextButtonClicked](next-button-clicked.md) | `open fun nextButtonClicked(confirmPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackClicked](on-back-clicked.md) | `open fun onBackClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onRetryClicked](on-retry-clicked.md) | `open fun onRetryClicked(password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [passwordCheck](password-check.md) | `open fun passwordCheck(changedPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, otherPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, isConfirmPassword: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
