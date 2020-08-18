[kin-android](../../index.md) / [kin.backupandrestore.backup.presenter](../index.md) / [SaveAndSharePresenterImpl](./index.md)

# SaveAndSharePresenterImpl

`open class SaveAndSharePresenterImpl : `[`BasePresenterImpl`](../../kin.backupandrestore.base/-base-presenter-impl/index.md)`<`[`SaveAndShareView`](../../kin.backupandrestore.backup.view/-save-and-share-view/index.md)`!>, `[`SaveAndSharePresenter`](../-save-and-share-presenter/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SaveAndSharePresenterImpl(callbackManager: `[`CallbackManager`](../../kin.backupandrestore.events/-callback-manager/index.md)`, backupNavigator: `[`BackupNavigator`](../../kin.backupandrestore.backup.view/-backup-navigator/index.md)`!, qrBarcodeGenerator: `[`QRBarcodeGenerator`](../../kin.backupandrestore.qr/-q-r-barcode-generator/index.md)`!, key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!, savedInstanceState: Bundle!)` |

### Functions

| Name | Summary |
|---|---|
| [couldNotLoadQRImage](could-not-load-q-r-image.md) | `open fun couldNotLoadQRImage(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [iHaveSavedChecked](i-have-saved-checked.md) | `open fun iHaveSavedChecked(isChecked: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onAttach](on-attach.md) | `open fun onAttach(view: `[`SaveAndShareView`](../../kin.backupandrestore.backup.view/-save-and-share-view/index.md)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackClicked](on-back-clicked.md) | `open fun onBackClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onSaveInstanceState](on-save-instance-state.md) | `open fun onSaveInstanceState(outState: Bundle!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendQREmailClicked](send-q-r-email-clicked.md) | `open fun sendQREmailClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
