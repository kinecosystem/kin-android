package org.kin.sdk.base.network.api.horizon

import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi.WhitelistTransactionResponse
import org.kin.sdk.base.network.api.KinTransactionWhitelistingApi.WhitelistTransactionResponse.Result

class DefaultHorizonKinTransactionWhitelistingApi :
    KinTransactionWhitelistingApi {
    override val isWhitelistingAvailable: Boolean = false

    override fun whitelistTransaction(
        request: KinTransactionWhitelistingApi.WhitelistTransactionRequest,
        onCompleted: (WhitelistTransactionResponse) -> Unit
    ) {
        /**
         * Developers are expected to call their back-end's to whitelist
         * a transaction. We just return the original transaction since this
         * implementation does not support whitelisting.
         */

        onCompleted(
            WhitelistTransactionResponse(
                Result.WhitelistingDisabled,
                request.base64EncodedTransactionEnvelopeBytes
            )
        )
    }
}
