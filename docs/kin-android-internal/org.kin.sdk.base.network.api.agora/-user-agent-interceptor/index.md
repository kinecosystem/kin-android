[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [UserAgentInterceptor](./index.md)

# UserAgentInterceptor

`class UserAgentInterceptor : ClientInterceptor`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UserAgentInterceptor(storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [storage](storage.md) | `val storage: `[`Storage`](../../org.kin.sdk.base.storage/-storage/index.md) |

### Functions

| Name | Summary |
|---|---|
| [interceptCall](intercept-call.md) | `fun <ReqT, RespT> interceptCall(method: MethodDescriptor<ReqT, RespT>?, callOptions: CallOptions?, next: Channel?): ClientCall<ReqT, RespT>` |
