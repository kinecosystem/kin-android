[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [AgoraKinAccountApiV4](./index.md)

# AgoraKinAccountApiV4

`class AgoraKinAccountApiV4 : `[`GrpcApi`](../-grpc-api/index.md)`, `[`KinAccountApiV4`](../../org.kin.sdk.base.network.api/-kin-account-api-v4/index.md)`, `[`KinStreamingApiV4`](../../org.kin.sdk.base.network.api/-kin-streaming-api-v4/index.md)

### Types

| Name | Summary |
|---|---|
| [AgoraEvent](-agora-event/index.md) | `sealed class AgoraEvent` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AgoraKinAccountApiV4(managedChannel: ManagedChannel, networkEnvironment: `[`NetworkEnvironment`](../../org.kin.sdk.base.stellar.models/-network-environment/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [getAccount](get-account.md) | `fun getAccount(request: GetAccountRequest, onCompleted: (GetAccountResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [openEventStream](open-event-stream.md) | `fun openEventStream(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<AgoraEvent>` |
| [resolveTokenAcounts](resolve-token-acounts.md) | `fun resolveTokenAcounts(request: ResolveTokenAccountsRequest, onCompleted: (ResolveTokenAccountsResponse) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [streamAccount](stream-account.md) | `fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [streamNewTransactions](stream-new-transactions.md) | `fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |
