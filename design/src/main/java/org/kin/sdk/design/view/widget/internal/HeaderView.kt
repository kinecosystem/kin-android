package org.kin.sdk.design.view.widget.internal

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.MeasureSpecTools
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.resolveColor
import java.util.Locale

class HeaderView(context: Context) : FrameLayout(context) {

    private val titleView = with(PrimaryTextView(context)) {
        setTextColor(context.resolveColor(R.color.kin_sdk_secondaryTextColor))

        addTo(this@HeaderView, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(16.dip, 12.dip, 16.dip, 4.dip)
        })
    }

    var title: String = ""
        set(value) {
            titleView.text = value

            field = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(
                widthMeasureSpec
            ), heightMeasureSpec)
    }
}
