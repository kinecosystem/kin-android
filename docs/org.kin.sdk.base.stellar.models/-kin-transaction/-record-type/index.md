[kin-android](../../../index.md) / [org.kin.sdk.base.stellar.models](../../index.md) / [KinTransaction](../index.md) / [RecordType](./index.md)

# RecordType

`sealed class RecordType`

### Types

| Name | Summary |
|---|---|
| [Acknowledged](-acknowledged/index.md) | `data class Acknowledged : RecordType` |
| [Historical](-historical/index.md) | `data class Historical : RecordType` |
| [InFlight](-in-flight/index.md) | `data class InFlight : RecordType` |

### Properties

| Name | Summary |
|---|---|
| [timestamp](timestamp.md) | `abstract val timestamp: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [value](value.md) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [parseResultCode](parse-result-code.md) | `fun parseResultCode(resultXdrBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`): ResultCode` |
