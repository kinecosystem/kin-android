package org.kin.sdk.base.repository

import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.tools.Observer
import org.kin.sdk.base.tools.Optional
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.ValueSubject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

interface InvoiceRepository {
    fun addInvoice(invoice: Invoice): Promise<Invoice>
    fun addAllInvoices(invoices: List<Invoice>): Promise<List<Invoice>>
    fun allInvoices(): Observer<List<Invoice>>
    fun invoiceById(id: Invoice.Id): Promise<Optional<Invoice>>
}

class InMemoryInvoiceRepositoryImpl(
    private val storage: MutableMap<Invoice.Id, Invoice> = mutableMapOf(),
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
) : InvoiceRepository {

    private val invoicesSubject = ValueSubject<List<Invoice>>()

    override fun addInvoice(invoice: Invoice): Promise<Invoice> {
        return Promise.create<Invoice> { resolve, _ ->
            storage.put(invoice.id, invoice)
            invoicesSubject.onNext(storage.values.toList())
            resolve(invoice)
        }.workOn(executorService)
    }

    override fun addAllInvoices(invoices: List<Invoice>): Promise<List<Invoice>> {
        return Promise.create<List<Invoice>> { resolve, _ ->
            storage.putAll(invoices.asIterable().map { Pair(it.id, it) })
            val allInvoices = storage.values.toList()
            invoicesSubject.onNext(allInvoices)
            resolve(allInvoices)
        }.workOn(executorService)
    }

    override fun allInvoices(): Observer<List<Invoice>> {
        return invoicesSubject.apply {
            onNext(storage.values.toList())
        }
    }

    override fun invoiceById(id: Invoice.Id): Promise<Optional<Invoice>> {
        return Promise.create<Optional<Invoice>> { resolve, _ ->
            resolve(Optional.ofNullable(storage.get(id)))
        }.workOn(executorService)
    }
}
