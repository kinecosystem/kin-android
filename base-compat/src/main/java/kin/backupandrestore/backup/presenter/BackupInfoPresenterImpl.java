package kin.backupandrestore.backup.presenter;

import androidx.annotation.NonNull;

import kin.backupandrestore.backup.view.BackupNavigator;
import kin.backupandrestore.base.BasePresenterImpl;
import kin.backupandrestore.base.BaseView;
import kin.backupandrestore.events.CallbackManager;

import static kin.backupandrestore.events.BackupEventCode.BACKUP_WELCOME_PAGE_START_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_WELCOME_PAGE_VIEWED;

public class BackupInfoPresenterImpl extends BasePresenterImpl<BaseView> implements BackupInfoPresenter {

    private final BackupNavigator backupNavigator;
    private final CallbackManager callbackManager;

    public BackupInfoPresenterImpl(@NonNull CallbackManager callbackManager,
                                   BackupNavigator backupNavigator) {
        this.backupNavigator = backupNavigator;
        this.callbackManager = callbackManager;
        this.callbackManager.sendBackupEvent(BACKUP_WELCOME_PAGE_VIEWED);
        this.callbackManager.setCancelledResult(); // make sure cancel will be called if nothing happened.
    }

    @Override
    public void onBackClicked() {
        backupNavigator.closeFlow();
    }

    @Override
    public void letsGoButtonClicked() {
        callbackManager.sendBackupEvent(BACKUP_WELCOME_PAGE_START_TAPPED);
        if (view != null) {
            backupNavigator.navigateToCreatePasswordPage();
        }
    }
}
