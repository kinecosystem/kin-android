package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import org.junit.Test
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.transaction.v3.TransactionService
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinTransactionApi
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
    fun CreateAccountRequest_toGrpcRequest() {
        val expected =
            AccountService.CreateAccountRequest.parseFrom(
                Base64.decodeBase64("CjoKOEdDNEFUS1lGNkM2NkRTV042UUJYQU5SWUVCU01HRFlDTkw2RlhLNU1aREpNS0lGVTJHU1ZMVFRR")
            )

        val createAccountRequest = KinAccountCreationApi.CreateAccountRequest(kinAccountId)

        val grpcRequest = createAccountRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun GetAccountRequest_toGrpcRequest() {
        val expected =
            AccountService.GetAccountInfoRequest.parseFrom(
                Base64.decodeBase64("CjoKOEdDNEFUS1lGNkM2NkRTV042UUJYQU5SWUVCU01HRFlDTkw2RlhLNU1aREpNS0lGVTJHU1ZMVFRR")
            )

        val getAccountRequest = KinAccountApi.GetAccountRequest(kinAccountId)

        val grpcRequest = getAccountRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun GetTransactionHistoryRequest_toGrpcRequest() {
        val expected =
            TransactionService.GetHistoryRequest.parseFrom(
                Base64.decodeBase64("CjoKOEdDNEFUS1lGNkM2NkRTV042UUJYQU5SWUVCU01HRFlDTkw2RlhLNU1aREpNS0lGVTJHU1ZMVFRRGAE=")
            )

        val getTransactionHistoryRequest = KinTransactionApi.GetTransactionHistoryRequest(
            kinAccountId
        )

        val grpcRequest = getTransactionHistoryRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun GetTransactionHistoryRequest_withPagingToken_toGrpcRequest() {
        val expected =
            TransactionService.GetHistoryRequest.parseFrom(
                Base64.decodeBase64("CjoKOEdDNEFUS1lGNkM2NkRTV042UUJYQU5SWUVCU01HRFlDTkw2RlhLNU1aREpNS0lGVTJHU1ZMVFRREgUKAwAQgxgB")
            )

        val getTransactionHistoryRequest = KinTransactionApi.GetTransactionHistoryRequest(
            kinAccountId,
            pagingToken = KinTransaction.PagingToken(
                    TransactionService.Cursor.newBuilder()
                        .setValue(ByteString.copyFromUtf8("ABCD"))
                        .build()
                        .value
                        .toStringUtf8()
            )
        )

        val grpcRequest = getTransactionHistoryRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun GetTransactionRequest_toGrpcRequest() {
        val expected =
            TransactionService.GetTransactionRequest.parseFrom(
                Base64.decodeBase64("CiIKIJFITCg8EL3D/kXjKV4t85k+AzQ9JQ1UuYA5+2FBOWV7")
            )

        val getTransactionRequest = KinTransactionApi.GetTransactionRequest(
            kinTransaction.transactionHash
        )

        val grpcRequest = getTransactionRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun SubmitTransactionRequest_toGrpcRequest() {
        val expected =
            TransactionService.SubmitTransactionRequest.parseFrom(
                Base64.decodeBase64("CtQBAAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=")
            )

        val submitTransactionRequest = KinTransactionApi.SubmitTransactionRequest(
            kinTransaction.bytesValue
        )

        val grpcRequest = submitTransactionRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
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
