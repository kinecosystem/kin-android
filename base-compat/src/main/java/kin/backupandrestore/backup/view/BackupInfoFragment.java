package kin.backupandrestore.backup.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.kin.base.compat.R;
import kin.backupandrestore.backup.presenter.BackupInfoPresenter;
import kin.backupandrestore.backup.presenter.BackupInfoPresenterImpl;
import kin.backupandrestore.base.BaseView;
import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;

public class BackupInfoFragment extends Fragment implements BaseView {

    public static BackupInfoFragment newInstance(@NonNull final BackupNavigator nextStepListener) {
        BackupInfoFragment fragment = new BackupInfoFragment();
        fragment.setNextStepListener(nextStepListener);
        return fragment;
    }

    private BackupNavigator nextStepListener;
    private BackupInfoPresenter backupInfoPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.backup_and_restore_fragment_backup_info, container, false);
        initViews(root);
        backupInfoPresenter = new BackupInfoPresenterImpl(
                new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(getActivity()))), nextStepListener);
        backupInfoPresenter.onAttach(this);
        return root;
    }

    private void initViews(View root) {
        root.findViewById(R.id.lets_go_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backupInfoPresenter.letsGoButtonClicked();
            }
        });
    }

    public void setNextStepListener(@NonNull final BackupNavigator nextStepListener) {
        this.nextStepListener = nextStepListener;
    }
}
