package kin.backupandrestore.restore.presenter;


import android.content.Intent;
import android.os.Bundle;

import kin.backupandrestore.AccountExtractor;
import kin.backupandrestore.base.BasePresenterImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.restore.view.RestoreView;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;

public class RestorePresenterImpl extends BasePresenterImpl<RestoreView> implements RestorePresenter {

    static final int STEP_UPLOAD = 0;
    static final int STEP_ENTER_PASSWORD = 1;
    static final int STEP_RESTORE_COMPLETED = 2;
    static final int STEP_FINISH = 3;

    static final String KEY_RESTORE_STEP = "kinrecovery_restore_step";
    public static final String KEY_ACCOUNT_KEY = "kinrecovery_restore_account_key";
    public static final String KEY_PUBLIC_ADDRESS = "kinrecovery_restore_public_address";

    private int currentStep;
    private String accountKey;
    private KinClient kinClient;
    private KinAccount kinAccount;

    private final CallbackManager callbackManager;

    public RestorePresenterImpl(CallbackManager callbackManager, KinClient kinClient, Bundle saveInstanceState) {
        this.callbackManager = callbackManager;
        this.kinClient = kinClient;
        this.kinAccount = getKinAccount(saveInstanceState);
        this.currentStep = getStep(saveInstanceState);
        this.accountKey = getAccountKey(saveInstanceState);
        this.callbackManager.setCancelledResult();
    }

    @Override
    public void onAttach(RestoreView view) {
        super.onAttach(view);
        switchToStep(currentStep);
    }

    private int getStep(Bundle saveInstanceState) {
        return saveInstanceState != null ? saveInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD) : STEP_UPLOAD;
    }

    private String getAccountKey(Bundle saveInstanceState) {
        return saveInstanceState != null ? saveInstanceState.getString(KEY_ACCOUNT_KEY) : null;
    }

    private KinAccount getKinAccount(Bundle saveInstanceState) {
        return saveInstanceState != null ? AccountExtractor
                .getKinAccount(kinClient, saveInstanceState.getString(KEY_PUBLIC_ADDRESS)) : null;
    }

    @Override
    public void onBackClicked() {
        previousStep();
    }

    private void switchToStep(int step) {
        RestoreView view = getView();
        currentStep = step;
        switch (step) {
            case STEP_UPLOAD:
                if (view != null) {
                    view.navigateToUpload();
                }
                break;
            case STEP_ENTER_PASSWORD:
                if (view != null) {
                    if (accountKey != null) {
                        view.navigateToEnterPassword(accountKey);
                    } else {
                        view.showError();
                    }
                }
                break;
            case STEP_RESTORE_COMPLETED:
                if (view != null) {
                    view.closeKeyboard();
                    if (kinAccount != null) {
                        view.navigateToRestoreCompleted();
                    } else {
                        view.showError();
                    }
                }
                break;
            case STEP_FINISH:
                if (kinAccount != null) {
                    callbackManager.sendRestoreSuccessResult(kinAccount.getPublicAddress());
                } else {
                    if (view != null) {
                        view.showError();
                    }
                }
                if (view != null) {
                    view.close();
                }
                break;
        }
    }

    @Override
    public void navigateToEnterPasswordPage(final String accountKey) {
        this.accountKey = accountKey;
        switchToStep(STEP_ENTER_PASSWORD);
    }

    @Override
    public void navigateToRestoreCompletedPage(final KinAccount kinAccount) {
        this.kinAccount = kinAccount;
        switchToStep(STEP_RESTORE_COMPLETED);
    }

    @Override
    public void closeFlow() {
        switchToStep(STEP_FINISH);
    }


    @Override
    public void previousStep() {
        RestoreView view = getView();
        if (view != null) {
            switch (currentStep) {
                case STEP_UPLOAD:
                    view.close();
                    break;
                case STEP_ENTER_PASSWORD:
                    view.navigateBack();
                    view.closeKeyboard();
                    break;
                case STEP_RESTORE_COMPLETED:
                    view.navigateBack();
                    break;
                case STEP_FINISH:
                    view.navigateBack();
                    break;
            }
        }
        currentStep--;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_RESTORE_STEP, currentStep);
        outState.putString(KEY_ACCOUNT_KEY, accountKey);
        if (kinAccount != null) {
            outState.putString(KEY_PUBLIC_ADDRESS, kinAccount.getPublicAddress());
        }
    }

    @Override
    public KinClient getKinClient() {
        return kinClient;
    }

}
