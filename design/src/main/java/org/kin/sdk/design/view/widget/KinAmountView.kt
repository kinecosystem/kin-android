package org.kin.sdk.design.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.tools.resolveDrawable
import org.kin.sdk.design.view.tools.setupViewExtensions
import org.kin.sdk.design.view.tools.tint
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil

class KinAmountView(
    context: Context,
    attrs: AttributeSet? = null,
    attributeSetId: Int = 0
) : LinearLayout(context, attrs, attributeSetId) {

    val innerPrefixTextView: AppCompatTextView = with(
        AppCompatTextView(context)
    ) {
        setTextColor(context.resolveColor(R.color.kin_sdk_primaryTextColor))
        textSize = 17f
        setSingleLine()
        maxLines = 1

        addTo(
            this@KinAmountView,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                1f
            )
        )
    }

    private val iconView: AppCompatImageView = with(
        AppCompatImageView(context)
    ) {
        background = context.resolveDrawable(R.drawable.ic_kin_logo)
            ?.tint(context.resolveColor(R.color.kin_sdk_primaryTextColor))
        visibility = View.INVISIBLE
        addTo(
            this@KinAmountView,
            LayoutParams(
                8.dip,
                8.dip
            ).apply {
                setMargins(0.dip, 3.dip, 5.dip, 2.dip)
            }
        )
    }

    val innerTextView: AppCompatTextView = with(
        AppCompatTextView(context)
    ) {
        setTextColor(context.resolveColor(R.color.kin_sdk_primaryTextColor))
        textSize = 17f
        setSingleLine()
        maxLines = 1

        addTo(
            this@KinAmountView,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                1f
            )
        )
    }
    var prefix: String? = ""
        set(value) {
            innerPrefixTextView.text = value
            field = value
        }

    var amount: BigDecimal = BigDecimal.ZERO
        set(value) {
            field = value
            updateTextView()
        }

    var isRounded: Boolean = false
        set(value) {
            field = value
            updateTextView()
        }

    private fun updateTextView() {
        if (amount == BigDecimal.ZERO) {
            innerTextView.text = ""
            iconView.visibility = View.INVISIBLE
        } else {
            iconView.visibility = View.VISIBLE
            val format = when {
                amount.toDouble() == ceil(amount.toDouble()) -> "%,.0f"
                isRounded -> "%,.2f"
                else -> "%,.${amount.setScale(5, RoundingMode.HALF_UP)
                    .stripTrailingZeros()
                    .scale()}f"
            }
            innerTextView.text = String.format(format, amount.stripTrailingZeros())
        }
    }

    var textSize: Float = 17f
        set(value) {
            innerTextView.textSize = value
            iconView.layoutParams = iconView.layoutParams.apply {
                val size = ceil(value / 2 - 1).toInt().dip
                width = size
                height = size
            }
            field = value
        }

    fun setTextColor(color: Int) {
        innerPrefixTextView.setTextColor(color)
        innerTextView.setTextColor(color)
        iconView.background = iconView.background.tint(color)
    }

    init {
        context.setupViewExtensions()
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        attrs?.apply {
            with(context.theme.obtainStyledAttributes(attrs, R.styleable.KinAmountView, 0, 0)) {
                try {
                    amount = getFloat(R.styleable.KinAmountView_amount, 0f).toBigDecimal()
                    textSize = getFloat(R.styleable.KinAmountView_textSize, textSize)
                    setTextColor(
                        getColor(
                            R.styleable.KinAmountView_textColor,
                            context.resolveColor(R.color.kin_sdk_primaryTextColor)
                        )
                    )
                } finally {
                    recycle()
                }
            }
        }
    }
}
