package org.kin.stellarfork

/**
 * Indicates that the object that has to be signed has not enough signatures.
 */
class NotEnoughSignaturesException : RuntimeException {
    constructor() : super() {}
    constructor(message: String?) : super(message) {}
}
