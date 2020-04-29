package org.kin.stellarfork.responses

import org.junit.Test
import org.kin.stellarfork.Asset
import org.kin.stellarfork.AssetTypeCreditAlphaNum
import org.kin.stellarfork.responses.GsonSingleton.instance
import kotlin.test.assertEquals

class AssetDeserializerTest {
    @Test
    fun testDeserializeNative() {
        val json = "{\"asset_type\": \"native\"}"
        val asset =
            instance!!.fromJson(
                json,
                Asset::class.java
            )
        assertEquals(asset.type, "native")
    }

    @Test
    fun testDeserializeCredit() {
        val json = "{\n" +
                "  \"asset_type\": \"credit_alphanum4\",\n" +
                "  \"asset_code\": \"CNY\",\n" +
                "  \"asset_issuer\": \"GAREELUB43IRHWEASCFBLKHURCGMHE5IF6XSE7EXDLACYHGRHM43RFOX\"\n" +
                "}"
        val asset =
            instance!!.fromJson(
                json,
                Asset::class.java
            )
        assertEquals(asset.type, "credit_alphanum4")
        val creditAsset = asset as AssetTypeCreditAlphaNum
        assertEquals(creditAsset.code, "CNY")
        assertEquals(
            creditAsset.issuer.accountId,
            "GAREELUB43IRHWEASCFBLKHURCGMHE5IF6XSE7EXDLACYHGRHM43RFOX"
        )
    }
}
