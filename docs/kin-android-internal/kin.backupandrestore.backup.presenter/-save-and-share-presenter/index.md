[kin-android](../../index.md) / [kin.backupandrestore.backup.presenter](../index.md) / [SaveAndSharePresenter](./index.md)

# SaveAndSharePresenter

`interface SaveAndSharePresenter : `[`BasePresenter`](../../kin.backupandrestore.base/-base-presenter/index.md)`<`[`SaveAndShareView`](../../kin.backupandrestore.backup.view/-save-and-share-view/index.md)`!>`

### Functions

| Name | Summary |
|---|---|
| [couldNotLoadQRImage](could-not-load-q-r-image.md) | `abstract fun couldNotLoadQRImage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [iHaveSavedChecked](i-have-saved-checked.md) | `abstract fun iHaveSavedChecked(isChecked: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSaveInstanceState](on-save-instance-state.md) | `abstract fun onSaveInstanceState(outState: Bundle!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendQREmailClicked](send-q-r-email-clicked.md) | `abstract fun sendQREmailClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [SaveAndSharePresenterImpl](../-save-and-share-presenter-impl/index.md) | `open class SaveAndSharePresenterImpl : `[`BasePresenterImpl`](../../kin.backupandrestore.base/-base-presenter-impl/index.md)`<`[`SaveAndShareView`](../../kin.backupandrestore.backup.view/-save-and-share-view/index.md)`!>, `[`SaveAndSharePresenter`](./index.md) |
