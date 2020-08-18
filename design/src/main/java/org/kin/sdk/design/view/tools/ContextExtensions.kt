package org.kin.sdk.design.view.tools

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

/**
 * Resolve a color defined in our theme via attrs. Should be used in almost all cases since it will allow for
 * changes of colours via dark theme automatically (unless you are caching the old value)
 */
fun Context.resolveColorAttr(@AttrRes attr: Int): Int {
    val outValue = TypedValue()

    theme.resolveAttribute(attr, outValue, true)

    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    val colorRes = if (outValue.resourceId != 0) outValue.resourceId else outValue.data
    return ContextCompat.getColor(this, colorRes)
}

fun Context.resolveColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.resolveDrawable(@DrawableRes resId: Int): Drawable? {
    return AppCompatResources.getDrawable(this, resId)
}
