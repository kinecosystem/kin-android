package org.kin.stellarfork.responses

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.kin.stellarfork.Asset
import org.kin.stellarfork.AssetTypeNative
import org.kin.stellarfork.KeyPair
import java.lang.reflect.Type

internal class AssetDeserializer :
    JsonDeserializer<Asset> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Asset {
        val type = json.asJsonObject["asset_type"].asString
        return if (type == "native") {
            AssetTypeNative
        } else {
            val code = json.asJsonObject["asset_code"].asString
            val issuer = json.asJsonObject["asset_issuer"].asString
            Asset.createNonNativeAsset(
                code,
                KeyPair.fromAccountId(issuer)
            )
        }
    }
}
