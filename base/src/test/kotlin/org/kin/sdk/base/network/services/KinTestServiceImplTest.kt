package org.kin.sdk.base.network.services

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.horizon.KinFriendBotApi
import org.kin.sdk.base.tools.KinLoggerFactoryImpl
import org.kin.sdk.base.tools.NetworkOperationsHandlerImpl
import org.kin.sdk.base.tools.TestUtils
import org.kin.sdk.base.tools.test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KinTestServiceImplTest {

    companion object {
        val account = TestUtils.newKinAccount()
    }

    lateinit var sut: KinTestService

    lateinit var mockFriendBotApi: KinFriendBotApi

    @Before
    fun setUp() {
        mockFriendBotApi = mock {
            on {
                fundAccount(
                    eq(KinAccountCreationApi.CreateAccountRequest(account.id)),
                    any()
                )
            } doAnswer {
                (it.getArgument(1) as (KinAccountCreationApi.CreateAccountResponse) -> Unit).invoke(
                    KinAccountCreationApi.CreateAccountResponse(
                        KinAccountCreationApi.CreateAccountResponse.Result.Ok,
                        account
                    )
                )
            }
        }

        sut = KinTestServiceImpl(
            NetworkOperationsHandlerImpl(logger = KinLoggerFactoryImpl(true)),
            mockFriendBotApi
        )
    }

    @Test
    fun fundAccount_success() {
        sut.fundAccount(account.id).test {
            assertEquals(account, value)
        }
    }

    @Test
    fun fundAccount_illegalResponse() {
        mockFriendBotApi = mock {
            on {
                fundAccount(
                    eq(KinAccountCreationApi.CreateAccountRequest(account.id)),
                    any()
                )
            } doAnswer {
                (it.getArgument(1) as (KinAccountCreationApi.CreateAccountResponse) -> Unit).invoke(
                    KinAccountCreationApi.CreateAccountResponse(
                        KinAccountCreationApi.CreateAccountResponse.Result.Ok,
                        null
                    )
                )
            }
        }

        sut = KinTestServiceImpl(
            NetworkOperationsHandlerImpl(logger = KinLoggerFactoryImpl(true)),
            mockFriendBotApi
        )

        sut.fundAccount(account.id).test {
            assertTrue(error is KinService.FatalError.IllegalResponse)
        }
    }

    @Test
    fun fundAccount_exists() {
        sut.fundAccount(account.id).test {
            assertEquals(account, value)
        }
    }

    @Test
    fun fundAccount_transientFailure() {

        doAnswer {
            (it.getArgument(1) as (KinAccountCreationApi.CreateAccountResponse) -> Unit).invoke(
                KinAccountCreationApi.CreateAccountResponse(
                    KinAccountCreationApi.CreateAccountResponse.Result.TransientFailure(Exception())
                )
            )
        }.whenever(mockFriendBotApi)
            .fundAccount(eq(KinAccountCreationApi.CreateAccountRequest(account.id)), any())

        sut.fundAccount(account.id).test {
            assertNull(value)
            assertTrue { error!! is KinService.FatalError.TransientFailure }
        }
    }

    @Test
    fun fundAccount_unexpectedFailure() {

        doAnswer {
            (it.getArgument(1) as (KinAccountCreationApi.CreateAccountResponse) -> Unit).invoke(
                KinAccountCreationApi.CreateAccountResponse(
                    KinAccountCreationApi.CreateAccountResponse.Result.UndefinedError(Exception())
                )
            )
        }.whenever(mockFriendBotApi)
            .fundAccount(eq(KinAccountCreationApi.CreateAccountRequest(account.id)), any())

        sut.fundAccount(account.id).test {
            assertNull(value)
            assertTrue { error!! is KinService.FatalError.UnexpectedServiceError }
        }
    }

    @Test
    fun fundAccount_permanentlyUnavailable() {

        doAnswer {
            (it.getArgument(1) as (KinAccountCreationApi.CreateAccountResponse) -> Unit).invoke(
                KinAccountCreationApi.CreateAccountResponse(
                    KinAccountCreationApi.CreateAccountResponse.Result.Unavailable
                )
            )
        }.whenever(mockFriendBotApi)
            .fundAccount(eq(KinAccountCreationApi.CreateAccountRequest(account.id)), any())

        sut.fundAccount(account.id).test {
            assertNull(value)
            assertTrue { error!! is KinService.FatalError.PermanentlyUnavailable }
        }
    }

    @Test
    fun fundAccount_SDKUpgradeRequired() {

        doAnswer {
            (it.getArgument(1) as (KinAccountCreationApi.CreateAccountResponse) -> Unit).invoke(
                KinAccountCreationApi.CreateAccountResponse(
                    KinAccountCreationApi.CreateAccountResponse.Result.UpgradeRequiredError
                )
            )
        }.whenever(mockFriendBotApi)
            .fundAccount(eq(KinAccountCreationApi.CreateAccountRequest(account.id)), any())

        sut.fundAccount(account.id).test {
            assertNull(value)
            assertTrue { error!! is KinService.FatalError.SDKUpgradeRequired }
        }
    }
}
