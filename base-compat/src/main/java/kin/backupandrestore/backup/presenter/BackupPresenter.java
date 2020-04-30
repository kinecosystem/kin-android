package kin.backupandrestore.backup.presenter;

import android.os.Bundle;

import kin.backupandrestore.backup.view.BackupNavigator;
import kin.backupandrestore.backup.view.BackupView;
import kin.backupandrestore.base.BasePresenter;
import kin.sdk.KinAccount;

public interface BackupPresenter extends BasePresenter<BackupView>, BackupNavigator {

    void onSaveInstanceState(Bundle outState);

    void setAccountKey(String key);

    KinAccount getKinAccount();
}
