package kin.backupandrestore.restore.view;


import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.kin.base.compat.R;
import org.kin.sdk.base.tools.BackupRestoreImpl;

import kin.backupandrestore.backup.view.TextWatcherAdapter;
import kin.backupandrestore.backup.view.TextWatcherAdapter.TextChangeListener;
import kin.backupandrestore.base.BaseToolbarActivity;
import kin.backupandrestore.base.KeyboardHandler;
import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;
import kin.backupandrestore.restore.presenter.RestoreEnterPasswordPresenter;
import kin.backupandrestore.restore.presenter.RestoreEnterPasswordPresenterImpl;
import kin.backupandrestore.widget.PasswordEditText;

import static kin.backupandrestore.restore.presenter.RestorePresenterImpl.KEY_ACCOUNT_KEY;


public class RestoreEnterPasswordFragment extends Fragment implements RestoreEnterPasswordView {

    public static final int VIEW_MIN_DELAY_MILLIS = 50;

    private RestoreEnterPasswordPresenter presenter;
    private KeyboardHandler keyboardHandler;
    private Button doneBtn;
    private TextView contentText;
    private PasswordEditText password;
    private TextWatcherAdapter textWatcherAdapter;

    public static RestoreEnterPasswordFragment newInstance(String keystoreData,
                                                           @NonNull KeyboardHandler keyboardHandler) {
        RestoreEnterPasswordFragment fragment = new RestoreEnterPasswordFragment();
        fragment.setKeyboardHandler(keyboardHandler);
        if (keystoreData != null) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_ACCOUNT_KEY, keystoreData);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public void setKeyboardHandler(@NonNull KeyboardHandler keyboardHandler) {
        this.keyboardHandler = keyboardHandler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.backup_and_restore_fragment_password_restore, container, false);
        initToolbar();
        initViews(root);

        String keystoreData = extractKeyStoreData(savedInstanceState);
        injectPresenter(keystoreData);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        presenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    private String extractKeyStoreData(@Nullable Bundle savedInstanceState) {
        String keystoreData;
        Bundle bundle = savedInstanceState != null ? savedInstanceState : getArguments();
        if (bundle == null) {
            throw new IllegalStateException("Bundle is null, can't extract required keystore data.");
        }
        keystoreData = bundle.getString(KEY_ACCOUNT_KEY);
        if (keystoreData == null) {
            throw new IllegalStateException("Can't find keystore data inside Bundle.");
        }
        return keystoreData;
    }

    private void injectPresenter(String keystoreData) {
        presenter = new RestoreEnterPasswordPresenterImpl(
                new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(getActivity()))),
                keystoreData,
                new BackupRestoreImpl()
        );
        presenter.onAttach(this, ((RestoreActivity) getActivity()).getPresenter());
    }

    private void initToolbar() {
        BaseToolbarActivity toolbarActivity = (BaseToolbarActivity) getActivity();
        toolbarActivity.setNavigationIcon(R.drawable.back);
        toolbarActivity.setToolbarColor(android.R.color.white);
        toolbarActivity.setToolbarTitle(R.string.backup_and_restore_upload_qr_toolbar_title);
        toolbarActivity.setNavigationClickListener(v -> presenter.onBackClicked());
    }

    private void initViews(View root) {
        password = root.findViewById(R.id.kinrecovery_password_edit);
        contentText = root.findViewById(R.id.kinrecovery_password_recovery_text);
        doneBtn = root.findViewById(R.id.kinrecovery_password_done_btn);
        doneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.restoreClicked(password.getText());
            }
        });
        textWatcherAdapter = new TextWatcherAdapter(editable -> presenter.onPasswordChanged(editable.toString()));

        password.addTextChangedListener(textWatcherAdapter);
        password.setFrameBackgroundColor(R.color.backup_and_restore_black);
        password.postDelayed(new Runnable() {
            @Override
            public void run() {
                openKeyboard(password);
            }
        }, VIEW_MIN_DELAY_MILLIS);
    }

    private void openKeyboard(View view) {
        keyboardHandler.openKeyboard(view);
    }

    @Override
    public void enableDoneButton() {
        doneBtn.setEnabled(true);
        doneBtn.setClickable(true);
    }

    @Override
    public void disableDoneButton() {
        doneBtn.setEnabled(false);
        doneBtn.setClickable(false);
        password.setFrameBackgroundColor(R.color.backup_and_restore_black);
    }

    @Override
    public void decodeError() {
        contentText.setText(R.string.backup_and_restore_restore_password_error);
        contentText.setTextColor(ContextCompat.getColor(getContext(), R.color.backup_and_restore_red));
        password.setFrameBackgroundColor(R.color.backup_and_restore_red);
        password.setTextColor(R.color.backup_and_restore_gray);
    }

    @Override
    public void invalidQrError() {
        contentText.setText(R.string.backup_and_restore_restore_invalid_qr);
        contentText.setTextColor(ContextCompat.getColor(getContext(), R.color.backup_and_restore_red));
        password.setFrameBackgroundColor(R.color.backup_and_restore_red);
        password.setTextColor(R.color.backup_and_restore_gray);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        textWatcherAdapter.release();
    }
}
