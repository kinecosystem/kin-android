package org.kin.sdk.demo.view.custom

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import org.kin.sdk.demo.R
import org.kin.sdk.demo.view.tools.MeasureSpecTools
import org.kin.sdk.demo.view.tools.addTo
import org.kin.sdk.demo.view.tools.dip
import java.math.BigDecimal

class WalletStatusHeaderView(context: Context) : LinearLayout(context) {
    private val balanceView = with(
        PrimaryTextView(
            context
        )
    ) {
        textSize = 40f
        typeface = Typeface.create(
            "sans-serif-medium",
            Typeface.NORMAL
        )

        addTo(this@WalletStatusHeaderView, LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(12.dip, 24.dip, 12.dip, 8.dip)
            gravity = Gravity.CENTER_HORIZONTAL
        })
    }

    private val addressView = with(
        CodeStyleTextView(
            context
        )
    ) {
        textSize = 19f
        setTextColor(resources.getColor(R.color.secondaryTextColor))

        addTo(this@WalletStatusHeaderView, LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(12.dip, 0.dip, 12.dip, 24.dip)
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
            balanceView.setText(value?.let { "${value.toDouble()} KIN" } ?: " ")
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