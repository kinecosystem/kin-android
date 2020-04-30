package kin.backupandrestore.backup.presenter;

import android.os.Bundle;

import kin.backupandrestore.backup.view.SaveAndShareView;
import kin.backupandrestore.base.BasePresenter;

public interface SaveAndSharePresenter extends BasePresenter<SaveAndShareView> {

    void iHaveSavedChecked(boolean isChecked);

    void sendQREmailClicked();

    void couldNotLoadQRImage();

    void onSaveInstanceState(Bundle outState);
}
