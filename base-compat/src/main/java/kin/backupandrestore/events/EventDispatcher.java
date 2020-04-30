package kin.backupandrestore.events;

import android.content.Intent;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import kin.backupandrestore.BackupEvents;
import kin.backupandrestore.RestoreEvents;

public interface EventDispatcher {


    @IntDef({BACKUP_EVENTS, RESTORE_EVENTS})
    @Retention(RetentionPolicy.SOURCE)
    @interface EventType {

    }

    int BACKUP_EVENTS = 0x00000001;
    int RESTORE_EVENTS = 0x00000002;

    String EXTRA_KEY_EVENT_TYPE = "EVENT_TYPE";
    String EXTRA_KEY_EVENT_ID = "EVENT_ID";

    void setBackupEvents(@Nullable BackupEvents backupEvents);

    void setRestoreEvents(@Nullable RestoreEvents restoreEvents);

    void sendEvent(@EventType final int eventType, final int eventID);

    void setActivityResult(int resultCode, Intent data);

    void unregister();
}
