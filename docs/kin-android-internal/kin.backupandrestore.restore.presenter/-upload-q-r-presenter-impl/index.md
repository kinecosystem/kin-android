[kin-android](../../index.md) / [kin.backupandrestore.restore.presenter](../index.md) / [UploadQRPresenterImpl](./index.md)

# UploadQRPresenterImpl

`open class UploadQRPresenterImpl : BaseChildPresenterImpl<`[`UploadQRView`](../../kin.backupandrestore.restore.view/-upload-q-r-view/index.md)`!>, `[`UploadQRPresenter`](../-upload-q-r-presenter/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UploadQRPresenterImpl(callbackManager: `[`CallbackManager`](../../kin.backupandrestore.events/-callback-manager/index.md)`, fileRequester: `[`FileSharingHelper`](../-file-sharing-helper/index.md)`!, qrBarcodeGenerator: `[`QRBarcodeGenerator`](../../kin.backupandrestore.qr/-q-r-barcode-generator/index.md)`!)` |

### Functions

| Name | Summary |
|---|---|
| [onActivityResult](on-activity-result.md) | `open fun onActivityResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onBackClicked](on-back-clicked.md) | `open fun onBackClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCancelPressed](on-cancel-pressed.md) | `open fun onCancelPressed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onOkPressed](on-ok-pressed.md) | `open fun onOkPressed(chooserTitle: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [uploadClicked](upload-clicked.md) | `open fun uploadClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
