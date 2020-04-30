package kin.backupandrestore.base;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.kin.base.compat.R;


public abstract class BaseToolbarActivity extends AppCompatActivity implements KeyboardHandler {

    public static final int EMPTY_TITLE = -1;
    public static final String BACKGROUND_COLOR = "background_color";

    protected abstract @LayoutRes
    int getContentLayout();

    private Toolbar topToolBar;
    private TextView stepsText;
    private ValueAnimator colorAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayout());
        setupToolbar(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveToolbarBackgroundColor(outState);
        super.onSaveInstanceState(outState);

    }

    private void saveToolbarBackgroundColor(Bundle outState) {
        ColorDrawable colorDrawable = ((ColorDrawable) topToolBar.getBackground());
        if (colorDrawable != null) {
            outState.putInt(BACKGROUND_COLOR, colorDrawable.getColor());
        }
    }

    private void setupToolbar(Bundle savedInstanceState) {
        topToolBar = findViewById(R.id.toolbar);
        int color = getColorFromBundle(savedInstanceState);
        topToolBar.setBackgroundColor(color);
        setSupportActionBar(topToolBar);
    }

    private int getColorFromBundle(Bundle savedInstanceState) {
        final int defaultColor = ContextCompat.getColor(getApplicationContext(), android.R.color.white);
        return savedInstanceState != null ? savedInstanceState
                .getInt(BACKGROUND_COLOR, defaultColor) : defaultColor;
    }

    public void setToolbarTitle(@StringRes int titleRes) {
        topToolBar = findViewById(R.id.toolbar);
        stepsText = findViewById(R.id.steps_text);
        if (titleRes != EMPTY_TITLE) {
            getSupportActionBar().setTitle(titleRes);
        } else {
            getSupportActionBar().setTitle("");
        }
    }

    public void setNavigationIcon(@DrawableRes int iconRes) {
        topToolBar.setNavigationIcon(iconRes);
    }

    public void setNavigationIcon(Drawable drawable) {
        topToolBar.setNavigationIcon(drawable);
    }

    public void hideNavigationIcon() {
        topToolBar.setNavigationIcon(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setToolbarColor(@ColorRes int colorRes) {
        stopToolbarColorAnim();
        topToolBar.setBackgroundResource(colorRes);
    }

    private void stopToolbarColorAnim() {
        if (colorAnimation != null) {
            colorAnimation.cancel();
        }
    }

    public void setToolbarColorWithAnim(@ColorRes int toColorRes, final int durationMilis) {
        int colorFrom = ((ColorDrawable) topToolBar.getBackground()).getColor();
        int colorTo = ContextCompat.getColor(getApplicationContext(), toColorRes);
        colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(durationMilis); // milliseconds
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                topToolBar.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        colorAnimation.start();
    }

    public void setStep(int current, int total) {
        stepsText.setText(getString(R.string.backup_and_restore_steps_format, current, total));
    }

    public void clearSteps() {
        stepsText.setText("");
    }

    public void setNavigationClickListener(View.OnClickListener clickListener) {
        topToolBar.setNavigationOnClickListener(clickListener);
    }

    @Override
    public void openKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            view.requestFocus();
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void closeKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
