package org.kin.sdk.demo.view.custom

import android.content.Context
import android.widget.ScrollView
import org.kin.sdk.demo.view.tools.MeasureSpecTools
import kotlin.math.min

class MaxHeightScrollView(context: Context) : ScrollView(context) {
    var maxHeight: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedHeight = if (MeasureSpec.getSize(heightMeasureSpec) == 0) {
            Int.MAX_VALUE
        } else {
            MeasureSpec.getSize(heightMeasureSpec)
        }

        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(widthMeasureSpec),
            MeasureSpec.makeMeasureSpec(min(maxHeight, requestedHeight), MeasureSpec.AT_MOST)
        )
    }
}
