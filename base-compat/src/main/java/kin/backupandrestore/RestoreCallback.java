package kin.backupandrestore;

import org.kin.sdk.base.KinAccountContext;

import kin.backupandrestore.exception.BackupAndRestoreException;

public interface RestoreCallback {

    void onSuccess(KinAccountContext kinAccountContext);

    void onCancel();

    void onFailure(BackupAndRestoreException throwable);
}
