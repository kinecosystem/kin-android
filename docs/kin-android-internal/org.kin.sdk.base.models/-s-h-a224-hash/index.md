[kin-android](../../index.md) / [org.kin.sdk.base.models](../index.md) / [SHA224Hash](./index.md)

# SHA224Hash

`data class SHA224Hash`

The SHA-224 hash of an Invoice or InvoiceList.

### Parameters

`encodedValue` -
* UTF-8 String representation of the 29 bytes representing the first 230 bits of a SHA-256

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | The SHA-224 hash of an Invoice or InvoiceList.`SHA224Hash(encodedValue: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Properties

| Name | Summary |
|---|---|
| [encodedValue](encoded-value.md) | <ul><li>UTF-8 String representation of the 29 bytes representing the first 230 bits of a SHA-256</li></ul>`val encodedValue: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| Name | Summary |
|---|---|
| [decode](decode.md) | `fun decode(): `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [just](just.md) | `fun just(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`SHA224Hash`](./index.md) |
| [of](of.md) | `fun of(bytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): `[`SHA224Hash`](./index.md) |
