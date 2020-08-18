[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [AgoraKinAccountsApi](./index.md)

# AgoraKinAccountsApi

`class AgoraKinAccountsApi : `[`GrpcApi`](../-grpc-api/index.md)`, `[`KinAccountApi`](../../org.kin.sdk.base.network.api/-kin-account-api/index.md)`, `[`KinStreamingApi`](../../org.kin.sdk.base.network.api/-kin-streaming-api/index.md)`, `[`KinAccountCreationApi`](../../org.kin.sdk.base.network.api/-kin-account-creation-api/index.md)

### Types

| Name | Summary |
|---|---|
| [AgoraEvent](-agora-event/index.md) | `sealed class AgoraEvent` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AgoraKinAccountsApi(managedChannel: ManagedChannel, networkEnvironment: `[`NetworkEnvironment`](../../org.kin.sdk.base.stellar.models/-network-environment/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [createAccount](create-account.md) | Developers are expected to call their back-end's to register this address with the main-net Kin Blockchain.`fun createAccount(request: CreateAccountRequest, onCompleted: (CreateAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getAccount](get-account.md) | `fun getAccount(request: GetAccountRequest, onCompleted: (GetAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [openEventStream](open-event-stream.md) | `fun openEventStream(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<AgoraEvent>` |
| [streamAccount](stream-account.md) | `fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [streamNewTransactions](stream-new-transactions.md) | `fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
