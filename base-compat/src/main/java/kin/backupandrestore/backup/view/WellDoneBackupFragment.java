package kin.backupandrestore.backup.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import org.kin.base.compat.R;

import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;

import static kin.backupandrestore.events.BackupEventCode.BACKUP_COMPLETED_PAGE_VIEWED;

public class WellDoneBackupFragment extends Fragment {

    public static WellDoneBackupFragment newInstance() {
        return new WellDoneBackupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.backup_and_restore_fragment_well_done_backup, container, false);
        final CallbackManager callbackManager = new CallbackManager(
                new EventDispatcherImpl(new BroadcastManagerImpl(getActivity())));
        callbackManager.sendBackupEvent(BACKUP_COMPLETED_PAGE_VIEWED);
        return root;
    }
}
