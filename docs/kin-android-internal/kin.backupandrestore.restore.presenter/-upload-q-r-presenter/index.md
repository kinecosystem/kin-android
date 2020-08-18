[kin-android](../../index.md) / [kin.backupandrestore.restore.presenter](../index.md) / [UploadQRPresenter](./index.md)

# UploadQRPresenter

`interface UploadQRPresenter : BaseChildPresenter<`[`UploadQRView`](../../kin.backupandrestore.restore.view/-upload-q-r-view/index.md)`!>`

### Functions

| Name | Summary |
|---|---|
| [onActivityResult](on-activity-result.md) | `abstract fun onActivityResult(requestCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, resultCode: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, data: Intent!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onCancelPressed](on-cancel-pressed.md) | `abstract fun onCancelPressed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onOkPressed](on-ok-pressed.md) | `abstract fun onOkPressed(chooserTitle: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`!): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [uploadClicked](upload-clicked.md) | `abstract fun uploadClicked(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [UploadQRPresenterImpl](../-upload-q-r-presenter-impl/index.md) | `open class UploadQRPresenterImpl : BaseChildPresenterImpl<`[`UploadQRView`](../../kin.backupandrestore.restore.view/-upload-q-r-view/index.md)`!>, `[`UploadQRPresenter`](./index.md) |
