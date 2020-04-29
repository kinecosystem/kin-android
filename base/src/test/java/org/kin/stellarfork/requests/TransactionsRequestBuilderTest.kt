package org.kin.stellarfork.requests

import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.kin.stellarfork.KeyPair.Companion.fromAccountId
import org.kin.stellarfork.Server

class TransactionsRequestBuilderTest {
    private var server: Server? = null
    @Before
    fun before() {
        server = Server(
            "https://horizon-testnet.stellar.org",
            OkHttpClient()
        )
    }

    @Test
    fun testTransactions() {
        val uri = server!!.transactions()
            .limit(200)
            .order(RequestBuilder.Order.DESC)
            .buildUri()
        Assert.assertEquals(
            "https://horizon-testnet.stellar.org/transactions?limit=200&order=desc",
            uri.toString()
        )
    }

    @Test
    fun testForAccount() {
        val uri = server!!.transactions()
            .forAccount(fromAccountId("GBRPYHIL2CI3FNQ4BXLFMNDLFJUNPU2HY3ZMFSHONUCEOASW7QC7OX2H"))
            .limit(200)
            .order(RequestBuilder.Order.DESC)
            .buildUri()
        Assert.assertEquals(
            "https://horizon-testnet.stellar.org/accounts/GBRPYHIL2CI3FNQ4BXLFMNDLFJUNPU2HY3ZMFSHONUCEOASW7QC7OX2H/transactions?limit=200&order=desc",
            uri.toString()
        )
    }

    @Test
    fun testForLedger() {
        val uri = server!!.transactions()
            .forLedger(200000000000L)
            .limit(50)
            .order(RequestBuilder.Order.ASC)
            .buildUri()
        Assert.assertEquals(
            "https://horizon-testnet.stellar.org/ledgers/200000000000/transactions?limit=50&order=asc",
            uri.toString()
        )
    }
}
