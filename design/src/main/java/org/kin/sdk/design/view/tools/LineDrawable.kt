package org.kin.sdk.design.view.tools

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class LineDrawable(private val lineColor: Int) : Drawable() {
    private val paint = Paint().apply {
        strokeWidth = 1.dip / 2f
        color = lineColor
        style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        canvas.drawLine(
            bounds.left.toFloat(),
            bounds.bottom.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            paint
        )
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setColorFilter(filter: ColorFilter?) {
        paint.colorFilter = filter
    }
}
