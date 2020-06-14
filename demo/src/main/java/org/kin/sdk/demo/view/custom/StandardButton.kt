package org.kin.sdk.demo.view.custom

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import org.kin.sdk.demo.R
import org.kin.sdk.demo.view.tools.dip

class StandardButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    attributeSetId: Int = R.attr.buttonStyle
) : AppCompatButton(context, attrs, attributeSetId) {

    init {
        gravity = Gravity.CENTER

        isAllCaps = true
        textSize = 15f
        typeface = Typeface.create(
            "sans-serif-medium",
            Typeface.NORMAL
        )

        setPadding(12.dip, 0, 12.dip, 0)
        compoundDrawablePadding = 12.dip

        setTextColor(context.resources.getColor(R.color.fullWhite))
        background =
            AppCompatResources.getDrawable(
                context,
                R.drawable.primary_button_background
            )

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