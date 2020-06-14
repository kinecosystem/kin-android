package org.kin.sdk.demo.view.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.ImageViewCompat
import org.kin.sdk.demo.R
import org.kin.sdk.demo.view.tools.LineDrawable
import org.kin.sdk.demo.view.tools.dip
import org.kin.sdk.demo.view.tools.selectableItemBackgroundBorderlessResource

class StandardEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    enum class State {
        NEUTRAL, ERROR, SUCCESS, PROGRESS
    }

    var state: State =
        State.NEUTRAL
        set(value) {
            field = value
            reconfigure()
        }

    enum class RightDrawableVisibility {
        ALWAYS, WHILE_EDITING, WHILE_NOT_EDITING, NEVER;

        companion object {
            @JvmStatic
            fun fromResourceIndex(index: Int): RightDrawableVisibility {
                if (index == 0) {
                    return ALWAYS
                }

                if (index == 1) {
                    return WHILE_EDITING
                }

                if (index == 2) {
                    return WHILE_NOT_EDITING
                }

                if (index == 3) {
                    return NEVER
                }

                return ALWAYS
            }
        }
    }

    var rightDrawableVisibility: RightDrawableVisibility =
        RightDrawableVisibility.ALWAYS
        set(value) {
            field = value
            reconfigureStateDrawable()
        }

    private var stateDrawables = mutableMapOf<State, Drawable>()
    private var stateDrawableTints = mutableMapOf<State, @ColorInt Int>()
    private var stateHelperTexts = mutableMapOf<State, CharSequence>()

    private var showFloatingHintAnimator: AnimatorSet? = null
    private var hideFloatingHintAnimator: AnimatorSet? = null

    private var progressRotationAnimator: ObjectAnimator? = null

    private val floatingHintTextView: AppCompatTextView by lazy {
        AppCompatTextView(context).apply {
            setTextColor(context.resources.getColor(R.color.secondaryTextColor))
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                gravity = Gravity.LEFT
            }
            pivotX = 0f
            pivotY = 0f
        }
    }

    val innerEditText: EditText by lazy {
        EditText(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, WRAP_CONTENT).apply {
                weight = 1f
            }

            setPadding(0, 0, 0, 0)
            layoutParams = layoutParams
            setHintTextColor(context.resources.getColor(R.color.secondaryTextColor))
            setTextColor(context.resources.getColor(R.color.primaryTextColor))
            textSize = 17f
            background = null
            setSingleLine(true)
            imeOptions = EditorInfo.IME_ACTION_DONE
        }

    }

    private val clearImageView: AppCompatImageView by lazy {
        AppCompatImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(16.dip, 16.dip).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
            layoutParams = layoutParams
            setBackgroundResource(selectableItemBackgroundBorderlessResource)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
//            setImageDrawable(layoutConfig.cancelIcon)
        }
    }

    private val stateImageView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(20.dip, 20.dip).apply {
                gravity = Gravity.CENTER_VERTICAL
                leftMargin = 4.dip
            }
        }
    }

    private val helperTextView: AppCompatTextView by lazy {
        AppCompatTextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                topMargin = 2.dip
                gravity = Gravity.RIGHT
            }
            isClickable = false
            textSize = 8f // layoutConfig.helperTextSize
        }
    }

    private val underlineView: View by lazy {
        View(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 2.dip)

//            underlineView.background = LineDrawable(context.resources.getColor(R.color.secondaryTextColor))
        }
    }

    private val innerWrapperViewGroup: LinearLayout by lazy {
        LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                topMargin = 0.dip
                bottomMargin = 0.dip
            }

            minimumHeight = 24.dip

            layoutParams = layoutParams
            orientation = HORIZONTAL

            addView(innerEditText)
            addView(clearImageView)
            addView(stateImageView)

        }
    }

    init {
        orientation = VERTICAL

        addView(floatingHintTextView)
        addView(innerWrapperViewGroup)
        addView(underlineView)
        addView(helperTextView)

        innerEditText.setOnFocusChangeListener { _, _ ->
            reconfigureUnderline()
            reconfigureCancelButton()
            reconfigureStateDrawable()
        }

        innerEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                reconfigureFloatingHintText()
                reconfigureCancelButton()
                reconfigureStateDrawable()
            }
        })

        floatingHintTextView.isClickable = false

        clearImageView.setOnClickListener {
            innerEditText.setText("")
        }

        reconfigure()
    }

    fun setDrawableForState(drawable: Drawable?, state: State) {

        if (drawable != null) {
            stateDrawables[state] = drawable.mutate()
        } else {
            stateDrawables.remove(state)
        }

        if (state == State.PROGRESS) {
            setupRotationAnimation(stateDrawables[state])
        }

        if (state == this.state) {
            reconfigureStateDrawable()
        }
    }

    fun setupRotationAnimation(drawable: Drawable?) {
        progressRotationAnimator?.apply {
            cancel()
        }
        progressRotationAnimator = null

        drawable?.let {
            progressRotationAnimator = ObjectAnimator.ofInt(it, "level", 0, 10000).apply {
                repeatCount = ObjectAnimator.INFINITE
                duration = 1000
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    fun setHelperTextForState(text: CharSequence?, state: State) {
        if (text != null) {
            stateHelperTexts[state] = text
        } else {
            stateHelperTexts.remove(state)
        }

        if (state == this.state) {
            reconfigureHelperText()
        }
    }

    fun setDrawableTintForState(tint: (@ColorInt Int)?, state: State) {
        if (tint != null) {
            stateDrawableTints[state] = tint
        } else {
            stateDrawableTints.remove(state)
        }

        if (state == this.state) {
            reconfigureStateDrawable()
        }
    }

    fun helperText(): CharSequence? {
        return getCurrentStateHelperText()
    }

    fun setHintText(text: CharSequence) {
        innerEditText.hint = text
        floatingHintTextView.text = text
        reconfigureFloatingHintText()
    }

    fun setText(text: CharSequence?) {
        innerEditText.setText(text)
        reconfigureFloatingHintText()
    }

    fun text(): CharSequence? {
        return innerEditText.text
    }

    fun addTextChangedListener(listener: TextWatcher) {
        innerEditText.addTextChangedListener(listener)
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        innerEditText.setOnFocusChangeListener(l)
    }

    /**
     * Checks a supplied inputType flag to see if the input type specified is a password type. This was copy-pasted from the TextView source (where it is
     * private, because why would anyone else ever need to know this?)
     */
    private fun isPasswordInputType(inputType: Int): Boolean {
        val variation = inputType and (EditorInfo.TYPE_MASK_CLASS or EditorInfo.TYPE_MASK_VARIATION)
        return (variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
            || variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            || variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
            || variation == EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
    }

    private fun reconfigure() {
        reconfigureFloatingHintText()
        reconfigureStateDrawable()
        reconfigureHelperText()
        reconfigureCancelButton()
        reconfigureUnderline()
    }

    private fun reconfigureFloatingHintText() {

        if (innerEditText.text.isNullOrEmpty()) {
            if (innerEditText.hasFocus()) {
                hideFloatingHintTextAnimated()
            } else {
                hideFloatingHintText()
            }
        } else {
            if (innerEditText.hasFocus()) {
                showFloatingHintTextAnimated()
            } else {
                showFloatingHintText()
            }
        }
    }

    private fun reconfigureStateDrawable() {
        val drawable = getCurrentStateDrawable()

        stateImageView.apply {
            setImageDrawable(drawable)

            val tint = stateDrawableTints[state]
            if (tint != null) {
                ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(tint));
            } else {
                ImageViewCompat.setImageTintList(this, null)
            }

            visibility = if (drawable == null) {
                GONE
            } else {
                if (rightDrawableVisibility == RightDrawableVisibility.ALWAYS) {
                    VISIBLE
                } else if (rightDrawableVisibility == RightDrawableVisibility.WHILE_EDITING) {
                    if (innerEditText.hasFocus()) {
                        VISIBLE
                    } else {
                        GONE
                    }
                } else if (rightDrawableVisibility == RightDrawableVisibility.WHILE_NOT_EDITING) {
                    if (innerEditText.hasFocus()) {
                        GONE
                    } else {
                        VISIBLE
                    }
                } else if (rightDrawableVisibility == RightDrawableVisibility.NEVER) {
                    GONE
                } else {
                    VISIBLE
                }
            }
        }
    }

    private fun reconfigureHelperText() {
        helperTextView.apply {
            text = getCurrentStateHelperText() ?: ""

            visibility = if (text.isNullOrEmpty()) GONE else VISIBLE

            helperTextView.setTextColor(
//                if (state == State.ERROR) {
//                    layoutConfig.destructiveRed
//                } else {
//                    layoutConfig.hintTextColor
//                }
                context.resources.getColor(R.color.secondaryTextColor)
            )
        }
    }

    private fun reconfigureCancelButton() {
        clearImageView.apply {

            visibility = if (innerEditText.hasFocus() && !innerEditText.text.isNullOrEmpty()) {
                VISIBLE
            } else {
                GONE
            }
        }
    }

    private fun reconfigureUnderline() {
        val lineColor = when {
            state == State.ERROR -> context.resources.getColor(R.color.destructiveColor)
            innerEditText.hasFocus() -> context.resources.getColor(R.color.primaryTextColor)
            else -> context.resources.getColor(R.color.secondaryTextColor)
        }

        underlineView.background =
            LineDrawable(lineColor)
    }

    private fun getCurrentStateDrawable(): Drawable? {
        return stateDrawables[state] ?: stateDrawables[State.NEUTRAL]
    }

    private fun getCurrentStateHelperText(): CharSequence? {
        return stateHelperTexts[state] ?: stateHelperTexts[State.NEUTRAL]
    }

    private fun showFloatingHintTextAnimated() {
        hideFloatingHintAnimator?.cancel()

        if (floatingHintTextView.visibility != View.VISIBLE) {
            val scaleXAnimator = ObjectAnimator.ofFloat(
                floatingHintTextView,
                View.SCALE_X,
                innerEditText.textSize / floatingHintTextView.textSize,
                1f)

            val scaleYAnimator = ObjectAnimator.ofFloat(
                floatingHintTextView,
                View.SCALE_Y,
                innerEditText.textSize / floatingHintTextView.textSize,
                1f)

            val translateYAnimator = ObjectAnimator.ofFloat(
                floatingHintTextView,
                View.TRANSLATION_Y,
                floatingHintTextView.height.toFloat(),
                0f)


            showFloatingHintAnimator = AnimatorSet().apply {
                playTogether(scaleXAnimator, scaleYAnimator, translateYAnimator)
                duration = 100
                interpolator = LinearInterpolator()

                if (!isStarted) {
                    start()
                }
            }
        }

        showFloatingHintText()
    }

    private fun showFloatingHintText() {
        floatingHintTextView.apply {
            visibility = View.VISIBLE
            translationY = 0f
            scaleX = 1f
            scaleY = 1f
        }

        innerEditText.hint = null
    }

    private fun hideFloatingHintTextAnimated() {
        showFloatingHintAnimator?.cancel()

        if (floatingHintTextView.visibility == View.VISIBLE) {
            val scaleXAnimator = ObjectAnimator.ofFloat(
                floatingHintTextView,
                View.SCALE_X,
                1f,
                innerEditText.textSize / floatingHintTextView.textSize)

            val scaleYAnimator = ObjectAnimator.ofFloat(
                floatingHintTextView,
                View.SCALE_Y,
                1f,
                innerEditText.textSize / floatingHintTextView.textSize)

            val translateYAnimator = ObjectAnimator.ofFloat(
                floatingHintTextView,
                View.TRANSLATION_Y,
                0f,
                floatingHintTextView.height.toFloat())


            hideFloatingHintAnimator = AnimatorSet().apply {

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        hideFloatingHintText()
                    }
                })

                playTogether(scaleXAnimator, scaleYAnimator, translateYAnimator)
                duration = 100
                interpolator = LinearInterpolator()

                if (!isStarted) {
                    start()
                }

            }
        }
    }

    private fun hideFloatingHintText() {
        floatingHintTextView.apply {
            visibility = View.INVISIBLE
            scaleX = innerEditText.textSize / floatingHintTextView.textSize
            scaleY = innerEditText.textSize / floatingHintTextView.textSize
            translationY = floatingHintTextView.height.toFloat() + 2.dip
        }

        innerEditText.hint = floatingHintTextView.text
    }
}
