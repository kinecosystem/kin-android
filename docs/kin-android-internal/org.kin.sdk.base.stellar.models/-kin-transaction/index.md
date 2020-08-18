[kin-android](../../index.md) / [org.kin.sdk.base.stellar.models](../index.md) / [KinTransaction](./index.md)

# KinTransaction

`data class KinTransaction`

### Types

| Name | Summary |
|---|---|
| [PagingToken](-paging-token/index.md) | `data class PagingToken : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<PagingToken>` |
| [RecordType](-record-type/index.md) | `sealed class RecordType` |
| [ResultCode](-result-code/index.md) | `sealed class ResultCode` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `KinTransaction(envelopeXdrBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html)`, recordType: RecordType = RecordType.InFlight(System.currentTimeMillis()), networkEnvironment: `[`NetworkEnvironment`](../-network-environment/index.md)`, invoiceList: `[`InvoiceList`](../../org.kin.sdk.base.models/-invoice-list/index.md)`? = null)` |

### Properties

| Name | Summary |
|---|---|
| [envelopeXdrBytes](envelope-xdr-bytes.md) | `val envelopeXdrBytes: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [fee](fee.md) | `val fee: `[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md) |
| [invoiceList](invoice-list.md) | `val invoiceList: `[`InvoiceList`](../../org.kin.sdk.base.models/-invoice-list/index.md)`?` |
| [memo](memo.md) | `val memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md) |
| [networkEnvironment](network-environment.md) | `val networkEnvironment: `[`NetworkEnvironment`](../-network-environment/index.md) |
| [paymentOperations](payment-operations.md) | `val paymentOperations: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Payment>` |
| [recordType](record-type.md) | `val recordType: RecordType` |
| [signingSequenceNumber](signing-sequence-number.md) | `val signingSequenceNumber: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [signingSource](signing-source.md) | `val signingSource: Id` |
| [transactionHash](transaction-hash.md) | `val transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md) |

### Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | `fun equals(other: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | `fun hashCode(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Extension Functions

| Name | Summary |
|---|---|
| [asKinPayments](../../org.kin.sdk.base.models/as-kin-payments.md) | `fun `[`KinTransaction`](./index.md)`.asKinPayments(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |
