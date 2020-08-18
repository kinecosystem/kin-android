[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [Tls12SocketFactory](./index.md)

# Tls12SocketFactory

`class Tls12SocketFactory : `[`SSLSocketFactory`](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html)

Implementation of [SSLSocketFactory](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html) that adds [TlsVersion.TLS_1_2](#) as an enabled protocol for every [SSLSocket](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocket.html)
created by [delegate](#).

[See this discussion for more details.](https://github.com/square/okhttp/issues/2372#issuecomment-244807676)

**See Also**

[SSLSocket](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocket.html)

[SSLSocketFactory](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Implementation of [SSLSocketFactory](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html) that adds [TlsVersion.TLS_1_2](#) as an enabled protocol for every [SSLSocket](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocket.html) created by [delegate](#).`Tls12SocketFactory(delegate: `[`SSLSocketFactory`](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html)`)` |

### Functions

| Name | Summary |
|---|---|
| [createSocket](create-socket.md) | `fun createSocket(s: `[`Socket`](https://docs.oracle.com/javase/6/docs/api/java/net/Socket.html)`, host: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, port: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, autoClose: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Socket`](https://docs.oracle.com/javase/6/docs/api/java/net/Socket.html)`?`<br>`fun createSocket(host: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, port: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Socket`](https://docs.oracle.com/javase/6/docs/api/java/net/Socket.html)`?`<br>`fun createSocket(host: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, port: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, localHost: `[`InetAddress`](https://docs.oracle.com/javase/6/docs/api/java/net/InetAddress.html)`, localPort: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Socket`](https://docs.oracle.com/javase/6/docs/api/java/net/Socket.html)`?`<br>`fun createSocket(host: `[`InetAddress`](https://docs.oracle.com/javase/6/docs/api/java/net/InetAddress.html)`, port: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Socket`](https://docs.oracle.com/javase/6/docs/api/java/net/Socket.html)`?`<br>`fun createSocket(address: `[`InetAddress`](https://docs.oracle.com/javase/6/docs/api/java/net/InetAddress.html)`, port: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, localAddress: `[`InetAddress`](https://docs.oracle.com/javase/6/docs/api/java/net/InetAddress.html)`, localPort: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Socket`](https://docs.oracle.com/javase/6/docs/api/java/net/Socket.html)`?` |
| [getDefaultCipherSuites](get-default-cipher-suites.md) | `fun getDefaultCipherSuites(): `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [getSupportedCipherSuites](get-supported-cipher-suites.md) | `fun getSupportedCipherSuites(): `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
