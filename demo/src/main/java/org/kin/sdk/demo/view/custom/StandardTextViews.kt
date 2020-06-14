package org.kin.sdk.demo.view.custom

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import org.kin.sdk.demo.R

class PrimaryTextView(context: Context) : AppCompatTextView(context) {
    init {
        textSize = 17f
        setTextColor(resources.getColor(R.color.primaryTextColor))
    }
}

class SecondaryTextView(context: Context) : AppCompatTextView(context) {
    init {
        textSize = 16f
        setTextColor(resources.getColor(R.color.secondaryTextColor))
    }
}

class CodeStyleTextView(context: Context) : AppCompatTextView(context) {
    init {
        setTextColor(resources.getColor(R.color.primaryTextColor))
        typeface = Typeface.MONOSPACE
    }
}
