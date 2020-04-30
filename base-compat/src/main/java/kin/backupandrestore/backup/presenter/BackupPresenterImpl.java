package kin.backupandrestore.backup.presenter;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kin.backupandrestore.backup.view.BackupView;
import kin.backupandrestore.base.BasePresenterImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.sdk.KinAccount;

import static kin.backupandrestore.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_BACK_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_QR_PAGE_BACK_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_WELCOME_PAGE_BACK_TAPPED;

public class BackupPresenterImpl extends BasePresenterImpl<BackupView> implements BackupPresenter {

    static final String KEY_STEP = "kinrecovery_backup_step";
    public static final String KEY_ACCOUNT_KEY = "kinrecovery_backup_account_key";

    private @Step
    int step;
    private final CallbackManager callbackManager;
    private final KinAccount kinAccount;
    private boolean isBackupSucceed = false;
    private String accountKey;


    public BackupPresenterImpl(@NonNull CallbackManager callbackManager, @NonNull KinAccount kinAccount,
                               @Nullable final Bundle savedInstanceState) {
        this.callbackManager = callbackManager;
        this.step = getStep(savedInstanceState);
        this.kinAccount = kinAccount;
        this.accountKey = getAccountKey(savedInstanceState);
    }

    private int getStep(Bundle savedInstanceState) {
        return savedInstanceState != null ? savedInstanceState.getInt(KEY_STEP, STEP_START) : STEP_START;
    }

    private String getAccountKey(Bundle savedInstanceState) {
        return savedInstanceState != null ? savedInstanceState.getString(KEY_ACCOUNT_KEY) : null;
    }

    @Override
    public void onAttach(BackupView view) {
        super.onAttach(view);
        switchToStep(step);
    }

    @Override
    public void onBackClicked() {
        sendBackEvent(step);
        if (step == STEP_WELL_DONE) {
            switchToStep(STEP_CLOSE);
        } else {
            if (view != null) {
                if (!isBackupSucceed && step == STEP_CREATE_PASSWORD) {
                    callbackManager.setCancelledResult();
                }
                step--;
                view.onBackButtonClicked();
            }
        }
    }

    private void sendBackEvent(@Step final int step) {
        switch (step) {
            case STEP_START:
                callbackManager.sendBackupEvent(BACKUP_WELCOME_PAGE_BACK_TAPPED);
                break;
            case STEP_CREATE_PASSWORD:
                callbackManager.sendBackupEvent(BACKUP_CREATE_PASSWORD_PAGE_BACK_TAPPED);
                break;
            case STEP_SAVE_AND_SHARE:
                callbackManager.sendBackupEvent(BACKUP_QR_PAGE_BACK_TAPPED);
                break;
        }
    }


    private void switchToStep(@Step final int step) {
        if (view != null) {
            this.step = step;
            switch (step) {
                case STEP_START:
                    view.startBackupFlow();
                    break;
                case STEP_CREATE_PASSWORD:
                    view.moveToCreatePasswordPage();
                    break;
                case STEP_SAVE_AND_SHARE:
                    if (accountKey != null) {
                        view.moveToSaveAndSharePage(accountKey);
                        isBackupSucceed = true;
                        callbackManager.sendBackupSuccessResult();
                    } else {
                        view.showError();
                        view.close();
                    }
                    break;
                case STEP_WELL_DONE:
                    view.moveToWellDonePage();
                    break;
                case STEP_CLOSE:
                    view.close();
                    break;
            }
        }
    }

    @Override
    public void navigateToCreatePasswordPage() {
        switchToStep(STEP_CREATE_PASSWORD);
    }

    @Override
    public void navigateToSaveAndSharePage(@NonNull String accountKey) {
        this.accountKey = accountKey;
        switchToStep(STEP_SAVE_AND_SHARE);
    }

    @Override
    public void navigateToWellDonePage() {
        switchToStep(STEP_WELL_DONE);
    }

    @Override
    public void closeFlow() {
        switchToStep(STEP_CLOSE);
    }

    @Override
    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    @Override
    public KinAccount getKinAccount() {
        return kinAccount;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_STEP, step);
        outState.putString(KEY_ACCOUNT_KEY, accountKey);
    }
}
