package kin.sdk

import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.stellarfork.KeyPair
import org.kin.stellarfork.Transaction
import java.math.BigDecimal

data class Transaction internal constructor(
    val destination: KeyPair,
    val source: KeyPair,
    val amount: BigDecimal,
    val fee: Int,
    val memo: String,
    /**
     * The transaction hash
     */
    val id: TransactionId,
    val stellarTransaction: Transaction?,
    val whitelistableTransaction: WhitelistableTransaction?
) {
    internal var kinTransaction: KinTransaction? = null
}
