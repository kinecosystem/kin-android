[kin-android](../../index.md) / [kin.backupandrestore.backup.view](../index.md) / [SaveAndShareFragment](./index.md)

# SaveAndShareFragment

`open class SaveAndShareFragment : Fragment, `[`SaveAndShareView`](../-save-and-share-view/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SaveAndShareFragment()` |

### Functions

| Name | Summary |
|---|---|
| [newInstance](new-instance.md) | `open static fun newInstance(listener: `[`BackupNavigator`](../-backup-navigator/index.md)`!, key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`SaveAndShareFragment`](./index.md)`!` |
| [onCreateView](on-create-view.md) | `open fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?` |
| [onSaveInstanceState](on-save-instance-state.md) | `open fun onSaveInstanceState(outState: Bundle): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setNextStepListener](set-next-step-listener.md) | `open fun setNextStepListener(nextStepListener: `[`BackupNavigator`](../-backup-navigator/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setQRImage](set-q-r-image.md) | `open fun setQRImage(qrURI: Uri!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showErrorTryAgainLater](show-error-try-again-later.md) | `open fun showErrorTryAgainLater(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showIHaveSavedCheckBox](show-i-have-saved-check-box.md) | `open fun showIHaveSavedCheckBox(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showSendIntent](show-send-intent.md) | `open fun showSendIntent(qrURI: Uri!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
