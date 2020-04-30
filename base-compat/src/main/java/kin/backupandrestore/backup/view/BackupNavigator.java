package kin.backupandrestore.backup.view;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface BackupNavigator {

    int STEP_START = 0x00000000;
    int STEP_CREATE_PASSWORD = 0x00000001;
    int STEP_SAVE_AND_SHARE = 0x00000002;
    int STEP_WELL_DONE = 0x00000003;
    int STEP_CLOSE = 0x00000004;


    @IntDef({STEP_START, STEP_CREATE_PASSWORD, STEP_SAVE_AND_SHARE, STEP_WELL_DONE, STEP_CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Step {

    }

    void navigateToCreatePasswordPage();

    void navigateToSaveAndSharePage(@NonNull String accountKey);

    void navigateToWellDonePage();

    void closeFlow();

}
