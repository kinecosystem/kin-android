package kin.backupandrestore.backup.view;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import kin.backupandrestore.Validator;

public class TextWatcherAdapter implements TextWatcher {

    private final static long DELAY_MILLIS = 500;

    private final Handler mainThreadHandler;
    private final TextChangeListener listener;

    public TextWatcherAdapter(final TextChangeListener textChangeListener) {
        Validator.checkNotNull(textChangeListener, "listener");
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
        this.listener = textChangeListener;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(final Editable editable) {
        removeCallbacks();
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.afterTextChanged(editable);
            }
        }, DELAY_MILLIS);
    }

    private void removeCallbacks() {
        mainThreadHandler.removeCallbacksAndMessages(null);
    }

    public void release() {
        removeCallbacks();
    }

    public interface TextChangeListener {

        void afterTextChanged(Editable editable);
    }
}
