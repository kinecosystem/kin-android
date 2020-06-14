package org.kin.sdk.demo.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.DrawableRes

class Titlebar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    fun addAction(@DrawableRes iconResource: Int, action: () -> Unit) {

    }
}