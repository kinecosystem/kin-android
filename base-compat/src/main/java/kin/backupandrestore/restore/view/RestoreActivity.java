package kin.backupandrestore.restore.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.kin.base.compat.R;

import kin.backupandrestore.base.BaseToolbarActivity;
import kin.backupandrestore.events.BroadcastManagerImpl;
import kin.backupandrestore.events.CallbackManager;
import kin.backupandrestore.events.EventDispatcherImpl;
import kin.backupandrestore.restore.presenter.RestorePresenter;
import kin.backupandrestore.restore.presenter.RestorePresenterImpl;

public class RestoreActivity extends BaseToolbarActivity implements RestoreView {

    private RestorePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RestorePresenterImpl(
                new CallbackManager(new EventDispatcherImpl(new BroadcastManagerImpl(this))),
                savedInstanceState);
        presenter.onAttach(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        presenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeKeyboard();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.backup_and_restore_frgment_activity;
    }

    @Override
    public void navigateToUpload() {
        final String fragmentName = UploadQRFragment.class.getSimpleName();
        UploadQRFragment fragment = (UploadQRFragment) getSupportFragmentManager()
                .findFragmentByTag(fragmentName);

        if (fragment == null) {
            fragment = UploadQRFragment.newInstance();
            replaceFragment(fragment, fragmentName, fragmentName, false);
        } else {
            // We should not add to back stack because it's already in stack.
            replaceFragment(fragment, null, fragmentName, false);
        }
    }

    @Override
    public void navigateToEnterPassword(String keystoreData) {
        final String fragmentName = RestoreEnterPasswordFragment.class.getSimpleName();
        RestoreEnterPasswordFragment fragment = getSavedRestoreEnterPasswordFragment();

        if (fragment == null) {
            fragment = RestoreEnterPasswordFragment.newInstance(keystoreData, this);
            replaceFragment(fragment, fragmentName, fragmentName, true);
        } else {
            fragment.setKeyboardHandler(this);
            // We should not add to back stack because it's already in stack.
            replaceFragment(fragment, null, fragmentName, true);
        }
    }

    @Override
    public void navigateToRestoreCompleted() {
        final String fragmentName = RestoreCompletedFragment.class.getSimpleName();
        RestoreCompletedFragment fragment = (RestoreCompletedFragment) getSupportFragmentManager()
                .findFragmentByTag(fragmentName);

        if (fragment == null) {
            fragment = RestoreCompletedFragment.newInstance();
            replaceFragment(fragment, fragmentName, fragmentName, true);
        } else {
            // We should not add to back stack because it's already in stack.
            replaceFragment(fragment, null, fragmentName, true);
        }
    }

    private void replaceFragment(Fragment fragment, String backStackName, String tag, boolean addAnimation) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        if (backStackName != null) {
            transaction.addToBackStack(backStackName);
        }

        if (addAnimation) {
            transaction.setCustomAnimations(
                    0,
                    0,
                    R.anim.backup_and_restore_slide_in_left,
                    R.anim.backup_and_restore_slide_out_right);
        }

        transaction.replace(R.id.fragment_frame, fragment, tag).commit();
    }

    @Override
    public void navigateBack() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count >= 1) {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
            if (entry.getName().equals(RestoreEnterPasswordFragment.class.getSimpleName())) {
                // After pressing back from RestoreCompletedPage, should put the attrs again.
                // This is the only fragment that should set arguments again on back.
                RestoreEnterPasswordFragment enterPasswordFragment = getSavedRestoreEnterPasswordFragment();
                if (enterPasswordFragment != null) {
                    enterPasswordFragment.setKeyboardHandler(this);
                }
            } else if (entry.getName().equals(RestoreCompletedFragment.class.getSimpleName())) {
                presenter.closeFlow();
                return;
            }
        }
        super.onBackPressed();
    }

    private RestoreEnterPasswordFragment getSavedRestoreEnterPasswordFragment() {
        return (RestoreEnterPasswordFragment) getSupportFragmentManager()
                .findFragmentByTag(RestoreEnterPasswordFragment.class.getSimpleName());
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

    public RestorePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onBackPressed() {
        presenter.previousStep();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }
}
