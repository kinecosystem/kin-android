[kin-android](../../index.md) / [kin.backupandrestore.backup.view](../index.md) / [CreatePasswordFragment](./index.md)

# CreatePasswordFragment

`open class CreatePasswordFragment : Fragment, `[`CreatePasswordView`](../-create-password-view/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `CreatePasswordFragment()` |

### Functions

| Name | Summary |
|---|---|
| [closeKeyboard](close-keyboard.md) | `open fun closeKeyboard(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [disableNextButton](disable-next-button.md) | `open fun disableNextButton(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [enableNextButton](enable-next-button.md) | `open fun enableNextButton(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [newInstance](new-instance.md) | `open static fun newInstance(nextStepListener: `[`BackupNavigator`](../-backup-navigator/index.md)`, keyboardHandler: `[`KeyboardHandler`](../../kin.backupandrestore.base/-keyboard-handler/index.md)`, kinAccount: `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`): `[`CreatePasswordFragment`](./index.md)`!` |
| [onCreateView](on-create-view.md) | `open fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?` |
| [onDestroy](on-destroy.md) | `open fun onDestroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onViewCreated](on-view-created.md) | `open fun onViewCreated(view: View, savedInstanceState: Bundle?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [resetConfirmPasswordField](reset-confirm-password-field.md) | `open fun resetConfirmPasswordField(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [resetEnterPasswordField](reset-enter-password-field.md) | `open fun resetEnterPasswordField(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setConfirmPasswordIsCorrect](set-confirm-password-is-correct.md) | `open fun setConfirmPasswordIsCorrect(isCorrect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setEnterPasswordIsCorrect](set-enter-password-is-correct.md) | `open fun setEnterPasswordIsCorrect(isCorrect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setKeyboardHandler](set-keyboard-handler.md) | `open fun setKeyboardHandler(keyboardHandler: `[`KeyboardHandler`](../../kin.backupandrestore.base/-keyboard-handler/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setKinAccount](set-kin-account.md) | `open fun setKinAccount(kinAccount: `[`KinAccount`](../../kin.sdk/-kin-account/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setNextStepListener](set-next-step-listener.md) | `open fun setNextStepListener(nextStepListener: `[`BackupNavigator`](../-backup-navigator/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setPasswordDoesNotMatch](set-password-does-not-match.md) | `open fun setPasswordDoesNotMatch(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showBackupFailed](show-backup-failed.md) | `open fun showBackupFailed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
