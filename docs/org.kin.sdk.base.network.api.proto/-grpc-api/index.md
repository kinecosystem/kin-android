[kin-android](../../index.md) / [org.kin.sdk.base.network.api.proto](../index.md) / [GrpcApi](./index.md)

# GrpcApi

`abstract class GrpcApi`

### Types

| Name | Summary |
|---|---|
| [UnrecognizedResultException](-unrecognized-result-exception.md) | `object UnrecognizedResultException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GrpcApi(managedChannel: ManagedChannel)` |

### Properties

| Name | Summary |
|---|---|
| [managedChannel](managed-channel.md) | `val managedChannel: ManagedChannel` |

### Functions

| Name | Summary |
|---|---|
| [callAsPromisedCallback](call-as-promised-callback.md) | `fun <Request, Response> `[`KFunction2`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-function2/index.html)`<Request, StreamObserver<Response>, `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`>.callAsPromisedCallback(request: Request, callback: `[`PromisedCallback`](../../org.kin.sdk.base.tools/-promised-callback/index.md)`<Response>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [canRetry](can-retry.md) | `fun `[`Throwable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)`.canRetry(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinAccountsApi](../-agora-kin-accounts-api/index.md) | `class AgoraKinAccountsApi : `[`GrpcApi`](./index.md)`, `[`KinAccountApi`](../../org.kin.sdk.base.network.api/-kin-account-api/index.md)`, `[`KinAccountCreationApi`](../../org.kin.sdk.base.network.api/-kin-account-creation-api/index.md) |
| [AgoraKinTransactionsApi](../-agora-kin-transactions-api/index.md) | `class AgoraKinTransactionsApi : `[`GrpcApi`](./index.md)`, `[`KinTransactionApi`](../../org.kin.sdk.base.network.api/-kin-transaction-api/index.md)`, `[`KinTransactionWhitelistingApi`](../../org.kin.sdk.base.network.api/-kin-transaction-whitelisting-api/index.md) |
