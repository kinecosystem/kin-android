package org.kin.sdk.demo.view.tools

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import org.kin.sdk.demo.ApplicationResolver
import org.kin.sdk.demo.R
import org.kin.sdk.demo.di.Resolver
import org.kin.sdk.demo.view.ActivityNavigatorImpl
import org.kin.sdk.demo.view.custom.LoadingSpinnerDialog
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.tools.ViewModel

abstract class BaseActivity<ViewModelType : ViewModel<ArgsType, StateType>, ArgsType, StateType> :
    AppCompatActivity() {

    protected lateinit var viewModel: ViewModelType
    protected lateinit var rootLayout: ViewGroup private set

    protected val navigator: Navigator by lazy { ActivityNavigatorImpl(this) }
    protected val applicationResolver: ApplicationResolver by lazy { application as ApplicationResolver }
    protected val resolver: Resolver by lazy { applicationResolver.resolver }

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
        rootLayout = createView(this)

        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayUseLogoEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val backButtonIcon =
            resources.getDrawable(R.drawable.abc_ic_ab_back_material).mutate().apply {
                setColorFilter(
                    resources.getColor(R.color.secondaryTextColor),
                    PorterDuff.Mode.SRC_ATOP
                )
            }

        supportActionBar?.setHomeAsUpIndicator(backButtonIcon)

        setContentView(rootLayout)
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

    protected open fun createView(context: Context): ViewGroup {
        val rootLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        return rootLayout
    }

    private var spinner: LoadingSpinnerDialog? = null

    private fun isSpinnerShowing(): Boolean = spinner != null

    protected fun showSpinner() {
        runOnUiThread {
            if (!isSpinnerShowing()) {
                spinner = LoadingSpinnerDialog(this)
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

    protected open fun handleBackPressed(): Boolean =
        if (isSpinnerShowing()) {
            hideSpinner()
            true
        } else false

    protected abstract fun onBindView(viewModel: ViewModelType)

    protected abstract fun onStateUpdated(state: StateType)

    protected abstract fun createViewModel(bundle: Bundle): ViewModelType
}
