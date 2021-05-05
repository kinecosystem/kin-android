package kin.backupandrestore.restore.presenter;


import android.content.Intent;
import android.os.Bundle;

import org.kin.sdk.base.models.KinAccount;
import org.kin.stellarfork.KeyPair;

import kin.backupandrestore.base.BasePresenter;
import kin.backupandrestore.restore.view.RestoreView;

public interface RestorePresenter extends BasePresenter<RestoreView> {

    void navigateToEnterPasswordPage(final String accountKey);

    void navigateToRestoreCompletedPage(final KeyPair kinAccount);

    void closeFlow();

    void previousStep();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSaveInstanceState(Bundle outState);
}
