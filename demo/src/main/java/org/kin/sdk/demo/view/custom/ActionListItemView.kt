package org.kin.sdk.demo.view.custom

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import org.kin.sdk.demo.R
import org.kin.sdk.demo.view.tools.MeasureSpecTools
import org.kin.sdk.demo.view.tools.addTo
import org.kin.sdk.demo.view.tools.dip
import org.kin.sdk.demo.view.tools.selectableItemBackgroundResource

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
            setMargins(12.dip, 0.dip, 12.dip, 0.dip)
        })
    }

    private val descriptionView = with(
        SecondaryTextView(
            context
        )
    ) {
        textSize = 14f
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
        visibility = GONE

        addTo(textStack, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            setMargins(12.dip, 0.dip, 12.dip, 0.dip)
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
            setMargins(12.dip, 0.dip, 12.dip, 0.dip)
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
                resources.getColor(R.color.destructiveColor)
            } else {
                resources.getColor(R.color.primaryTextColor)
            })
        }

    var isAdditive: Boolean = false
        set(value) {
            titleView.setTextColor(if (value) {
                resources.getColor(R.color.additiveColor)
            } else {
                resources.getColor(R.color.primaryTextColor)
            })
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(
                widthMeasureSpec
            ), MeasureSpec.makeMeasureSpec(48.dip, MeasureSpec.EXACTLY))
    }

    init {
        setBackgroundResource(selectableItemBackgroundResource)
        isClickable = true
    }
}
