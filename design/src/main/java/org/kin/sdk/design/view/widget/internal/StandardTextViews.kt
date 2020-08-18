package org.kin.sdk.design.view.widget.internal

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.tools.setupViewExtensions

class PrimaryTextView(context: Context) : AppCompatTextView(context) {
    init {
        context.setupViewExtensions()
        textSize = 17f
        setLineSpacing(3f, 1f)
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        setTextColor(context.resolveColor(R.color.kin_sdk_primaryTextColor))
    }
}

class SecondaryTextView(context: Context) : AppCompatTextView(context) {
    init {
        context.setupViewExtensions()
        textSize = 16f
        setLineSpacing(2f, 1f)
        setTextColor(context.resolveColor(R.color.kin_sdk_secondaryTextColor))
    }
}

class TertiaryTextView(context: Context) : AppCompatTextView(context) {
    init {
        context.setupViewExtensions()
        textSize = 12f
        setLineSpacing(2f, 1f)
        typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
        setTextColor(context.resolveColor(R.color.kin_sdk_tertiaryTextColor))
    }
}

class CodeStyleTextView(context: Context) : AppCompatTextView(context) {
    init {
        context.setupViewExtensions()
        setTextColor(context.resolveColor(R.color.kin_sdk_primaryTextColor))
        typeface = Typeface.MONOSPACE
    }
}

