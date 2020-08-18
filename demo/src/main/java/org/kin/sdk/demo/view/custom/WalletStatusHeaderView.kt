package org.kin.sdk.demo.view.custom

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import org.kin.sdk.demo.R
import org.kin.sdk.design.view.tools.MeasureSpecTools
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.widget.internal.CodeStyleTextView
import org.kin.sdk.design.view.widget.KinAmountView
import java.math.BigDecimal

class WalletStatusHeaderView(context: Context) : LinearLayout(context) {
    private val balanceView = with(
        KinAmountView(context)
    ) {
        textSize = 40f
        innerTextView.typeface = Typeface.create(
            "sans-serif-medium",
            Typeface.NORMAL
        )

        addTo(this@WalletStatusHeaderView, LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16.dip, 24.dip, 16.dip, 8.dip)
            gravity = Gravity.CENTER_HORIZONTAL
        })
    }

    private val addressView = with(
        CodeStyleTextView(
            context
        )
    ) {
        textSize = 19f
        setTextColor(context.resolveColor(R.color.kin_sdk_secondaryTextColor))

        addTo(this@WalletStatusHeaderView, LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16.dip, 0.dip, 16.dip, 24.dip)
            gravity = Gravity.CENTER_HORIZONTAL
        })
    }

    private val addressActions = with(LinearLayout(context)) {
        addTo(this@WalletStatusHeaderView, LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        })
    }

    var balance: BigDecimal? = null
        set(value) {
            if (value != null) {
                balanceView.amount = value
            } else {
                balanceView.innerTextView.text = " "
            }
            field = value
        }

    var address: String = ""
        set(value) {
            val lineLength = value.length / 2
            val displayAddress = IntRange(0, 1).map { value.substring(it * lineLength, (it + 1) * lineLength) }.joinToString("\n")

            addressView.setText(displayAddress)

            field = value
        }

    init {
        orientation = VERTICAL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(
                widthMeasureSpec
            ), heightMeasureSpec)
    }
}
