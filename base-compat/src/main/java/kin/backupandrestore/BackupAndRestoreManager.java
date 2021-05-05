package kin.backupandrestore;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import org.kin.sdk.base.KinAccountContext;
import org.kin.sdk.base.KinEnvironment;
import org.kin.sdk.base.models.Key;
import org.kin.sdk.base.models.KinAccount;
import org.kin.sdk.base.storage.Storage;

import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;
import kin.backupandrestore.events.InternalRestoreCallback;
import kin.backupandrestore.exception.BackupAndRestoreException;

public final class BackupAndRestoreManager {

    public static final String NETWORK_URL_EXTRA = "networkUrlExtra";
    public static final String NETWORK_PASSPHRASE_EXTRA = "networkPassphraseExtra";
    public static final String APP_ID_EXTRA = "appIdExtra";
    public static final String STORE_KEY_EXTRA = "storeKeyExtra";
    public static final String PRIVATE_KEY_EXTRA = "publicAddressExtra";

    private static BackupAndRestoreManager __instance__;

    public static BackupAndRestoreManager instance()  {
        return __instance__;
    }

    private final CallbackManager callbackManager;
    private final int reqCodeBackup;
    private final int reqCodeRestore;
    private Activity activity;
    private KinEnvironment environment;

    public BackupAndRestoreManager(@NonNull final Activity activity, int reqCodeBackup, int reqCodeRestore) {
        Validator.checkNotNull(activity, "activity");
        this.activity = activity;
        this.callbackManager = new CallbackManager(
                new EventDispatcherImpl(new BroadcastManagerImpl(activity)), reqCodeBackup, reqCodeRestore);
        this.reqCodeBackup = reqCodeBackup;
        this.reqCodeRestore = reqCodeRestore;

        __instance__ = this;
    }

    public void backup(KinEnvironment environment, KinAccount.Id kinAccountId) {
        Key.PrivateKey privateKey = ((Key.PrivateKey) environment.getStorage().getAccount(kinAccountId).getKey());
        new Launcher(activity, environment).backupFlow(privateKey, reqCodeBackup);
    }

    public void restore(KinEnvironment environment) {
        this.environment = environment;
        new Launcher(activity, environment).restoreFlow(reqCodeRestore);
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
                restoreCallback.onSuccess(new KinAccountContext.Builder(environment).useExistingAccount(new KinAccount.Id(publicAddress)).build());
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

    public KinEnvironment getEnvironment() {
        return environment;
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
