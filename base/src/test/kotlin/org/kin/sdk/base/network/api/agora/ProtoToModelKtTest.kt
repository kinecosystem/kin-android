package org.kin.sdk.base.network.api.agora

import org.junit.Assert.assertEquals
import org.junit.Test
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.common.v3.Model
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.tools.TestUtils

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
}
