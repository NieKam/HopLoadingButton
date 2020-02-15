package pl.com.hop.components

import android.content.res.Resources
import android.graphics.Paint
import android.os.Build

/**
 * Converts dp to pixel
 */
val Int.dpToPx: Float get() = (this * Resources.getSystem().displayMetrics.density)

/**
 * Converts pixel to dp
 */
val Float.pxToDp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Resources.getDefaultStrokeWidth(): Float {
    return getDimension(R.dimen.default_stroke_width)
}

fun Resources.getDefaultColor(): Int {
    return if (Build.VERSION.SDK_INT >= 23) {
        getColor(R.color.default_primary, null)
    } else {
        getColor(R.color.default_primary)
    }
}

fun Resources.getDefaultSecondaryColor(): Int {
    return if (Build.VERSION.SDK_INT >= 23) {
        getColor(R.color.default_secondary, null)
    } else {
        getColor(R.color.default_secondary)
    }
}

fun Paint.initLinePaint(lineWidth: Float, color: Int) {
    this.color = color
    this.style = Paint.Style.STROKE
    this.strokeWidth = lineWidth
    this.isDither = true
    this.strokeJoin = Paint.Join.ROUND
    this.strokeCap = Paint.Cap.ROUND
}