package org.kin.stellarfork

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.kin.stellarfork.KeyPair.Companion.fromAccountId
import org.kin.stellarfork.KeyPair.Companion.fromSecretSeed
import org.kin.stellarfork.Memo.Companion.text
import org.kin.stellarfork.Network.Companion.testNetwork
import org.mockito.MockitoAnnotations
import java.io.IOException
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ServerTest {
    private var mockWebServer: MockWebServer? = null
    private val successResponse = ("{\n" +
            "  \"_links\": {\n" +
            "    \"transaction\": {\n" +
            "      \"href\": \"/transactions/2634d2cf5adcbd3487d1df042166eef53830115844fdde1588828667bf93ff42\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"hash\": \"2634d2cf5adcbd3487d1df042166eef53830115844fdde1588828667bf93ff42\",\n" +
            "  \"ledger\": 826150,\n" +
            "  \"envelope_xdr\": \"AAAAAKu3N77S+cHLEDfVD2eW/CqRiN9yvAKH+qkeLjHQs1u+AAAAZAAMkoMAAAADAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAbYQq8ek1GitmNBUloGnetfWxSpxlsgK48Xi66dIL3MoAAAAAC+vCAAAAAAAAAAAB0LNbvgAAAEDadQ25SNHWTg0L+2wr/KNWd8/EwSNFkX/ncGmBGA3zkNGx7lAow78q8SQmnn2IsdkD9MwICirhsOYDNbaqShwO\",\n"
            +
            "  \"result_xdr\": \"AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAA=\",\n" +
            "  \"result_meta_xdr\": \"AAAAAAAAAAEAAAACAAAAAAAMmyYAAAAAAAAAAG2EKvHpNRorZjQVJaBp3rX1sUqcZbICuPF4uunSC9zKAAAAAAvrwgAADJsmAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAQAMmyYAAAAAAAAAAKu3N77S+cHLEDfVD2eW/CqRiN9yvAKH+qkeLjHQs1u+AAAAFzCfYtQADJKDAAAAAwAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA\"\n"
            +
            "}")
    private val failureResponse = ("{\n" +
            "  \"type\": \"https://stellar.org/horizon-errors/transaction_failed\",\n" +
            "  \"title\": \"Transaction Failed\",\n" +
            "  \"status\": 400,\n" +
            "  \"detail\": \"TODO\",\n" +
            "  \"instance\": \"horizon-testnet-001.prd.stellar001.internal.stellar-ops.com/IxhaI70Tqo-112305\",\n" +
            "  \"extras\": {\n" +
            "    \"envelope_xdr\": \"AAAAAK4Pg4OEkjGmSN0AN37K/dcKyKPT2DC90xvjjawKp136AAAAZAAKsZQAAAABAAAAAAAAAAEAAAAJSmF2YSBGVFchAAAAAAAAAQAAAAAAAAABAAAAAG9wfBI7rRYoBlX3qRa0KOnI75W5BaPU6NbyKmm2t71MAAAAAAAAAAABMS0AAAAAAAAAAAEKp136AAAAQOWEjL+Sm+WP2puE9dLIxWlOibIEOz8PsXyG77jOCVdHZfQvkgB49Mu5wqKCMWWIsDSLFekwUsLaunvmXrpyBwQ=\",\n"
            +
            "    \"result_codes\": {\n" +
            "      \"transaction\": \"tx_failed\",\n" +
            "      \"operations\": [\n" +
            "        \"op_no_destination\"\n" +
            "      ]\n" +
            "    },\n" +
            "    \"result_xdr\": \"AAAAAAAAAGT/////AAAAAQAAAAAAAAAB////+wAAAAA=\"\n" +
            "  }\n" +
            "}")
    private var server: Server? = null

    @Before
    @Throws(URISyntaxException::class, IOException::class)
    public fun setUp() {
        MockitoAnnotations.initMocks(this)
        mockWebServer = MockWebServer()
        mockWebServer!!.start()
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        server = Server(mockWebServer!!.url("/").url().toString(), client)
    }

    @Throws(IOException::class)
    fun buildTransaction(): Transaction { // GBPMKIRA2OQW2XZZQUCQILI5TMVZ6JNRKM423BSAISDM7ZFWQ6KWEBC4
        val source =
            fromSecretSeed("SCH27VUZZ6UAKB67BDNF6FA42YMBMQCBKXWGMFD5TZ6S5ZZCZFLRXKHS")
        val destination =
            fromAccountId("GDW6AUTBXTOC7FIKUO5BOO3OGLK4SF7ZPOBLMQHMZDI45J2Z6VXRB5NR")
        val account =
            Account(source, 2908908335136768L)
        val builder =
            Transaction.Builder(
                account,
                testNetwork
            )
                .addOperation(
                    CreateAccountOperation.Builder(
                        destination,
                        "2000"
                    ).build()
                )
                .addMemo(text("Hello world!"))
        assertEquals(1, builder.operationsCount)
        val transaction = builder.build()
        assertEquals(2908908335136769L, transaction.sequenceNumber)
        assertEquals(2908908335136769L, account.sequenceNumber)
        transaction.sign(source)
        return transaction
    }

    @Test
    @Throws(IOException::class)
    fun testSubmitTransactionSuccess() {
        mockWebServer!!.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(successResponse)
        )
        val response =
            server!!.transactions().submitTransaction(buildTransaction())
        assertTrue(response!!.isSuccess)
        assertEquals(response.ledger, 826150L)
        assertEquals(
            response.hash,
            "2634d2cf5adcbd3487d1df042166eef53830115844fdde1588828667bf93ff42"
        )
        assertNull(response.extras)
    }

    @Test
    @Throws(IOException::class)
    fun test_ResponseCodeHttp307_SubmitTransactionSuccess() {
        val mockWebServerHttp307 = MockWebServer()
        mockWebServerHttp307.start()
        val location = mockWebServerHttp307.url("/").url().toString()
        mockWebServer!!.enqueue(
            MockResponse()
                .setResponseCode(307)
                .setHeader("Location", location)
        )
        mockWebServerHttp307.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(successResponse)
        )
        val response =
            server!!.transactions().submitTransaction(buildTransaction())
        assertTrue(response!!.isSuccess)
        assertEquals(response.ledger, 826150L)
        assertEquals(
            response.hash,
            "2634d2cf5adcbd3487d1df042166eef53830115844fdde1588828667bf93ff42"
        )
        assertNull(response.extras)
    }

    @Test
    @Throws(IOException::class)
    fun testSubmitTransactionFail() {
        mockWebServer!!.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody(failureResponse)
        )
        val response =
            server!!.transactions().submitTransaction(buildTransaction())
        assertFalse(response!!.isSuccess)
        assertNull(response.ledger)
        assertNull(response.hash)
        assertEquals(
            response.extras.envelopeXdr,
            "AAAAAK4Pg4OEkjGmSN0AN37K/dcKyKPT2DC90xvjjawKp136AAAAZAAKsZQAAAABAAAAAAAAAAEAAAAJSmF2YSBGVFchAAAAAAAAAQAAAAAAAAABAAAAAG9wfBI7rRYoBlX3qRa0KOnI75W5BaPU6NbyKmm2t71MAAAAAAAAAAABMS0AAAAAAAAAAAEKp136AAAAQOWEjL+Sm+WP2puE9dLIxWlOibIEOz8PsXyG77jOCVdHZfQvkgB49Mu5wqKCMWWIsDSLFekwUsLaunvmXrpyBwQ="
        )
        assertEquals(
            response.extras.resultXdr,
            "AAAAAAAAAGT/////AAAAAQAAAAAAAAAB////+wAAAAA="
        )
        assertNotNull(response.extras)
        assertEquals("tx_failed", response.extras.resultCodes.transactionResultCode)
        assertEquals(
            "op_no_destination",
            response.extras.resultCodes.operationsResultCodes[0]
        )
    }
}
