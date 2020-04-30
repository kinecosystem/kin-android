package kin.backupandrestore.restore.presenter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kin.backupandrestore.events.CallbackManager
import kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_DONE_TAPPED
import kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED
import kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_VIEWED
import kin.backupandrestore.restore.presenter.RestorePresenterImpl.KEY_ACCOUNT_KEY
import kin.backupandrestore.restore.view.RestoreEnterPasswordView
import kin.sdk.KinAccount
import kin.sdk.KinClient
import kin.sdk.exception.CorruptedDataException
import kin.sdk.exception.CryptoException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RestoreEnterPasswordPresenterImplTest {
    companion object {
        const val PASS = "pass"
        const val keyStoreData = "key store data"
    }

    private val callbackManager: CallbackManager = mock()
    private val kinAccount: KinAccount = mock()
    private val kinClient: KinClient = mock()
    private val view: RestoreEnterPasswordView = mock()
    private val parentPresenter: RestorePresenter = mock()

    private lateinit var presenter: RestoreEnterPasswordPresenterImpl

    @Before
    fun setUp() {
        createPresenter()
    }

    @Test
    fun `send event page viewed on create object`() {
        verify(callbackManager).sendRestoreEvent(RESTORE_PASSWORD_ENTRY_PAGE_VIEWED)
    }

    @Test
    fun `password changed, now its empty should disable done button`() {
        presenter.onPasswordChanged("")
        verify(view).disableDoneButton()
    }

    @Test
    fun `password changed, not empty should enable done button`() {
        presenter.onPasswordChanged(PASS)
        verify(view).enableDoneButton()
    }

    @Test
    fun `restore clicked, send event password done tapped`() {
        whenever(kinClient.importAccount(any(), any())).thenReturn(kinAccount)
        whenever(parentPresenter.kinClient) doReturn (kinClient)
        presenter.restoreClicked(PASS)
        verify(callbackManager).sendRestoreEvent(RESTORE_PASSWORD_DONE_TAPPED)
    }

    @Test
    fun `restore clicked import account succeed, navigate to complete page`() {
        whenever(kinClient.importAccount(any(), any())).thenReturn(kinAccount)
        whenever(parentPresenter.kinClient) doReturn (kinClient)
        presenter.restoreClicked(PASS)
        verify(parentPresenter).navigateToRestoreCompletedPage(kinAccount)
    }

    @Test
    fun `restore clicked, exception with error code CODE_RESTORE_INVALID_KEYSTORE_FORMAT, show invalid qr error`() {
        whenever(
            kinClient.importAccount(
                any(),
                any()
            )
        ).thenThrow(CorruptedDataException("some msg"))
        whenever(parentPresenter.kinClient) doReturn (kinClient)
        presenter.restoreClicked(PASS)
        verify(view).invalidQrError()
    }

    @Test
    fun `restore clicked, exception with decode error , show decode error`() {
        whenever(kinClient.importAccount(any(), any())).thenThrow(CryptoException::class.java)
        whenever(parentPresenter.kinClient) doReturn (kinClient)
        presenter.restoreClicked(PASS)
        verify(view).decodeError()
    }

//    @Test
//    fun `onSaveInstanceState save the the correct data`() {
//        val bundle = Bundle()
//        presenter.onSaveInstanceState(bundle)
//        assertEquals(keyStoreData, bundle.getString(KEY_ACCOUNT_KEY))
//    }

    @Test
    fun `back clicked send event and go to previous step`() {
        presenter.onBackClicked()
        verify(callbackManager).sendRestoreEvent(RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED)
        verify(parentPresenter).previousStep()
    }

    private fun createPresenter() {
        presenter = RestoreEnterPasswordPresenterImpl(callbackManager, keyStoreData)
        presenter.onAttach(view, parentPresenter)
    }
}
