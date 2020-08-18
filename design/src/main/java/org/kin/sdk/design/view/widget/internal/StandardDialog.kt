package org.kin.sdk.design.view.widget.internal

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.setupViewExtensions

class StandardDialog(context: Context) : Dialog(context) {
    private val rootLayout: LinearLayout
    private val titleText: PrimaryTextView
    private val descriptionText: SecondaryTextView
    private val errorText: CodeStyleTextView
    private val buttonLayout: LinearLayout

    companion object {
        fun confirm(context: Context, title: String = "", description: String = "", confirmed: (Boolean) -> Unit) {
            val standardDialog =
                StandardDialog(context)

            standardDialog.title = title
            standardDialog.description = description

            with(DialogButton(context)) {
                setText(context.getString(R.string.kin_sdk_button_ok))
                setOnClickListener {
                    standardDialog.dismiss()
                    confirmed(true)
                }

                addTo(standardDialog.buttonLayout)
            }

            with(DialogButton(context)) {
                setText(context.getString(R.string.kin_sdk_button_cancel))
                setOnClickListener {
                    standardDialog.dismiss()
                    confirmed(false)
                }

                addTo(standardDialog.buttonLayout)
            }

            standardDialog.setOnCancelListener {
                confirmed(false)
            }

            standardDialog.show()
        }

        fun error(context: Context, title: String = "", description: String = "", error: String = "") {
            val standardDialog =
                StandardDialog(context)

            standardDialog.title = title
            standardDialog.description = description
            standardDialog.error = error

            with(DialogButton(context)) {
                setText(context.getString(R.string.kin_sdk_button_ok))
                setOnClickListener {
                    standardDialog.dismiss()
                }

                addTo(standardDialog.buttonLayout)
            }

            standardDialog.show()
        }
    }

    init {
        context.setupViewExtensions()
        rootLayout = LinearLayout(context).also { rootLayout ->
            rootLayout.orientation = LinearLayout.VERTICAL

            titleText = with(
                PrimaryTextView(
                    context
                )
            ) {
                setText(title)
                textSize = 22f
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                visibility = GONE

                addTo(rootLayout, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    leftMargin = 16.dip
                    rightMargin = 16.dip
                    topMargin = 16.dip
                    bottomMargin = 8.dip
                    gravity = Gravity.LEFT
                })
            }
            descriptionText = with(
                SecondaryTextView(
                    context
                )
            ) {
                setText(description)
                visibility = GONE

                addTo(rootLayout, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    leftMargin = 16.dip
                    rightMargin = 16.dip
                    topMargin = 8.dip
                    bottomMargin = 8.dip
                    gravity = Gravity.LEFT
                })
            }
            errorText = with(
                CodeStyleTextView(
                    context
                )
            ) {
                setText(error)
                visibility = GONE

                val scrollWrapper = with(
                    MaxHeightScrollView(
                        context
                    )
                ) {
                    maxHeight = 120.dip

                    addTo(rootLayout, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                        leftMargin = 16.dip
                        rightMargin = 16.dip
                        topMargin = 8.dip
                        bottomMargin = 8.dip
                        gravity = Gravity.LEFT
                    })
                }

                addTo(scrollWrapper, WRAP_CONTENT, WRAP_CONTENT)
            }

            buttonLayout = with(LinearLayout(context)) {
                addTo(rootLayout, LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    leftMargin = 8.dip
                    rightMargin = 8.dip
                    topMargin = 8.dip
                    bottomMargin = 8.dip
                    gravity = Gravity.RIGHT
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(rootLayout)
    }

    var title: String = ""
        set(value) {
            field = value

            titleText.visibility = if (value.isEmpty()) GONE else VISIBLE
            titleText.setText(value)
        }

    var description: String = ""
        set(value) {
            field = value

            descriptionText.visibility = if (value.isEmpty()) GONE else VISIBLE
            descriptionText.setText(value)
        }

    var error: String = ""
        set(value) {
            field = value

            errorText.visibility = if (value.isEmpty()) GONE else VISIBLE
            errorText.setText(value)
        }
}
