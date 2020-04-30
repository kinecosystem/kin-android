package kin.backupandrestore.restore.presenter

import android.content.Intent
import android.os.Bundle
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kin.backupandrestore.events.CallbackManager
import kin.backupandrestore.restore.presenter.RestorePresenterImpl.KEY_ACCOUNT_KEY
import kin.backupandrestore.restore.presenter.RestorePresenterImpl.KEY_PUBLIC_ADDRESS
import kin.backupandrestore.restore.presenter.RestorePresenterImpl.KEY_RESTORE_STEP
import kin.backupandrestore.restore.presenter.RestorePresenterImpl.STEP_ENTER_PASSWORD
import kin.backupandrestore.restore.presenter.RestorePresenterImpl.STEP_FINISH
import kin.backupandrestore.restore.presenter.RestorePresenterImpl.STEP_RESTORE_COMPLETED
import kin.backupandrestore.restore.presenter.RestorePresenterImpl.STEP_UPLOAD
import kin.backupandrestore.restore.view.RestoreView
import kin.sdk.KinAccount
import kin.sdk.KinClient
import org.junit.Assert.assertEquals
import org.junit.Test

class RestorePresenterImplTest {

    private val callbackManager: CallbackManager = mock()
    private val kinAccount: KinAccount = mock()
    private val kinClient: KinClient = mock()
    private val savedInstanceState: Bundle = mock()
    private val view: RestoreView = mock()

    private val accountKey = "some_fake_account_key"
    private val publicAddress = " some_fake_public_address"
    private lateinit var presenter: RestorePresenterImpl

    @Test
    fun `initial step is null, default navigate to upload qr page`() {
        createPresenter()
        verify(view).navigateToUpload()
    }

    @Test
    fun `initial step is STEP_ENTER_PASSWORD and accountKey is set, navigate to enter password page`() {
        whenever(
            savedInstanceState.getInt(
                KEY_RESTORE_STEP,
                STEP_UPLOAD
            )
        ) doReturn (STEP_ENTER_PASSWORD)
        whenever(savedInstanceState.getString(KEY_ACCOUNT_KEY)) doReturn (accountKey)
        createPresenter()
        verify(view).navigateToEnterPassword(accountKey)
    }

    @Test
    fun `initial step is STEP_ENTER_PASSWORD and accountKey is null, show error`() {
        whenever(
            savedInstanceState.getInt(
                KEY_RESTORE_STEP,
                STEP_UPLOAD
            )
        ) doReturn (STEP_ENTER_PASSWORD)
        whenever(savedInstanceState.getString(KEY_ACCOUNT_KEY)).thenReturn(null)
        createPresenter()
        verify(view).showError()
    }

    @Test
    fun `initial step is STEP_RESTORE_COMPLETED and kinAccount is set, navigate to restore completed page`() {
        whenever(
            savedInstanceState.getInt(
                KEY_RESTORE_STEP,
                STEP_UPLOAD
            )
        ) doReturn (STEP_RESTORE_COMPLETED)
        mockKinAccountAtConstructor()
        createPresenter()
        verify(view).closeKeyboard()
        verify(view).navigateToRestoreCompleted()
    }

    @Test
    fun `initial step is STEP_RESTORE_COMPLETED and kinAccount is not set, show error`() {
        whenever(
            savedInstanceState.getInt(
                KEY_RESTORE_STEP,
                STEP_UPLOAD
            )
        ) doReturn (STEP_RESTORE_COMPLETED)
        createPresenter()
        verify(view).closeKeyboard()
        verify(view).showError()
    }

    @Test
    fun `initial step is STEP_FINISH and kinAccount is set, set result success`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_FINISH)
        mockKinAccountAtConstructor()
        createPresenter()
        verify(callbackManager).sendRestoreSuccessResult(publicAddress)
        verify(view).close()
    }

    @Test
    fun `initial step is STEP_FINISH and kinAccount is not set, show error`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_FINISH)
        createPresenter()
        verify(view).showError()
    }

    @Test
    fun `navigate to enter password page`() {
        createPresenter()
        presenter.navigateToEnterPasswordPage(accountKey)
        verify(view).navigateToEnterPassword(accountKey)
    }

    @Test
    fun `navigate to restore completed page`() {
        createPresenter()
        presenter.navigateToRestoreCompletedPage(kinAccount)
        verify(view).navigateToRestoreCompleted()
    }

    @Test
    fun `close flow`() {
        whenever(kinAccount.publicAddress) doReturn (publicAddress)
        createPresenter()
        presenter.navigateToRestoreCompletedPage(kinAccount)
        presenter.closeFlow()
        verify(callbackManager).sendRestoreSuccessResult(publicAddress)
        verify(view).close()
    }

    @Test
    fun `close flow called not in the correct state`() {
        whenever(kinAccount.publicAddress).doReturn(publicAddress)
        createPresenter()
        presenter.closeFlow()
        verify(view).showError()
        verify(view).close()
    }

    @Test
    fun `onBackClicked called to previous step to the correct step`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_FINISH)
        createPresenter()
        presenter.onBackClicked()
        verify(view).navigateBack()
    }

    @Test
    fun `previous step cancel on back from upload page`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_FINISH)
        createPresenter()
        presenter.apply {
            previousStep()
            previousStep()
            previousStep()
            previousStep()
        }

        inOrder(callbackManager, view).apply {
            verify(view, times(3)).navigateBack()
            verify(view).closeKeyboard()
            verify(view).close()
        }
    }

    @Test
    fun `onActivityResult passes the correct data`() {
        createPresenter()
        val data = Intent("some_action")
        presenter.onActivityResult(1, 2, data)
        verify(callbackManager).onActivityResult(1, 2, data)
    }

//    @Test
//    fun `onSaveInstanceState is updated correctly`() {
//        createPresenter()
//        val outState = Bundle()
//        presenter.apply {
//            navigateToEnterPasswordPage(accountKey)
//            onSaveInstanceState(outState)
//        }
//
//        outState.apply {
//            assertEquals(STEP_ENTER_PASSWORD, getInt(KEY_RESTORE_STEP))
//            assertEquals(accountKey, getString(KEY_ACCOUNT_KEY))
//            assertEquals(null, getString(publicAddress))
//        }
//
//        presenter.apply {
//            navigateToRestoreCompletedPage(kinAccount)
//            whenever(kinAccount.publicAddress) doReturn (publicAddress)
//            onSaveInstanceState(outState)
//        }
//
//        outState.apply {
//            assertEquals(STEP_RESTORE_COMPLETED, getInt(KEY_RESTORE_STEP))
//            assertEquals(accountKey, getString(KEY_ACCOUNT_KEY))
//            assertEquals(publicAddress, getString(KEY_PUBLIC_ADDRESS))
//        }
//    }

    // If you want to used it correctly then it should be called before 'createPresenter' method.
    private fun mockKinAccountAtConstructor() {
        whenever(savedInstanceState.getString(KEY_PUBLIC_ADDRESS)) doReturn (publicAddress)
        whenever(kinClient.accountCount) doReturn (1)
        whenever(kinClient.getAccount(0)) doReturn (kinAccount)
        whenever(kinAccount.publicAddress) doReturn (publicAddress)
    }

    private fun createPresenter() {
        presenter = RestorePresenterImpl(callbackManager, kinClient, savedInstanceState)
        presenter.onAttach(view)
    }
}
