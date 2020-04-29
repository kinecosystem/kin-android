[kin-android](../../index.md) / [kin.sdk](../index.md) / [PaymentInfo](./index.md)

# PaymentInfo

`interface PaymentInfo`

Represents payment issued on the blockchain.

### Functions

| Name | Summary |
|---|---|
| [amount](amount.md) | Payment amount in kin.`abstract fun amount(): `[`BigDecimal`](https://docs.oracle.com/javase/6/docs/api/java/math/BigDecimal.html) |
| [createdAt](created-at.md) | Transaction creation time.`abstract fun createdAt(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [destinationPublicKey](destination-public-key.md) | Destination account public id.`abstract fun destinationPublicKey(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [fee](fee.md) | Amount of fee(in stroops) for this payment.`abstract fun fee(): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [hash](hash.md) | Transaction id (hash).`abstract fun hash(): `[`TransactionId`](../-transaction-id/index.md) |
| [memo](memo.md) | An optional string, up-to 28 characters, included on the transaction record.`abstract fun memo(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [sourcePublicKey](source-public-key.md) | Source account public id.`abstract fun sourcePublicKey(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [PaymentInfoImpl](../../kin.sdk.internal/-payment-info-impl/index.md) | `data class PaymentInfoImpl : `[`PaymentInfo`](./index.md) |
