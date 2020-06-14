package org.kin.sdk.demo.view.custom

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import org.kin.sdk.demo.R
import org.kin.sdk.demo.view.tools.MeasureSpecTools
import org.kin.sdk.demo.view.tools.addTo
import org.kin.sdk.demo.view.tools.dip

class PlaceholderView(context: Context) : FrameLayout(context) {

    private val titleView = with(AppCompatTextView(context)) {
        setTextColor(resources.getColor(R.color.secondaryTextColor))

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
