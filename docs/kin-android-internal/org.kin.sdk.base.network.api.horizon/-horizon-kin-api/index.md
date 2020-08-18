[kin-android](../../index.md) / [org.kin.sdk.base.network.api.horizon](../index.md) / [HorizonKinApi](./index.md)

# HorizonKinApi

`class HorizonKinApi : `[`KinJsonApi`](../-kin-json-api/index.md)`, `[`KinAccountApi`](../../org.kin.sdk.base.network.api/-kin-account-api/index.md)`, `[`KinTransactionApi`](../../org.kin.sdk.base.network.api/-kin-transaction-api/index.md)`, `[`KinStreamingApi`](../../org.kin.sdk.base.network.api/-kin-streaming-api/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `HorizonKinApi(environment: `[`ApiConfig`](../../org.kin.sdk.base.stellar.models/-api-config/index.md)`, okHttpClient: OkHttpClient)` |

### Functions

| Name | Summary |
|---|---|
| [getAccount](get-account.md) | `fun getAccount(request: GetAccountRequest, onCompleted: (GetAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransaction](get-transaction.md) | `fun getTransaction(request: GetTransactionRequest, onCompleted: (GetTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransactionHistory](get-transaction-history.md) | `fun getTransactionHistory(request: GetTransactionHistoryRequest, onCompleted: (GetTransactionHistoryResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getTransactionMinFee](get-transaction-min-fee.md) | `fun getTransactionMinFee(onCompleted: (GetMinFeeForTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [streamAccount](stream-account.md) | `fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [streamNewTransactions](stream-new-transactions.md) | `fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
| [submitTransaction](submit-transaction.md) | `fun submitTransaction(request: SubmitTransactionRequest, onCompleted: (SubmitTransactionResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
