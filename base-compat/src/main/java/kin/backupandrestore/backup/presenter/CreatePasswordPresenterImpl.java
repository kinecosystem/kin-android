package kin.backupandrestore.backup.presenter;

import androidx.annotation.NonNull;

import org.kin.sdk.base.models.Key;
import org.kin.sdk.base.tools.BackupRestore;
import org.kin.stellarfork.KeyPair;

import java.util.regex.Pattern;

import kin.backupandrestore.Validator;
import kin.backupandrestore.backup.view.BackupNavigator;
import kin.backupandrestore.backup.view.CreatePasswordView;
import kin.backupandrestore.base.BasePresenterImpl;
import kin.backupandrestore.events.CallbackManager;

import static kin.backupandrestore.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_NEXT_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_VIEWED;

public class CreatePasswordPresenterImpl extends BasePresenterImpl<CreatePasswordView> implements
        CreatePasswordPresenter {

    private final BackupNavigator backupNavigator;
    private final CallbackManager callbackManager;
    private final BackupRestore backupRestore;
    private Key.PrivateKey privateKey;

    private boolean isPasswordRulesOK = false;
    private boolean isIUnderstandChecked = false;
    private final Pattern pattern;

    public CreatePasswordPresenterImpl(@NonNull final CallbackManager callbackManager,
                                       @NonNull final BackupNavigator backupNavigator,
                                       @NonNull final BackupRestore backupRestore,
                                       @NonNull Key.PrivateKey privateKey) {
        this.backupNavigator = backupNavigator;
        this.callbackManager = callbackManager;
        this.callbackManager.sendBackupEvent(BACKUP_CREATE_PASSWORD_PAGE_VIEWED);
        this.backupRestore = backupRestore;
        this.privateKey = privateKey;
        this.pattern = getPattern();
    }

    @Override
    public void onBackClicked() {
        backupNavigator.closeFlow();
    }

    @Override
    public void passwordCheck(String changedPassword, String otherPassword, boolean isConfirmPassword) {
        boolean changedPasswordIsEmpty = changedPassword.isEmpty();
        if (validatePassword(changedPassword)) {
            isPasswordRulesOK = true;
            handlePasswordCorrectness(isConfirmPassword, true);
        } else {
            isPasswordRulesOK = false;
            if (changedPasswordIsEmpty) {
                handlePasswordIsEmpty(isConfirmPassword);
            } else {
                handlePasswordCorrectness(isConfirmPassword, false);
            }
        }
        checkAllCompleted(otherPassword, changedPassword);
    }

    private void handlePasswordCorrectness(boolean isConfirmPassword, boolean isCorrect) {
        if (view != null) {
            if (isConfirmPassword) {
                view.setConfirmPasswordIsCorrect(isCorrect);
            } else {
                view.setEnterPasswordIsCorrect(isCorrect);
            }
        }
    }

    private void handlePasswordIsEmpty(boolean isConfirmPassword) {
        if (view != null) {
            if (isConfirmPassword) {
                view.resetConfirmPasswordField();
            } else {
                view.resetEnterPasswordField();
            }
        }
    }

    @Override
    public void iUnderstandChecked(boolean isChecked, String enterPassword, String confirmPassword) {
        isIUnderstandChecked = isChecked;
        checkAllCompleted(enterPassword, confirmPassword);
    }

    @Override
    public void nextButtonClicked(String confirmPassword, String password) {
        if (confirmPassword.equals(password)) {
            callbackManager.sendBackupEvent(BACKUP_CREATE_PASSWORD_PAGE_NEXT_TAPPED);
            exportAccount(password);
        } else {
            if (view != null) {
                view.setPasswordDoesNotMatch();
            }
        }
    }

    private void exportAccount(String password) {
        try {
            final String accountKey = backupRestore.exportWallet(KeyPair.fromSecretSeed(privateKey.stellarBase32Encode()), password);
            backupNavigator.navigateToSaveAndSharePage(accountKey);
        } catch (org.kin.sdk.base.tools.CryptoException e) {
            if (view != null) {
                view.showBackupFailed();
            }
        }
    }

    @Override
    public void onRetryClicked(String password) {
        exportAccount(password);
    }

    @Override
    public void checkAllCompleted(String password, String otherPassword) {
        if (isPasswordRulesOK && isIUnderstandChecked && !(password.isEmpty() || otherPassword.isEmpty())) {
            enableNextButton();
        } else {
            disableNextButton();
        }
    }

    private void disableNextButton() {
        if (view != null) {
            view.disableNextButton();
        }
    }

    private void enableNextButton() {
        if (view != null) {
            view.enableNextButton();
        }
    }

    private Pattern getPattern() {
        String digit = "(?=.*\\d)";
        String upper = "(?=.*[A-Z])";
        String lower = "(?=.*[a-z])";
        String special = "(?=.*[!@#$%^&*()_+{}\\[\\]])";
        int min = 9;
        int max = 20;
        StringBuilder sb = new StringBuilder()
                .append("^")
                .append(digit)
                .append(upper)
                .append(lower)
                .append(special)
                .append("(.{")
                .append(min).append(",")
                .append(max).append("})$");
        return Pattern.compile(sb.toString());
    }


    private boolean validatePassword(@NonNull final String password) {
        Validator.checkNotNull(password, "password");
        return pattern.matcher(password).matches();
    }
}
