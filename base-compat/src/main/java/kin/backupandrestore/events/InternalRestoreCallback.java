package kin.backupandrestore.events;

import kin.backupandrestore.exception.BackupAndRestoreException;

public interface InternalRestoreCallback {

    void onSuccess(String publicAddress);

    void onCancel();

    void onFailure(BackupAndRestoreException throwable);

}
