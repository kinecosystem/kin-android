package org.kin.stellarfork

import org.kin.stellarfork.Util.paddedByteArray
import org.kin.stellarfork.xdr.AccountID
import org.kin.stellarfork.xdr.Asset
import org.kin.stellarfork.xdr.Asset.AssetAlphaNum4
import org.kin.stellarfork.xdr.AssetType

/**
 * Represents all assets with codes 1-4 characters long.
 *
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
class AssetTypeCreditAlphaNum4(
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
        if (code.isEmpty() || code.length > 4) {
            throw AssetCodeLengthInvalidException()
        }
    }

    override val type: String
        get() = "credit_alphanum4"

    override fun toXdr(): Asset {
        val xdr = Asset()
        xdr.discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM4
        val credit = AssetAlphaNum4()
        credit.assetCode = paddedByteArray(code, 4)
        val accountID = AccountID()
        accountID.accountID = issuer.xdrPublicKey
        credit.issuer = accountID
        xdr.alphaNum4 = credit
        return xdr
    }
}
