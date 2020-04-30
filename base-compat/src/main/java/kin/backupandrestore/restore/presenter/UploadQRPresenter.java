package kin.backupandrestore.restore.presenter;


import android.content.Intent;

import kin.backupandrestore.restore.view.UploadQRView;

public interface UploadQRPresenter extends BaseChildPresenter<UploadQRView> {

    void uploadClicked();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onOkPressed(String chooserTitle);

    void onCancelPressed();
}
