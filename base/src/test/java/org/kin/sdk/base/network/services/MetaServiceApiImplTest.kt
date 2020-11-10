package org.kin.sdk.base.network.services

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.network.api.KinTransactionApiV4.GetMiniumumKinVersionRequest
import org.kin.sdk.base.network.api.KinTransactionApiV4.GetMiniumumKinVersionResponse
import org.kin.sdk.base.storage.Storage
import org.kin.sdk.base.tools.KinLoggerFactoryImpl
import org.kin.sdk.base.tools.NetworkOperationsHandler
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.test
import kotlin.test.assertEquals


class MetaServiceApiImplTest {

    lateinit var opHandler: NetworkOperationsHandler
    lateinit var api: KinTransactionApiV4
    lateinit var storage: Storage

    lateinit var sut: MetaServiceApiImpl

    @Before
    fun setUp() {

        opHandler = NetworkOperationsHandlerImpl(logger = KinLoggerFactoryImpl(true))
        api = mock {
            on {
                getMinKinVersion(eq(GetMiniumumKinVersionRequest), any())
            } doAnswer {
                val callback = (it.getArgument(1) as ((GetMiniumumKinVersionResponse) -> Unit))
                val response =
                    GetMiniumumKinVersionResponse(GetMiniumumKinVersionResponse.Result.Ok, 4)
                callback(response)
            }
        }
        storage = mock {
            on { getMinApiVersion() } doReturn Promise.of(Optional.empty())
            on { setMinApiVersion(eq(3)) } doReturn Promise.of(3)
            on { setMinApiVersion(eq(4)) } doReturn Promise.of(4)
        }

        sut = MetaServiceApiImpl(
            3,
            opHandler,
            api,
            storage
        )
    }

    @Test
    fun testInit_storageIsEmpty() {
        assertEquals(3, sut.configuredMinApi)
        sut.postInit().test {}
        assertEquals(3, sut.configuredMinApi)
        verify(storage).getMinApiVersion()
    }

    @Test
    fun testInit_storageIsSame() {
        assertEquals(3, sut.configuredMinApi)
        sut.postInit().test {}
        assertEquals(3, sut.configuredMinApi)
        verify(storage).getMinApiVersion()
    }

    @Test
    fun testInit_storageIsNewer() {
        doReturn(Promise.of(Optional.of(4))).whenever(storage).getMinApiVersion()

        assertEquals(3, sut.configuredMinApi)
        sut.postInit().test {}
        assertEquals(4, sut.configuredMinApi)
        verify(storage).getMinApiVersion()
    }

    @Test
    fun getMinApiVersion_upgradeSuccess() {

        sut.getMinApiVersion().test {
            assertEquals(4, value)
            verify(storage).setMinApiVersion(eq(4))
            assertEquals(4, sut.configuredMinApi)
        }
    }

    @Test
    fun getMinApiVersion_onError1() {

        doAnswer {
            val callback = (it.getArgument(1) as ((GetMiniumumKinVersionResponse) -> Unit))
            val response = GetMiniumumKinVersionResponse(
                GetMiniumumKinVersionResponse.Result.TransientFailure(Exception()), -1
            )
            callback(response)
        }.whenever(api).getMinKinVersion(eq(GetMiniumumKinVersionRequest), any())

        sut.getMinApiVersion().test {
            assertEquals(3, value)
            verifyNoMoreInteractions(storage)
        }
    }

    @Test
    fun getMinApiVersion_onError2() {

        doAnswer {
            val callback = (it.getArgument(1) as ((GetMiniumumKinVersionResponse) -> Unit))
            val response = GetMiniumumKinVersionResponse(
                GetMiniumumKinVersionResponse.Result.UndefinedError(Exception()), -1
            )
            callback(response)
        }.whenever(api).getMinKinVersion(eq(GetMiniumumKinVersionRequest), any())

        sut.getMinApiVersion().test {
            assertEquals(3, value)
            verifyNoMoreInteractions(storage)
        }
    }
}
