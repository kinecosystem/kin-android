package org.kin.stellarfork.requests

import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kin.stellarfork.Server

class LedgersRequestBuilderTest {
    private var server: Server? = null
    @Before
    fun before() {
        server = Server(
            "https://horizon-testnet.stellar.org",
            OkHttpClient()
        )
    }

    @Test
    fun testAccounts() {
        val uri = server!!.ledgers()
            .limit(200)
            .order(RequestBuilder.Order.ASC)
            .buildUri()
        Assert.assertEquals(
            "https://horizon-testnet.stellar.org/ledgers?limit=200&order=asc",
            uri.toString()
        )
    }
}
