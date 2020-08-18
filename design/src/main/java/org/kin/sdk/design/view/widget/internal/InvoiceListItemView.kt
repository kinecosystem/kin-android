package org.kin.sdk.design.view.widget.internal

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.selectableItemBackgroundResource
import org.kin.sdk.design.view.widget.KinAmountView
import java.math.BigDecimal

class InvoiceListItemView(context: Context) : LinearLayout(context) {

    private val textStack = with(LinearLayout(context)) {
        orientation = VERTICAL

        addTo(this@InvoiceListItemView, LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
            setMargins(0, 4.dip, 0, 4.dip)
        })
    }

    private val titleView = with(
        PrimaryTextView(
            context
        )
    ) {
        setSingleLine()
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END

        addTo(textStack, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16.dip, 0.dip, 16.dip, 0.dip)
        })
    }

    private val descriptionView = with(
        SecondaryTextView(
            context
        )
    ) {
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        visibility = GONE

        addTo(textStack, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(16.dip, 0.dip, 16.dip, 0.dip)
        })
    }

    private val amountView = with(
        KinAmountView(context)
    ) {
        innerTextView.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        innerTextView.setSingleLine()
        innerTextView.maxLines = 1

        addTo(this@InvoiceListItemView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0.dip, 0.dip, 16.dip, 0.dip)
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
        })
    }

    var title: String = ""
        set(value) {
            titleView.text = value
            field = value
        }

    var description: String = ""
        set(value) {
            if (value.isBlank()) {
                descriptionView.visibility = View.GONE
            } else {
                descriptionView.visibility = View.VISIBLE
            }
            descriptionView.text = value
            field = value
        }

    var amount: BigDecimal = BigDecimal.ONE
        set(value) {
            amountView.amount = value
            field = value
        }

    init {
        orientation = HORIZONTAL
        setBackgroundResource(selectableItemBackgroundResource)
        isClickable = true
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

