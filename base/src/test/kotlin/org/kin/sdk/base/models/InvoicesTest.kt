package org.kin.sdk.base.models

import org.kin.sdk.base.tools.toByteArray
import java.util.Random
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class InvoicesTest {

    companion object {
        val random = Random(System.currentTimeMillis())
        fun randomLineItem() = LineItem
            .Builder(
                (0..127).map { (random.nextInt() % 50).toChar().toString() }
                    .reduce { acc, i -> acc + i }, KinAmount(5)
            )
            .setDescription((0..255).map { (random.nextInt() % 50).toChar().toString() }
                .reduce { acc, i -> acc + i })
            .setSKU(SKU(UUID.randomUUID().toByteArray()))
            .build()

        fun randomInvoice() = Invoice.Builder()
            .addLineItem(randomLineItem())
            .addLineItem(randomLineItem())
            .addLineItem(randomLineItem())
            .build()
    }

    @Test
    fun testLineItemAllowedCharacteristics() {

        LineItem.Builder("testTitle", KinAmount(5))
            .build()

        LineItem.Builder("testTitle", KinAmount(5))
            .setDescription("testDesc")
            .build()

        LineItem.Builder("testTitle", KinAmount(5))
            .setDescription("testDesc")
            .setSKU(SKU(UUID.randomUUID().toByteArray()))
            .build()
    }

    @Test(expected = LineItem.Builder.LineItemFormatException::class)
    fun testLineItemInvalidTitle() {
        LineItem.Builder("", KinAmount(5))
            .build()
    }

    @Test(expected = LineItem.Builder.LineItemFormatException::class)
    fun testLineItemInvalidTitle2() {
        LineItem.Builder((0..129).map { "A" }.reduce { acc, i -> acc + i }, KinAmount(5))
            .build()
    }

    @Test(expected = LineItem.Builder.LineItemFormatException::class)
    fun testLineItemInvalidDescription() {
        LineItem.Builder((0..256).map { "A" }.reduce { acc, i -> acc + i }, KinAmount(5))
            .setDescription("")
            .build()
    }

    @Test(expected = LineItem.Builder.LineItemFormatException::class)
    fun testLineItemInvalidSKU() {
        LineItem.Builder("testTitle", KinAmount(5))
            .setDescription("testDesc")
            .setSKU(SKU((0..129).map { "A".toByteArray() }.reduce { acc, i -> acc + i }))
            .build()
    }

    @Test
    fun testLineItemEquality() {
        val randomBytes = UUID.randomUUID().toByteArray()

        val lineItem1 = LineItem.Builder("testTitle", KinAmount(5))
            .setDescription("testDesc")
            .setSKU(SKU(randomBytes))
            .build()

        val lineItem2 = LineItem.Builder("testTitle", KinAmount(5))
            .setDescription("testDesc")
            .setSKU(SKU(randomBytes))
            .build()

        val lineItem3 = LineItem.Builder("aDifferentTestTitle", KinAmount(5))
            .setDescription("testDesc")
            .setSKU(SKU(UUID.randomUUID().toByteArray()))
            .build()

        assertEquals(lineItem1, lineItem2)
        assertEquals(lineItem1.hashCode(), lineItem2.hashCode())
        assertNotEquals(lineItem1, lineItem3)
        assertNotEquals(lineItem1.hashCode(), lineItem3.hashCode())
    }

    @Test
    fun testInvoiceAllowedCharacteristics() {
        Invoice.Builder()
            .addLineItem(randomLineItem())
            .addLineItems(listOf(randomLineItem(), randomLineItem()))
            .build()
    }

    @Test(expected = Invoice.Builder.InvoiceFormatException::class)
    fun testInvoiceInvalidLineItemCount() {
        Invoice.Builder()
            .addLineItems(mutableListOf<LineItem>().apply {
                (0..1025).forEach { add(randomLineItem()) }
            })
            .build()
    }

    @Test
    fun testInvoiceListAllowedCharacteristics() {
        InvoiceList.Builder()
            .addInvoice(randomInvoice())
            .addInvoices(listOf(randomInvoice(), randomInvoice()))
            .build()
    }

    @Test(expected = InvoiceList.Builder.InvoiceListFormatException::class)
    fun testInvoiceListInvalidInvoiceCount() {
        InvoiceList.Builder()
            .addInvoices(mutableListOf<Invoice>().apply {
                (0..101).forEach { add(randomInvoice()) }
            })
            .build()
    }

    @Test
    fun testInvoiceListEquality() {
        val invoice = randomInvoice()

        val list1 = InvoiceList.Builder()
            .addInvoice(invoice)
            .build()

        val list2 = InvoiceList.Builder()
            .addInvoice(invoice)
            .build()

        val list3 = InvoiceList.Builder()
            .addInvoice(randomInvoice())
            .build()

        assertEquals(list1, list2)
        assertNotEquals(list1, list3)
    }

    @Test
    fun testSerialization() {
        val invoice = randomInvoice()
        assertEquals(invoice, Invoice.parseFrom(invoice.toProtoBytes()))
    }
}
