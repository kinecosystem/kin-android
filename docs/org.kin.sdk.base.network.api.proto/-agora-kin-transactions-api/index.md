[kin-android](../../index.md) / [org.kin.sdk.base.network.api.proto](../index.md) / [AgoraKinTransactionsApi](./index.md)

# AgoraKinTransactionsApi

`class AgoraKinTransactionsApi : `[`GrpcApi`](../-grpc-api/index.md)`, `[`KinTransactionApi`](../../org.kin.sdk.base.network.api/-kin-transaction-api/index.md)`, `[`KinTransactionWhitelistingApi`](../../org.kin.sdk.base.network.api/-kin-transaction-whitelisting-api/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AgoraKinTransactionsApi(managedChannel: ManagedChannel, networkEnvironment: `[`NetworkEnvironment`](../../org.kin.sdk.base.stellar.models/-network-environment/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [isWhitelistingAvailable](is-whitelisting-available.md) | `val isWhitelistingAvailable: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Functions

| Name | Summary |
|---|---|
| [getTransaction](get-transaction.md) | `fun getTransaction(request: GetTransactionRequest, onCompleted: (GetTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransactionHistory](get-transaction-history.md) | `fun getTransactionHistory(request: GetTransactionHistoryRequest, onCompleted: (GetTransactionHistoryResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransactionMinFee](get-transaction-min-fee.md) | `fun getTransactionMinFee(onCompleted: (GetMinFeeForTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [streamNewTransactions](stream-new-transactions.md) | `fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [submitTransaction](submit-transaction.md) | `fun submitTransaction(request: SubmitTransactionRequest, onCompleted: (SubmitTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [whitelistTransaction](whitelist-transaction.md) | `fun whitelistTransaction(request: WhitelistTransactionRequest, onCompleted: (WhitelistTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
