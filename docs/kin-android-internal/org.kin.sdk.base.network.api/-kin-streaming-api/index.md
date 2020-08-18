[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinStreamingApi](./index.md)

# KinStreamingApi

`interface KinStreamingApi`

### Functions

| Name | Summary |
|---|---|
| [streamAccount](stream-account.md) | `abstract fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [streamNewTransactions](stream-new-transactions.md) | `abstract fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinAccountsApi](../../org.kin.sdk.base.network.api.agora/-agora-kin-accounts-api/index.md) | `class AgoraKinAccountsApi : `[`GrpcApi`](../../org.kin.sdk.base.network.api.agora/-grpc-api/index.md)`, `[`KinAccountApi`](../-kin-account-api/index.md)`, `[`KinStreamingApi`](./index.md)`, `[`KinAccountCreationApi`](../-kin-account-creation-api/index.md) |
| [HorizonKinApi](../../org.kin.sdk.base.network.api.horizon/-horizon-kin-api/index.md) | `class HorizonKinApi : `[`KinJsonApi`](../../org.kin.sdk.base.network.api.horizon/-kin-json-api/index.md)`, `[`KinAccountApi`](../-kin-account-api/index.md)`, `[`KinTransactionApi`](../-kin-transaction-api/index.md)`, `[`KinStreamingApi`](./index.md) |
