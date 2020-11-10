[kin-android](../../index.md) / [org.kin.sdk.base.stellar.models](../index.md) / [KinTransaction](./index.md)

# KinTransaction

`interface KinTransaction`

### Types

| Name | Summary |
|---|---|
| [PagingToken](-paging-token/index.md) | `data class PagingToken : `[`Comparable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)`<PagingToken>` |
| [RecordType](-record-type/index.md) | `sealed class RecordType` |
| [ResultCode](-result-code/index.md) | `sealed class ResultCode` |

### Properties

| Name | Summary |
|---|---|
| [bytesValue](bytes-value.md) | `abstract val bytesValue: `[`ByteArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-byte-array/index.html) |
| [fee](fee.md) | `abstract val fee: `[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md) |
| [invoiceList](invoice-list.md) | `abstract val invoiceList: `[`InvoiceList`](../../org.kin.sdk.base.models/-invoice-list/index.md)`?` |
| [memo](memo.md) | `abstract val memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md) |
| [networkEnvironment](network-environment.md) | `abstract val networkEnvironment: `[`NetworkEnvironment`](../-network-environment/index.md) |
| [paymentOperations](payment-operations.md) | `abstract val paymentOperations: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<Payment>` |
| [recordType](record-type.md) | `abstract val recordType: RecordType` |
| [signingSource](signing-source.md) | `abstract val signingSource: Id` |
| [transactionHash](transaction-hash.md) | `abstract val transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md) |

### Extension Functions

| Name | Summary |
|---|---|
| [asKinPayments](../../org.kin.sdk.base.models/as-kin-payments.md) | `fun `[`KinTransaction`](./index.md)`.asKinPayments(): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [SolanaKinTransaction](../-solana-kin-transaction/index.md) | `data class SolanaKinTransaction : `[`KinTransaction`](./index.md) |
| [StellarKinTransaction](../-stellar-kin-transaction/index.md) | `data class StellarKinTransaction : `[`KinTransaction`](./index.md) |
