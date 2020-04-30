package kin.backupandrestore.restore.view;


import kin.backupandrestore.base.BaseView;

public interface UploadQRView extends BaseView {

    void showConsentDialog();

    void showErrorLoadingFileDialog();

    void showErrorDecodingQRDialog();
}
