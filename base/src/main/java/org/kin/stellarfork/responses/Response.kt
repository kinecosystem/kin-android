package org.kin.stellarfork.responses

abstract class Response {
    /**
     * Returns X-RateLimit-Limit header from the response.
     * This number represents the he maximum number of requests that the current client can
     * make in one hour.
     *
     * @see [Rate Limiting](https://www.stellar.org/developers/horizon/learn/rate-limiting.html)
     */
    var rateLimitLimit = 0
        protected set
    /**
     * Returns X-RateLimit-Remaining header from the response.
     * The number of remaining requests for the current window.
     *
     * @see [Rate Limiting](https://www.stellar.org/developers/horizon/learn/rate-limiting.html)
     */
    var rateLimitRemaining = 0
        protected set
    /**
     * Returns X-RateLimit-Reset header from the response. Seconds until a new window starts.
     *
     * @see [Rate Limiting](https://www.stellar.org/developers/horizon/learn/rate-limiting.html)
     */
    var rateLimitReset = 0
        protected set

    fun setHeaders(
        limit: String?,
        remaining: String?,
        reset: String?
    ) {
        try {
            rateLimitLimit = safeParse(limit)
            rateLimitRemaining = safeParse(remaining)
            rateLimitReset = safeParse(reset)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun safeParse(number: String?): Int {
        return if (number == null || number.isEmpty()) 0 else number.toInt()
    }
}
