[kin-android](../../index.md) / [kin.backupandrestore.backup.view](../index.md) / [CreatePasswordView](./index.md)

# CreatePasswordView

`interface CreatePasswordView : `[`BaseView`](../../kin.backupandrestore.base/-base-view.md)

### Functions

| Name | Summary |
|---|---|
| [closeKeyboard](close-keyboard.md) | `abstract fun closeKeyboard(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [disableNextButton](disable-next-button.md) | `abstract fun disableNextButton(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [enableNextButton](enable-next-button.md) | `abstract fun enableNextButton(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [resetConfirmPasswordField](reset-confirm-password-field.md) | `abstract fun resetConfirmPasswordField(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [resetEnterPasswordField](reset-enter-password-field.md) | `abstract fun resetEnterPasswordField(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setConfirmPasswordIsCorrect](set-confirm-password-is-correct.md) | `abstract fun setConfirmPasswordIsCorrect(isCorrect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setEnterPasswordIsCorrect](set-enter-password-is-correct.md) | `abstract fun setEnterPasswordIsCorrect(isCorrect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setPasswordDoesNotMatch](set-password-does-not-match.md) | `abstract fun setPasswordDoesNotMatch(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showBackupFailed](show-backup-failed.md) | `abstract fun showBackupFailed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [CreatePasswordFragment](../-create-password-fragment/index.md) | `open class CreatePasswordFragment : Fragment, `[`CreatePasswordView`](./index.md) |
