[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [OkHttpChannelBuilderForcedTls12](./index.md)

# OkHttpChannelBuilderForcedTls12

`class OkHttpChannelBuilderForcedTls12 : OkHttpChannelBuilder`

OkHttpChannelBuilder which forces Tls1.2 ssl context in the builder in case it is not the default set by the system

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | OkHttpChannelBuilder which forces Tls1.2 ssl context in the builder in case it is not the default set by the system`OkHttpChannelBuilderForcedTls12(host: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, port: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [forAddress](for-address.md) | If on [Build.VERSION_CODES.LOLLIPOP](#) or lower, sets [OkHttpChannelBuilder.sslSocketFactory](#) to an instance of [Tls12SocketFactory](../-tls12-socket-factory/index.md) that wraps the default [SSLContext.getSocketFactory](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLContext.html#getSocketFactory()) for [TlsVersion.TLS_1_2](#).`fun forAddress(host: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, port: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`OkHttpChannelBuilderForcedTls12`](./index.md) |
