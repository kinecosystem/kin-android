package kin.backupandrestore.restore.view;


import kin.backupandrestore.base.BaseView;

public interface RestoreView extends BaseView {

    void navigateToUpload();

    void navigateToEnterPassword(String keystoreData);

    void navigateToRestoreCompleted();

    void navigateBack();

    void close();

    void closeKeyboard();

    void showError();
}
