/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kin.stellarfork.codec

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * Thrown when there is a failure condition during the decoding process. This exception is thrown when a [Decoder]
 * encounters a decoding specific exception such as invalid data, or characters outside of the expected range.
 *
 * @author Apache Software Foundation
 * @version $Id$
 */
class DecoderException : Exception {
    /**
     * Constructs a new exception with `null` as its detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to [.initCause].
     *
     * @since 1.4
     */
    constructor() : super() {}

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to [.initCause].
     *
     * @param message The detail message which is saved for later retrieval by the [.getMessage] method.
     */
    constructor(message: String?) : super(message) {}

    /**
     * Constructsa new exception with the specified detail message and cause.
     *
     *
     *
     * Note that the detail message associated with `cause` is not automatically incorporated into this
     * exception's detail message.
     *
     *
     * @param message The detail message which is saved for later retrieval by the [.getMessage] method.
     * @param cause   The cause which is saved for later retrieval by the [.getCause] method. A `null`
     * value is permitted, and indicates that the cause is nonexistent or unknown.
     * @since 1.4
     */
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}

    /**
     * Constructs a new exception with the specified cause and a detail message of `(cause==null ?
     * null : cause.toString())` (which typically contains the class and detail message of `cause`).
     * This constructor is useful for exceptions that are little more than wrappers for other throwables.
     *
     * @param cause The cause which is saved for later retrieval by the [.getCause] method. A `null`
     * value is permitted, and indicates that the cause is nonexistent or unknown.
     * @since 1.4
     */
    constructor(cause: Throwable?) : super(cause) {}

    companion object {
        /**
         * Declares the Serial Version Uid.
         *
         * @see [Always Declare Serial Version Uid](http://c2.com/cgi/wiki?AlwaysDeclareSerialVersionUid)
         */
        private const val serialVersionUID = 1L
    }
}
