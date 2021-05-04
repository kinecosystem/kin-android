package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import org.junit.Test
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.transaction.v3.TransactionService
import org.kin.sdk.base.KinAccountContextImplTest
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.SolanaKinTransaction
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.codec.Base64
import kotlin.test.assertEquals

class ModelToProtoKtTest {

    companion object {
        val kinAccountId = TestUtils.fromAccountId(
            "GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ"
        ).id
        val kinTransaction: KinTransaction =
            SolanaKinTransaction(
                Base64.decodeBase64("AgFFFAAAAAAAAAAAAAAFFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABhp+xHw13QTBg0/2iNL+W0wZ/W9fAF76qqZH8Al+djtT4xw1fIufa+fmDQ5EFlwziYixu4Xt3tqgIwcJm41DgCAgABBCsVqc1L7yzYCeRkVw0qbL2bzGTjLqTrvPdIdXu7PdW94cG3LT0a+kX6wqa2O0qYbqC6rMebhyR0LBQgHRjoebjh8kk+ML92HthfRee5O1nEhhSh3G/n3/omSR1jJoT8qQbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/emg+dGNKfC5/F0EbUpvD4rCYPM6q/9+KMsPHsXv4esBAwMBAgEJA+CuuwAAAAAA")!!,
                networkEnvironment = KinAccountContextImplTest.networkEnvironment
            )
    }

    @Test
    fun KinAccount_Id_toProtoStellarAccountId() {
        val expected =
            Model.StellarAccountId.parseFrom(
                Base64.decodeBase64("CjhHQzRBVEtZRjZDNjZEU1dONlFCWEFOUllFQlNNR0RZQ05MNkZYSzVNWkRKTUtJRlUyR1NWTFRUUQ==")
            )

        val kinAccountId =
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").id

        val stellarAccountId = kinAccountId.toProtoStellarAccountId()

        assertEquals(expected, stellarAccountId)
    }

    @Test
    fun TransactionHash_toProtoTransactionHash() {
        val expected =
            Model.TransactionHash.parseFrom(
                Base64.decodeBase64("CkABRRQAAAAAAAAAAAAABRQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYaf")
            )

        val transactionHash = kinTransaction.transactionHash

        val protoTransactionHash = transactionHash.toProtoTransactionHash()

        assertEquals(expected, protoTransactionHash)
    }
}
