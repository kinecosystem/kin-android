package kin.backupandrestore.restore.presenter;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.qr.QRBarcodeGenerator;
import kin.backupandrestore.qr.QRBarcodeGenerator.QRBarcodeGeneratorException;
import kin.backupandrestore.qr.QRBarcodeGenerator.QRFileHandlingException;
import kin.backupandrestore.restore.presenter.FileSharingHelper.RequestFileResult;
import kin.backupandrestore.restore.view.UploadQRView;
import kin.backupandrestore.utils.Logger;

import static kin.backupandrestore.events.RestoreEventCode.RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_ARE_YOUR_SURE_OK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_BACK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED;

public class UploadQRPresenterImpl extends BaseChildPresenterImpl<UploadQRView> implements UploadQRPresenter {

    private final FileSharingHelper fileRequester;
    private final QRBarcodeGenerator qrBarcodeGenerator;
    private final CallbackManager callbackManager;

    public UploadQRPresenterImpl(@NonNull final CallbackManager callbackManager, FileSharingHelper fileRequester,
                                 QRBarcodeGenerator qrBarcodeGenerator) {
        this.callbackManager = callbackManager;
        this.fileRequester = fileRequester;
        this.qrBarcodeGenerator = qrBarcodeGenerator;
        this.callbackManager.sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED);
    }

    @Override
    public void uploadClicked() {
        UploadQRView view = getView();
        if (view != null) {
            view.showConsentDialog();
        }
        callbackManager.sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED);
    }

    @Override
    public void onOkPressed(String chooserTitle) {
        callbackManager.sendRestoreEvent(RESTORE_ARE_YOUR_SURE_OK_TAPPED);
        fileRequester.requestImageFile(chooserTitle);
    }

    @Override
    public void onCancelPressed() {
        callbackManager.sendRestoreEvent(RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        RequestFileResult requestFileResult = fileRequester.extractUriFromActivityResult(requestCode, resultCode, data);
        switch (requestFileResult.getResult()) {
            case FileSharingHelper.REQUEST_RESULT_CANCELED:
                break;
            case FileSharingHelper.REQUEST_RESULT_FAILED:
                view.showErrorLoadingFileDialog();
                break;
            case FileSharingHelper.REQUEST_RESULT_OK:
                loadEncryptedKeyStore(requestFileResult.getFileUri());
                break;
        }
    }

    private void loadEncryptedKeyStore(Uri fileUri) {
        try {
            String encryptedKeyStore = qrBarcodeGenerator.decodeQR(fileUri);
            getParentPresenter().navigateToEnterPasswordPage(encryptedKeyStore);
        } catch (QRFileHandlingException e) {
            Logger.e("loadEncryptedKeyStore - loading file failed.", e);
            view.showErrorLoadingFileDialog();
        } catch (QRBarcodeGeneratorException e) {
            Logger.e("loadEncryptedKeyStore - decoding QR failed.", e);
            view.showErrorDecodingQRDialog();
        }
    }

    @Override
    public void onBackClicked() {
        callbackManager.sendRestoreEvent(RESTORE_UPLOAD_QR_CODE_BACK_TAPPED);
        getParentPresenter().previousStep();
    }
}
