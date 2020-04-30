package kin.backupandrestore.backup.view;

import android.net.Uri;

import kin.backupandrestore.base.BaseView;

public interface SaveAndShareView extends BaseView {

    void setQRImage(Uri qrURI);

    void showSendIntent(Uri qrURI);

    void showIHaveSavedCheckBox();

    void showErrorTryAgainLater();
}
