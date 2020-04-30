package kin.backupandrestore;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import org.kin.base.compat.R;

import kin.backupandrestore.backup.view.BackupActivity;
import kin.backupandrestore.restore.view.RestoreActivity;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;

import static kin.backupandrestore.BackupAndRestoreManager.APP_ID_EXTRA;
import static kin.backupandrestore.BackupAndRestoreManager.NETWORK_PASSPHRASE_EXTRA;
import static kin.backupandrestore.BackupAndRestoreManager.NETWORK_URL_EXTRA;
import static kin.backupandrestore.BackupAndRestoreManager.PUBLIC_ADDRESS_EXTRA;
import static kin.backupandrestore.BackupAndRestoreManager.STORE_KEY_EXTRA;

class Launcher {

    private final Activity activity;
    private final KinClient kinClient;

    Launcher(@NonNull final Activity activity, @NonNull final KinClient kinClient) {
        this.activity = activity;
        this.kinClient = kinClient;
    }

    void backupFlow(KinAccount kinAccount, int reqCodeBackup) {
        Intent intent = new Intent(activity, BackupActivity.class);
        addKinClientExtras(intent);
        intent.putExtra(PUBLIC_ADDRESS_EXTRA, kinAccount.getPublicAddress());
        startForResult(intent, reqCodeBackup);
    }

    void restoreFlow(int reqCodeRestore) {
        Intent intent = new Intent(activity, RestoreActivity.class);
        addKinClientExtras(intent);
        startForResult(intent, reqCodeRestore);
    }

    private void addKinClientExtras(Intent intent) {
        intent.putExtra(NETWORK_URL_EXTRA, kinClient.getEnvironment().getNetworkUrl());
        intent.putExtra(NETWORK_PASSPHRASE_EXTRA, kinClient.getEnvironment().getNetworkPassphrase());
        intent.putExtra(APP_ID_EXTRA, kinClient.getAppId());
        intent.putExtra(STORE_KEY_EXTRA, kinClient.getStoreKey());
    }

    private void startForResult(@NonNull final Intent intent, final int reqCode) {
        activity.startActivityForResult(intent, reqCode);
        activity.overridePendingTransition(R.anim.backup_and_restore_slide_in_right,
                R.anim.backup_and_restore_slide_out_left);
    }
}
