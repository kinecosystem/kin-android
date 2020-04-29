package org.kin.stellarfork.responses

import org.junit.Assert.assertEquals
import org.junit.Test
import org.kin.stellarfork.responses.GsonSingleton.instance

class AccountDeserializerTest {
    @Test
    fun testDeserialize() {
        val response: AccountResponse = instance!!.fromJson(
            json,
            AccountResponse::class.java
        )
        assertEquals(
            response.keypair.accountId,
            "GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7"
        )
        assertEquals(response.pagingToken, "1")
        assertEquals(response.sequenceNumber, 2319149195853854L)
        assertEquals(response.subentryCount, 0)
        assertEquals(
            response.inflationDestination,
            "GAGRSA6QNQJN2OQYCBNQGMFLO4QLZFNEHIFXOMTQVSUTWVTWT66TOFSC"
        )
        assertEquals(response.homeDomain, "stellar.org")
        assertEquals(response.thresholds?.lowThreshold, 10)
        assertEquals(response.thresholds?.medThreshold, 20)
        assertEquals(response.thresholds?.highThreshold, 30)
        assertEquals(response.flags!!.authRequired, false)
        assertEquals(response.flags!!.authRevocable, true)
        assertEquals(response.balances[0].assetType, "credit_alphanum4")
        assertEquals(response.balances[0].assetCode, "ABC")
        assertEquals(
            response.balances[0].getAssetIssuer().accountId,
            "GCRA6COW27CY5MTKIA7POQ2326C5ABYCXODBN4TFF5VL4FMBRHOT3YHU"
        )
        assertEquals(response.balances[0].balance, "1001.0000000")
        assertEquals(response.balances[0].limit, "12000.4775807")
        assertEquals(response.balances[1].assetType, "native")
        assertEquals(response.balances[1].balance, "20.0000300")
        assertEquals(response.balances[1].limit, null)
        assertEquals(
            response.signers[0].accountId,
            "GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7"
        )
        assertEquals(response.signers[0].weight, 0)
        assertEquals(
            response.signers[1].accountId,
            "GCR2KBCIU6KQXSQY5F5GZYC4WLNHCHCKW4NEGXNEZRYWLTNZIRJJY7D2"
        )
        assertEquals(response.signers[1].weight, 1)
        assertEquals(
            response.links!!.effects.href,
            "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/effects{?cursor,limit,order}"
        )
        assertEquals(
            response.links!!.offers.href,
            "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/offers{?cursor,limit,order}"
        )
        assertEquals(
            response.links!!.operations.href,
            "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/operations{?cursor,limit,order}"
        )
        assertEquals(
            response.links!!.self.href,
            "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7"
        )
        assertEquals(
            response.links!!.transactions.href,
            "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/transactions{?cursor,limit,order}"
        )
        assertEquals(
            0,
            response.subentryCount
        )
        assertEquals(
            "GAGRSA6QNQJN2OQYCBNQGMFLO4QLZFNEHIFXOMTQVSUTWVTWT66TOFSC",
            response.inflationDestination
        )
        assertEquals(
            "stellar.org",
            response.homeDomain
        )
        assertEquals(
            AccountResponse.Thresholds(
                10, 20, 30
            ),
            response.thresholds
        )
        assertEquals(
            AccountResponse.Flags(false, true),
            response.flags
        )
        assertEquals(
            AccountResponse.Signer(
                "GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7",
                0
            ),
            response.signers[0]
        )
        assertEquals(
            AccountResponse.Signer(
                "GCR2KBCIU6KQXSQY5F5GZYC4WLNHCHCKW4NEGXNEZRYWLTNZIRJJY7D2",
                1
            ),
            response.signers[1]
        )
        assertEquals(
            AccountResponse.Links(
                Link(
                    "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/effects{?cursor,limit,order}",
                    true
                ),
                Link(
                    "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/offers{?cursor,limit,order}",
                    true
                ),
                Link(
                    "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/operations{?cursor,limit,order}",
                    true
                ),
                Link("/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7", false),
                Link(
                    "/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/transactions{?cursor,limit,order}",
                    true
                )
            ),
            response.links
        )
    }

    var json = "{\n" +
            "  \"_links\": {\n" +
            "    \"effects\": {\n" +
            "      \"href\": \"/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/effects{?cursor,limit,order}\",\n" +
            "      \"templated\": true\n" +
            "    },\n" +
            "    \"offers\": {\n" +
            "      \"href\": \"/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/offers{?cursor,limit,order}\",\n" +
            "      \"templated\": true\n" +
            "    },\n" +
            "    \"operations\": {\n" +
            "      \"href\": \"/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/operations{?cursor,limit,order}\",\n" +
            "      \"templated\": true\n" +
            "    },\n" +
            "    \"self\": {\n" +
            "      \"href\": \"/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7\"\n" +
            "    },\n" +
            "    \"transactions\": {\n" +
            "      \"href\": \"/accounts/GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7/transactions{?cursor,limit,order}\",\n" +
            "      \"templated\": true\n" +
            "    }\n" +
            "  }," +
            "  \"id\": \"GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7\",\n" +
            "  \"paging_token\": \"1\",\n" +
            "  \"account_id\": \"GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7\",\n" +
            "  \"sequence\": 2319149195853854,\n" +
            "  \"subentry_count\": 0,\n" +
            "  \"inflation_destination\": \"GAGRSA6QNQJN2OQYCBNQGMFLO4QLZFNEHIFXOMTQVSUTWVTWT66TOFSC\",\n" +
            "  \"home_domain\": \"stellar.org\",\n" +
            "  \"thresholds\": {\n" +
            "    \"low_threshold\": 10,\n" +
            "    \"med_threshold\": 20,\n" +
            "    \"high_threshold\": 30\n" +
            "  },\n" +
            "  \"flags\": {\n" +
            "    \"auth_required\": false,\n" +
            "    \"auth_revocable\": true\n" +
            "  },\n" +
            "  \"balances\": [\n" +
            "    {\n" +
            "      \"balance\": \"1001.0000000\",\n" +
            "      \"limit\": \"12000.4775807\",\n" +
            "      \"asset_type\": \"credit_alphanum4\",\n" +
            "      \"asset_code\": \"ABC\",\n" +
            "      \"asset_issuer\": \"GCRA6COW27CY5MTKIA7POQ2326C5ABYCXODBN4TFF5VL4FMBRHOT3YHU\"\n" +
            "    }," +
            "    {\n" +
            "      \"asset_type\": \"native\",\n" +
            "      \"balance\": \"20.0000300\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"signers\": [\n" +
            "    {\n" +
            "      \"public_key\": \"GAAZI4TCR3TY5OJHCTJC2A4QSY6CJWJH5IAJTGKIN2ER7LBNVKOCCWN7\",\n" +
            "      \"weight\": 0\n" +
            "    },\n" +
            "    {\n" +
            "      \"public_key\": \"GCR2KBCIU6KQXSQY5F5GZYC4WLNHCHCKW4NEGXNEZRYWLTNZIRJJY7D2\",\n" +
            "      \"weight\": 1\n" +
            "    }\n" +
            "  ]\n" +
            "}"
}
