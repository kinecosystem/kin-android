package kin.backupandrestore.events;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kin.backupandrestore.BackupCallback;
import kin.backupandrestore.BackupEvents;
import kin.backupandrestore.RestoreEvents;
import kin.backupandrestore.exception.BackupAndRestoreException;

import static kin.backupandrestore.exception.BackupAndRestoreException.CODE_UNEXPECTED;

public class CallbackManager {

    // Result Code
    static final int RES_CODE_SUCCESS = 5000;
    static final int RES_CODE_CANCEL = 5001;
    static final int RES_CODE_FAILED = 5002;
    static final String EXTRA_KEY_ERROR_MESSAGE = "EXTRA_KEY_ERROR_MESSAGE";
    static final String EXTRA_KEY_ERROR_CODE = "EXTRA_KEY_ERROR_CODE";
    static final String EXTRA_KEY_PUBLIC_ADDRESS = "EXTRA_KEY_PUBLIC_ADDRESS";

    // Request Codes
    private int reqCodeBackup;
    private int reqCodeRestore;

    @Nullable
    private BackupCallback backupCallback;
    @Nullable
    private InternalRestoreCallback internalRestoreCallback;

    private final EventDispatcher eventDispatcher;

    public CallbackManager(@NonNull final EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public CallbackManager(@NonNull final EventDispatcher eventDispatcher, int reqCodeBackup, int reqCodeRestore) {
        this.eventDispatcher = eventDispatcher;
        this.reqCodeBackup = reqCodeBackup;
        this.reqCodeRestore = reqCodeRestore;
    }

    public void setBackupCallback(@Nullable BackupCallback backupCallback) {
        this.backupCallback = backupCallback;
    }

    public void setInternalRestoreCallback(@Nullable InternalRestoreCallback internalRestoreCallback) {
        this.internalRestoreCallback = internalRestoreCallback;
    }

    public void setBackupEvents(@Nullable BackupEvents backupEvents) {
        this.eventDispatcher.setBackupEvents(backupEvents);
    }

    public void setRestoreEvents(@Nullable RestoreEvents restoreEvents) {
        this.eventDispatcher.setRestoreEvents(restoreEvents);
    }

    public void unregisterCallbacksAndEvents() {
        this.eventDispatcher.unregister();
        this.backupCallback = null;
        this.internalRestoreCallback = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == reqCodeBackup) {
            handleBackupResult(resultCode, data);
        } else if (requestCode == reqCodeRestore) {
            handleRestoreResult(resultCode, data);
        }
    }

    public void sendRestoreSuccessResult(String publicAddress) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY_PUBLIC_ADDRESS, publicAddress);
        eventDispatcher.setActivityResult(RES_CODE_SUCCESS, intent);
    }

    public void sendBackupSuccessResult() {
        eventDispatcher.setActivityResult(RES_CODE_SUCCESS, null);
    }

    public void setCancelledResult() {
        eventDispatcher.setActivityResult(RES_CODE_CANCEL, null);
    }

    private void handleRestoreResult(int resultCode, Intent data) {
        if (internalRestoreCallback != null) {
            switch (resultCode) {
                case RES_CODE_SUCCESS:
                    final String publicAddress = data.getStringExtra(EXTRA_KEY_PUBLIC_ADDRESS);
                    if (publicAddress == null) {
                        internalRestoreCallback.onFailure(new BackupAndRestoreException(CODE_UNEXPECTED,
                                "Unexpected error - imported account public address not found"));
                    }
                    internalRestoreCallback.onSuccess(publicAddress);
                    break;
                case RES_CODE_CANCEL:
                    internalRestoreCallback.onCancel();
                    break;
                case RES_CODE_FAILED:
                    String errorMessage = data.getStringExtra(EXTRA_KEY_ERROR_MESSAGE);
                    int code = data.getIntExtra(EXTRA_KEY_ERROR_CODE, 0);
                    internalRestoreCallback.onFailure(new BackupAndRestoreException(code, errorMessage));
                    break;
                default:
                    internalRestoreCallback.onFailure(
                            new BackupAndRestoreException(CODE_UNEXPECTED,
                                    "Unexpected error - unknown result code " + resultCode));
                    break;
            }
        }
    }

    private void handleBackupResult(int resultCode, Intent data) {
        if (backupCallback != null) {
            switch (resultCode) {
                case RES_CODE_SUCCESS:
                    backupCallback.onSuccess();
                    break;
                case RES_CODE_CANCEL:
                    backupCallback.onCancel();
                    break;
                case RES_CODE_FAILED:
                    String errorMessage = data.getStringExtra(EXTRA_KEY_ERROR_MESSAGE);
                    int code = data.getIntExtra(EXTRA_KEY_ERROR_CODE, 0);
                    backupCallback.onFailure(new BackupAndRestoreException(code, errorMessage));
                    break;
                default:
                    backupCallback.onFailure(
                            new BackupAndRestoreException(CODE_UNEXPECTED,
                                    "Unexpected error - unknown result code " + resultCode));
                    break;
            }
        }
    }

    public void sendBackupEvent(@BackupEventCode int eventCode) {
        eventDispatcher.sendEvent(EventDispatcher.BACKUP_EVENTS, eventCode);
    }

    public void sendRestoreEvent(@RestoreEventCode int eventCode) {
        eventDispatcher.sendEvent(EventDispatcher.RESTORE_EVENTS, eventCode);
    }
}
