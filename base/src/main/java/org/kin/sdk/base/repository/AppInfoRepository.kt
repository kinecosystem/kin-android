package org.kin.sdk.base.repository

import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise

interface AppInfoRepository {
    fun addAppInfo(appInfo: AppInfo)
    fun appInfoByAppIndex(appIndex: AppIdx): Promise<Optional<AppInfo>>
}

class InMemoryAppInfoRepositoryImpl(
    private val storage: MutableMap<AppIdx, AppInfo> = mutableMapOf<AppIdx, AppInfo>()
) : AppInfoRepository {

    override fun addAppInfo(appInfo: AppInfo) {
        storage[appInfo.appIndex] = appInfo
    }

    override fun appInfoByAppIndex(appIndex: AppIdx): Promise<Optional<AppInfo>> =
        Promise.of(Optional.ofNullable(storage[appIndex]))
}
