package kin.backupandrestore.events;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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

@IntDef({BACKUP_WELCOME_PAGE_VIEWED,
        BACKUP_WELCOME_PAGE_BACK_TAPPED,
        BACKUP_WELCOME_PAGE_START_TAPPED,
        BACKUP_CREATE_PASSWORD_PAGE_VIEWED,
        BACKUP_CREATE_PASSWORD_PAGE_BACK_TAPPED,
        BACKUP_CREATE_PASSWORD_PAGE_NEXT_TAPPED,
        BACKUP_QR_CODE_PAGE_VIEWED,
        BACKUP_QR_PAGE_BACK_TAPPED,
        BACKUP_QR_PAGE_SEND_QR_TAPPED,
        BACKUP_QR_PAGE_QR_SAVED_TAPPED,
        BACKUP_COMPLETED_PAGE_VIEWED})
@Retention(RetentionPolicy.SOURCE)
public @interface BackupEventCode {

    // Welcome page
    int BACKUP_WELCOME_PAGE_VIEWED = 70000;
    int BACKUP_WELCOME_PAGE_BACK_TAPPED = 70001;
    int BACKUP_WELCOME_PAGE_START_TAPPED = 70002;
    // Create Password page
    int BACKUP_CREATE_PASSWORD_PAGE_VIEWED = 71000;
    int BACKUP_CREATE_PASSWORD_PAGE_BACK_TAPPED = 71001;
    int BACKUP_CREATE_PASSWORD_PAGE_NEXT_TAPPED = 71002;
    // QR page
    int BACKUP_QR_CODE_PAGE_VIEWED = 72000;
    int BACKUP_QR_PAGE_BACK_TAPPED = 72001;
    int BACKUP_QR_PAGE_SEND_QR_TAPPED = 72002;
    int BACKUP_QR_PAGE_QR_SAVED_TAPPED = 72003;
    // Completed page
    int BACKUP_COMPLETED_PAGE_VIEWED = 73000;
}
