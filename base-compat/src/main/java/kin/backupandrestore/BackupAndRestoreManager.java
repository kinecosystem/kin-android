package kin.backupandrestore;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;
import kin.backupandrestore.events.InternalRestoreCallback;
import kin.backupandrestore.exception.BackupAndRestoreException;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;

public final class BackupAndRestoreManager {

    public static final String NETWORK_URL_EXTRA = "networkUrlExtra";
    public static final String NETWORK_PASSPHRASE_EXTRA = "networkPassphraseExtra";
    public static final String APP_ID_EXTRA = "appIdExtra";
    public static final String STORE_KEY_EXTRA = "storeKeyExtra";
    public static final String PUBLIC_ADDRESS_EXTRA = "publicAddressExtra";

    private final CallbackManager callbackManager;
    private final int reqCodeBackup;
    private final int reqCodeRestore;
    private KinClient kinClient;
    private Activity activity;

    public BackupAndRestoreManager(@NonNull final Activity activity, int reqCodeBackup, int reqCodeRestore) {
        Validator.checkNotNull(activity, "activity");
        this.activity = activity;
        this.callbackManager = new CallbackManager(
                new EventDispatcherImpl(new BroadcastManagerImpl(activity)), reqCodeBackup, reqCodeRestore);
        this.reqCodeBackup = reqCodeBackup;
        this.reqCodeRestore = reqCodeRestore;
    }

    public void backup(KinClient kinClient, KinAccount kinAccount) {
        this.kinClient = kinClient;
        new Launcher(activity, kinClient).backupFlow(kinAccount, reqCodeBackup);
    }

    public void restore(KinClient kinClient) {
        this.kinClient = kinClient;
        new Launcher(activity, kinClient).restoreFlow(reqCodeRestore);
    }

    public void registerBackupCallback(@NonNull final BackupCallback backupCallback) {
        Validator.checkNotNull(backupCallback, "backupCallback");
        this.callbackManager.setBackupCallback(backupCallback);
    }

    public void registerRestoreCallback(@NonNull final RestoreCallback restoreCallback) {
        Validator.checkNotNull(restoreCallback, "restoreCallback");
        this.callbackManager.setInternalRestoreCallback(new InternalRestoreCallback() {

            @Override
            public void onSuccess(String publicAddress) {
                // Because we recovered a new account then we need to refresh the kinClient so we create a new one.
                kinClient = new KinClient(activity, kinClient.getEnvironment(), kinClient.getAppId(),
                        kinClient.getStoreKey());
                restoreCallback.onSuccess(kinClient, AccountExtractor.getKinAccount(kinClient, publicAddress));
            }

            @Override
            public void onCancel() {
                restoreCallback.onCancel();
            }

            @Override
            public void onFailure(BackupAndRestoreException throwable) {
                restoreCallback.onFailure(throwable);
            }
        });
    }

//	public void registerBackupEvents(@NonNull final BackupEvents backupEvents) {
//		Validator.checkNotNull(backupEvents, "backupEvents");
//		this.callbackManager.setBackupEvents(backupEvents);
//	}

//	public void registerRestoreEvents(@NonNull final RestoreEvents restoreEvents) {
//		Validator.checkNotNull(restoreEvents, "restoreEvents");
//		this.callbackManager.setRestoreEvents(restoreEvents);
//	}

    public void release() {
        this.callbackManager.unregisterCallbacksAndEvents();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
