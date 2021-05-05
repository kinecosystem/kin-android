package kin.backupandrestore.backup.presenter;

import android.os.Bundle;

import org.kin.sdk.base.models.Key;
import org.kin.sdk.base.models.KinAccount;

import kin.backupandrestore.backup.view.BackupNavigator;
import kin.backupandrestore.backup.view.BackupView;
import kin.backupandrestore.base.BasePresenter;

public interface BackupPresenter extends BasePresenter<BackupView>, BackupNavigator {

    void onSaveInstanceState(Bundle outState);

    void setAccountKey(String key);

    Key.PrivateKey getPrivateKey();
}
