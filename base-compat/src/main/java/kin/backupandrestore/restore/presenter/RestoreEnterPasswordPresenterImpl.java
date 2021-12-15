package kin.backupandrestore.restore.presenter;


import android.os.Bundle;

import androidx.annotation.NonNull;

import org.kin.sdk.base.models.Key;
import org.kin.sdk.base.tools.BackupRestore;
import org.kin.sdk.base.tools.CorruptedDataException;
import org.kin.sdk.base.tools.CryptoException;
import org.kin.stellarfork.KeyPair;

import kin.backupandrestore.BackupAndRestoreManager;
import kin.backupandrestore.Validator;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.exception.BackupAndRestoreException;
import kin.backupandrestore.restore.view.RestoreEnterPasswordView;

import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_DONE_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_VIEWED;
import static kin.backupandrestore.exception.BackupAndRestoreException.CODE_RESTORE_FAILED;
import static kin.backupandrestore.exception.BackupAndRestoreException.CODE_RESTORE_INVALID_KEYSTORE_FORMAT;
import static kin.backupandrestore.restore.presenter.RestorePresenterImpl.KEY_ACCOUNT_KEY;

public class RestoreEnterPasswordPresenterImpl extends BaseChildPresenterImpl<RestoreEnterPasswordView> implements
        RestoreEnterPasswordPresenter {

    private final String keystoreData;
    private final CallbackManager callbackManager;
    private final BackupRestore backupRestore;

    public RestoreEnterPasswordPresenterImpl(@NonNull final CallbackManager callbackManager, String keystoreData, BackupRestore backupRestore) {
        this.callbackManager = callbackManager;
        this.keystoreData = keystoreData;
        this.callbackManager.sendRestoreEvent(RESTORE_PASSWORD_ENTRY_PAGE_VIEWED);
        this.backupRestore = backupRestore;
    }

    @Override
    public void onPasswordChanged(String password) {
        RestoreEnterPasswordView view = getView();
        if (view != null) {
            if (password.isEmpty()) {
                view.disableDoneButton();
            } else {
                view.enableDoneButton();
            }
        }
    }

    @Override
    public void restoreClicked(String password) {
        callbackManager.sendRestoreEvent(RESTORE_PASSWORD_DONE_TAPPED);
        try {
            KeyPair kinAccount = importAccount(keystoreData, password);
            getParentPresenter().navigateToRestoreCompletedPage(kinAccount);
        } catch (BackupAndRestoreException e) {
            RestoreEnterPasswordView view = getView();
            if (view != null) {
                if (e.getCode() == CODE_RESTORE_INVALID_KEYSTORE_FORMAT) {
                    view.invalidQrError();
                } else {
                    view.decodeError();
                }
            }
        }
    }

    private KeyPair importAccount(@NonNull final String keystore, @NonNull final String password)
            throws BackupAndRestoreException {
        Validator.checkNotNull(keystore, "keystore");
        Validator.checkNotNull(keystore, "password");
        KeyPair importedAccount;
        byte[] pk;
        try {
            importedAccount = backupRestore.importWallet(keystore, password);
            if (importedAccount.getRawSecretSeed() != null) {
                pk = importedAccount.getRawSecretSeed();
            } else {
                pk = importedAccount.getPrivateKey();
            }
            if(BackupAndRestoreManager.instance() != null) {
                BackupAndRestoreManager.instance()
                    .getEnvironment()
                    .importPrivateKey(new Key.PrivateKey(pk))
                    .resolve();
            }
        } catch (CryptoException e) {
            throw new BackupAndRestoreException(CODE_RESTORE_FAILED, "Could not import the account");
        }
        catch (CorruptedDataException e) {
            throw new BackupAndRestoreException(CODE_RESTORE_INVALID_KEYSTORE_FORMAT,
                    "The keystore is invalid - wrong format");
        }
        return importedAccount;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_ACCOUNT_KEY, keystoreData);
    }

    @Override
    public void onBackClicked() {
        callbackManager.sendRestoreEvent(RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED);
        getParentPresenter().previousStep();
    }
}
