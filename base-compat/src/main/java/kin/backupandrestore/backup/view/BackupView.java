package kin.backupandrestore.backup.view;

import kin.backupandrestore.base.BaseView;
import kin.backupandrestore.base.KeyboardHandler;

public interface BackupView extends BaseView, KeyboardHandler {

    void startBackupFlow();

    void moveToCreatePasswordPage();

    void moveToSaveAndSharePage(String key);

    void onBackButtonClicked();

    void moveToWellDonePage();

    void close();

    void showError();
}
