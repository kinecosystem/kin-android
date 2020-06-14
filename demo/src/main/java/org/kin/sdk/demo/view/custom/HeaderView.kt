package org.kin.sdk.demo.view.custom

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import org.kin.sdk.demo.R
import org.kin.sdk.demo.view.tools.MeasureSpecTools
import org.kin.sdk.demo.view.tools.addTo
import org.kin.sdk.demo.view.tools.dip

class HeaderView(context: Context) : FrameLayout(context) {

    private val titleView = with(AppCompatTextView(context)) {
        setTextColor(resources.getColor(R.color.secondaryTextColor))

        addTo(this@HeaderView, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(12.dip, 12.dip, 12.dip, 4.dip)
        })
    }

    var title: String = ""
        set(value) {
            titleView.setText(value.toUpperCase())

            field = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(
                widthMeasureSpec
            ), heightMeasureSpec)
    }
}
