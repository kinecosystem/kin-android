package kin.backupandrestore.restore.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import org.kin.base.compat.R;
import kin.backupandrestore.base.BaseToolbarActivity;
import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;
import kin.backupandrestore.qr.QRBarcodeGeneratorImpl;
import kin.backupandrestore.qr.QRFileUriHandlerImpl;
import kin.backupandrestore.restore.presenter.FileSharingHelper;
import kin.backupandrestore.restore.presenter.UploadQRPresenter;
import kin.backupandrestore.restore.presenter.UploadQRPresenterImpl;
import kin.backupandrestore.utils.ViewUtils;

import static kin.backupandrestore.base.BaseToolbarActivity.EMPTY_TITLE;


public class UploadQRFragment extends Fragment implements UploadQRView {

    private UploadQRPresenter presenter;

    public static UploadQRFragment newInstance() {
        return new UploadQRFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.backup_and_restore_fragment_upload_qr, container, false);

        injectPresenter();
        presenter.onAttach(this, ((RestoreActivity) getActivity()).getPresenter());

        initToolbar();
        initViews(root);
        return root;
    }

    private void injectPresenter() {
        presenter = new UploadQRPresenterImpl(
                new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(getActivity()))),
                new FileSharingHelper(this),
                new QRBarcodeGeneratorImpl(new QRFileUriHandlerImpl(getContext())));
    }

    private void initViews(View root) {
        Group btnUploadGroup = root.findViewById(R.id.btn_group);
        ViewUtils.registerToGroupOnClickListener(btnUploadGroup, root, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.uploadClicked();
            }
        });
    }

    private void initToolbar() {
        BaseToolbarActivity toolbarActivity = (BaseToolbarActivity) getActivity();
        toolbarActivity.setNavigationIcon(R.drawable.back);
        toolbarActivity.setToolbarColor(android.R.color.white);
        toolbarActivity.setToolbarTitle(EMPTY_TITLE);
        toolbarActivity.setNavigationClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackClicked();
            }
        });
    }

    @Override
    public void showConsentDialog() {
        int leftRightPadding = (int) getResources().getDimension(R.dimen.backup_and_restore_dialog_left_right_padding);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.BackupAndRestoreAlertDialogTheme)
                .setCustomTitle(getDialogTitle(leftRightPadding))
                .setMessage(R.string.backup_and_restore_consent_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String chooserTitle = getString(R.string.backup_and_restore_choose_qr_image);
                        presenter.onOkPressed(chooserTitle);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onCancelPressed();
                    }
                })
                .create();
        alertDialog.show();
        setDialogMessage(leftRightPadding, alertDialog);
    }

    private TextView getDialogTitle(int leftRightPadding) {
        TextView titleTxtView = new TextView(getActivity());
        titleTxtView.setTextAppearance(getActivity(), R.style.BackupAndRestoreAlertDialogTitleText);
        titleTxtView.setText(R.string.backup_and_restore_restore_consent_title);
        int titleTopPadding = (int) getResources().getDimension(R.dimen.backup_and_restore_dialog_title_top_padding);
        titleTxtView.setPadding(leftRightPadding, titleTopPadding, leftRightPadding, 0);
        return titleTxtView;
    }

    private void setDialogMessage(int leftRightPadding, AlertDialog alertDialog) {
        TextView messageTextView = alertDialog.findViewById(android.R.id.message);
        if (messageTextView != null) {
            messageTextView.setTextAppearance(getActivity(), R.style.BackupAndRestoreAlertDialogMessageText);
            messageTextView.setText(R.string.backup_and_restore_consent_message);
            int messageTopPadding = (int) getResources()
                    .getDimension(R.dimen.backup_and_restore_dialog_message_top_padding);
            messageTextView.setPadding(leftRightPadding, messageTopPadding, leftRightPadding, 0);
        }
    }

    @Override
    public void showErrorDecodingQRDialog() {
        Toast.makeText(getContext(), R.string.backup_and_restore_error_decoding_QR, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorLoadingFileDialog() {
        Toast.makeText(getContext(), R.string.backup_and_restore_loading_file_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        presenter.onActivityResult(requestCode, resultCode, data);
    }
}
