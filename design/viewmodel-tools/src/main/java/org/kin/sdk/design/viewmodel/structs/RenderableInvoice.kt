package org.kin.sdk.design.viewmodel.structs

import java.math.BigDecimal
import java.net.URI

data class RenderableInvoice(
    val items: List<RenderableLineItem>,
    val subTotal: BigDecimal,
    val fee: BigDecimal,
    val total: BigDecimal
) {
    data class RenderableLineItem(
        val title: String,
        val description: String,
        val amount: BigDecimal
    )
}
