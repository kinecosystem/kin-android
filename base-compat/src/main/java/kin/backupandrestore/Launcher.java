package kin.backupandrestore;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import org.kin.base.compat.R;
import org.kin.sdk.base.KinEnvironment;
import org.kin.sdk.base.models.Key;
import org.kin.sdk.base.storage.Storage;

import kin.backupandrestore.backup.view.BackupActivity;
import kin.backupandrestore.restore.view.RestoreActivity;

import static kin.backupandrestore.BackupAndRestoreManager.PRIVATE_KEY_EXTRA;
import static kin.backupandrestore.BackupAndRestoreManager.STORE_KEY_EXTRA;

class Launcher {

    private final Activity activity;
    private final KinEnvironment environment;

    Launcher(@NonNull final Activity activity, @NonNull final KinEnvironment environment) {
        this.activity = activity;
        this.environment = environment;
    }

    void backupFlow(Key.PrivateKey privateKey, int reqCodeBackup) {
        Intent intent = new Intent(activity, BackupActivity.class);
//        addKinClientExtras(intent);
        intent.putExtra(PRIVATE_KEY_EXTRA, privateKey.stellarBase32Encode());
        startForResult(intent, reqCodeBackup);
    }

    void restoreFlow(int reqCodeRestore) {
        Intent intent = new Intent(activity, RestoreActivity.class);
//        addKinClientExtras(intent);
        startForResult(intent, reqCodeRestore);
    }

//    private void addKinClientExtras(Intent intent) {
//        intent.putExtra(NETWORK_URL_EXTRA, kinClient.getEnvironment().getNetworkUrl());
//        intent.putExtra(NETWORK_PASSPHRASE_EXTRA, kinClient.getEnvironment().getNetworkPassphrase());
//        intent.putExtra(APP_ID_EXTRA, kinClient.getAppId());
//        intent.putExtra(STORE_KEY_EXTRA, storage.getStoreKey());
//    }

    private void startForResult(@NonNull final Intent intent, final int reqCode) {
        activity.startActivityForResult(intent, reqCode);
        activity.overridePendingTransition(R.anim.backup_and_restore_slide_in_right,
                R.anim.backup_and_restore_slide_out_left);
    }
}
