package org.kin.sdk.base.viewmodel.utils

import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.SKU
import org.kin.sdk.base.tools.toByteArray
import org.kin.sdk.design.viewmodel.structs.RenderableInvoice
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals

class ModelToModelKtTest {
    companion object {
        val invoice = Invoice.Builder().apply {
            addLineItem(
                LineItem.Builder("Start a Chat", KinAmount(50))
                    .setSKU(SKU(UUID.randomUUID().toByteArray()))
                    .build()
            )
        }.build()
    }

    @Before
    fun setUp() {
    }

    @Test
    fun testInvoice_to_RenderableInvoice() {
        val renderableInvoice = invoice.toRenderableInvoice(KinAmount(50.001))

        val expected = RenderableInvoice(
            listOf(
                RenderableInvoice.RenderableLineItem(
                    "Start a Chat", "", KinAmount(50).value
                )
            ),
            KinAmount(50).value,
            KinAmount(0.001).value,
            KinAmount(50.001).value
        )
        assertEquals(expected, renderableInvoice)
    }

    @Test
    fun testInvoice_to_RenderableInvoice_noFee() {
        val renderableInvoice = invoice.toRenderableInvoice(KinAmount(50))

        val expected = RenderableInvoice(
            listOf(
                RenderableInvoice.RenderableLineItem(
                    "Start a Chat", "", KinAmount(50).value
                )
            ),
            KinAmount(50).value,
            KinAmount.ZERO.value,
            KinAmount(50).value
        )
        assertEquals(expected, renderableInvoice)
    }

    @Test
    fun testInvoice_to_RenderableInvoice_nonePaid() {
        val renderableInvoice = invoice.toRenderableInvoice()

        val expected = RenderableInvoice(
            listOf(
                RenderableInvoice.RenderableLineItem(
                    "Start a Chat", "", KinAmount(50).value
                )
            ),
            KinAmount(50).value,
            KinAmount.ZERO.value,
            KinAmount(50).value
        )
        assertEquals(expected, renderableInvoice)
    }
}
