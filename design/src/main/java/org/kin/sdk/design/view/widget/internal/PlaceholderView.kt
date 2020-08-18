package org.kin.sdk.design.view.widget.internal

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.MeasureSpecTools
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.resolveColor

class PlaceholderView(context: Context) : FrameLayout(context) {

    private val titleView = with(AppCompatTextView(context)) {
        setTextColor(context.resolveColor(R.color.kin_sdk_secondaryTextColor))

        gravity = Gravity.CENTER_HORIZONTAL

        addTo(this@PlaceholderView, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(30.dip, 12.dip, 30.dip, 12.dip)
        })
    }

    var title: String = ""
        set(value) {
            titleView.setText(value)

            field = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(
                widthMeasureSpec
            ), heightMeasureSpec)
    }
}
