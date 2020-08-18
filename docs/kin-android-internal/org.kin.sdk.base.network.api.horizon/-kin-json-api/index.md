[kin-android](../../index.md) / [org.kin.sdk.base.network.api.horizon](../index.md) / [KinJsonApi](./index.md)

# KinJsonApi

`abstract class KinJsonApi`

### Types

| Name | Summary |
|---|---|
| [MalformedBodyException](-malformed-body-exception.md) | `object MalformedBodyException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |
| [TimeoutException](-timeout-exception.md) | `object TimeoutException : `[`Exception`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `KinJsonApi(environment: `[`ApiConfig`](../../org.kin.sdk.base.stellar.models/-api-config/index.md)`, okHttpClient: OkHttpClient, server: KinServer = Server(environment.networkEndpoint, okHttpClient))` |

### Properties

| Name | Summary |
|---|---|
| [environment](environment.md) | `val environment: `[`ApiConfig`](../../org.kin.sdk.base.stellar.models/-api-config/index.md) |
| [okHttpClient](ok-http-client.md) | `val okHttpClient: OkHttpClient` |
| [server](server.md) | `val server: KinServer` |

### Inheritors

| Name | Summary |
|---|---|
| [HorizonKinApi](../-horizon-kin-api/index.md) | `class HorizonKinApi : `[`KinJsonApi`](./index.md)`, `[`KinAccountApi`](../../org.kin.sdk.base.network.api/-kin-account-api/index.md)`, `[`KinTransactionApi`](../../org.kin.sdk.base.network.api/-kin-transaction-api/index.md)`, `[`KinStreamingApi`](../../org.kin.sdk.base.network.api/-kin-streaming-api/index.md) |
