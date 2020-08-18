[kin-android](../../index.md) / [kin.backupandrestore.backup.presenter](../index.md) / [CreatePasswordPresenter](./index.md)

# CreatePasswordPresenter

`interface CreatePasswordPresenter : `[`BasePresenter`](../../kin.backupandrestore.base/-base-presenter/index.md)`<`[`CreatePasswordView`](../../kin.backupandrestore.backup.view/-create-password-view/index.md)`!>`

### Functions

| Name | Summary |
|---|---|
| [checkAllCompleted](check-all-completed.md) | `abstract fun checkAllCompleted(password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, otherPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [iUnderstandChecked](i-understand-checked.md) | `abstract fun iUnderstandChecked(isChecked: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`, enterPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, confirmPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [nextButtonClicked](next-button-clicked.md) | `abstract fun nextButtonClicked(confirmPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onRetryClicked](on-retry-clicked.md) | `abstract fun onRetryClicked(password: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [passwordCheck](password-check.md) | `abstract fun passwordCheck(changedPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, otherPassword: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, isConfirmPassword: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [CreatePasswordPresenterImpl](../-create-password-presenter-impl/index.md) | `open class CreatePasswordPresenterImpl : `[`BasePresenterImpl`](../../kin.backupandrestore.base/-base-presenter-impl/index.md)`<`[`CreatePasswordView`](../../kin.backupandrestore.backup.view/-create-password-view/index.md)`!>, `[`CreatePasswordPresenter`](./index.md) |
