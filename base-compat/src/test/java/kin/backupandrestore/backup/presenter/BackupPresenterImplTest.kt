package kin.backupandrestore.backup.presenter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kin.backupandrestore.backup.presenter.BackupPresenterImpl.KEY_ACCOUNT_KEY
import kin.backupandrestore.backup.presenter.BackupPresenterImpl.KEY_STEP
import kin.backupandrestore.backup.view.BackupNavigator.STEP_CREATE_PASSWORD
import kin.backupandrestore.backup.view.BackupNavigator.STEP_SAVE_AND_SHARE
import kin.backupandrestore.backup.view.BackupNavigator.STEP_START
import kin.backupandrestore.backup.view.BackupNavigator.STEP_WELL_DONE
import kin.backupandrestore.backup.view.BackupView
import kin.backupandrestore.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_BACK_TAPPED
import kin.backupandrestore.events.BackupEventCode.BACKUP_QR_PAGE_BACK_TAPPED
import kin.backupandrestore.events.BackupEventCode.BACKUP_WELCOME_PAGE_BACK_TAPPED
import kin.backupandrestore.events.CallbackManager
import kin.sdk.KinAccount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackupPresenterImplTest {

    private val callbackManager: CallbackManager = mock()
    private val kinAccount: KinAccount = mock()
    private val savedInstanceState: Bundle = mock()
    private val view: BackupView = mock()

    private lateinit var backupPresenter: BackupPresenter

    @Test
    fun `initial step saveInstanceState not contains KEY_STEP`() {
        createPresenter()
        verify(view).startBackupFlow()
    }

    @Test
    fun `initial step saveInstanceState is null`() {
        backupPresenter = BackupPresenterImpl(callbackManager, kinAccount, null)
        backupPresenter.onAttach(view)
        verify(view).startBackupFlow()
    }

    @Test
    fun `initial step STEP_CREATE_PASSWORD`() {
        whenever(savedInstanceState.getInt(KEY_STEP, STEP_START)) doReturn (STEP_CREATE_PASSWORD)
        createPresenter()
        verify(view).moveToCreatePasswordPage()
    }

    @Test
    fun `initial step STEP_SAVE_AND_SHARE`() {
        val accountKey = "some_fake_account_key"
        whenever(savedInstanceState.getInt(KEY_STEP, STEP_START)) doReturn (STEP_SAVE_AND_SHARE)
        whenever(savedInstanceState.getString(KEY_ACCOUNT_KEY)) doReturn (accountKey)
        createPresenter()
        verify(view).moveToSaveAndSharePage(accountKey)
    }

    @Test
    fun `initial step STEP_SAVE_AND_SHARE accountKey is null, close flow`() {
        whenever(savedInstanceState.getInt(KEY_STEP, STEP_START)) doReturn (STEP_SAVE_AND_SHARE)
        createPresenter()
        verify(view).showError()
        verify(view).close()
    }

    @Test
    fun `initial step STEP_WELL_DONE`() {
        whenever(savedInstanceState.getInt(KEY_STEP, STEP_START)) doReturn (STEP_WELL_DONE)
        createPresenter()
        verify(view).moveToWellDonePage()
    }

    @Test
    fun `navigate to create password page`() {
        createPresenter()
        backupPresenter.navigateToCreatePasswordPage()
        verify(view).moveToCreatePasswordPage()
    }

    @Test
    fun `navigate to save and share page`() {
        createPresenter()
        val accountKey = "some_fake_account_key"
        backupPresenter.navigateToSaveAndSharePage(accountKey)
        verify(view).moveToSaveAndSharePage(accountKey)
    }

    @Test
    fun `navigate to well done page`() {
        createPresenter()
        backupPresenter.navigateToWellDonePage()
        verify(view).moveToWellDonePage()
    }

    @Test
    fun `close flow`() {
        createPresenter()
        backupPresenter.closeFlow()
        verify(view).close()
    }

    @Test
    fun `onBackClicked send back events once and send cancel result, backup not succeeded`() {
        whenever(savedInstanceState.getInt(KEY_STEP, STEP_START)) doReturn (STEP_SAVE_AND_SHARE)
        createPresenter()
        backupPresenter.apply {
            onBackClicked()
            onBackClicked()
            onBackClicked()
        }


        inOrder(callbackManager, view).apply {
            verify(callbackManager).sendBackupEvent(BACKUP_QR_PAGE_BACK_TAPPED)
            verify(view).onBackButtonClicked()

            verify(callbackManager).sendBackupEvent(BACKUP_CREATE_PASSWORD_PAGE_BACK_TAPPED)
            verify(callbackManager).setCancelledResult()
            verify(view).onBackButtonClicked()

            verify(callbackManager).sendBackupEvent(BACKUP_WELCOME_PAGE_BACK_TAPPED)
            verify(view).onBackButtonClicked()
        }
    }

    @Test
    fun `onBackClicked when step is STEP_WELL_DONE close the flow`() {
        whenever(savedInstanceState.getInt(KEY_STEP, STEP_START)) doReturn (STEP_WELL_DONE)
        createPresenter()
        backupPresenter.onBackClicked()
        verify(view).close()
    }

//    @Test
//    fun `save instance state and keep state updated`() {
//        createPresenter()
//        val accountKeyA = "accountKey_A"
//        val instanceStateBundle = Bundle()
//        backupPresenter.onSaveInstanceState(instanceStateBundle)
//
//        instanceStateBundle.apply {
//            assertEquals(STEP_START, getInt(KEY_STEP))
//            assertNull(getString(KEY_ACCOUNT_KEY))
//        }
//
//        backupPresenter.apply {
//            navigateToCreatePasswordPage()
//            setAccountKey(accountKeyA)
//            onSaveInstanceState(instanceStateBundle)
//        }
//
//        instanceStateBundle.apply {
//            assertEquals(STEP_CREATE_PASSWORD, getInt(KEY_STEP))
//            assertEquals(accountKeyA, getString(KEY_ACCOUNT_KEY))
//        }
//    }

    private fun createPresenter() {
        backupPresenter = BackupPresenterImpl(callbackManager, kinAccount, savedInstanceState)
        backupPresenter.onAttach(view)
    }
}
