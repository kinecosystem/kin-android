[kin-android](../../index.md) / [org.kin.sdk.base.network.api.agora](../index.md) / [Tls12SocketFactory](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`Tls12SocketFactory(delegate: `[`SSLSocketFactory`](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html)`)`

Implementation of [SSLSocketFactory](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html) that adds [TlsVersion.TLS_1_2](#) as an enabled protocol for every [SSLSocket](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocket.html)
created by [delegate](#).

[See this discussion for more details.](https://github.com/square/okhttp/issues/2372#issuecomment-244807676)

**See Also**

[SSLSocket](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocket.html)

[SSLSocketFactory](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLSocketFactory.html)

