package org.kin.sdk.design.view.widget

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import org.kin.sdk.design.R
import org.kin.sdk.design.view.tools.RecyclerViewTools
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.build
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.tools.frameLayout
import org.kin.sdk.design.view.tools.hang
import org.kin.sdk.design.view.tools.hangEnd
import org.kin.sdk.design.view.tools.hangStart
import org.kin.sdk.design.view.tools.resolveColor
import org.kin.sdk.design.view.tools.resolveDrawable
import org.kin.sdk.design.view.tools.tint
import org.kin.sdk.design.view.tools.updateItems
import org.kin.sdk.design.view.widget.internal.LineItemListItemView
import org.kin.sdk.design.view.widget.internal.PrimaryTextView
import org.kin.sdk.design.view.widget.internal.SecondaryTextView
import org.kin.sdk.design.view.widget.internal.VerticalRecyclerView
import org.kin.sdk.design.viewmodel.structs.RenderableInvoice

class InvoiceRenderer(
    context: Context,
    attrs: AttributeSet? = null,
    attributeSetId: Int = 0
) : LinearLayout(context, attrs, attributeSetId) {

    init {
        orientation = VERTICAL
    }

    private val rootLayout = this

    private val items = with(
        VerticalRecyclerView(
            context
        )
    ) {
        build {
            layout<LineItemListItemView, RenderableInvoice.RenderableLineItem> {
                create(::LineItemListItemView)

                bind { view, viewModel ->
                    view.title = viewModel.title
                    view.description = viewModel.description
                    view.amount = viewModel.amount
                    view.setOnClickListener { }
                }
            }
        }

        addTo(
            rootLayout, LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
    }
    private val subtotalView: KinAmountView
    private val feeView: KinAmountView
    private val totalView: KinAmountView


    val totalsCard = with(LinearLayout(context)) {
        orientation = VERTICAL
        val card = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = 15.dip.toFloat()
            translationZ
            background = context.resolveDrawable(R.drawable.bg_bottom_sheet_dialog_fragment)
                ?.tint(context.resolveColor(R.color.kin_sdk_white))

        }
        subtotalView = KinAmountView(context)
            .apply {
                innerTextView.textSize = 17f
                isRounded = true
            }

        frameLayout(context) {
            gravity = Gravity.CENTER_VERTICAL
            hang(PrimaryTextView(context).apply {
                text = context.getString(R.string.subtotal)
                typeface = Typeface.create("sans-serif", Typeface.BOLD)
                textSize = 17f
            }, Gravity.START or Gravity.CENTER_VERTICAL)
            hangEnd(subtotalView)
            addTo(
                card,
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16.dip, 30.dip, 16.dip, 0.dip)
                }
            )
        }

        feeView = KinAmountView(context).apply {
            innerTextView.textSize = 17f
            setTextColor(context.resolveColor(R.color.kin_sdk_tertiaryTextColor))
        }

        frameLayout(context) {
            hang(SecondaryTextView(context).apply {
                text = context.getString(R.string.fee)
                textSize = 17f
                setTextColor(context.resolveColor(R.color.kin_sdk_tertiaryTextColor))
            }, Gravity.START or Gravity.CENTER_VERTICAL)
            hangEnd(feeView)
            addTo(
                card,
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16.dip, 10.dip, 16.dip, 0.dip)
                }
            )
        }

        totalView = KinAmountView(context)
            .apply {
                innerTextView.textSize = 28f
            }

        frameLayout(context) {
            gravity = Gravity.CENTER_VERTICAL
            hang(PrimaryTextView(context).apply {
                text = context.getString(R.string.total)
                typeface = Typeface.create("sans-serif", Typeface.BOLD)
            }, Gravity.START or Gravity.CENTER_VERTICAL)
            hangEnd(totalView)
            addTo(
                card,
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16.dip, 20.dip, 16.dip, 20.dip)
                }
            )
        }

        addTo(rootLayout)
    }

    var invoice: RenderableInvoice? = null
        set(value) {
            if (value != null) {
                items.updateItems(
                    listOf(
                        RecyclerViewTools.header(R.string.title_items),
                        *value.items.toTypedArray()
                    )
                )
                subtotalView.amount = value.subTotal
                feeView.amount = value.fee
                totalView.amount = value.total

            } else items.updateItems(emptyList<Any>())
            field = value
        }
}
