[kin-android](../../index.md) / [org.kin.sdk.base.network.services](../index.md) / [KinService](./index.md)

# KinService

`interface KinService`

### Types

| Name | Summary |
|---|---|
| [Order](-order/index.md) | `sealed class Order` |

### Exceptions

| Name | Summary |
|---|---|
| [FatalError](-fatal-error/index.md) | `sealed class FatalError : `[`RuntimeException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-runtime-exception/index.html) |

### Properties

| Name | Summary |
|---|---|
| [testService](test-service.md) | WARNING: This *ONLY* works in test environments.`abstract val testService: `[`KinTestService`](../-kin-test-service/index.md) |

### Functions

| Name | Summary |
|---|---|
| [buildAndSignTransaction](build-and-sign-transaction.md) | `abstract fun buildAndSignTransaction(ownerKey: PrivateKey, sourceKey: PublicKey, nonce: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`, paymentItems: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`, fee: `[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [buildSignAndSubmitTransaction](build-sign-and-submit-transaction.md) | `abstract fun buildSignAndSubmitTransaction(buildAndSignTransaction: () -> `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [canWhitelistTransactions](can-whitelist-transactions.md) | `abstract fun canWhitelistTransactions(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [createAccount](create-account.md) | Creates a [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) and activates it on the network.`abstract fun createAccount(accountId: Id, signer: PrivateKey): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [getAccount](get-account.md) | `abstract fun getAccount(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [getLatestTransactions](get-latest-transactions.md) | `abstract fun getLatestTransactions(kinAccountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [getMinFee](get-min-fee.md) | `abstract fun getMinFee(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>` |
| [getTransaction](get-transaction.md) | `abstract fun getTransaction(transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [getTransactionPage](get-transaction-page.md) | `abstract fun getTransactionPage(kinAccountId: Id, pagingToken: PagingToken, order: Order = Order.Descending): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [invalidateBlockhashCache](invalidate-blockhash-cache.md) | `abstract fun invalidateBlockhashCache(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [resolveTokenAccounts](resolve-token-accounts.md) | `abstract fun resolveTokenAccounts(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<PublicKey>>` |
| [streamAccount](stream-account.md) | `abstract fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [streamNewTransactions](stream-new-transactions.md) | `abstract fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [submitTransaction](submit-transaction.md) | `abstract fun submitTransaction(transaction: `[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [KinServiceImpl](../-kin-service-impl/index.md) | `class KinServiceImpl : `[`KinService`](./index.md) |
| [KinServiceImplV4](../-kin-service-impl-v4/index.md) | `class KinServiceImplV4 : `[`KinService`](./index.md) |
| [KinServiceWrapper](../-kin-service-wrapper/index.md) | `class KinServiceWrapper : `[`KinService`](./index.md) |
