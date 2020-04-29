package org.kin.stellarfork.requests

/**
 * Exception thrown when too many requests were sent to the Horizon server.
 *
 * @see [Rate Limiting](https://www.stellar.org/developers/horizon/learn/rate-limiting.html)
 */
class TooManyRequestsException(
    /**
     * Returns number of seconds a client should wait before sending requests again.
     */
    val retryAfter: Int
) : RuntimeException("The rate limit for the requesting IP address is over its alloted limit.")
