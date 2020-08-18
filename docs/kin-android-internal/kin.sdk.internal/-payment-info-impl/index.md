[kin-android](../../index.md) / [kin.sdk.internal](../index.md) / [PaymentInfoImpl](./index.md)

# PaymentInfoImpl

`data class PaymentInfoImpl : `[`PaymentInfo`](../../kin.sdk/-payment-info/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `PaymentInfoImpl(kinPayment: `[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [kinPayment](kin-payment.md) | `val kinPayment: `[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md) |

### Functions

| Name | Summary |
|---|---|
| [amount](amount.md) | Payment amount in kin.`fun amount(): `[`BigDecimal`](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html) |
| [createdAt](created-at.md) | Transaction creation time.`fun createdAt(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [destinationPublicKey](destination-public-key.md) | Destination account public id.`fun destinationPublicKey(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [fee](fee.md) | Amount of fee(in stroops) for this payment.`fun fee(): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [hash](hash.md) | Transaction id (hash).`fun hash(): `[`TransactionId`](../../kin.sdk/-transaction-id/index.md) |
| [memo](memo.md) | An optional string, up-to 28 characters, included on the transaction record.`fun memo(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [sourcePublicKey](source-public-key.md) | Source account public id.`fun sourcePublicKey(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
