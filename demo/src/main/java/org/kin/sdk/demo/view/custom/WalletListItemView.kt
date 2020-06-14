package org.kin.sdk.demo.view.custom

import android.content.Context
import android.text.TextUtils
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import org.kin.sdk.demo.view.tools.MeasureSpecTools
import org.kin.sdk.demo.view.tools.addTo
import org.kin.sdk.demo.view.tools.dip
import org.kin.sdk.demo.view.tools.selectableItemBackgroundResource

class WalletListItemView(context: Context) : LinearLayout(context) {

    private val walletAddressView = with(
        CodeStyleTextView(
            context
        )
    ) {
        textSize = 17f
        setSingleLine()
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END

        addTo(this@WalletListItemView, LinearLayout.LayoutParams(0, WRAP_CONTENT, 1f).apply {
            setMargins(12.dip, 12.dip, 12.dip, 12.dip)
        })
    }

    var address: String = ""
        set(value) {
            walletAddressView.text = value
            field = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpecTools.makeMaxWidthMeasureSpec(
                widthMeasureSpec
            ), heightMeasureSpec)
    }

    init {
        setBackgroundResource(selectableItemBackgroundResource)
        isClickable = true
    }
}
