package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import org.junit.Test
import org.kin.agora.gen.account.v4.AccountService
import org.kin.agora.gen.common.v4.Model
import org.kin.agora.gen.transaction.v4.TransactionService
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.asPrivateKey
import org.kin.sdk.base.models.asPublicKey
import org.kin.sdk.base.models.solana.FixedByteArray32
import org.kin.sdk.base.models.solana.Hash
import org.kin.sdk.base.models.solana.SystemProgram
import org.kin.sdk.base.models.solana.TokenProgram
import org.kin.sdk.base.models.solana.Transaction
import org.kin.sdk.base.models.solana.unmarshal
import org.kin.sdk.base.models.toKeyPair
import org.kin.sdk.base.models.toSigningKeyPair
import org.kin.sdk.base.network.api.KinAccountApiV4
import org.kin.sdk.base.network.api.KinAccountCreationApiV4
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.codec.Base64
import kotlin.test.assertEquals

class ModelToProtoV4KtTest {

    companion object {
        val kinAccountId = TestUtils.fromAccountId(
            "GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ"
        ).id
        val kinTransaction: KinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD7SgRBfjyh1qkQCsj51yD3yqMXKYPPuJZWjR7omIiSyf+USAES8pO1TjzPsNSyt83QMx/cNQ32X2aakjiymAINAgABBO8oot1gdFzu7PD9FVa1d7qVwJMMaA9eHCYwdUXnQVthXcX6W5Rx/UxdWFA1UzmGZgUAY7yHYMvnC/isIcIY7/shBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAwMBAgEJA+CuuwAAAAAA",
            KinTransaction.RecordType.InFlight(System.currentTimeMillis())
        )
    }

    @Test
    fun KinAccount_Id_toProtoSolanaAccountId() {
        val expected =
            Model.SolanaAccountId.parseFrom(
                Base64.decodeBase64("CiC4CasF8L3hys30A3A2OCBkww8Cavxbq6zI0sUgtNGlVQ==")
            )

        val kinAccountId =
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").id

        val solanaAccountId = kinAccountId.toProtoSolanaAccountId()

        assertEquals(expected, solanaAccountId)
    }

    @Test
    fun CreateAccountRequest_toGrpcRequest() {

        val account =
            TestUtils.fromSecretSeed("SDFDPC5VK7FSFDH4Q3CQPQRA4OPFXYM6CFRXVQOA767OGXFYBEDEQGMF")
                .copy(
                    balance = KinBalance(KinAmount(5000)),
                    status = KinAccount.Status.Registered(0)
                )

        val subsidizerId =
            TestUtils.fromAccountId("GDXSRIW5MB2FZ3XM6D6RKVVVO65JLQETBRUA6XQ4EYYHKRPHIFNWCVNC").id
        val minRentExemptionInLamports = 555L // would actually be size dependant in real life
        val mintKey = TokenProgram.PROGRAM_KEY
        val recentBlockHash = Hash(FixedByteArray32())

        val subsidizer: Key.PublicKey = subsidizerId.toKeyPair().asPublicKey()
        val accountPub: Key.PublicKey = account.id.toKeyPair().asPublicKey()

        val transaction = Transaction.newTransaction(
            subsidizer,
            SystemProgram.CreateAccount(
                subsidizer = subsidizer,
                address = accountPub,
                owner = TokenProgram.PROGRAM_KEY,
                lamports = minRentExemptionInLamports,
                size = TokenProgram.accountSize
            ).instruction,
            TokenProgram.InitializeAccount(
                account = accountPub,
                mint = mintKey,
                owner = account.key.asPublicKey(),
                TokenProgram.PROGRAM_KEY
            ).instruction
        ).copyAndSetRecentBlockhash(recentBlockHash)
            .copyAndSign(account.toSigningKeyPair().asPrivateKey())

        val expected =
            AccountService.CreateAccountRequest.parseFrom(
                Base64.decodeBase64("CooDCocDAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB3XiJ2sqJkgfdMUQzOPNqKh8oYXK5jjrVawtYvicry/B+kC5b5SRtjYYHrsjCabskFKFZYkrIAjpw4fcL/+ecKAgADBe8oot1gdFzu7PD9FVa1d7qVwJMMaA9eHCYwdUXnQVthXcX6W5Rx/UxdWFA1UzmGZgUAY7yHYMvnC/isIcIY7/sGp9UXGSxcUSGMyUw9SvF/WNruCJuh/UTj29mKAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIDAgABNAAAAAArAgAAAAAAAKUAAAAAAAAABt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkEBAEEAQIBARAB")
            )

        val createAccountRequest = KinAccountCreationApiV4.CreateAccountRequest(transaction)

        val grpcRequest = createAccountRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun GetAccountRequest_toGrpcRequest() {
        val expected =
            AccountService.GetAccountInfoRequest.parseFrom(
                Base64.decodeBase64("CiIKILgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEAE=")
            )

        val getAccountRequest = KinAccountApiV4.GetAccountRequest(kinAccountId)

        val grpcRequest = getAccountRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun GetTransactionHistoryRequest_toGrpcRequest() {
        val expected =
            TransactionService.GetHistoryRequest.parseFrom(
                Base64.decodeBase64("CiIKILgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVGAE=")
            )

        val getTransactionHistoryRequest = KinTransactionApiV4.GetTransactionHistoryRequest(
            kinAccountId
        )

        val grpcRequest = getTransactionHistoryRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun GetTransactionHistoryRequest_withPagingToken_toGrpcRequest() {
        val expected =
            TransactionService.GetHistoryRequest.parseFrom(
                Base64.decodeBase64("CiIKILgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEgUKAwAQgxgB")
            )

        val getTransactionHistoryRequest = KinTransactionApiV4.GetTransactionHistoryRequest(
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
                Base64.decodeBase64("CkIKQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAQ==")
            )

        val getTransactionRequest = KinTransactionApiV4.GetTransactionRequest(
            kinTransaction.transactionHash
        )

        val grpcRequest = getTransactionRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }

    @Test
    fun SubmitTransactionRequest_toGrpcRequest() {
        val expected =
            TransactionService.SubmitTransactionRequest.parseFrom(
                Base64.decodeBase64("CrgCCrUCAgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD7SgRBfjyh1qkQCsj51yD3yqMXKYPPuJZWjR7omIiSyf+USAES8pO1TjzPsNSyt83QMx/cNQ32X2aakjiymAINAgABBO8oot1gdFzu7PD9FVa1d7qVwJMMaA9eHCYwdUXnQVthXcX6W5Rx/UxdWFA1UzmGZgUAY7yHYMvnC/isIcIY7/shBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogLwbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCpAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAwMBAgEJA+CuuwAAAAAA")
            )

        val submitTransactionRequest = KinTransactionApiV4.SubmitTransactionRequest(
            Transaction.unmarshal(kinTransaction.bytesValue)
        )

        val grpcRequest = submitTransactionRequest.toGrpcRequest()

        assertEquals(expected, grpcRequest)
    }
}
