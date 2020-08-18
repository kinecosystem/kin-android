[kin-android](../../index.md) / [kin.backupandrestore.qr](../index.md) / [QRBarcodeGenerator](./index.md)

# QRBarcodeGenerator

`interface QRBarcodeGenerator`

### Exceptions

| Name | Summary |
|---|---|
| [QRBarcodeGeneratorException](-q-r-barcode-generator-exception.md) | `open class QRBarcodeGeneratorException : `[`Exception`](https://docs.oracle.com/javase/6/docs/api/java/lang/Exception.html) |
| [QRFileHandlingException](-q-r-file-handling-exception.md) | `open class QRFileHandlingException : QRBarcodeGeneratorException` |
| [QRNotFoundInImageException](-q-r-not-found-in-image-exception.md) | `open class QRNotFoundInImageException : QRBarcodeGeneratorException` |

### Functions

| Name | Summary |
|---|---|
| [decodeQR](decode-q-r.md) | `abstract fun decodeQR(uri: Uri): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [generate](generate.md) | `abstract fun generate(text: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): Uri` |

### Inheritors

| Name | Summary |
|---|---|
| [QRBarcodeGeneratorImpl](../-q-r-barcode-generator-impl/index.md) | `open class QRBarcodeGeneratorImpl : `[`QRBarcodeGenerator`](./index.md) |
