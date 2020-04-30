package kin.backupandrestore.backup.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kin.backupandrestore.AccountExtractor;
import kin.backupandrestore.BackupAndRestoreManager;
import org.kin.base.compat.R;
import kin.backupandrestore.backup.presenter.BackupPresenter;
import kin.backupandrestore.backup.presenter.BackupPresenterImpl;
import kin.backupandrestore.base.BaseToolbarActivity;
import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;
import kin.sdk.Environment;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;

public class BackupActivity extends BaseToolbarActivity implements BackupView {

    public static final String MOVE_TO_SAVE_AND_SHARE = "move_to_save_and_share";
    public static final int TOOLBAR_COLOR_ANIM_DURATION = 500;
    public static final String TAG_WELL_DONE_PAGE = WellDoneBackupFragment.class.getSimpleName();
    public static final String TAG_SAVE_AND_SHARE_PAGE = SaveAndShareFragment.class.getSimpleName();
    public static final String TAG_CREATE_PASSWORD_PAGE = CreatePasswordFragment.class.getSimpleName();
    private BackupPresenter backupPresenter;

    @Override
    protected int getContentLayout() {
        return R.layout.backup_and_restore_frgment_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KinAccount kinAccount = getKinAccountFromClient();
        backupPresenter = new BackupPresenterImpl(
                new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(this))), kinAccount,
                savedInstanceState);
        backupPresenter.onAttach(this);
        setNavigationClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backupPresenter.onBackClicked();
            }
        });
    }

    @Nullable
    private KinAccount getKinAccountFromClient() {
        KinAccount kinAccount = null;
        Intent intent = getIntent();
        if (intent != null) {
            KinClient kinClient = getKinClientFromIntent(intent);
            String publicAddress = intent.getStringExtra(BackupAndRestoreManager.PUBLIC_ADDRESS_EXTRA);
            kinAccount = AccountExtractor.getKinAccount(kinClient, publicAddress);
        }
        return kinAccount;
    }

    @NonNull
    private KinClient getKinClientFromIntent(Intent intent) {
        String networkUrl = intent.getStringExtra(BackupAndRestoreManager.NETWORK_URL_EXTRA);
        String networkPassphrase = intent.getStringExtra(BackupAndRestoreManager.NETWORK_PASSPHRASE_EXTRA);
        String appId = intent.getStringExtra(BackupAndRestoreManager.APP_ID_EXTRA);
        String storeKey = intent.getStringExtra(BackupAndRestoreManager.STORE_KEY_EXTRA);
        return new KinClient(getApplicationContext(), new Environment(networkUrl,
                networkPassphrase), appId, storeKey);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        backupPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void startBackupFlow() {
        setToolbarColor(android.R.color.white);
        Drawable vectorDrawable = AppCompatResources.getDrawable(this, R.drawable.back);
        setNavigationIcon(vectorDrawable);
        setToolbarTitle(EMPTY_TITLE);
        BackupInfoFragment backupInfoFragment = (BackupInfoFragment) getSupportFragmentManager()
                .findFragmentByTag(BackupInfoFragment.class.getSimpleName());

        if (backupInfoFragment == null) {
            backupInfoFragment = BackupInfoFragment.newInstance(backupPresenter);
        } else {
            backupInfoFragment.setNextStepListener(backupPresenter);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, backupInfoFragment)
                .commit();
    }

    @Override
    public void moveToCreatePasswordPage() {
        setToolbarColorWithAnim(android.R.color.white, TOOLBAR_COLOR_ANIM_DURATION);
        setNavigationIcon(R.drawable.back);
        setToolbarTitle(R.string.backup_and_restore_create_password);
        setStep(1, 2);
        CreatePasswordFragment createPasswordFragment = getSavedCreatePasswordFragment();

        if (createPasswordFragment == null) {
            createPasswordFragment = CreatePasswordFragment
                    .newInstance(backupPresenter, this, backupPresenter.getKinAccount());
        } else {
            setCreatePasswordFragmentAttributes(createPasswordFragment);
        }

        replaceFragment(createPasswordFragment, null, TAG_CREATE_PASSWORD_PAGE);
    }

    @Override
    public void moveToSaveAndSharePage(String key) {
        setNavigationIcon(R.drawable.back);
        setToolbarTitle(R.string.backup_and_restore_my_kin_wallet_qr_code);
        setStep(2, 2);
        backupPresenter.setAccountKey(key);
        SaveAndShareFragment saveAndShareFragment = (SaveAndShareFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_SAVE_AND_SHARE_PAGE);

        if (saveAndShareFragment == null) {
            saveAndShareFragment = SaveAndShareFragment.newInstance(backupPresenter, key);
            replaceFragment(saveAndShareFragment, MOVE_TO_SAVE_AND_SHARE, TAG_SAVE_AND_SHARE_PAGE);
        } else {
            saveAndShareFragment.setNextStepListener(backupPresenter);
            // We should not add to back stack because it's already in stack.
            replaceFragment(saveAndShareFragment, null, TAG_SAVE_AND_SHARE_PAGE);
        }
    }

    @Override
    public void moveToWellDonePage() {
        setToolbarColorWithAnim(android.R.color.white, TOOLBAR_COLOR_ANIM_DURATION);
        setNavigationIcon(R.drawable.close);
        setToolbarTitle(EMPTY_TITLE);
        clearSteps();
        WellDoneBackupFragment wellDoneFragment = (WellDoneBackupFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_WELL_DONE_PAGE);

        if (wellDoneFragment == null) {
            wellDoneFragment = WellDoneBackupFragment.newInstance();
        }

        replaceFragment(wellDoneFragment, null, TAG_WELL_DONE_PAGE);
    }

    private void replaceFragment(Fragment backupFragment, @Nullable String backStackName, @NonNull String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.backup_and_restore_slide_in_right,
                        R.anim.backup_and_restore_slide_out_left,
                        R.anim.backup_and_restore_slide_in_left,
                        R.anim.backup_and_restore_slide_out_right)
                .replace(R.id.fragment_frame, backupFragment, tag);

        if (backStackName != null) {
            transaction.addToBackStack(backStackName);
        }
        transaction.commit();
    }

    @Override
    public void close() {
        closeKeyboard(); // Verify the keyboard is hidden
        finish();
        overridePendingTransition(0, R.anim.backup_and_restore_slide_out_right);
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.backup_and_restore_something_went_wrong_title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        backupPresenter.onBackClicked();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeKeyboard(); // Verify the keyboard is hidden
    }

    @Override
    public void onBackButtonClicked() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count >= 1) {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
            if (entry.getName().equals(MOVE_TO_SAVE_AND_SHARE)) {
                // After pressing back from SaveAndShareFragment, should put the attrs again.
                // Because this is the only fragment that should be in stack.
                CreatePasswordFragment createPasswordFragment = getSavedCreatePasswordFragment();
                if (createPasswordFragment != null) {
                    setCreatePasswordFragmentAttributes(createPasswordFragment);
                }
            }
        }
        super.onBackPressed();
        if (count == 0) {
            closeKeyboard(); // Verify the keyboard is hidden
            overridePendingTransition(0, R.anim.backup_and_restore_slide_out_right);
        }
    }

    private void setCreatePasswordFragmentAttributes(CreatePasswordFragment createPasswordFragment) {
        createPasswordFragment.setNextStepListener(backupPresenter);
        createPasswordFragment.setKeyboardHandler(this);
        createPasswordFragment.setKinAccount(backupPresenter.getKinAccount());
    }

    private CreatePasswordFragment getSavedCreatePasswordFragment() {
        return (CreatePasswordFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_CREATE_PASSWORD_PAGE);
    }
}
