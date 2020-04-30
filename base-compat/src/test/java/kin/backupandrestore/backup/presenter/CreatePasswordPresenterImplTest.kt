package kin.backupandrestore.backup.presenter

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kin.backupandrestore.backup.view.BackupNavigator
import kin.backupandrestore.backup.view.CreatePasswordView
import kin.backupandrestore.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_VIEWED
import kin.backupandrestore.events.CallbackManager
import kin.sdk.KinAccount
import kin.sdk.exception.CryptoException
import org.junit.Before
import org.junit.Test

class CreatePasswordPresenterImplTest {

    private val callbackManager: CallbackManager = mock()
    private val kinAccount: KinAccount = mock()
    private val backupNavigator: BackupNavigator = mock()

    private val view: CreatePasswordView = mock()

    private val pass = "1234qwerQ!"
    private val otherPass = "something_else"
    private val accountKey = "some_account_key"

    private lateinit var presenter: CreatePasswordPresenterImpl

    @Before
    fun setUp() {
        presenter = CreatePasswordPresenterImpl(callbackManager, backupNavigator, kinAccount)
        presenter.onAttach(view)
    }

    @Test
    fun `send create password page view event on create`() {
        verify(callbackManager).sendBackupEvent(BACKUP_CREATE_PASSWORD_PAGE_VIEWED)
    }

    @Test
    fun `onBackClicked and navigator has been notified`() {
        presenter.onBackClicked()
        verify(backupNavigator).closeFlow()
    }

    @Test
    fun `enter password changed, password is valid but did not complete all other requirements`() {
        presenter.passwordCheck(pass, otherPass, false)
        verify(view).disableNextButton()
    }

    @Test
    fun `enter password changed, password is valid and completed all other requirements`() {
        presenter.apply {
            passwordCheck(pass, "", false)
            passwordCheck(pass, pass, true)
            iUnderstandChecked(true, pass, pass)
        }
        verify(view).enableNextButton()
    }

    @Test
    fun `enter password changed, password is not empty but not valid`() {
        presenter.passwordCheck(otherPass, pass, false)
        verify(view).setEnterPasswordIsCorrect(false)
        verify(view).disableNextButton()
    }

    @Test
    fun `enter password changed, password is empty reset fields`() {
        presenter.passwordCheck("", otherPass, false)
        verify(view).resetEnterPasswordField()
    }

    @Test
    fun `confirm password changed, password does not match`() {
        presenter.passwordCheck(otherPass, pass, true)
        verify(view).setConfirmPasswordIsCorrect(false)
    }

    @Test
    fun `confirm password changed, password matches`() {
        presenter.passwordCheck(pass, pass, true)
        verify(view).setConfirmPasswordIsCorrect(true)
    }

    @Test
    fun `i understand is checked and passwords are matches, next button should become enabled`() {
        presenter.apply {
            passwordCheck(pass, pass, false)
            passwordCheck(pass, pass, true)
            iUnderstandChecked(true, pass, pass)
        }
        verify(view).enableNextButton()
    }

    @Test
    fun `i understand is unchecked`() {
        presenter.apply {
            passwordCheck(pass, pass, false)
            passwordCheck(otherPass, pass, true)
            iUnderstandChecked(false, pass, otherPass)
        }
        verify(view, times(3)).disableNextButton()
    }

    @Test
    fun `next button clicked and export succeeded`() {
        whenever(kinAccount.export(pass)) doReturn (accountKey)
        presenter.nextButtonClicked(pass, pass)
        verify(backupNavigator).navigateToSaveAndSharePage(accountKey)
    }

    @Test
    fun `next button clicked and export failed`() {
        whenever(kinAccount.export(pass)) doThrow (CryptoException::class)
        presenter.nextButtonClicked(pass, pass)
        verify(view).showBackupFailed()
    }

    @Test
    fun `onRetryClicked and export succeed`() {
        whenever(kinAccount.export(pass)) doReturn (accountKey)
        presenter.onRetryClicked(pass)
        verify(backupNavigator).navigateToSaveAndSharePage(accountKey)
    }

    @Test
    fun `onRetryClicked and export failed`() {
        whenever(kinAccount.export(pass)) doThrow (CryptoException::class)
        presenter.onRetryClicked(pass)
        verify(view).showBackupFailed()
    }
}
