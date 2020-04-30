package kin.backupandrestore.events;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class BroadcastManagerImpl implements BroadcastManager {

    private final Activity activity;
    private final List<BroadcastReceiver> receiversList;

    static final String ACTION_EVENTS_BACKUP = "ACTION_EVENTS_BACKUP";
    static final String ACTION_EVENTS_RESTORE = "ACTION_EVENTS_RESTORE";

    @StringDef({ACTION_EVENTS_BACKUP, ACTION_EVENTS_RESTORE})
    @Retention(RetentionPolicy.SOURCE)
    @interface ActionName {

    }

    public BroadcastManagerImpl(@NonNull Activity activity) {
        this.activity = activity;
        this.receiversList = new ArrayList<>();
    }

    @Override
    public void register(@NonNull final Listener listener, @ActionName final String actionName) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent data) {
                listener.onReceive(data);
            }
        };
        receiversList.add(broadcastReceiver);
        LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver, new IntentFilter(actionName));
    }

    @Override
    public void unregisterAll() {
        if (!receiversList.isEmpty()) {
            for (BroadcastReceiver receiver : receiversList) {
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
            }
        }
    }

    @Override
    public void sendEvent(Intent data, @ActionName final String actionName) {
        data.setAction(actionName);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(data);
    }

    @Override
    public void setActivityResult(int resultCode, Intent data) {
        activity.setResult(resultCode, data);
    }
}
