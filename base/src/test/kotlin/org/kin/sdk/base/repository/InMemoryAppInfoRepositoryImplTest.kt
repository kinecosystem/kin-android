package org.kin.sdk.base.repository

import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.kin.sdk.base.models.AppIdx
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.test

class InMemoryAppInfoRepositoryImplTest {

    companion object {
        val testAppInfo = AppInfo(AppIdx.TEST_APP_IDX, KinAccount.Id(ByteArray(0)), "", 123)
    }

    lateinit var sut: AppInfoRepository

    @Before
    fun setUp() {
        sut = InMemoryAppInfoRepositoryImpl()
    }

    @Test
    fun addAppInfo_not_found() {
        sut.appInfoByAppIndex(AppIdx.TEST_APP_IDX).test {
            assertEquals(Optional.empty<AppInfo>(), value)
        }
    }

    @Test
    fun addAppInfo_found() {
        sut.addAppInfo(testAppInfo)

        sut.appInfoByAppIndex(AppIdx.TEST_APP_IDX).test {
            assertEquals(Optional.of(testAppInfo), value)
        }
    }
}
