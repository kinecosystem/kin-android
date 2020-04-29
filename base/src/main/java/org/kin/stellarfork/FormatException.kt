package org.kin.stellarfork

/**
 * Indicates that there was a problem decoding strkey encoded string.
 *
 * @see KeyPair
 */
class FormatException : RuntimeException {
    constructor() : super() {}
    constructor(message: String?) : super(message) {}
}
