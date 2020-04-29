package org.kin.stellarfork

/**
 * Indicates that asset code is not valid for a specified asset class
 *
 * @see AssetTypeCreditAlphaNum4
 *
 * @see AssetTypeCreditAlphaNum12
 */
class AssetCodeLengthInvalidException : RuntimeException {
    constructor() : super() {}
    constructor(message: String?) : super(message) {}
}
