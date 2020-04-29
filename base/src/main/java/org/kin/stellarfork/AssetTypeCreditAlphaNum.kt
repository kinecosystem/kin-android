package org.kin.stellarfork

/**
 * Base class for AssetTypeCreditAlphaNum4 and AssetTypeCreditAlphaNum12 subclasses.
 *
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
abstract class AssetTypeCreditAlphaNum(
    /**
     * Returns asset code
     */
    val code: String,
    /**
     * Returns asset issuer
     */
    val issuer: KeyPair
) : Asset() {
    override fun hashCode(): Int {
        return arrayOf<Any>(
            code,
            issuer.accountId
        ).contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this.javaClass != other?.javaClass) {
            return false
        }
        val o = other as AssetTypeCreditAlphaNum?
        return code == o!!.code && issuer.accountId == o.issuer.accountId
    }
}
