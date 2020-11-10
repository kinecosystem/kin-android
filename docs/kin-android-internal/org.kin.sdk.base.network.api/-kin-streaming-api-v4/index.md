[kin-android](../../index.md) / [org.kin.sdk.base.network.api](../index.md) / [KinStreamingApiV4](./index.md)

# KinStreamingApiV4

`interface KinStreamingApiV4`

### Functions

| Name | Summary |
|---|---|
| [streamAccount](stream-account.md) | `abstract fun streamAccount(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinAccount`](../../org.kin.sdk.base.models/-kin-account/index.md)`>` |
| [streamNewTransactions](stream-new-transactions.md) | `abstract fun streamNewTransactions(kinAccountId: Id): `[`Observer`](../../org.kin.sdk.base.tools/-observer/index.md)`<`[`KinTransaction`](../../org.kin.sdk.base.stellar.models/-kin-transaction/index.md)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [AgoraKinAccountApiV4](../../org.kin.sdk.base.network.api.agora/-agora-kin-account-api-v4/index.md) | `class AgoraKinAccountApiV4 : `[`GrpcApi`](../../org.kin.sdk.base.network.api.agora/-grpc-api/index.md)`, `[`KinAccountApiV4`](../-kin-account-api-v4/index.md)`, `[`KinStreamingApiV4`](./index.md) |
