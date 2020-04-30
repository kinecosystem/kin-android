package kin.backupandrestore.restore.view;


import kin.backupandrestore.base.BaseView;

public interface RestoreEnterPasswordView extends BaseView {

    void enableDoneButton();

    void disableDoneButton();

    void decodeError();

    void invalidQrError();
}
