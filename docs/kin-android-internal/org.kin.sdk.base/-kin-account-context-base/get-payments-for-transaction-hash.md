[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountContextBase](index.md) / [getPaymentsForTransactionHash](./get-payments-for-transaction-hash.md)

# getPaymentsForTransactionHash

`open fun getPaymentsForTransactionHash(transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>`

Retrieves the [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s that were processed in the referred [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)

### Parameters

`transactionHash` - is the referencing hash for a [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)
that contain a list of the given [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s.

**Return**
a [Promise](../../org.kin.sdk.base.tools/-promise/index.md) containing the list of [KinPayment](../../org.kin.sdk.base.models/-kin-payment/index.md)s in the referencing [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)
or an error

