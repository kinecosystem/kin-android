package org.kin.sdk.base.repository

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.SKU
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.test
import org.kin.sdk.base.tools.toByteArray
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class InMemoryInvoiceRepositoryImplTest {

    companion object {
        val invoice = Invoice.Builder().apply {
            addLineItem(
                LineItem.Builder("Start a Chat", KinAmount(50))
                    .setSKU(SKU(UUID.randomUUID().toByteArray()))
                    .build()
            )
        }.build()
        val invoice2 = Invoice.Builder().apply {
            addLineItem(
                LineItem.Builder("Sticker", KinAmount(25))
                    .setSKU(SKU(UUID.randomUUID().toByteArray()))
                    .build()
            )
        }.build()
    }

    lateinit var sut: InvoiceRepository

    @Before
    fun setUp() {

        sut = InMemoryInvoiceRepositoryImpl()
    }

    @Test
    fun invoice_not_found() {
        sut.invoiceById(invoice.id).test {
            assertEquals(Optional.empty<Invoice>(), value)
        }
    }

    @Test
    fun invoice_found() {
        sut.addInvoice(invoice).test {
            assertEquals(invoice, value)
        }

        sut.invoiceById(invoice.id).test {
            assertEquals(Optional.of(invoice), value)
        }
    }

    @Test
    fun invoices_found() {
        sut.addAllInvoices(listOf(invoice, invoice2)).test {
            assertEquals(listOf(invoice, invoice2), value)
        }

        sut.invoiceById(invoice.id).test {
            assertEquals(Optional.of(invoice), value)
        }

        sut.invoiceById(invoice2.id).test {
            assertEquals(Optional.of(invoice2), value)
        }
    }

    @Test
    fun listenToInvoices() {

        val values = mutableListOf<List<Invoice>>()
        val latch = CountDownLatch(3)
        sut.allInvoices().add {
            values.add(it)
            latch.countDown()
        }

        sut.addInvoice(invoice).resolve()
        sut.addInvoice(invoice2).resolve()

        latch.await(5, TimeUnit.SECONDS)

        assertEquals(emptyList<List<Invoice>>(), values[0])
        assertEquals(listOf(invoice), values[1])
        assertEquals(listOf(invoice, invoice2), values[2])
    }
}
