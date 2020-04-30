package kin.backupandrestore.backup.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.kin.base.compat.R;
import kin.backupandrestore.backup.presenter.SaveAndSharePresenter;
import kin.backupandrestore.backup.presenter.SaveAndSharePresenterImpl;
import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;
import kin.backupandrestore.qr.QRBarcodeGenerator;
import kin.backupandrestore.qr.QRBarcodeGeneratorImpl;
import kin.backupandrestore.qr.QRFileUriHandlerImpl;
import kin.backupandrestore.utils.ViewUtils;

import static kin.backupandrestore.backup.presenter.BackupPresenterImpl.KEY_ACCOUNT_KEY;

public class SaveAndShareFragment extends Fragment implements SaveAndShareView {

    public static SaveAndShareFragment newInstance(BackupNavigator listener, String key) {
        SaveAndShareFragment fragment = new SaveAndShareFragment();
        fragment.setNextStepListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACCOUNT_KEY, key);
        fragment.setArguments(bundle);
        return fragment;
    }

    private BackupNavigator nextStepListener;
    private SaveAndSharePresenter saveAndSharePresenter;

    private CheckBox iHaveSavedCheckbox;
    private TextView iHaveSavedText;
    private ImageView qrImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.backup_and_restore_fragment_save_and_share_qr, container, false);
        initViews(root);
        String key = getArguments().getString(KEY_ACCOUNT_KEY, null);
        final QRBarcodeGenerator qrBarcodeGenerator = new QRBarcodeGeneratorImpl(
                new QRFileUriHandlerImpl(getContext()));
        saveAndSharePresenter = new SaveAndSharePresenterImpl(
                new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(getActivity()))), nextStepListener,
                qrBarcodeGenerator, key, savedInstanceState);
        saveAndSharePresenter.onAttach(this);
        return root;
    }

    private void initViews(View root) {
        iHaveSavedCheckbox = root.findViewById(R.id.i_saved_my_qr_checkbox);
        iHaveSavedText = root.findViewById(R.id.i_saved_my_qr_text);
        qrImageView = root.findViewById(R.id.qr_image);

        iHaveSavedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveAndSharePresenter.iHaveSavedChecked(isChecked);
            }
        });
        iHaveSavedText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                iHaveSavedCheckbox.performClick();
            }
        });

        root.findViewById(R.id.send_email_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndSharePresenter.sendQREmailClicked();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveAndSharePresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showIHaveSavedCheckBox() {
        iHaveSavedCheckbox.setVisibility(View.VISIBLE);
        iHaveSavedText.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorTryAgainLater() {
        new AlertDialog.Builder(getActivity(), R.style.BackupAndRestoreAlertDialogTheme)
                .setTitle(R.string.backup_and_restore_something_went_wrong_title)
                .setMessage(R.string.backup_and_restore_could_not_load_the_qr_please_try_again_later)
                .setNegativeButton(R.string.backup_and_restore_cancel, null)
                .create()
                .show();
    }

    public void setNextStepListener(@NonNull final BackupNavigator nextStepListener) {
        this.nextStepListener = nextStepListener;
    }

    @Override
    public void setQRImage(Uri qrURI) {
        Bitmap qrBitmap;
        try {
            qrBitmap = Media.getBitmap(getContext().getContentResolver(), qrURI);
            qrImageView.setImageBitmap(qrBitmap);
        } catch (IOException e) {
            saveAndSharePresenter.couldNotLoadQRImage();
        }
    }

    @Override
    public void showSendIntent(Uri qrURI) {
        String appName = ViewUtils.getApplicationName(getContext());
        String subject = getString(R.string.your_app_qr_code_subject, appName);
        String body = getString(R.string.your_app_qr_code_body, appName);
        String backupCreated = getString(R.string.backup_and_restore_backup_created_on);
        Date date = Calendar.getInstance(TimeZone.getDefault()).getTime();
        String dateString = SimpleDateFormat.getDateInstance().format(date);
        SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm");
        String time = timeFormat.format(date);

        StringBuilder bodyBuilder = new StringBuilder(body).append("\n")
                .append(backupCreated).append(" ").append(dateString).append(" | ").append(time);

        Intent emailIntent = new Intent(Intent.ACTION_SEND)
                .setType("Image/*")
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_STREAM, qrURI)
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, bodyBuilder.toString());
        startActivity(Intent.createChooser(emailIntent, getString(R.string.backup_and_restore_send_email)));
    }
}
