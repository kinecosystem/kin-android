package org.kin.stellarfork.responses

import java.io.IOException

open class ClientProtocolException : IOException {
    constructor() : super() {}
    constructor(s: String?) : super(s) {}
    constructor(cause: Throwable?) {
        initCause(cause)
    }

    constructor(message: String?, cause: Throwable?) : super(message) {
        initCause(cause)
    }
}
