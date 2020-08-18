package org.kin.sdk.base.models

/**
 * @param appIndex - An assigned integer for this application. Make sure you are using the correct one for your app!
 * @param kinAccountId - This [KinAccount.Id] is for the account you wish to collect Kin from by default.
 * @property appName - The name of your App that can be used to display to a user in certain contexts.
 * @property appIconResourceId - The ResourceId of your app's icon.
 */
data class AppInfo(val appIndex: AppIdx, val kinAccountId: KinAccount.Id, val appName: String, val appIconResourceId: Int)
