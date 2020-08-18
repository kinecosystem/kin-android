package org.kin.sdk.design.view.widget.internal

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.MeasureSpecTools
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.tools.selectableItemBackgroundResource
import org.kin.sdk.design.view.tools.setupViewExtensions

class ActionListItemView(context: Context) : LinearLayout(context) {

    private val textStack = with(LinearLayout(context)) {
        orientation = VERTICAL

        addTo(this@ActionListItemView, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
            gravity = Gravity.CENTER_VERTICAL
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

        addTo(textStack, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(16.dip, 0.dip, 16.dip, 0.dip)
        })
    }

    private val descriptionView = with(
        SecondaryTextView(
            context
        )
    ) {
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
        visibility = GONE

        addTo(textStack, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(16.dip, 0.dip, 16.dip, 0.dip)
        })
    }

    private val statusView = with(
        SecondaryTextView(
            context
        )
    ) {
        textSize = 17f
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        visibility = GONE

        addTo(this@ActionListItemView, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            setMargins(16.dip, 0.dip, 16.dip, 0.dip)
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

    var status: String = ""
        set(value) {
            if (value.isBlank()) {
                statusView.visibility = View.GONE
            } else {
                statusView.visibility = View.VISIBLE
            }
            statusView.text = value
            field = value
        }

    var isDestructive: Boolean = false
        set(value) {
            titleView.setTextColor(if (value) {
                context.resolveColor(R.color.kin_sdk_destructiveColor)
            } else {
                context.resolveColor(R.color.kin_sdk_primaryTextColor)
            })
        }

    var isAdditive: Boolean = false
        set(value) {
            titleView.setTextColor(if (value) {
                context.resolveColor(R.color.kin_sdk_additiveColor)
            } else {
                context.resolveColor(R.color.kin_sdk_primaryTextColor)
            })
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(
                widthMeasureSpec
            ), MeasureSpec.makeMeasureSpec(48.dip, MeasureSpec.EXACTLY))
    }

    init {
        context.setupViewExtensions()
        setBackgroundResource(selectableItemBackgroundResource)
        isClickable = true
    }
}
