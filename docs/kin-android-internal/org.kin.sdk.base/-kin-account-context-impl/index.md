[kin-android](../../index.md) / [org.kin.sdk.base](../index.md) / [KinAccountContextImpl](./index.md)

# KinAccountContextImpl

`class KinAccountContextImpl : `[`KinAccountContextBase`](../-kin-account-context-base/index.md)`, `[`KinAccountContext`](../-kin-account-context/index.md)

Instantiate a [KinAccountContextImpl](./index.md) to operate on a [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) when you have a [PrivateKey](#)
Can be used to:

* create an account
* get account data, payment history, and listen to changes over time
* send payments

### Types

| Name | Summary |
|---|---|
| [ExistingAccountBuilder](-existing-account-builder/index.md) | Let's you access the specified [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md)`class ExistingAccountBuilder` |
| [NewAccountBuilder](-new-account-builder/index.md) | Creates a new [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md)`class NewAccountBuilder` |

### Properties

| Name | Summary |
|---|---|
| [accountId](account-id.md) | denoting the [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) to get information from`val accountId: Id` |
| [appInfoProvider](app-info-provider.md) | `val appInfoProvider: `[`AppInfoProvider`](../../org.kin.sdk.base.network.services/-app-info-provider/index.md)`?` |
| [executors](executors.md) | defines a set of executors to be used`val executors: `[`ExecutorServices`](../../org.kin.sdk.base.tools/-executor-services/index.md) |
| [logger](logger.md) | `val logger: `[`KinLoggerFactory`](../../org.kin.sdk.base.tools/-kin-logger-factory/index.md) |
| [service](service.md) | a service used to retrieve all account and payment data`val service: `[`KinService`](../../org.kin.sdk.base.network.services/-kin-service/index.md) |
| [storage](storage.md) | stores all account and payment data. @see [KinFileStorage](#) for provided implementation.`val storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md) |

### Functions

| Name | Summary |
|---|---|
| [clearStorage](clear-storage.md) | `fun clearStorage(clearCompleteCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getAccount](get-account.md) | Returns the account info`fun getAccount(forceUpdate: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [getPaymentsForTransactionHash](get-payments-for-transaction-hash.md) | `fun getPaymentsForTransactionHash(transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`, paymentsCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [payInvoice](pay-invoice.md) | `fun payInvoice(invoice: `[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`, destinationAccount: Id, processingAppIdx: `[`AppIdx`](../../org.kin.sdk.base.models/-app-idx/index.md)`, type: TransferType): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>` |
| [sendKinPayment](send-kin-payment.md) | Send an amount of Kin to a [destinationAccount](../-kin-payment-write-operations/send-kin-payment.md#org.kin.sdk.base.KinPaymentWriteOperations$sendKinPayment(org.kin.sdk.base.models.KinAmount, org.kin.sdk.base.models.KinAccount.Id, org.kin.sdk.base.models.KinMemo, org.kin.sdk.base.tools.Optional((org.kin.sdk.base.models.Invoice)))/destinationAccount) to the Kin Blockchain for processing.`fun sendKinPayment(amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`, destinationAccount: Id, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`, invoice: `[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>``fun sendKinPayment(amount: `[`KinAmount`](../../org.kin.sdk.base.models/-kin-amount/index.md)`, destinationAccount: Id, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`, invoice: `[`Optional`](../../org.kin.sdk.base.tools/-optional/index.md)`<`[`Invoice`](../../org.kin.sdk.base.models/-invoice/index.md)`>, paymentCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendKinPayments](send-kin-payments.md) | `fun sendKinPayments(payments: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`, sourceAccountSpec: `[`AccountSpec`](../../org.kin.sdk.base.models/-account-spec/index.md)`, destinationAccountSpec: `[`AccountSpec`](../../org.kin.sdk.base.models/-account-spec/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>`<br>`fun sendKinPayments(payments: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`, paymentsCallback: `[`Callback`](../../org.kin.sdk.base.tools/-callback/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [sendKinTransaction](send-kin-transaction.md) | Directly sends a [KinTransaction](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md). Currently only exposed to support the kin-android:base-compat library`fun sendKinTransaction(buildTransaction: () -> `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPayment`](../../org.kin.sdk.base.models/-kin-payment/index.md)`>>` |
