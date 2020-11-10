[kin-android](../index.md) / [org.kin.sdk.base.network.api.agora](./index.md)

## Package org.kin.sdk.base.network.api.agora

### Types

| Name | Summary |
|---|---|
| [AgoraKinAccountApiV4](-agora-kin-account-api-v4/index.md) | `class AgoraKinAccountApiV4 : `[`GrpcApi`](-grpc-api/index.md)`, `[`KinAccountApiV4`](../org.kin.sdk.base.network.api/-kin-account-api-v4/index.md)`, `[`KinStreamingApiV4`](../org.kin.sdk.base.network.api/-kin-streaming-api-v4/index.md) |
| [AgoraKinAccountCreationApiV4](-agora-kin-account-creation-api-v4/index.md) | `class AgoraKinAccountCreationApiV4 : `[`GrpcApi`](-grpc-api/index.md)`, `[`KinAccountCreationApiV4`](../org.kin.sdk.base.network.api/-kin-account-creation-api-v4/index.md) |
| [AgoraKinAccountsApi](-agora-kin-accounts-api/index.md) | `class AgoraKinAccountsApi : `[`GrpcApi`](-grpc-api/index.md)`, `[`KinAccountApi`](../org.kin.sdk.base.network.api/-kin-account-api/index.md)`, `[`KinStreamingApi`](../org.kin.sdk.base.network.api/-kin-streaming-api/index.md)`, `[`KinAccountCreationApi`](../org.kin.sdk.base.network.api/-kin-account-creation-api/index.md) |
| [AgoraKinTransactionsApi](-agora-kin-transactions-api/index.md) | `class AgoraKinTransactionsApi : `[`GrpcApi`](-grpc-api/index.md)`, `[`KinTransactionApi`](../org.kin.sdk.base.network.api/-kin-transaction-api/index.md)`, `[`KinTransactionWhitelistingApi`](../org.kin.sdk.base.network.api/-kin-transaction-whitelisting-api/index.md) |
| [AgoraKinTransactionsApiV4](-agora-kin-transactions-api-v4/index.md) | `class AgoraKinTransactionsApiV4 : `[`GrpcApi`](-grpc-api/index.md)`, `[`KinTransactionApiV4`](../org.kin.sdk.base.network.api/-kin-transaction-api-v4/index.md) |
| [AppUserAuthInterceptor](-app-user-auth-interceptor/index.md) | `class AppUserAuthInterceptor : ClientInterceptor` |
| [GrpcApi](-grpc-api/index.md) | `abstract class GrpcApi` |
| [LoggingInterceptor](-logging-interceptor/index.md) | `class LoggingInterceptor : ClientInterceptor` |
| [OkHttpChannelBuilderForcedTls12](-ok-http-channel-builder-forced-tls12/index.md) | OkHttpChannelBuilder which forces Tls1.2 ssl context in the builder in case it is not the default set by the system`class OkHttpChannelBuilderForcedTls12 : OkHttpChannelBuilder` |
| [Tls12SocketFactory](-tls12-socket-factory/index.md) | Implementation of [SSLSocketFactory](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html) that adds [TlsVersion.TLS_1_2](#) as an enabled protocol for every [SSLSocket](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocket.html) created by [delegate](#).`class Tls12SocketFactory : `[`SSLSocketFactory`](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html) |
| [UpgradeApiV4Interceptor](-upgrade-api-v4-interceptor/index.md) | `class UpgradeApiV4Interceptor : ClientInterceptor` |
| [UserAgentInterceptor](-user-agent-interceptor/index.md) | `class UserAgentInterceptor : ClientInterceptor` |

### Extensions for External Classes

| Name | Summary |
|---|---|
| [kotlin.collections.List](kotlin.collections.-list/index.md) |  |
| [org.kin.agora.gen.common.v3.Model.Invoice](org.kin.agora.gen.common.v3.-model.-invoice/index.md) |  |
| [org.kin.agora.gen.common.v3.Model.InvoiceList](org.kin.agora.gen.common.v3.-model.-invoice-list/index.md) |  |

### Functions

| Name | Summary |
|---|---|
| [toGrpcRequest](to-grpc-request.md) | `fun CreateAccountRequest.toGrpcRequest(): CreateAccountRequest`<br>`fun GetAccountRequest.toGrpcRequest(): GetAccountInfoRequest`<br>`fun GetTransactionHistoryRequest.toGrpcRequest(): GetHistoryRequest`<br>`fun GetTransactionRequest.toGrpcRequest(): GetTransactionRequest`<br>`fun SubmitTransactionRequest.toGrpcRequest(): SubmitTransactionRequest?` |
| [toProto](to-proto.md) | `fun `[`LineItem`](../org.kin.sdk.base.models/-line-item/index.md)`.toProto(): LineItem!`<br>`fun `[`Invoice`](../org.kin.sdk.base.models/-invoice/index.md)`.toProto(): Invoice!`<br>`fun `[`InvoiceList`](../org.kin.sdk.base.models/-invoice-list/index.md)`.toProto(): InvoiceList!` |
| [toProtoStellarAccountId](to-proto-stellar-account-id.md) | `fun Id.toProtoStellarAccountId(): StellarAccountId` |
| [toProtoTransactionHash](to-proto-transaction-hash.md) | `fun `[`TransactionHash`](../org.kin.sdk.base.models/-transaction-hash/index.md)`.toProtoTransactionHash(): TransactionHash` |
