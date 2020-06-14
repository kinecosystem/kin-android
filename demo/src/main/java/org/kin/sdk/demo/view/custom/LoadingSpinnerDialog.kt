package org.kin.sdk.demo.view.custom

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.FrameLayout
import android.widget.ProgressBar
import org.kin.sdk.demo.R
import org.kin.sdk.demo.view.tools.addTo
import org.kin.sdk.demo.view.tools.dip

class LoadingSpinnerDialog(context: Context) : Dialog(context) {
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = FrameLayout(context).also { rootLayout ->
            progressBar = with(ProgressBar(context)) {
                addTo(rootLayout, 32.dip, 32.dip, leftMargin = 16.dip, rightMargin = 16.dip, topMargin = 16.dip, bottomMargin = 16.dip)
            }

            with(PrimaryTextView(context)) {
                setText(context.getString(R.string.title_loading))

                addTo(rootLayout, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    leftMargin = 60.dip
                    rightMargin = 16.dip
                    topMargin = 16.dip
                    bottomMargin = 16.dip
                    gravity = Gravity.CENTER_VERTICAL
                })
            }
        }

        val accentColor = context.resources.getColor(R.color.colorAccent)

        progressBar.indeterminateDrawable.setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY)
        progressBar.isIndeterminate = true

        setCancelable(false)
        setCanceledOnTouchOutside(false)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(rootLayout)
    }
}
