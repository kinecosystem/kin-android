package org.kin.sdk.design.view.widget

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.MeasureSpecTools
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.tools.resolveDrawable
import org.kin.sdk.design.view.tools.setupViewExtensions
import org.kin.sdk.design.view.tools.tint

class StandardButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    attributeSetId: Int = R.attr.buttonStyle
) : AppCompatButton(context, attrs, attributeSetId) {

    enum class Type(val value: Int) {
        TYPE_POSITIVE(0),
        TYPE_NEGATIVE(1),
        TYPE_INLINE(2)
    }

    var type: Type = Type.TYPE_POSITIVE
        set(value) {
            when (value) {
                Type.TYPE_POSITIVE -> {
                    setTextColor(context.resolveColor(R.color.kin_sdk_white))
                    background = context.resolveDrawable(R.drawable.primary_button_background)
                        ?.tint(context.resolveColor(R.color.kin_sdk_purple))
                }
                Type.TYPE_NEGATIVE -> {
                    setTextColor(context.resolveColor(R.color.kin_sdk_gray_1))
                    background = context.resolveDrawable(R.drawable.primary_button_background)
                        ?.tint(context.resolveColor(R.color.kin_sdk_gray_4))
                }
                Type.TYPE_INLINE -> {
                    setTextColor(context.resolveColor(R.color.kin_sdk_purple))
                    background = context.resolveDrawable(R.drawable.primary_button_background)
                        ?.tint(context.resolveColor(R.color.kin_sdk_white))
                }
            }

            field = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(44.dip, MeasureSpec.EXACTLY)
        )
    }

    init {
        context.setupViewExtensions()

        gravity = Gravity.CENTER
        isAllCaps = false
        textSize = 15f
        typeface = Typeface.create(
            "sans-serif-medium",
            Typeface.NORMAL
        )

        setPadding(16.dip, 0, 16.dip, 0)
        compoundDrawablePadding = 12.dip

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 0f
            stateListAnimator =
                AnimatorInflater.loadStateListAnimator(
                    context,
                    R.animator.primary_button_no_shadow_animator
                )
        }
    }
}
