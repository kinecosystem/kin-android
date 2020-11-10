package kin.sdk.internal

import kin.sdk.PaymentInfo
import kin.sdk.TransactionId
import org.kin.sdk.base.models.KinDateFormat
import org.kin.sdk.base.models.KinPayment
import org.kin.sdk.base.models.toUTF8String
import java.math.BigDecimal

data class PaymentInfoImpl(val kinPayment: KinPayment) : PaymentInfo {
    override fun createdAt(): String = KinDateFormat.timestampToString(kinPayment.timestamp)
    override fun destinationPublicKey(): String = kinPayment.destinationAccountId.stellarBase32Encode()
    override fun sourcePublicKey(): String = kinPayment.sourceAccountId.stellarBase32Encode()
    override fun amount(): BigDecimal = kinPayment.amount.value
    override fun hash(): TransactionId = kinPayment.id.transactionHash.toTransactionId()
    override fun memo(): String = kinPayment.memo.rawValue.toUTF8String()
    override fun fee(): Long = kinPayment.fee.value
}
