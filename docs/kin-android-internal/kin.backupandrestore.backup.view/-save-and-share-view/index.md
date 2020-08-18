[kin-android](../../index.md) / [kin.backupandrestore.backup.view](../index.md) / [SaveAndShareView](./index.md)

# SaveAndShareView

`interface SaveAndShareView : `[`BaseView`](../../kin.backupandrestore.base/-base-view.md)

### Functions

| Name | Summary |
|---|---|
| [setQRImage](set-q-r-image.md) | `abstract fun setQRImage(qrURI: Uri!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showErrorTryAgainLater](show-error-try-again-later.md) | `abstract fun showErrorTryAgainLater(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showIHaveSavedCheckBox](show-i-have-saved-check-box.md) | `abstract fun showIHaveSavedCheckBox(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showSendIntent](show-send-intent.md) | `abstract fun showSendIntent(qrURI: Uri!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [SaveAndShareFragment](../-save-and-share-fragment/index.md) | `open class SaveAndShareFragment : Fragment, `[`SaveAndShareView`](./index.md) |
