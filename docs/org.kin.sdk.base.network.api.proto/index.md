[kin-android](../index.md) / [org.kin.sdk.base.network.api.proto](./index.md)

## Package org.kin.sdk.base.network.api.proto

### Types

| Name | Summary |
|---|---|
| [AgoraKinAccountsApi](-agora-kin-accounts-api/index.md) | `class AgoraKinAccountsApi : `[`GrpcApi`](-grpc-api/index.md)`, `[`KinAccountApi`](../org.kin.sdk.base.network.api/-kin-account-api/index.md)`, `[`KinAccountCreationApi`](../org.kin.sdk.base.network.api/-kin-account-creation-api/index.md) |
| [AgoraKinTransactionsApi](-agora-kin-transactions-api/index.md) | `class AgoraKinTransactionsApi : `[`GrpcApi`](-grpc-api/index.md)`, `[`KinTransactionApi`](../org.kin.sdk.base.network.api/-kin-transaction-api/index.md)`, `[`KinTransactionWhitelistingApi`](../org.kin.sdk.base.network.api/-kin-transaction-whitelisting-api/index.md) |
| [GrpcApi](-grpc-api/index.md) | `abstract class GrpcApi` |

### Extensions for External Classes

| Name | Summary |
|---|---|
| [org.kin.gen.transaction.v3.TransactionService.HistoryItem](org.kin.gen.transaction.v3.-transaction-service.-history-item/index.md) |  |

### Functions

| Name | Summary |
|---|---|
| [toGrpcRequest](to-grpc-request.md) | `fun CreateAccountRequest.toGrpcRequest(): CreateAccountRequest!`<br>`fun GetAccountRequest.toGrpcRequest(): GetAccountInfoRequest!`<br>`fun GetTransactionHistoryRequest.toGrpcRequest(): GetHistoryRequest!`<br>`fun GetTransactionRequest.toGrpcRequest(): GetTransactionRequest!`<br>`fun SubmitTransactionRequest.toGrpcRequest(): SubmitSendRequest!` |
| [toProtoStellarAccountId](to-proto-stellar-account-id.md) | `fun Id.toProtoStellarAccountId(): StellarAccountId!` |
| [toProtoTransactionHash](to-proto-transaction-hash.md) | `fun `[`TransactionHash`](../org.kin.sdk.base.models/-transaction-hash/index.md)`.toProtoTransactionHash(): TransactionHash!` |
