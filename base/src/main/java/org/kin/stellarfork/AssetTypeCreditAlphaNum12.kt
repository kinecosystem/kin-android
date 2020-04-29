package org.kin.stellarfork

import org.kin.stellarfork.Util.paddedByteArray
import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.Asset
import org.kin.stellarfork.xdr.Asset.AssetAlphaNum12
import org.kin.stellarfork.xdr.AssetType

/**
 * Represents all assets with codes 5-12 characters long.
 *
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
class AssetTypeCreditAlphaNum12(
    code: String,
    issuer: KeyPair?
) : AssetTypeCreditAlphaNum(code, issuer!!) {
    /**
     * Class constructor
     *
     * @param code   Asset code
     * @param issuer Asset issuer
     */
    init {
        if (code.length < 5 || code.length > 12) {
            throw AssetCodeLengthInvalidException()
        }
    }

    override val type: String
        get() = "credit_alphanum12"

    override fun toXdr(): Asset {
        val xdr = Asset()
        xdr.discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM12
        val credit = AssetAlphaNum12()
        credit.assetCode = paddedByteArray(code, 12)
        val accountID = AccountID()
        accountID.accountID = issuer.xdrPublicKey
        credit.issuer = accountID
        xdr.alphaNum12 = credit
        return xdr
    }
}
