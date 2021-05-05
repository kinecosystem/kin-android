package kin.backupandrestore.restore.presenter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kin.backupandrestore.events.CallbackManager
import kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_DONE_TAPPED
import kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED
import kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_VIEWED
import kin.backupandrestore.restore.view.RestoreEnterPasswordView
import kin.sdk.exception.CorruptedDataException
import kin.sdk.exception.CryptoException
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.tools.BackupRestore
import org.kin.stellarfork.KeyPair

class RestoreEnterPasswordPresenterImplTest {
    companion object {
        const val PASS = "pass"
        const val keyStoreData = "key store data"
    }

    private val callbackManager: CallbackManager = mock()
    private val kinAccount: KeyPair  = KeyPair.random()
    private val view: RestoreEnterPasswordView = mock()
    private val parentPresenter: RestorePresenter = mock()
    private val backupRestore: BackupRestore = mock()

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
        whenever(backupRestore.importWallet(any(), any())).thenReturn(kinAccount)
//        whenever(parentPresenter.kinClient) doReturn (kinClient)
        presenter.restoreClicked(PASS)
        verify(callbackManager).sendRestoreEvent(RESTORE_PASSWORD_DONE_TAPPED)
    }

    @Test
    fun `restore clicked import account succeed, navigate to complete page`() {
        whenever(backupRestore.importWallet(any(), any())).thenReturn(kinAccount)
//        whenever(parentPresenter.kinClient) doReturn (kinClient)
        presenter.restoreClicked(PASS)
        verify(parentPresenter).navigateToRestoreCompletedPage(kinAccount)
    }

    @Test
    fun `restore clicked, exception with error code CODE_RESTORE_INVALID_KEYSTORE_FORMAT, show invalid qr error`() {
        whenever(
            backupRestore.importWallet(
                any(),
                any()
            )
        ).thenThrow(org.kin.sdk.base.tools.CorruptedDataException("some msg"))
//        whenever(parentPresenter.kinClient) doReturn (kinClient)
        presenter.restoreClicked(PASS)
        verify(view).invalidQrError()
    }

    @Test
    fun `restore clicked, exception with decode error , show decode error`() {
        whenever(backupRestore.importWallet(any(), any())).thenThrow(org.kin.sdk.base.tools.CryptoException::class.java)
//        whenever(parentPresenter.kinClient) doReturn (kinClient)
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
        presenter = RestoreEnterPasswordPresenterImpl(callbackManager, keyStoreData, backupRestore)
        presenter.onAttach(view, parentPresenter)
    }
}
