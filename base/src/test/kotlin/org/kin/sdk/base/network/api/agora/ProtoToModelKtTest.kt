package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import org.junit.Assert.assertEquals
import org.junit.Test
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.transaction.v3.TransactionService
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.TransactionResult
import org.kin.stellarfork.xdr.TransactionResultCode
import org.kin.stellarfork.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertTrue

class ProtoToModelKtTest {

    @Test
    fun AccountInfo_toKinAccount() {
        val expectedKinAccount = KinAccount(
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key,
            balance = KinBalance(KinAmount(5000)),
            status = KinAccount.Status.Registered(12345)
        )

        val resultKinAccount = AccountService.AccountInfo.newBuilder()
            .setAccountId(
                Model.StellarAccountId.newBuilder()
                    .setValue("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
            )
            .setBalance(500000000)
            .setSequenceNumber(12345)
            .build()
            .toKinAccount()

        assertEquals(expectedKinAccount, resultKinAccount)
    }

    @Test
    fun StellarAccountId_toPublicKey() {
        val expectedPublicKey =
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key
        val resultPublicKey = Model.StellarAccountId.newBuilder()
            .setValue("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
            .build()
            .toPublicKey()

        assertEquals(expectedPublicKey, resultPublicKey)
    }

    // Utils

    private fun createTransactionResultXdr(resultCode: TransactionResultCode): ByteArray {
        val transactionResult = TransactionResult().apply {
            result = TransactionResult.TransactionResultResult().apply {
                discriminant = resultCode
            }
            feeCharged = Int64().apply {
                int64 = 100
            }
            ext = TransactionResult.TransactionResultExt().apply {
                discriminant = 0
            }
        }
        val os = ByteArrayOutputStream()
        val outputStream = XdrDataOutputStream(os)
        TransactionResult.encode(outputStream, transactionResult)
        return os.toByteArray()
    }
}
