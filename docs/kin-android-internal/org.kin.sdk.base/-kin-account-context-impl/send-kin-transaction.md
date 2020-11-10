[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountContextImpl](index.md) / [sendKinTransaction](./send-kin-transaction.md)

# sendKinTransaction

`fun sendKinTransaction(buildTransaction: () -> `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>`

Directly sends a [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md).
Currently only exposed to support the kin-android:base-compat library

This is not meant for other external consumption. Use at your own risk.

Payments should instead be sent with [sendKinPayment](../-kin-payment-write-operations/send-kin-payment.md) or [sendKinPayments](../-kin-payment-write-operations/send-kin-payments.md) functions.

