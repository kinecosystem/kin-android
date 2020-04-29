package org.kin.stellarfork

import org.kin.stellarfork.xdr.AssetType

/**
 * Represents Stellar native asset - [lumens (XLM)](https://www.stellar.org/developers/learn/concepts/assets.html)
 *
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
object AssetTypeNative : Asset() {
    override val type: String = "native"

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int = 0

    override fun toXdr(): org.kin.stellarfork.xdr.Asset =
        org.kin.stellarfork.xdr.Asset().apply {
            discriminant = AssetType.ASSET_TYPE_NATIVE
        }
}
