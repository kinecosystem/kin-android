package org.kin.sdk.design.showcase

import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.mproberts.museum.annotations.Exhibit
import org.kin.sdk.design.view.tools.addTo
import org.kin.sdk.design.view.tools.dip
import org.kin.sdk.design.view.widget.InvoiceRenderer
import org.kin.sdk.design.view.widget.KinAmountView
import org.kin.sdk.design.view.widget.PrimaryButton
import org.kin.sdk.design.view.widget.StandardButton
import org.kin.sdk.design.viewmodel.structs.RenderableInvoice
import java.math.BigDecimal

@Exhibit(
    "PrimaryButton",
    "Buttons",
    ["Button", "Primary"],
    "Used to take forward actions typically in a full screen view"
)
fun primaryActionButtonView(parent: ViewGroup) = with(PrimaryButton(parent.context)) {
    text = "Pay Now"

    addTo(parent, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(12.dip, 12.dip, 12.dip, 12.dip)
    })
}

@Exhibit(
    "StandardButton (Positive)",
    "Buttons",
    ["Button", "Positive", "Action"],
    "with Positive Action: Used to take forward actions typically in a modal screen."
)
fun positiveActionButtonView(parent: ViewGroup) = with(StandardButton(parent.context)) {
    text = "Confirm"
    type = StandardButton.Type.TYPE_POSITIVE

    addTo(parent, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(12.dip, 12.dip, 12.dip, 12.dip)
    })
}

@Exhibit(
    "StandardButton (Negative)",
    "Buttons",
    ["Button", "Negative", "Action"],
    "with Negative Action: Used to take reverse/cancelling actions typically in a modal screen."
)
fun negativeActionButtonView(parent: ViewGroup) = with(StandardButton(parent.context)) {
    text = "Cancel"
    type = StandardButton.Type.TYPE_NEGATIVE

    addTo(parent, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(12.dip, 12.dip, 12.dip, 12.dip)
    })
}

@Exhibit(
    "StandardButton (Inline)",
    "Buttons",
    ["Button", "Inline", "Action"],
    ""
)
fun inlineButtonView(parent: ViewGroup) = with(StandardButton(parent.context)) {
    text = "Cancel"
    type = StandardButton.Type.TYPE_INLINE

    addTo(parent, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(12.dip, 12.dip, 12.dip, 12.dip)
    })
}

@Exhibit(
    "KinAmountView (Positive)",
    "TextView",
    ["TextView", "Positive", "Kin"],
    ""
)
fun kinPositiveAmountView(parent: ViewGroup) = with(LinearLayout(parent.context)) {
    val holder = this
    holder.orientation = LinearLayout.VERTICAL

    with(KinAmountView(parent.context)) {
        amount = BigDecimal(200000)

        addTo(holder, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4.dip, 4.dip, 4.dip, 4.dip)
        })
    }

    with(KinAmountView(parent.context)) {
        amount = BigDecimal(200000.12345)

        addTo(holder, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4.dip, 4.dip, 4.dip, 4.dip)
        })
    }

    with(KinAmountView(parent.context)) {
        amount = BigDecimal(200000.12345)
        isRounded = true

        addTo(holder, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4.dip, 4.dip, 4.dip, 4.dip)
        })
    }

    addTo(parent, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(12.dip, 12.dip, 12.dip, 12.dip)
    })
}

@Exhibit(
    "KinAmountView (Negative)",
    "TextView",
    ["TextView", "Negative", "Kin"],
    ""
)
fun kinNegativeAmountView(parent: ViewGroup) = with(LinearLayout(parent.context)) {
    val holder = this
    holder.orientation = LinearLayout.VERTICAL

    with(KinAmountView(parent.context)) {
        amount = BigDecimal(-200000)

        addTo(holder, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4.dip, 4.dip, 4.dip, 4.dip)
        })
    }

    with(KinAmountView(parent.context)) {
        amount = BigDecimal(-200000.12345)

        addTo(holder, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4.dip, 4.dip, 4.dip, 4.dip)
        })
    }

    with(KinAmountView(parent.context)) {
        amount = BigDecimal(-200000.12345)
        isRounded = true

        addTo(holder, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(4.dip, 4.dip, 4.dip, 4.dip)
        })
    }

    addTo(parent, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(12.dip, 12.dip, 12.dip, 12.dip)
    })
}

@Exhibit(
    "InvoiceRenderer",
    "Composite",
    ["Invoice", "Composite", "Renderer"],
    "A composite view component to showcase the details of an Invoice"
)
fun invoiceRenderer(parent: ViewGroup) = with(InvoiceRenderer(parent.context)) {
    invoice = RenderableInvoice(
        listOf(
            RenderableInvoice.RenderableLineItem(
                "First Item",
                "Lorem ipsum one line description",
                BigDecimal.TEN
            ),
            RenderableInvoice.RenderableLineItem(
                "Second Item",
                "Lorem ipsum one line description",
                BigDecimal(25)
            ),
            RenderableInvoice.RenderableLineItem(
                "Third Item",
                "Lorem ipsum two line description if needed but no more than two.",
                BigDecimal(44)
            )
        ),
        BigDecimal(10 + 25 + 44),
        BigDecimal(0.001),
        BigDecimal(10 + 25 + 44 + 0.001)
    )

    addTo(parent, LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(-8.dip,8.dip, -8.dip, -8.dip)
    })
}
