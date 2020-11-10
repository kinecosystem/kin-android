[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinPaymentWriteOperations](index.md) / [payInvoice](./pay-invoice.md)

# payInvoice

`abstract fun payInvoice(invoice: `[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`, destinationAccount: Id, processingAppIdx: `[`AppIdx`](../../org.kin.sdk.base.models/-app-idx/index.md)` = appInfoProvider?.appInfo?.appIndex
            ?: throw RuntimeException("Need to specify an AppIdx"), type: TransferType = KinBinaryMemo.TransferType.Spend): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>`