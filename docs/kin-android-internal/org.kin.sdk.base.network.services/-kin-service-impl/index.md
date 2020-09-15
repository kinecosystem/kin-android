[kin-android](../../index.md) / [org.kin.sdk.base.network.services](../index.md) / [KinServiceImpl](./index.md)

# KinServiceImpl

`class KinServiceImpl : `[`KinService`](../-kin-service/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `KinServiceImpl(networkEnvironment: `[`NetworkEnvironment`](../../org.kin.sdk.base.stellar.models/-network-environment/index.md)`, networkOperationsHandler: `[`NetworkOperationsHandler`](../../org.kin.sdk.base.tools/-network-operations-handler/index.md)`, accountApi: `[`KinAccountApi`](../../org.kin.sdk.base.network.api/-kin-account-api/index.md)`, transactionApi: `[`KinTransactionApi`](../../org.kin.sdk.base.network.api/-kin-transaction-api/index.md)`, streamingApi: `[`KinStreamingApi`](../../org.kin.sdk.base.network.api/-kin-streaming-api/index.md)`, accountCreationApi: `[`KinAccountCreationApi`](../../org.kin.sdk.base.network.api/-kin-account-creation-api/index.md)`, transactionWhitelistingApi: `[`KinTransactionWhitelistingApi`](../../org.kin.sdk.base.network.api/-kin-transaction-whitelisting-api/index.md)`, logger: `[`KinLoggerFactory`](../../org.kin.sdk.base.tools/-kin-logger-factory/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [testService](test-service.md) | WARNING: This *ONLY* works in test environments.`val testService: `[`KinTestService`](../-kin-test-service/index.md) |

### Functions

| Name | Summary |
|---|---|
| [buildAndSignTransaction](build-and-sign-transaction.md) | `fun buildAndSignTransaction(sourceKinAccount: `[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`, paymentItems: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinPaymentItem`](../../org.kin.sdk.base.models/-kin-payment-item/index.md)`>, memo: `[`KinMemo`](../../org.kin.sdk.base.models/-kin-memo/index.md)`, fee: `[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [canWhitelistTransactions](can-whitelist-transactions.md) | `fun canWhitelistTransactions(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`>` |
| [createAccount](create-account.md) | Creates a [KinAccount](../../org.kin.sdk.base.models/-kin-account/index.md) and activates it on the network.`fun createAccount(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [getAccount](get-account.md) | `fun getAccount(accountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [getLatestTransactions](get-latest-transactions.md) | `fun getLatestTransactions(kinAccountId: Id): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [getMinFee](get-min-fee.md) | `fun getMinFee(): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`QuarkAmount`](../../org.kin.sdk.base.models/-quark-amount/index.md)`>` |
| [getTransaction](get-transaction.md) | `fun getTransaction(transactionHash: `[`TransactionHash`](../../org.kin.sdk.base.models/-transaction-hash/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [getTransactionPage](get-transaction-page.md) | `fun getTransactionPage(kinAccountId: Id, pagingToken: PagingToken, order: Order): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>>` |
| [streamAccount](stream-account.md) | `fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [streamNewTransactions](stream-new-transactions.md) | `fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [submitTransaction](submit-transaction.md) | `fun submitTransaction(transaction: `[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`): `[`Promise`](../../org.kin.sdk.base.tools/-promise/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
