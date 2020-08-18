package org.kin.sdk.base.viewmodel.utils

import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.design.viewmodel.structs.RenderableInvoice

fun Invoice.toRenderableInvoice(amountPaid: KinAmount = KinAmount.ZERO): RenderableInvoice {
    val renderableLineItems = lineItems.map {
        RenderableInvoice.RenderableLineItem(
            it.title,
            it.description,
            it.amount.value
        )
    }
    val fee = if (amountPaid != KinAmount.ZERO) {
        amountPaid.value - total.value
    } else {
        KinAmount.ZERO.value
    }

    return RenderableInvoice(
        items = renderableLineItems,
        subTotal = total.value,
        fee = fee,
        total = total.value + fee
    )
}
