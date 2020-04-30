package kin.backupandrestore.events;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static kin.backupandrestore.events.RestoreEventCode.RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_ARE_YOUR_SURE_OK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_DONE_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_PASSWORD_ENTRY_PAGE_VIEWED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_BACK_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED;
import static kin.backupandrestore.events.RestoreEventCode.RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED;

@IntDef({RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED,
        RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED,
        RESTORE_UPLOAD_QR_CODE_BACK_TAPPED,
        RESTORE_ARE_YOUR_SURE_OK_TAPPED,
        RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED,
        RESTORE_PASSWORD_ENTRY_PAGE_VIEWED,
        RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED,
        RESTORE_PASSWORD_DONE_TAPPED})
@Retention(RetentionPolicy.SOURCE)
public @interface RestoreEventCode {

    // Upload qr page
    int RESTORE_UPLOAD_QR_CODE_PAGE_VIEWED = 80000;
    int RESTORE_UPLOAD_QR_CODE_BUTTON_TAPPED = 80001;
    int RESTORE_UPLOAD_QR_CODE_BACK_TAPPED = 80002;
    int RESTORE_ARE_YOUR_SURE_OK_TAPPED = 80003;
    int RESTORE_ARE_YOUR_SURE_CANCEL_TAPPED = 80004;
    // Enter Password page
    int RESTORE_PASSWORD_ENTRY_PAGE_VIEWED = 81000;
    int RESTORE_PASSWORD_ENTRY_PAGE_BACK_TAPPED = 81001;
    // Complete page
    int RESTORE_PASSWORD_DONE_TAPPED = 82000;
}
