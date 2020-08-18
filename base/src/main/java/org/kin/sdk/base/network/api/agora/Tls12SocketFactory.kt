package org.kin.sdk.base.network.api.agora

import okhttp3.TlsVersion
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

/**
 * Implementation of [SSLSocketFactory] that adds [TlsVersion.TLS_1_2] as an enabled protocol for every [SSLSocket]
 * created by [delegate].
 *
 * [See this discussion for more details.](https://github.com/square/okhttp/issues/2372#issuecomment-244807676)
 *
 * @see SSLSocket
 * @see SSLSocketFactory
 */
class Tls12SocketFactory(private val delegate: SSLSocketFactory) : SSLSocketFactory() {
    /**
     * Forcefully adds [TlsVersion.TLS_1_2] as an enabled protocol if called on an [SSLSocket]
     *
     * @return the (potentially modified) [Socket]
     */
    private fun Socket.patchForTls12(): Socket {
        return (this as? SSLSocket)?.apply {
            enabledProtocols += TlsVersion.TLS_1_2.javaName()
        } ?: this
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket? {
        return delegate.createSocket(s, host, port, autoClose)
            .patchForTls12()
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int): Socket? {
        return delegate.createSocket(host, port)
            .patchForTls12()
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket? {
        return delegate.createSocket(host, port, localHost, localPort)
            .patchForTls12()
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket? {
        return delegate.createSocket(host, port)
            .patchForTls12()
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket? {
        return delegate.createSocket(address, port, localAddress, localPort)
            .patchForTls12()
    }
}
