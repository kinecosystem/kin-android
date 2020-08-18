package org.kin.sdk.design.view.tools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import org.kin.sdk.design.view.widget.internal.LoadingSpinnerDialog
import org.kin.sdk.design.R
import org.kin.sdk.design.di.Resolver
import org.kin.sdk.design.viewmodel.Navigator
import org.kin.sdk.design.viewmodel.tools.ViewModel

abstract class InvisibleBaseActivity<ViewModelType : ViewModel<ArgsType, StateType>, ArgsType, StateType, ResolverType : Resolver, NavigatorType : Navigator> :
    AppCompatActivity() {

    protected lateinit var viewModel: ViewModelType
    protected lateinit var rootLayout: ViewGroup private set

    protected abstract val navigator: NavigatorType
    protected open val resolver: ResolverType by lazy {
        @Suppress("UNCHECKED_CAST")
        (application as ResolverType)
    }

    override fun onBackPressed() {
        if (!handleBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installProviderFromPlayServicesIfNeeded()

        supportActionBar?.setDisplayUseLogoEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val backButtonIcon =
            resolveDrawable(R.drawable.abc_ic_ab_back_material)?.mutate()?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    colorFilter = BlendModeColorFilter( resolveColor(R.color.kin_sdk_secondaryTextColor), BlendMode.SRC_ATOP)
                } else {
                    @Suppress("DEPRECATION")
                    setColorFilter( resolveColor(R.color.kin_sdk_secondaryTextColor), PorterDuff.Mode.SRC_ATOP)
                }
            }

        supportActionBar?.setHomeAsUpIndicator(backButtonIcon)
    }


    override fun onResume() {
        super.onResume()

        viewModel = createViewModel(intent.extras ?: Bundle())

        onBindView(viewModel)

        viewModel.addStateUpdateListener {
            runOnUiThread {
                onStateUpdated(it)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        resolver.activityResultSink?.onNewResult(resultCode, data)
    }

    override fun onPause() {
        super.onPause()

        viewModel.removeStateUpdateListener(this::onStateUpdated)

        viewModel.cleanup()

        if (isSpinnerShowing()) {
            hideSpinner()
        }
    }


    override fun onDestroy() {
        viewModel.removeStateUpdateListener(this::onStateUpdated)

        viewModel.cleanup()

        if (isSpinnerShowing()) {
            hideSpinner()
        }

        super.onDestroy()
    }

    private var spinner: LoadingSpinnerDialog? = null

    private fun isSpinnerShowing(): Boolean = spinner != null

    protected fun showSpinner() {
        runOnUiThread {
            if (!isSpinnerShowing()) {
                spinner =
                    LoadingSpinnerDialog(
                        this
                    )
                spinner?.show()
            }
        }
    }

    protected fun hideSpinner() {
        runOnUiThread {
            if (isSpinnerShowing()) {
                spinner?.dismiss()
                spinner = null
            }
        }
    }

    protected fun openKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        if (inputMethodManager != null) {
            view.requestFocus()
            inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    protected fun closeKeyboard() {
        if (currentFocus != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    protected open fun handleBackPressed(): Boolean =
        if (isSpinnerShowing()) {
            hideSpinner()
            true
        } else false

    protected abstract fun onBindView(viewModel: ViewModelType)

    protected abstract fun onStateUpdated(state: StateType)

    protected abstract fun createViewModel(bundle: Bundle): ViewModelType
}
