package kin.backupandrestore.backup.presenter;

import kin.backupandrestore.backup.view.CreatePasswordView;
import kin.backupandrestore.base.BasePresenter;

public interface CreatePasswordPresenter extends BasePresenter<CreatePasswordView> {

    void passwordCheck(String changedPassword, String otherPassword, boolean isConfirmPassword);

    void iUnderstandChecked(boolean isChecked, String enterPassword, String confirmPassword);

    void nextButtonClicked(String confirmPassword, String password);

    void checkAllCompleted(String password, String otherPassword);

    void onRetryClicked(String password);
}
