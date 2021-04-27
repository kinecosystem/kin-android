package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import org.junit.Test
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.transaction.v3.TransactionService
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.codec.Base64
import kotlin.test.assertEquals

class ModelToProtoKtTest {

    companion object {
        val kinAccountId = TestUtils.fromAccountId(
            "GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ"
        ).id
        val kinTransaction: KinTransaction =
            TestUtils.kinTransactionFromXdr(
                "AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=",
                KinTransaction.RecordType.InFlight(
                    System.currentTimeMillis()
                )
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
                Base64.decodeBase64("CiCRSEwoPBC9w/5F4yleLfOZPgM0PSUNVLmAOfthQTllew==")
            )

        val transactionHash = kinTransaction.transactionHash

        val protoTransactionHash = transactionHash.toProtoTransactionHash()

        assertEquals(expected, protoTransactionHash)
    }
}
