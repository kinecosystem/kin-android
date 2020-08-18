package org.kin.sdk.design.view.widget

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.MeasureSpecTools
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.tools.resolveDrawable
import org.kin.sdk.design.view.tools.setupViewExtensions

class PrimaryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    attributeSetId: Int = R.attr.buttonStyle
) : AppCompatButton(context, attrs, attributeSetId) {

    init {
        context.setupViewExtensions()
        gravity = Gravity.CENTER

        isAllCaps = false
        textSize = 15f
        typeface = Typeface.create(
            "sans-serif-medium",
            Typeface.NORMAL
        )

        setPadding(12.dip, 0, 12.dip, 0)
        compoundDrawablePadding = 12.dip

        setTextColor(context.resolveColor(R.color.kin_sdk_white))
        background = context.resolveDrawable(R.drawable.primary_button_background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 0f
            stateListAnimator =
                AnimatorInflater.loadStateListAnimator(
                    context,
                    R.animator.primary_button_no_shadow_animator
                )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(widthMeasureSpec),
            MeasureSpec.makeMeasureSpec(54.dip, MeasureSpec.EXACTLY)
        )
    }
}
