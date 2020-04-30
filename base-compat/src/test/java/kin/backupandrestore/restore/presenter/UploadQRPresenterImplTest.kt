package kin.backupandrestore.restore.presenter

import android.net.Uri
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import kin.backupandrestore.events.CallbackManager
import kin.backupandrestore.events.RestoreEventCode.RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED
import kin.backupandrestore.events.RestoreEventCode.RESTORE_ARE_YOUR_SURE_OK_TAPPED
import kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_BACK_TAPPED
import kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED
import kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED
import kin.backupandrestore.qr.QRBarcodeGenerator
import kin.backupandrestore.restore.presenter.FileSharingHelper.REQUEST_RESULT_CANCELED
import kin.backupandrestore.restore.presenter.FileSharingHelper.REQUEST_RESULT_FAILED
import kin.backupandrestore.restore.presenter.FileSharingHelper.REQUEST_RESULT_OK
import kin.backupandrestore.restore.view.UploadQRView
import org.junit.Before
import org.junit.Test


class UploadQRPresenterImplTest {

    private val callbackManager: CallbackManager = mock()
    private val fileSharingHelper: FileSharingHelper = mock()
    private val qrBarcodeGenerator: QRBarcodeGenerator = mock()
    private val parentPresenter: RestorePresenter = mock()

    private val view: UploadQRView = mock()

    private lateinit var presenter: UploadQRPresenterImpl

    @Before
    fun setUp() {
        presenter = UploadQRPresenterImpl(callbackManager, fileSharingHelper, qrBarcodeGenerator)
        presenter.onAttach(view, parentPresenter)
    }

    @Test
    fun `send event on attach upload qr page viewed`() {
        verify(callbackManager).sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED)
    }

    @Test
    fun `uploadClicked, show consent dialog and send event`() {
        presenter.uploadClicked()
        verify(view).showConsentDialog()
        verify(callbackManager).sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED)
    }

    @Test
    fun `onOkPressed request load image and send event`() {
        val title = "some title"
        presenter.onOkPressed(title)
        verify(fileSharingHelper).requestImageFile(title)
        verify(callbackManager).sendRestoreEvent(RESTORE_ARE_YOUR_SURE_OK_TAPPED)
    }

    @Test
    fun `onCancelPressed send event`() {
        presenter.onCancelPressed()
        verify(callbackManager).sendRestoreEvent(RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED)
    }

    @Test
    fun `onActivityResult result cancel, do nothing`() {
        val req = 1
        val res = 1
        val data = null
        verify(callbackManager).sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED)
        whenever(fileSharingHelper.extractUriFromActivityResult(req, res, data)).thenReturn(
            FileSharingHelper.RequestFileResult(REQUEST_RESULT_CANCELED, null)
        )
        presenter.onActivityResult(req, res, data)
        verify(fileSharingHelper).extractUriFromActivityResult(req, res, data)
        verifyNoMoreInteractions(view, callbackManager, fileSharingHelper, qrBarcodeGenerator)
    }

    @Test
    fun `onActivityResult result failed, show error dialog`() {
        val req = 1
        val res = 1
        val data = null
        verify(callbackManager).sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED)
        whenever(fileSharingHelper.extractUriFromActivityResult(req, res, data)).thenReturn(
            FileSharingHelper.RequestFileResult(REQUEST_RESULT_FAILED, null)
        )
        presenter.onActivityResult(req, res, data)

        verify(fileSharingHelper).extractUriFromActivityResult(req, res, data)
        verify(view).showErrorLoadingFileDialog()
    }

    @Test
    fun `onActivityResult result ok, load encrypted keystore`() {
        val req = 1
        val res = 1
        val data = null
        val accountKey = "some_account_key"
        val result: FileSharingHelper.RequestFileResult = mock()
        val uri: Uri = mock()

        verify(callbackManager).sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED)
        whenever(fileSharingHelper.extractUriFromActivityResult(req, res, data)).thenReturn(result)
        whenever(result.result).thenReturn(REQUEST_RESULT_OK)
        whenever(result.fileUri).thenReturn(uri)
        whenever(qrBarcodeGenerator.decodeQR(uri)).thenReturn(accountKey)
        presenter.onActivityResult(req, res, data)
        verify(fileSharingHelper).extractUriFromActivityResult(req, res, data)
        verify(qrBarcodeGenerator).decodeQR(uri)
        verify(parentPresenter).navigateToEnterPasswordPage(accountKey)
        verify(view, never()).showErrorLoadingFileDialog()
        verify(view, never()).showErrorDecodingQRDialog()
    }

    @Test
    fun `onActivityResult result ok, load encrypted throw QRFileHandlingException, show error loading dialog`() {
        val req = 1
        val res = 1
        val data = null
        val accountKey = "some_account_key"
        val result: FileSharingHelper.RequestFileResult = mock()
        val uri: Uri = mock()

        verify(callbackManager).sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED)
        whenever(fileSharingHelper.extractUriFromActivityResult(req, res, data)).thenReturn(result)
        whenever(result.result).thenReturn(REQUEST_RESULT_OK)
        whenever(result.fileUri).thenReturn(uri)
        whenever(qrBarcodeGenerator.decodeQR(uri)) doThrow (QRBarcodeGenerator.QRFileHandlingException::class)
        presenter.onActivityResult(req, res, data)
        verify(fileSharingHelper).extractUriFromActivityResult(req, res, data)
        verify(qrBarcodeGenerator).decodeQR(uri)
        verify(view).showErrorLoadingFileDialog()
        verify(parentPresenter, never()).navigateToEnterPasswordPage(accountKey)
        verify(view, never()).showErrorDecodingQRDialog()
    }

    @Test
    fun `onActivityResult result ok, load encrypted throw QRBarcodeGeneratorException, show error decoding qr dialog`() {
        val req = 1
        val res = 1
        val data = null
        val accountKey = "some_account_key"
        val result: FileSharingHelper.RequestFileResult = mock()
        val uri: Uri = mock()

        verify(callbackManager).sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED)
        whenever(fileSharingHelper.extractUriFromActivityResult(req, res, data)).thenReturn(result)
        whenever(result.result).thenReturn(REQUEST_RESULT_OK)
        whenever(result.fileUri).thenReturn(uri)
        whenever(qrBarcodeGenerator.decodeQR(uri)).thenReturn(accountKey)
        presenter.onActivityResult(req, res, data)
        verify(fileSharingHelper).extractUriFromActivityResult(req, res, data)
        verify(qrBarcodeGenerator).decodeQR(uri)
        verify(parentPresenter).navigateToEnterPasswordPage(accountKey)
        verify(view, never()).showErrorLoadingFileDialog()
        verify(view, never()).showErrorDecodingQRDialog()
    }

    @Test
    fun `onBackClicked do previousStep and send event`() {
        presenter.onBackClicked()
        verify(callbackManager).sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_BACK_TAPPED)
        verify(parentPresenter).previousStep()
    }
}
