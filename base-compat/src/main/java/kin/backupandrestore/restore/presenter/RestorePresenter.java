package kin.backupandrestore.restore.presenter;


import android.content.Intent;
import android.os.Bundle;

import kin.backupandrestore.base.BasePresenter;
import kin.backupandrestore.restore.view.RestoreView;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;

public interface RestorePresenter extends BasePresenter<RestoreView> {

    void navigateToEnterPasswordPage(final String accountKey);

    void navigateToRestoreCompletedPage(final KinAccount kinAccount);

    void closeFlow();

    void previousStep();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSaveInstanceState(Bundle outState);

    KinClient getKinClient();
}
