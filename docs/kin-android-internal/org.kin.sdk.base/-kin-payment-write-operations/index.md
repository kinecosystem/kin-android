[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinPaymentWriteOperations](./index.md)

# KinPaymentWriteOperations

`interface KinPaymentWriteOperations : `[`KinPaymentWriteOperationsAltIdioms`](../-kin-payment-write-operations-alt-idioms/index.md)

### Properties

| Name | Summary |
|---|---|
| [appInfoProvider](app-info-provider.md) | `abstract val appInfoProvider: `[`AppInfoProvider`](../../org.kin.sdk.base.network.services/-app-info-provider/index.md)`?` |

### Functions

| Name | Summary |
|---|---|
| [payInvoice](pay-invoice.md) | `abstract fun payInvoice(invoice: `[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`, destinationAccount: Id, processingAppIdx: `[`AppIdx`](../../org.kin.sdk.base.models/-app-idx/index.md)` = appInfoProvider?.appInfo?.appIndex
            ?: throw RuntimeException("Need to specify an AppIdx"), type: TransferType = KinBinaryMemo.TransferType.Spend): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |
| [sendKinPayment](send-kin-payment.md) | Send an amount of Kin to a [destinationAccount](send-kin-payment.md#org.kin.sdk.base.KinPaymentWriteOperations$sendKinPayment(org.kin.sdk.base.models.KinAmount, org.kin.sdk.base.models.KinAccount.Id, org.kin.sdk.base.models.KinMemo, org.kin.sdk.base.tools.Optional((org.kin.sdk.base.models.Invoice)))/destinationAccount) to the Kin Blockchain for processing.`abstract fun sendKinPayment(amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`, destinationAccount: Id, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)` = KinMemo.NONE, invoice: `[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`> = Optional.empty()): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |
| [sendKinPayments](send-kin-payments.md) | `abstract fun sendKinPayments(payments: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)` = KinMemo.NONE): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>` |
| [sendKinTransaction](send-kin-transaction.md) | Directly sends a [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md). Currently only exposed to support the kin-android:base-compat library`abstract fun sendKinTransaction(transaction: `[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinAccountContext](../-kin-account-context/index.md) | `interface KinAccountContext : `[`KinAccountContextReadOnly`](../-kin-account-context-read-only/index.md)`, `[`KinPaymentWriteOperations`](./index.md) |
