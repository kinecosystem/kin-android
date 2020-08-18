package org.kin.sdk.base.network.services

import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.AppUserCreds

interface AppInfoProvider {
    val appInfo: AppInfo

    /**
     * This information is passed though in the headers of the request
     * send to Agora and forwarded on to the webhook
     *
     * Header params of the SubmitTransaction request
     *  - app-user-id
     *  - app-user-passkey
     *
     *  For more information regarding these parameters and webhook integration
     *  please consult: https://docs.kin.org/how-it-works#webhooks
     *
     */
    fun getPassthroughAppUserCredentials(): AppUserCreds
}
