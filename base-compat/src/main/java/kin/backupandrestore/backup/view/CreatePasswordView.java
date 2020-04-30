package kin.backupandrestore.backup.view;

import kin.backupandrestore.base.BaseView;

public interface CreatePasswordView extends BaseView {

    void setEnterPasswordIsCorrect(boolean isCorrect);

    void setConfirmPasswordIsCorrect(boolean isCorrect);

    void enableNextButton();

    void disableNextButton();

    void showBackupFailed();

    void closeKeyboard();

    void resetEnterPasswordField();

    void resetConfirmPasswordField();

    void setPasswordDoesNotMatch();
}
