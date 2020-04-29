package org.kin.stellarfork.responses

import org.junit.Test
import org.kin.stellarfork.responses.GsonSingleton.instance
import kotlin.test.assertEquals

class LedgerDeserializerTest {
    @Test
    fun testDeserialize() {
        val json = "{\n" +
                "  \"_links\": {\n" +
                "    \"effects\": {\n" +
                "      \"href\": \"/ledgers/898826/effects{?cursor,limit,order}\",\n" +
                "      \"templated\": true\n" +
                "    },\n" +
                "    \"operations\": {\n" +
                "      \"href\": \"/ledgers/898826/operations{?cursor,limit,order}\",\n" +
                "      \"templated\": true\n" +
                "    },\n" +
                "    \"self\": {\n" +
                "      \"href\": \"/ledgers/898826\"\n" +
                "    },\n" +
                "    \"transactions\": {\n" +
                "      \"href\": \"/ledgers/898826/transactions{?cursor,limit,order}\",\n" +
                "      \"templated\": true\n" +
                "    }\n" +
                "  },\n" +
                "  \"id\": \"686bb246db89b099cd3963a4633eb5e4315d89dfd3c00594c80b41a483847bfa\",\n" +
                "  \"paging_token\": \"3860428274794496\",\n" +
                "  \"hash\": \"686bb246db89b099cd3963a4633eb5e4315d89dfd3c00594c80b41a483847bfa\",\n" +
                "  \"prev_hash\": \"50c8695eb32171a19858413e397cc50b504ceacc819010bdf8ff873aff7858d7\",\n" +
                "  \"sequence\": 898826,\n" +
                "  \"transaction_count\": 1,\n" +
                "  \"operation_count\": 2,\n" +
                "  \"closed_at\": \"2015-11-19T21:35:59Z\",\n" +
                "  \"total_coins\": \"101343867604.8975480\",\n" +
                "  \"fee_pool\": \"1908.2248818\",\n" +
                "  \"base_fee_in_stroops\": 100,\n" +
                "  \"base_reserve\": \"10.0000000\",\n" +
                "  \"max_tx_set_size\": 50\n" +
                "}"
        val response = instance!!.fromJson(
            json,
            LedgerResponse::class.java
        )
        assertEquals(
            response.hash,
            "686bb246db89b099cd3963a4633eb5e4315d89dfd3c00594c80b41a483847bfa"
        )
        assertEquals(response.pagingToken, "3860428274794496")
        assertEquals(
            response.prevHash,
            "50c8695eb32171a19858413e397cc50b504ceacc819010bdf8ff873aff7858d7"
        )
        assertEquals(response.sequence, 898826L)
        assertEquals(response.transactionCount, 1)
        assertEquals(response.operationCount, 2)
        assertEquals(response.closedAt, "2015-11-19T21:35:59Z")
        assertEquals(response.totalCoins, "101343867604.8975480")
        assertEquals(response.feePool, "1908.2248818")
        assertEquals(response.baseFee, 100)
        assertEquals(response.baseReserve, "10.0000000")
        assertEquals(response.maxTxSetSize, 50)
        assertEquals(
            response.links.effects.href,
            "/ledgers/898826/effects{?cursor,limit,order}"
        )
        assertEquals(
            response.links.operations.href,
            "/ledgers/898826/operations{?cursor,limit,order}"
        )
        assertEquals(response.links.self.href, "/ledgers/898826")
        assertEquals(
            response.links.transactions.href,
            "/ledgers/898826/transactions{?cursor,limit,order}"
        )
    }
}
