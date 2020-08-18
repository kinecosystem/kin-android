[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [AppUserAuthInterceptor](./index.md)

# AppUserAuthInterceptor

`class AppUserAuthInterceptor : ClientInterceptor`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `AppUserAuthInterceptor(appInfoProvider: `[`AppInfoProvider`](../../org.kin.sdk.base.network.services/-app-info-provider/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [interceptCall](intercept-call.md) | `fun <ReqT, RespT> interceptCall(method: MethodDescriptor<ReqT, RespT>?, callOptions: CallOptions?, next: Channel?): ClientCall<ReqT, RespT>` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [HEADER_KEY_APP_USER_ID](-h-e-a-d-e-r_-k-e-y_-a-p-p_-u-s-e-r_-i-d.md) | `val HEADER_KEY_APP_USER_ID: Key<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [HEADER_KEY_APP_USER_PASSKEY](-h-e-a-d-e-r_-k-e-y_-a-p-p_-u-s-e-r_-p-a-s-s-k-e-y.md) | `val HEADER_KEY_APP_USER_PASSKEY: Key<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
