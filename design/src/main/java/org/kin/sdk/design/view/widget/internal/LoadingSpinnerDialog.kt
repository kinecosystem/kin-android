package org.kin.sdk.design.view.widget.internal

import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.tools.setupViewExtensions

class LoadingSpinnerDialog(context: Context) : Dialog(context) {
    private lateinit var progressBar: ProgressBar

    init {
        context.setupViewExtensions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = FrameLayout(context).also { rootLayout ->
            progressBar = with(ProgressBar(context)) {
                addTo(rootLayout, 32.dip, 32.dip, leftMargin = 16.dip, rightMargin = 16.dip, topMargin = 16.dip, bottomMargin = 16.dip)
            }

            with(PrimaryTextView(context)) {
                setText(context.getString(R.string.kin_sdk_title_loading))

                addTo(rootLayout, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    leftMargin = 60.dip
                    rightMargin = 16.dip
                    topMargin = 16.dip
                    bottomMargin = 16.dip
                    gravity = Gravity.CENTER_VERTICAL
                })
            }
        }

        val accentColor = context.resolveColor(R.color.kin_sdk_purple)

        with(progressBar.indeterminateDrawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(accentColor, BlendModeCompat.SRC_ATOP)
            } else {
                @Suppress("DEPRECATION")
                setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY)
            }
        }
        progressBar.isIndeterminate = true

        setCancelable(false)
        setCanceledOnTouchOutside(false)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(rootLayout)
    }
}
