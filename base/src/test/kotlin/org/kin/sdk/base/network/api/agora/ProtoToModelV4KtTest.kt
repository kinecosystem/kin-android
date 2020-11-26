package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.kin.agora.gen.account.v4.AccountService
import org.kin.agora.gen.common.v3.Model.InvoiceError
import org.kin.agora.gen.common.v4.Model
import org.kin.agora.gen.transaction.v4.TransactionService
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.solana.FixedByteArray32
import org.kin.sdk.base.models.solana.Hash
import org.kin.sdk.base.models.solana.TokenProgram
import org.kin.sdk.base.models.solana.Transaction
import org.kin.sdk.base.models.solana.unmarshal
import org.kin.sdk.base.network.api.KinAccountApiV4
import org.kin.sdk.base.network.api.KinAccountCreationApiV4
import org.kin.sdk.base.network.api.KinTransactionApiV4
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.codec.Base64
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProtoToModelV4KtTest {

    @Test
    fun AccountInfo_toKinAccount() {
        val expectedKinAccount = KinAccount(
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key,
            balance = KinBalance(KinAmount(5000)),
            status = KinAccount.Status.Registered(0)
        )

        val resultKinAccount = AccountService.AccountInfo.newBuilder()
            .setAccountId(
                Model.SolanaAccountId.newBuilder()
                    .setValue(ByteString.copyFrom(KinAccount.Id("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").value))
            )
            .setBalance(500000000)
            .build()
            .toKinAccount()

        assertEquals(expectedKinAccount, resultKinAccount)
    }

    @Test
    fun StellarAccountId_toPublicKey() {
        val expectedPublicKey =
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key
        val resultPublicKey = Model.SolanaAccountId.newBuilder()
            .setValue(ByteString.copyFrom(KinAccount.Id("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").value))
            .build()
            .toPublicKey()

        assertEquals(expectedPublicKey, resultPublicKey)
    }

    @Test
    fun HistoryItem_toAcknowledgedKinTransaction() {
        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        val resultKinTransaction = TransactionService.HistoryItem.newBuilder()
            .setSolanaTransaction(
                Model.Transaction.newBuilder()
                    .setValue(ByteString.copyFrom(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")))
            )
            .build()
            .toAcknowledgedKinTransaction(NetworkEnvironment.KinStellarTestNetKin3)

        assertTrue { expectedKinTransaction.bytesValue.contentEquals(resultKinTransaction.bytesValue) }
        assertTrue { resultKinTransaction.recordType is KinTransaction.RecordType.Acknowledged }
    }

    @Test
    fun HistoryItem_toHistoricalKinTransaction() {
        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        val resultKinTransaction = TransactionService.HistoryItem.newBuilder()
            .setSolanaTransaction(
                Model.Transaction.newBuilder()
                    .setValue(ByteString.copyFrom(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")))
            )
            .build()
            .toHistoricalKinTransaction(NetworkEnvironment.KinStellarTestNetKin3)

        assertTrue { expectedKinTransaction.bytesValue.contentEquals(resultKinTransaction.bytesValue) }
        assertTrue { resultKinTransaction.recordType is KinTransaction.RecordType.Historical }
    }

    @Test
    fun createAccountResponse_success() {
        val expectedKinAccount = KinAccount(
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key,
            balance = KinBalance(KinAmount(5000)),
            status = KinAccount.Status.Registered(0)
        )

        val onCompleted: (KinAccountCreationApiV4.CreateAccountResponse) -> Unit = {
            assertEquals(KinAccountCreationApiV4.CreateAccountResponse.Result.Ok, it.result)
            assertEquals(
                expectedKinAccount,
                it.account
            )
        }

        onCompleted.createAccountResponse().onSuccess(
            AccountService.CreateAccountResponse.newBuilder()
                .setResult(AccountService.CreateAccountResponse.Result.OK)
                .setAccountInfo(
                    AccountService.AccountInfo.newBuilder()
                        .setAccountId(
                            Model.SolanaAccountId.newBuilder()
                                .setValue(ByteString.copyFrom(KinAccount.Id("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").value))
                        )
                        .setBalance(500000000)
                )
                .build()
        )
    }

    @Test
    fun createAccountResponse_exists() {
        val onCompleted: (KinAccountCreationApiV4.CreateAccountResponse) -> Unit = {
            assertEquals(KinAccountCreationApiV4.CreateAccountResponse.Result.Exists, it.result)
            assertNull(it.account)
        }

        onCompleted.createAccountResponse().onSuccess(
            AccountService.CreateAccountResponse.newBuilder()
                .setResult(AccountService.CreateAccountResponse.Result.EXISTS)
                .build()
        )
    }

    @Test
    fun createAccountResponse_undefinedFailure_fromResponse() {
        val onCompleted: (KinAccountCreationApiV4.CreateAccountResponse) -> Unit = {
            assertTrue(it.result is KinAccountCreationApiV4.CreateAccountResponse.Result.UndefinedError)
            assertNull(it.account)
        }

        onCompleted.createAccountResponse().onSuccess(
            AccountService.CreateAccountResponse.newBuilder()
                .setResultValue(9000)
                .build()
        )
    }

    @Test
    fun createAccountResponse_undefinedFailure_fromError() {
        val onCompleted: (KinAccountCreationApiV4.CreateAccountResponse) -> Unit = {
            assertTrue(it.result is KinAccountCreationApiV4.CreateAccountResponse.Result.UndefinedError)
            assertNull(it.account)
        }

        onCompleted.createAccountResponse().onError?.invoke(
            StatusRuntimeException(Status.UNAUTHENTICATED)
        )
    }

    @Test
    fun createAccountResponse_transientFailure() {
        val error = StatusRuntimeException(Status.INTERNAL)
        val onCompleted: (KinAccountCreationApiV4.CreateAccountResponse) -> Unit = {
            assertEquals(
                KinAccountCreationApiV4.CreateAccountResponse.Result.TransientFailure(error),
                it.result
            )
            assertNull(it.account)
        }

        onCompleted.createAccountResponse().onError?.invoke(
            error
        )
    }

    @Test
    fun getAccountResponse_success() {
        val expectedKinAccount = KinAccount(
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key,
            balance = KinBalance(KinAmount(5000)),
            status = KinAccount.Status.Registered(0)
        )

        val onCompleted: (KinAccountApiV4.GetAccountResponse) -> Unit = {
            assertEquals(KinAccountApiV4.GetAccountResponse.Result.Ok, it.result)
            assertEquals(
                expectedKinAccount,
                it.account
            )
        }

        onCompleted.getAccountInfoResponse().onSuccess(
            AccountService.GetAccountInfoResponse.newBuilder()
                .setResult(AccountService.GetAccountInfoResponse.Result.OK)
                .setAccountInfo(
                    AccountService.AccountInfo.newBuilder()
                        .setAccountId(
                            Model.SolanaAccountId.newBuilder()
                                .setValue(ByteString.copyFrom(KinAccount.Id("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").value))
                        )
                        .setBalance(500000000)
                )
                .build()
        )
    }

    @Test
    fun getAccountResponse_notFound() {
        { it: KinAccountApiV4.GetAccountResponse ->
            assertTrue(it.result is KinAccountApiV4.GetAccountResponse.Result.NotFound)
            assertNull(it.account)
        }.getAccountInfoResponse()
            .onSuccess(
                AccountService.GetAccountInfoResponse.newBuilder()
                    .setResult(AccountService.GetAccountInfoResponse.Result.NOT_FOUND)
                    .build()
            )
    }

    @Test
    fun getAccountResponse_undefinedError() {
        { it: KinAccountApiV4.GetAccountResponse ->
            assertTrue(it.result is KinAccountApiV4.GetAccountResponse.Result.UndefinedError)
            assertNull(it.account)
        }.getAccountInfoResponse()
            .onSuccess(
                AccountService.GetAccountInfoResponse.newBuilder()
                    .setResultValue(9000)
                    .build()
            )
    }

    @Test
    fun getAccountResponse_retryable() {
        { it: KinAccountApiV4.GetAccountResponse ->
            assertTrue(it.result is KinAccountApiV4.GetAccountResponse.Result.TransientFailure)
            assertNull(it.account)
        }.getAccountInfoResponse()
            .onError?.invoke(StatusRuntimeException(Status.CANCELLED))
    }

    @Test
    fun getAccountResponse_undefinedError2() {
        { it: KinAccountApiV4.GetAccountResponse ->
            assertTrue(it.result is KinAccountApiV4.GetAccountResponse.Result.UndefinedError)
            assertNull(it.account)
        }.getAccountInfoResponse()
            .onError?.invoke(GrpcApi.UnrecognizedResultException(Exception()))
    }

    @Test
    fun getTransactionHistoryResponse_success() {
        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        val onCompleted: (KinTransactionApiV4.GetTransactionHistoryResponse) -> Unit = {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transactions!!.first().bytesValue) }
            assertTrue { it.transactions?.first()?.recordType is KinTransaction.RecordType.Historical }
        }

        onCompleted.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3).onSuccess(
            TransactionService.GetHistoryResponse.newBuilder()
                .setResult(TransactionService.GetHistoryResponse.Result.OK)
                .addItems(
                    TransactionService.HistoryItem.newBuilder()
                        .setSolanaTransaction(
                            Model.Transaction.newBuilder()
                                .setValue(ByteString.copyFrom(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")))
                        )
                        .build()
                )
                .build()
        )
    }

    @Test
    fun getTransactionHistoryResponse_notFound() {
        { it: KinTransactionApiV4.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApiV4.GetTransactionHistoryResponse.Result.NotFound)
            assertNull(it.transactions)
        }.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onSuccess(
                TransactionService.GetHistoryResponse.newBuilder()
                    .setResult(TransactionService.GetHistoryResponse.Result.NOT_FOUND)
                    .build()
            )
    }

    @Test
    fun getTransactionHistoryResponse_unrecognized() {
        { it: KinTransactionApiV4.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApiV4.GetTransactionHistoryResponse.Result.UndefinedError)
            assertNull(it.transactions)
        }.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onSuccess(
                TransactionService.GetHistoryResponse.newBuilder()
                    .setResultValue(9000)
                    .build()
            )
    }

    @Test
    fun getTransactionHistoryResponse_retryable() {
        { it: KinTransactionApiV4.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApiV4.GetTransactionHistoryResponse.Result.TransientFailure)
            assertNull(it.transactions)
        }.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(StatusRuntimeException(Status.CANCELLED))
    }

    @Test
    fun getTransactionHistoryResponse_statusRuntimeException() {
        { it: KinTransactionApiV4.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApiV4.GetTransactionHistoryResponse.Result.NotFound)
            assertNull(it.transactions)
        }.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(StatusRuntimeException(Status.NOT_FOUND))
    }

    @Test
    fun getTransactionHistoryResponse_undefined() {
        { it: KinTransactionApiV4.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApiV4.GetTransactionHistoryResponse.Result.UndefinedError)
            assertNull(it.transactions)
        }.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(GrpcApi.UnrecognizedResultException(Exception()))
    }

    @Test
    fun getTransactionResponse_success() {
        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        val onCompleted: (KinTransactionApiV4.GetTransactionResponse) -> Unit = {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transaction!!.bytesValue) }
            assertTrue { it.transaction?.recordType is KinTransaction.RecordType.Historical }
        }

        onCompleted.getTransactionResponse(NetworkEnvironment.KinStellarTestNetKin3).onSuccess(
            TransactionService.GetTransactionResponse.newBuilder()
                .setState(TransactionService.GetTransactionResponse.State.SUCCESS)
                .setItem(
                    TransactionService.HistoryItem.newBuilder()
                        .setSolanaTransaction(
                            Model.Transaction.newBuilder()
                                .setValue(ByteString.copyFrom(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")))
                        )
                )
                .build()
        )
    }

    @Test
    fun getTransactionResponse_notFound() {
        { it: KinTransactionApiV4.GetTransactionResponse ->
            assertTrue(it.result is KinTransactionApiV4.GetTransactionResponse.Result.NotFound)
            assertNull(it.transaction)
        }.getTransactionResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onSuccess(
                TransactionService.GetTransactionResponse.newBuilder()
                    .setState(TransactionService.GetTransactionResponse.State.UNKNOWN)
                    .build()
            )
    }

    @Test
    fun getTransactionResponse_retryable() {
        { it: KinTransactionApiV4.GetTransactionResponse ->
            assertTrue(it.result is KinTransactionApiV4.GetTransactionResponse.Result.TransientFailure)
            assertNull(it.transaction)
        }.getTransactionResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(StatusRuntimeException(Status.CANCELLED))
    }

    @Test
    fun getTransactionResponse_undefined() {
        { it: KinTransactionApiV4.GetTransactionResponse ->
            assertTrue(it.result is KinTransactionApiV4.GetTransactionResponse.Result.UndefinedError)
            assertNull(it.transaction)
        }.getTransactionResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(GrpcApi.UnrecognizedResultException(Exception()))
    }

    @Test
    fun submitTransactionResponse_success() {
        val expectedKinTransaction = TestUtils.kinTransactionFromSolanaTransaction(
            "AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=",
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        val onCompleted: (KinTransactionApiV4.SubmitTransactionResponse) -> Unit = {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transaction!!.bytesValue) }
            assertTrue { it.transaction?.recordType is KinTransaction.RecordType.Acknowledged }
        }

        val request = KinTransactionApiV4.SubmitTransactionRequest(
            Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
        )

        onCompleted.submitTransactionResponse(request, NetworkEnvironment.KinStellarTestNetKin3)
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.OK)
                    .setSignature(
                        Model.TransactionSignature.newBuilder()
                            .setValue(ByteString.copyFrom(ByteArray(64)))
                    )
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_insufficientBalance() {
        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.InsufficientBalance }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.FAILED)
                    .setTransactionError(
                        Model.TransactionError.newBuilder().setReason(
                            Model.TransactionError.Reason.INSUFFICIENT_FUNDS
                        )
                    )
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_internalError() {
        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.UndefinedError }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.FAILED)
                    .setTransactionError(
                        Model.TransactionError.newBuilder().setReason(
                            Model.TransactionError.Reason.UNKNOWN
                        )
                    )
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_unrecogniedError() {
        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.UndefinedError }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResultValue(9000)
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_invoiceErrors() {
        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR)
                    .addInvoiceErrors(
                        InvoiceError.newBuilder().setReason(InvoiceError.Reason.ALREADY_PAID)
                    )
                    .build()
            );

        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR)
                    .addInvoiceErrors(
                        InvoiceError.newBuilder().setReason(InvoiceError.Reason.SKU_NOT_FOUND)
                    )
                    .build()
            );

        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR)
                    .addInvoiceErrors(
                        InvoiceError.newBuilder().setReason(InvoiceError.Reason.WRONG_DESTINATION)
                    )
                    .build()
            );

        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.InvoiceErrors }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR)
                    .addInvoiceErrors(
                        InvoiceError.newBuilder().setReason(InvoiceError.Reason.UNKNOWN)
                    )
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_webhook_rejected() {
        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.WebhookRejected }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.REJECTED)
                    .build()
            );
    }

    @Test
    fun submitTransactionResponse_retryable() {
        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.TransientFailure }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onError?.invoke(
                StatusRuntimeException(Status.CANCELLED)
            )
    }

    @Test
    fun submitTransactionResponse_undefinedError() {
        { it: KinTransactionApiV4.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApiV4.SubmitTransactionResponse.Result.UndefinedError }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApiV4.SubmitTransactionRequest(
                    Transaction.unmarshal(Base64.decodeBase64("AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADB4OsvLL0IBYszd/Y15PRQ7SE2CRaokCmoJKhjSKmoPPHdDX2aQGSweWQTrwoKavFczm0x10JRj2taeJHVa68BAgABBbgJqwXwveHKzfQDcDY4IGTDDwJq/FurrMjSxSC00aVVEnE1mqibexZ5drQRlPTvQJHcjDl+2/PYmqKSiKnPBTwhBy6pDvoUUywETo/12Fol9ti5cGuxfxDfxT3Gt4ogL13F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEEAwMCAQkD4K67AAAAAAA=")!!)
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onError?.invoke(
                Exception("unknown")
            )
    }

    @Test
    fun getServiceConfig_success() {
        { it: KinTransactionApiV4.GetServiceConfigResponse ->
            fun validate() {
                assertTrue { it.result is KinTransactionApiV4.GetServiceConfigResponse.Result.Ok }
                assertNotNull(it.subsidizerAccount)
                assertNotNull(it.token)
                assertNotNull(it.tokenProgram)
            }
            validate()
        }.createServiceConfigResponse()
            .onSuccess.invoke(
                TransactionService.GetServiceConfigResponse.newBuilder()
                    .setSubsidizerAccount(
                        TestUtils.newPublicKey().asKinAccountId().toProtoSolanaAccountId()
                    )
                    .setToken(TestUtils.newPublicKey().asKinAccountId().toProtoSolanaAccountId())
                    .setTokenProgram(
                        TokenProgram.PROGRAM_KEY.asKinAccountId().toProtoSolanaAccountId()
                    )
                    .build()
            )
    }

    @Test
    fun getServiceConfig_retryable() {
        { it: KinTransactionApiV4.GetServiceConfigResponse ->
            fun validate() {
                assertTrue { it.result is KinTransactionApiV4.GetServiceConfigResponse.Result.TransientFailure }
                assertNull(it.subsidizerAccount)
                assertNull(it.token)
                assertNull(it.tokenProgram)
            }
            validate()
        }.createServiceConfigResponse()
            .onError?.invoke(
                StatusRuntimeException(Status.CANCELLED)
            )
    }

    @Test
    fun getServiceConfig_undefinedError() {
        { it: KinTransactionApiV4.GetServiceConfigResponse ->
            fun validate() {
                assertTrue { it.result is KinTransactionApiV4.GetServiceConfigResponse.Result.UndefinedError }
                assertNull(it.subsidizerAccount)
                assertNull(it.token)
                assertNull(it.tokenProgram)
            }
            validate()
        }.createServiceConfigResponse()
            .onError?.invoke(
                Exception("unknown")
            )
    }

    @Test
    fun getMinRentExemption_success() {
        { it: KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse ->
            assertEquals(
                KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse.Result.Ok,
                it.result
            )
            assertEquals(555L, it.lamports)
        }.getMinimumBalanceForRentExemptionResponse()
            .onSuccess.invoke(
                TransactionService.GetMinimumBalanceForRentExemptionResponse.newBuilder()
                    .setLamports(555)
                    .build()
            )
    }

    @Test
    fun getMinRentExemption_retryable() {
        { it: KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse ->
            fun validate() {
                assertTrue { it.result is KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse.Result.TransientFailure }
                assertNull(it.lamports)
            }
            validate()
        }.getMinimumBalanceForRentExemptionResponse()
            .onError?.invoke(
                StatusRuntimeException(Status.CANCELLED)
            )
    }

    @Test
    fun getMinRentExemption_undefinedError() {
        { it: KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse ->
            fun validate() {
                assertTrue { it.result is KinTransactionApiV4.GetMinimumBalanceForRentExemptionResponse.Result.UndefinedError }
                assertNull(it.lamports)
            }
            validate()
        }.getMinimumBalanceForRentExemptionResponse()
            .onError?.invoke(
                Exception("unknown")
            )
    }

    @Test
    fun resolveTokenAccounts_success() {
        { it: KinAccountApiV4.ResolveTokenAccountsResponse ->
            assertEquals(
                KinAccountApiV4.ResolveTokenAccountsResponse.Result.Ok,
                it.result
            )
        }.resolveTokenAccountsResponse()
            .onSuccess.invoke(
                AccountService.ResolveTokenAccountsResponse.newBuilder()
                    .addAllTokenAccounts(
                        listOf(
                            Model.SolanaAccountId.newBuilder()
                                .setValue(ByteString.copyFrom(KinAccount.Id("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").value))
                                .build()
                        )
                    )
                    .build()
            )
    }

    @Test
    fun resolveTokenAccounts_retryable() {
        { it: KinAccountApiV4.ResolveTokenAccountsResponse ->
            fun validate() {
                assertTrue { it.result is KinAccountApiV4.ResolveTokenAccountsResponse.Result.TransientFailure }
                assertTrue { it.accounts.isEmpty() }
            }
            validate()
        }.resolveTokenAccountsResponse()
            .onError?.invoke(
                StatusRuntimeException(Status.CANCELLED)
            )
    }

    @Test
    fun resolveTokenAccounts_undefinedError() {
        { it: KinAccountApiV4.ResolveTokenAccountsResponse ->
            fun validate() {
                assertTrue { it.result is KinAccountApiV4.ResolveTokenAccountsResponse.Result.UndefinedError }
                assertTrue { it.accounts.isEmpty() }
            }
            validate()
        }.resolveTokenAccountsResponse()
            .onError?.invoke(
                Exception("unknown")
            )
    }

    @Test
    fun getRecentBlockchainHash_success() {
        { it: KinTransactionApiV4.GetRecentBlockHashResponse ->
            assertEquals(KinTransactionApiV4.GetRecentBlockHashResponse.Result.Ok, it.result)
            assertEquals(
                Hash(
                    FixedByteArray32(
                        byteArrayOf(
                            12,
                            24,
                            36,
                            48,
                            12,
                            24,
                            36,
                            48,
                            12,
                            24,
                            36,
                            48,
                            12,
                            24,
                            36,
                            48,
                            12,
                            24,
                            36,
                            48,
                            12,
                            24,
                            36,
                            48,
                            12,
                            24,
                            36,
                            48,
                            12,
                            24,
                            36,
                            48
                        )
                    )
                ), it.blockHash
            )
        }.getRecentBlockHashResponse()
            .onSuccess.invoke(
                TransactionService.GetRecentBlockhashResponse.newBuilder()
                    .setBlockhash(
                        Model.Blockhash.newBuilder()
                            .setValue(
                                ByteString.copyFrom(
                                    byteArrayOf(
                                        12,
                                        24,
                                        36,
                                        48,
                                        12,
                                        24,
                                        36,
                                        48,
                                        12,
                                        24,
                                        36,
                                        48,
                                        12,
                                        24,
                                        36,
                                        48,
                                        12,
                                        24,
                                        36,
                                        48,
                                        12,
                                        24,
                                        36,
                                        48,
                                        12,
                                        24,
                                        36,
                                        48,
                                        12,
                                        24,
                                        36,
                                        48
                                    )
                                )
                            )
                            .build()
                    )
                    .build()
            )
    }

    @Test
    fun getRecentBlockchainHash_retryable() {
        { it: KinTransactionApiV4.GetRecentBlockHashResponse ->
            fun validate() {
                assertTrue { it.result is KinTransactionApiV4.GetRecentBlockHashResponse.Result.TransientFailure }
                assertNull(it.blockHash)
            }
            validate()
        }.getRecentBlockHashResponse()
            .onError?.invoke(
                StatusRuntimeException(Status.CANCELLED)
            )
    }

    @Test
    fun getRecentBlockchainHash_undefinedError() {
        { it: KinTransactionApiV4.GetRecentBlockHashResponse ->
            fun validate() {
                assertTrue { it.result is KinTransactionApiV4.GetRecentBlockHashResponse.Result.UndefinedError }
                assertNull(it.blockHash)
            }
            validate()
        }.getRecentBlockHashResponse()
            .onError?.invoke(
                Exception("unknown")
            )
    }
}
