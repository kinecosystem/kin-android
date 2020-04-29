package org.kin.stellarfork.responses

class HttpResponseException(val statusCode: Int, s: String?, val body: String? = null) : ClientProtocolException(s)


class ServerGoneException(val statusCode: Int, message: String?) : ClientProtocolException(message)

