[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [OkHttpChannelBuilderForcedTls12](index.md) / [forAddress](./for-address.md)

# forAddress

`fun forAddress(host: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, port: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`OkHttpChannelBuilderForcedTls12`](index.md)

If on [Build.VERSION_CODES.LOLLIPOP](#) or lower, sets [OkHttpChannelBuilder.sslSocketFactory](#) to an instance of
[Tls12SocketFactory](../-tls12-socket-factory/index.md) that wraps the default [SSLContext.getSocketFactory](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLContext.html#getSocketFactory()) for [TlsVersion.TLS_1_2](#).

For some reason, Android supports TLS v1.2 from [Build.VERSION_CODES.JELLY_BEAN](#), but the spec only has it
enabled by default from API [Build.VERSION_CODES.KITKAT](#). Furthermore, some devices on
[Build.VERSION_CODES.LOLLIPOP](#) don't have it enabled, despite the spec saying they should.

**Return**
the (potentially modified) [OkHttpChannelBuilder](#)

