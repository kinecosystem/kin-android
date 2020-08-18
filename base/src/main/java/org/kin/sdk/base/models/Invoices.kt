package org.kin.sdk.base.models

import org.kin.agora.gen.common.v3.Model
import org.kin.sdk.base.network.api.agora.sha224Hash
import org.kin.sdk.base.network.api.agora.toInvoice
import org.kin.sdk.base.network.api.agora.toProto


/**
 * @param bytes - can by up to 128 bytes in size
 */
data class SKU(val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SKU) return false

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}

/**
 * An individual item in an [Invoice]
 * @param title - 1-128 characters of renderable text describing the item.
 * @param description - Optional 0-256 characters of renderable text describing the item
 * @param amount - the [KinAmount] that the item costs
 * @param sku - an app defined identifier to key the [LineItem] on. Should at least be unique per item, if not per item + user who is purchasing it.
 */
data class LineItem @JvmOverloads internal constructor(
    val title: String,
    val description: String,
    val amount: KinAmount,
    val sku: SKU? = null
) {
    data class Builder(val title: String, val amount: KinAmount) {
        data class LineItemFormatException(override val message: String) :
            IllegalArgumentException("Invalid LineItem: $message")

        private var sku: SKU? = null
        private var description: String = ""

        fun setDescription(description: String): Builder = apply {
            this.description = description
        }

        fun setSKU(sku: SKU): Builder = apply {
            this.sku = sku
        }

        fun build(): LineItem {
            return when {
                title.isEmpty() || title.length > 128 -> throw LineItemFormatException("title too short. Must be > 1 and < 128 characters")
                description.length > 256 -> throw LineItemFormatException("title too short. Must be > 1 and < 128 characters")
                sku?.bytes?.size ?: 0 > 128 -> throw LineItemFormatException("SKU cannot exceed 128 bytes")
                else -> LineItem(title, description, amount, sku)
            }
        }
    }
}

/**
 * Contains the information about what a given [KinPayment] was for.
 * @param id - identifier for the [Invoice] that contains a SHA-224 of the [lineItems] data
 * @param lineItems - 1-1024 [LineItem]s describing an itemized list of what the [Invoice] is for.
 */
data class Invoice internal constructor(val id: Id, val lineItems: List<LineItem>) {
    data class Id(val invoiceHash: SHA224Hash)

    val total: KinAmount by lazy {
        lineItems.map { it.amount }
            .reduce { acc, kinAmount -> acc + kinAmount }
    }

    class Builder {

        data class InvoiceFormatException(override val message: String) :
            IllegalArgumentException("Invalid Invoice: $message")
        private val lineItems = mutableListOf<LineItem>()

        fun addLineItem(lineItem: LineItem): Builder = apply {
            lineItems.add(lineItem)
        }

        fun addLineItems(lineItems: Collection<LineItem>): Builder = apply {
            this.lineItems.addAll(lineItems)
        }

        fun build(): Invoice {
            return when {
                lineItems.isEmpty() -> throw InvoiceFormatException("Must have at least one LineItem")
                lineItems.size > 1024 -> throw InvoiceFormatException("Maximum of 1024 LineItem's allowed")
                else -> Invoice(
                    with(Model.Invoice.newBuilder()) {
                        Id(addAllItems(lineItems.toProto()).build().sha224Hash())
                    },
                    lineItems
                )
            }
        }
    }

    fun toProtoBytes(): ByteArray = toProto().toByteArray()

    companion object {
        fun parseFrom(bytes: ByteArray): Invoice {
            return Model.Invoice.parseFrom(bytes).toInvoice()
        }
    }
}

/**
 * A collection of [Invoice]s.
 * Often submitted in the same [KinTransaction] together.
 *  @param id - identifier for the [InvoiceList] that contains a SHA-224 of the [invoices] data
 *  @param invoices - all the [Invoice]s in the list
 */
data class InvoiceList internal constructor(val id: Id, val invoices: List<Invoice>) {
    data class Id(val invoiceHash: SHA224Hash)

    class Builder {
        data class InvoiceListFormatException(override val message: String) :
            IllegalArgumentException("Invalid InvoiceList: $message")

        private val invoices = mutableListOf<Invoice>()

        fun addInvoice(invoice: Invoice): Builder = apply {
            invoices.add(invoice)
        }

        fun addInvoices(invoices: List<Invoice>): Builder = apply {
            this.invoices.addAll(invoices)
        }

        fun build(): InvoiceList {

            return when {
                invoices.isEmpty() -> throw InvoiceListFormatException("Must have at least one Invoice")
                invoices.size > 100 -> throw InvoiceListFormatException("Maximum of 100 invoices allowed")
                else -> InvoiceList(
                    Id(invoices.toProto().sha224Hash()),
                    invoices
                )
            }
        }
    }
}




