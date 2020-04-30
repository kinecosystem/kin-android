package kin.backupandrestore.restore.presenter;


import android.os.Bundle;

import kin.backupandrestore.restore.view.RestoreEnterPasswordView;

public interface RestoreEnterPasswordPresenter extends BaseChildPresenter<RestoreEnterPasswordView> {

    void onPasswordChanged(String password);

    void restoreClicked(String password);

    void onSaveInstanceState(Bundle outState);
}
