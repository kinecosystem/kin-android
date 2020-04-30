package kin.backupandrestore;

import kin.backupandrestore.exception.BackupAndRestoreException;

public interface BackupCallback {

    void onSuccess();

    void onCancel();

    void onFailure(BackupAndRestoreException exception);
}
