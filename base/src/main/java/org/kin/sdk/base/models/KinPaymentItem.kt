package org.kin.sdk.base.models

import org.kin.sdk.base.tools.Optional

/**
 * @param amount - always the amount transferred in the payment
 * @param destinationAccount - the KinAccount.Id where the funds are to be transferred to
 * @param invoice - (optional) - an Invoice that this transfer refers to. [amount] should match invoice.total, but is not strictly enforced. Where they differ [amount] will be the kin actually transferred.
 */
data class KinPaymentItem(val amount: KinAmount, val destinationAccount: KinAccount.Id, val invoice: Optional<Invoice> = Optional.empty())
