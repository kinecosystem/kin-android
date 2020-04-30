package kin.backupandrestore.events;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kin.backupandrestore.BackupEvents;
import kin.backupandrestore.RestoreEvents;
import kin.backupandrestore.events.BroadcastManager.Listener;
import kin.backupandrestore.events.BroadcastManagerImpl.ActionName;

import static kin.backupandrestore.events.BackupEventCode.BACKUP_COMPLETED_PAGE_VIEWED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_BACK_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_NEXT_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_CREATE_PASSWORD_PAGE_VIEWED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_QR_CODE_PAGE_VIEWED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_QR_PAGE_BACK_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_QR_PAGE_QR_SAVED_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_QR_PAGE_SEND_QR_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_WELCOME_PAGE_BACK_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_WELCOME_PAGE_START_TAPPED;
import static kin.backupandrestore.events.BackupEventCode.BACKUP_WELCOME_PAGE_VIEWED;
import static kin.backupandrestore.events.BroadcastManagerImpl.ACTION_EVENTS_BACKUP;
import static kin.backupandrestore.events.BroadcastManagerImpl.ACTION_EVENTS_RESTORE;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_ARE_YOUR_SURE_OK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_DONE_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_VIEWED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_BACK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED;

public class EventDispatcherImpl implements EventDispatcher {

    @Nullable
    private BackupEvents backupEvents;
    @Nullable
    private RestoreEvents restoreEvents;

    @NonNull
    private final BroadcastManager broadcastManager;
    private Listener broadcastListener;

    public EventDispatcherImpl(@NonNull final BroadcastManager broadcastManager) {
        this.broadcastManager = broadcastManager;
    }

    @Override
    public void setBackupEvents(@Nullable BackupEvents backupEvents) {
        this.backupEvents = backupEvents;
        if (backupEvents != null) {
            registerBroadcastListener(ACTION_EVENTS_BACKUP);
        }
    }

    @Override
    public void setRestoreEvents(@Nullable RestoreEvents restoreEvents) {
        this.restoreEvents = restoreEvents;
        if (restoreEvents != null) {
            registerBroadcastListener(ACTION_EVENTS_RESTORE);
        }
    }

    @Override
    public void sendEvent(@EventType final int eventType, final int eventID) {
        Intent data = new Intent();
        data.putExtra(EXTRA_KEY_EVENT_TYPE, eventType);
        data.putExtra(EXTRA_KEY_EVENT_ID, eventID);
        broadcastManager.sendEvent(data, eventType == BACKUP_EVENTS ? ACTION_EVENTS_BACKUP : ACTION_EVENTS_RESTORE);
    }

    @Override
    public void setActivityResult(int resultCode, Intent data) {
        broadcastManager.setActivityResult(resultCode, data);
    }

    @Override
    public void unregister() {
        if (broadcastListener != null) {
            broadcastManager.unregisterAll();
        }
    }

    private void registerBroadcastListener(@ActionName final String actionName) {
        if (broadcastListener == null) {
            broadcastListener = new Listener() {
                @Override
                public void onReceive(Intent data) {
                    parseData(data);
                }
            };
        }
        broadcastManager.register(broadcastListener, actionName);
    }

    private void parseData(Intent data) {
        final int eventType = data.getIntExtra(EXTRA_KEY_EVENT_TYPE, -1);
        final int eventID = data.getIntExtra(EXTRA_KEY_EVENT_ID, -1);
        if (eventType == BACKUP_EVENTS) {
            handleBackupEvents(eventID);
        } else {
            if (eventType == RESTORE_EVENTS) {
                handleRestoreEvents(eventID);
            }
        }
    }

    private void handleBackupEvents(@BackupEventCode int eventID) {
        if (backupEvents != null) {
            switch (eventID) {
                case BACKUP_WELCOME_PAGE_VIEWED:
                    backupEvents.onBackupWelcomePageViewed();
                    break;
                case BACKUP_WELCOME_PAGE_BACK_TAPPED:
                    backupEvents.onBackupWelcomePageBackButtonTapped();
                    break;
                case BACKUP_WELCOME_PAGE_START_TAPPED:
                    backupEvents.onBackupStartButtonTapped();
                    break;
                case BACKUP_CREATE_PASSWORD_PAGE_VIEWED:
                    backupEvents.onBackupCreatePasswordPageViewed();
                    break;
                case BACKUP_CREATE_PASSWORD_PAGE_BACK_TAPPED:
                    backupEvents.onBackupCreatePasswordBackButtonTapped();
                    break;
                case BACKUP_CREATE_PASSWORD_PAGE_NEXT_TAPPED:
                    backupEvents.onBackupCreatePasswordNextButtonTapped();
                    break;
                case BACKUP_QR_CODE_PAGE_VIEWED:
                    backupEvents.onBackupQrCodePageViewed();
                    break;
                case BACKUP_QR_PAGE_BACK_TAPPED:
                    backupEvents.onBackupQrCodeBackButtonTapped();
                    break;
                case BACKUP_QR_PAGE_QR_SAVED_TAPPED:
                    backupEvents.onBackupQrCodeMyQrCodeButtonTapped();
                    break;
                case BACKUP_QR_PAGE_SEND_QR_TAPPED:
                    backupEvents.onBackupQrCodeSendButtonTapped();
                    break;
                case BACKUP_COMPLETED_PAGE_VIEWED:
                    backupEvents.onBackupCompletedPageViewed();
                    break;
            }
        }
    }

    private void handleRestoreEvents(@RestoreEventCode int eventID) {
        if (restoreEvents != null) {
            switch (eventID) {
                case RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED:
                    restoreEvents.onRestoreUploadQrCodePageViewed();
                    break;
                case RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED:
                    restoreEvents.onRestoreUploadQrCodeButtonTapped();
                    break;
                case RESTORE_UPLOAD_QR_CODE_BACK_TAPPED:
                    restoreEvents.onRestoreUploadQrCodeBackButtonTapped();
                    break;
                case RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED:
                    restoreEvents.onRestoreAreYouSureCancelButtonTapped();
                    break;
                case RESTORE_PASSWORD_ENTRY_PAGE_VIEWED:
                    restoreEvents.onRestorePasswordEntryPageViewed();
                    break;
                case RESTORE_PASSWORD_DONE_TAPPED:
                    restoreEvents.onRestorePasswordDoneButtonTapped();
                    break;
                case RESTORE_ARE_YOUR_SURE_OK_TAPPED:
                    restoreEvents.onRestoreAreYouSureOkButtonTapped();
                    break;
                case RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED:
                    restoreEvents.onRestorePasswordEntryBackButtonTapped();
                    break;

            }
        }
    }

}
