package org.kin.sdk.base.network.api.agora

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannelBuilder
import io.grpc.internal.AbstractManagedChannelImplBuilder
import io.grpc.okhttp.OkHttpChannelBuilder
import okhttp3.TlsVersion
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * OkHttpChannelBuilder which forces Tls1.2 ssl context in the builder in case it is not the default set by the system
 */
class OkHttpChannelBuilderForcedTls12(host: String?, port: Int) : AbstractManagedChannelImplBuilder<OkHttpChannelBuilder>() {

    private val builder = OkHttpChannelBuilder.forAddress(host, port)

    companion object {
        /**
         * @return [X509TrustManager] from [TrustManagerFactory]
         *
         * @throws [NoSuchElementException] if not found. According to the Android docs for [TrustManagerFactory], this
         * should never happen because PKIX is the only supported algorithm
         */
        private val trustManager by lazy {
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(null as KeyStore?)
            }.trustManagers.first { it is X509TrustManager } as X509TrustManager
        }

        /**
         * If on [Build.VERSION_CODES.LOLLIPOP] or lower, sets [OkHttpChannelBuilder.sslSocketFactory] to an instance of
         * [Tls12SocketFactory] that wraps the default [SSLContext.getSocketFactory] for [TlsVersion.TLS_1_2].
         *
         * For some reason, Android supports TLS v1.2 from [Build.VERSION_CODES.JELLY_BEAN], but the spec only has it
         * enabled by default from API [Build.VERSION_CODES.KITKAT]. Furthermore, some devices on
         * [Build.VERSION_CODES.LOLLIPOP] don't have it enabled, despite the spec saying they should.
         *insertNewTransaction
         * @return the (potentially modified) [OkHttpChannelBuilder]
         */
        fun forAddress(host: String?, port: Int): ManagedChannelBuilder<*> {
            val builder = OkHttpChannelBuilderForcedTls12(host, port)
            try {
                val sslContext = SSLContext.getInstance(TlsVersion.TLS_1_2.javaName())
                sslContext.init(null, arrayOf(trustManager), null)

                builder.builder.sslSocketFactory(Tls12SocketFactory(sslContext.socketFactory))
            } catch (e: Exception) {
            }
            return builder
        }
    }

    override fun delegate(): ManagedChannelBuilder<*> {
        return builder
    }
}
