package org.kin.sdk.demo.view.custom

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import org.kin.sdk.demo.view.tools.MeasureSpecTools
import org.kin.sdk.demo.view.tools.addTo
import org.kin.sdk.demo.view.tools.dip
import org.kin.sdk.demo.view.tools.selectableItemBackgroundResource
import java.math.BigDecimal

class HistoryListItemView(context: Context) : LinearLayout(context) {

    init {
        orientation = HORIZONTAL
    }

    private val leftWrapper = with(LinearLayout(context)) {
        orientation = VERTICAL
        addTo(this@HistoryListItemView, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
            setMargins(12.dip, 8.dip, 12.dip, 8.dip)
        })
    }

    private val walletAddressView = with(
        CodeStyleTextView(
            context
        )
    ) {
        textSize = 17f
        setSingleLine()
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END

        addTo(leftWrapper, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
    }

    private val memoView = with(
        SecondaryTextView(
            context
        )
    ) {
        textSize = 15f
        setSingleLine()
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END

        addTo(leftWrapper, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
    }

    private val amountView = with(
        PrimaryTextView(
            context
        )
    ) {
        textSize = 17f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        setSingleLine()
        maxLines = 1

        addTo(this@HistoryListItemView, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            setMargins(0.dip, 0.dip, 8.dip, 0.dip)
            gravity = Gravity.CENTER_VERTICAL
        })
    }

    var address: String = ""
        set(value) {
            walletAddressView.text = value
            field = value
        }

    var memo: String = ""
        set(value) {
            memoView.text = value
            field = value
        }

    var amount: BigDecimal = BigDecimal.ONE
        set(value) {
            val prefix = if (value.signum() > 0) {
                "+"
            } else {
                ""
            }

            amountView.setText("$prefix$value")
            field = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(
                widthMeasureSpec
            ), heightMeasureSpec)
    }

    init {
        setBackgroundResource(selectableItemBackgroundResource)
        isClickable = true
    }
}
