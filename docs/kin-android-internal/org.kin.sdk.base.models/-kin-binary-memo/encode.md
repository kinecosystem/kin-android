[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [KinBinaryMemo](index.md) / [encode](./encode.md)

# encode

`fun encode(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)

Fields below are packed from LSB to MSB order:

magicByteIndicator    2 bits   | &lt; 4
version               3 bits   | &lt; 8
typeId                5 bits   | &lt; 32
appIdx                16 bits  | &lt; 65,536
foreignKey            230 bits | Often a SHA-224 of an [InvoiceList](../-invoice-list/index.md) but could be anything

