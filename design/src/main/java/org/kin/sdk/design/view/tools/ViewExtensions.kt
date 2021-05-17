package org.kin.sdk.design.view.tools

import android.R
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import java.math.BigInteger
import javax.net.ssl.SSLContext

fun <ViewType : View> ViewType.addTo(
    group: ViewGroup,
    params: ViewGroup.LayoutParams
): ViewType = also {
    group.addView(it, params)
}

fun <ViewType : View> ViewType.addTo(
    group: ViewGroup,
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    topMargin: Int = 0,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    bottomMargin: Int = 0
): ViewType = addTo(group,
    when (group) {
        is FrameLayout -> FrameLayout.LayoutParams(width, height)
        is LinearLayout -> LinearLayout.LayoutParams(width, height)
        is RelativeLayout -> RelativeLayout.LayoutParams(width, height)
        else -> ViewGroup.MarginLayoutParams(width, height)
    }.apply {
        setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
    }
)

val View.selectableItemBackgroundResource: Int
    get() {
        val outValue = TypedValue()

        context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
        return outValue.resourceId
    }

val View.selectableItemBackgroundBorderlessResource: Int
    get() {
        val outValue = TypedValue()

        context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)
        return outValue.resourceId
    }

internal var dipConversionRate: Float = 0f

fun Context.setupViewExtensions() {
    if (dipConversionRate == 0f) {
        dipConversionRate = resources.displayMetrics.density + 0.5f
    }
}

val Int.dip: Int
    get() {
        return (this * dipConversionRate).toInt()
    }

object MeasureSpecTools {
    fun makeMaxWidthMeasureSpec(widthMeasureSpec: Int): Int {
        val availableWidth = if (View.MeasureSpec.getSize(widthMeasureSpec) <= 0) {
            Resources.getSystem().displayMetrics.widthPixels
        } else {
            View.MeasureSpec.getSize(widthMeasureSpec)
        }

        return View.MeasureSpec.makeMeasureSpec(availableWidth, View.MeasureSpec.EXACTLY)
    }
}

fun EditText.addIntegerChangedListener(onChanged: (BigInteger) -> Unit) {
    filters = arrayOf(object : InputFilter {
        override fun filter(
            source: CharSequence, start: Int,
            end: Int, dest: Spanned?, dstart: Int, dend: Int
        ): CharSequence? {
            try {
                BigInteger(source.toString())
            } catch (ex: Exception) {
                return ""
            }

            return null
        }
    })

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(text: Editable?) {
            onChanged(text?.toString()?.let {
                try {
                    BigInteger(it)
                } catch (ex: Exception) {
                    null
                }
            } ?: BigInteger.ZERO)
        }

        override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    })
}

fun EditText.addBase32ChangedListener(onChanged: (String) -> Unit) {
    val validCharacters = setOf(*"ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray().toTypedArray())

    filters = arrayOf(object : InputFilter {
        override fun filter(
            source: CharSequence, start: Int,
            end: Int, dest: Spanned?, dstart: Int, dend: Int
        ): CharSequence? {
            return source.filter { validCharacters.contains(it) }
        }
    })

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(text: Editable?) {
            onChanged(text?.toString() ?: "")
        }

        override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    })
}

fun EditText.addBase58ChangedListener(onChanged: (String) -> Unit) {
    val validCharacters = setOf(*"123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray().toTypedArray())

    filters = arrayOf(object : InputFilter {
        override fun filter(
            source: CharSequence, start: Int,
            end: Int, dest: Spanned?, dstart: Int, dend: Int
        ): CharSequence? {
            return source.filter { validCharacters.contains(it) }
        }
    })

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(text: Editable?) {
            onChanged(text?.toString() ?: "")
        }

        override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    })
}

fun View.fadeIn() {
    alpha = 0f
    animate().alpha(1f)
        .setDuration(300)
        .setInterpolator(LinearInterpolator())
        .start()
    visibility = View.VISIBLE
}

fun View.fadeOut() {
    animate().alpha(0f)
        .setDuration(300)
        .setInterpolator(LinearInterpolator())
        .start()
    visibility = View.INVISIBLE
}

fun fadeInViews(vararg views: View) = views.forEach {
    it.fadeIn()
}

fun fadeOutViews(vararg views: View) = views.forEach {
    it.fadeOut()
}

fun Activity.installProviderFromPlayServicesIfNeeded() {
    try {
        SSLContext.getInstance("TLSv1.2")
        ProviderInstaller.installIfNeeded(this)
    } catch (e: GooglePlayServicesRepairableException) {
        // Thrown when Google Play Services is not installed, up-to-date, or enabled
        // Show dialog to allow users to install, update, or otherwise enable Google Play services.
        GoogleApiAvailability.getInstance()
            .getErrorDialog(
                this,
                e.connectionStatusCode,
                0
            )
    } catch (e: GooglePlayServicesNotAvailableException) {
        Log.e("SecurityException", "Google Play Services not available.")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun frameLayout(context: Context, layout: (FrameLayout).() -> Unit) =
    with(FrameLayout(context)) {
        layout(this)
    }

fun FrameLayout.hang(
    view: View,
    gravity: Int,
    layoutParams: (FrameLayout.LayoutParams).() -> Unit = {}
) = with(view) {
    addTo(
        this@hang,
        FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            gravity
        ).apply {
            layoutParams(this)
        }
    )
}

fun FrameLayout.hangStart(
    view: View,
    layoutParams: (FrameLayout.LayoutParams).() -> Unit = {}
) = hang(view, Gravity.START, layoutParams)

fun FrameLayout.hangEnd(
    view: View,
    layoutParams: (FrameLayout.LayoutParams).() -> Unit = {}
) = hang(view, Gravity.END, layoutParams)
