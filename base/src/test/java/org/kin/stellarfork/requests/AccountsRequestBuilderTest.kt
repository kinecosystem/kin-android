package org.kin.stellarfork.requests

import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.Server

class AccountsRequestBuilderTest {
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
        val uri = server!!.accounts()
            .cursor("13537736921089")
            .limit(200)
            .order(RequestBuilder.Order.ASC)
            .buildUri()
        Assert.assertEquals(
            "https://horizon-testnet.stellar.org/accounts?cursor=13537736921089&limit=200&order=asc",
            uri.toString()
        )
    }

    @Test
    fun testAccounts_acount() {
        val uri = server!!.accounts()
            .forAccount(KeyPair.fromAccountId("GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN"))
            .buildUri()
        Assert.assertEquals(
            "https://horizon-testnet.stellar.org/accounts/GAQQOLVJB35BIUZMARHI75OYLIS7NWFZOBV3C7YQ37CT3RVXRIQC6CXN",
            uri.toString()
        )
    }
}
