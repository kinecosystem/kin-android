package org.kin.sdk.design.view.tools

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

fun Drawable.tint(@ColorInt color: Int): Drawable = DrawableCompat.wrap(mutate()).apply {
    DrawableCompat.setTint(this, color)
    DrawableCompat.setTintMode(this, PorterDuff.Mode.SRC_IN)
}
