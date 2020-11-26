package org.kin.sdk.base.network.api.agora

import com.google.protobuf.ByteString
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.junit.Assert.assertEquals
import org.junit.Test
import org.kin.agora.gen.account.v3.AccountService
import org.kin.agora.gen.common.v3.Model
import org.kin.agora.gen.common.v3.Model.InvoiceError
import org.kin.agora.gen.transaction.v3.TransactionService
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.KinBalance
import org.kin.sdk.base.network.api.KinAccountApi
import org.kin.sdk.base.network.api.KinAccountCreationApi
import org.kin.sdk.base.network.api.KinTransactionApi
import org.kin.sdk.base.stellar.models.KinTransaction
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.tools.TestUtils
import org.kin.stellarfork.codec.Base64
import org.kin.stellarfork.xdr.Int64
import org.kin.stellarfork.xdr.TransactionResult
import org.kin.stellarfork.xdr.TransactionResultCode
import org.kin.stellarfork.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import kotlin.test.assertNull
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

    @Test
    fun HistoryItem_toAcknowledgedKinTransaction() {
        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        val resultKinTransaction = TransactionService.HistoryItem.newBuilder()
            .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")))
            .setEnvelopeXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")))
            .build()
            .toAcknowledgedKinTransaction(NetworkEnvironment.KinStellarTestNetKin3)

        assertTrue { expectedKinTransaction.bytesValue.contentEquals(resultKinTransaction!!.bytesValue) }
        assertTrue { resultKinTransaction?.recordType is KinTransaction.RecordType.Acknowledged }
    }

    @Test
    fun HistoryItem_toHistoricalKinTransaction() {
        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        val resultKinTransaction = TransactionService.HistoryItem.newBuilder()
            .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")))
            .setEnvelopeXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")))
            .build()
            .toHistoricalKinTransaction(NetworkEnvironment.KinStellarTestNetKin3)

        assertTrue { expectedKinTransaction.bytesValue.contentEquals(resultKinTransaction!!.bytesValue) }
        assertTrue { resultKinTransaction?.recordType is KinTransaction.RecordType.Historical }
    }

    @Test
    fun createAccountResponse_success() {
        val expectedKinAccount = KinAccount(
            TestUtils.fromAccountId("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ").key,
            balance = KinBalance(KinAmount(5000)),
            status = KinAccount.Status.Registered(12345)
        )

        val onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit = {
            assertEquals(KinAccountCreationApi.CreateAccountResponse.Result.Ok, it.result)
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
                            Model.StellarAccountId.newBuilder()
                                .setValue("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
                        )
                        .setBalance(500000000)
                        .setSequenceNumber(12345)
                )
                .build()
        )
    }

    @Test
    fun createAccountResponse_exists() {
        val onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit = {
            assertEquals(KinAccountCreationApi.CreateAccountResponse.Result.Exists, it.result)
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
        val onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit = {
            assertTrue(it.result is KinAccountCreationApi.CreateAccountResponse.Result.UndefinedError)
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
        val onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit = {
            assertTrue(it.result is KinAccountCreationApi.CreateAccountResponse.Result.UndefinedError)
            assertNull(it.account)
        }

        onCompleted.createAccountResponse().onError?.invoke(
            StatusRuntimeException(Status.UNAUTHENTICATED)
        )
    }

    @Test
    fun createAccountResponse_transientFailure() {
        val error = StatusRuntimeException(Status.INTERNAL)
        val onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit = {
            assertEquals(
                KinAccountCreationApi.CreateAccountResponse.Result.TransientFailure(error),
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
            status = KinAccount.Status.Registered(12345)
        )

        val onCompleted: (KinAccountApi.GetAccountResponse) -> Unit = {
            assertEquals(KinAccountApi.GetAccountResponse.Result.Ok, it.result)
            assertEquals(
                expectedKinAccount,
                it.account
            )
        }

        onCompleted.getAccountResponse().onSuccess(
            AccountService.GetAccountInfoResponse.newBuilder()
                .setResult(AccountService.GetAccountInfoResponse.Result.OK)
                .setAccountInfo(
                    AccountService.AccountInfo.newBuilder()
                        .setAccountId(
                            Model.StellarAccountId.newBuilder()
                                .setValue("GC4ATKYF6C66DSWN6QBXANRYEBSMGDYCNL6FXK5MZDJMKIFU2GSVLTTQ")
                        )
                        .setBalance(500000000)
                        .setSequenceNumber(12345)
                )
                .build()
        )
    }

    @Test
    fun getAccountResponse_notFound() {
        { it: KinAccountApi.GetAccountResponse ->
            assertTrue(it.result is KinAccountApi.GetAccountResponse.Result.NotFound)
            assertNull(it.account)
        }.getAccountResponse()
            .onSuccess(
                AccountService.GetAccountInfoResponse.newBuilder()
                    .setResult(AccountService.GetAccountInfoResponse.Result.NOT_FOUND)
                    .build()
            )
    }

    @Test
    fun getAccountResponse_undefinedError() {
        { it: KinAccountApi.GetAccountResponse ->
            assertTrue(it.result is KinAccountApi.GetAccountResponse.Result.UndefinedError)
            assertNull(it.account)
        }.getAccountResponse()
            .onSuccess(
                AccountService.GetAccountInfoResponse.newBuilder()
                    .setResultValue(9000)
                    .build()
            )
    }

    @Test
    fun getAccountResponse_retryable() {
        { it: KinAccountApi.GetAccountResponse ->
            assertTrue(it.result is KinAccountApi.GetAccountResponse.Result.TransientFailure)
            assertNull(it.account)
        }.getAccountResponse()
            .onError?.invoke(StatusRuntimeException(Status.CANCELLED))
    }

    @Test
    fun getAccountResponse_undefinedError2() {
        { it: KinAccountApi.GetAccountResponse ->
            assertTrue(it.result is KinAccountApi.GetAccountResponse.Result.UndefinedError)
            assertNull(it.account)
        }.getAccountResponse()
            .onError?.invoke(GrpcApi.UnrecognizedResultException(Exception()))
    }

    @Test
    fun getTransactionHistoryResponse_success() {
        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        val onCompleted: (KinTransactionApi.GetTransactionHistoryResponse) -> Unit = {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transactions!!.first().bytesValue) }
            assertTrue { it.transactions?.first()?.recordType is KinTransaction.RecordType.Historical }
        }

        onCompleted.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3).onSuccess(
            TransactionService.GetHistoryResponse.newBuilder()
                .setResult(TransactionService.GetHistoryResponse.Result.OK)
                .addItems(
                    TransactionService.HistoryItem.newBuilder()
                        .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")))
                        .setEnvelopeXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")))
                        .build()
                )
                .build()
        )
    }

    @Test
    fun getTransactionHistoryResponse_notFound() {
        { it: KinTransactionApi.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.NotFound)
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
        { it: KinTransactionApi.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.UndefinedError)
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
        { it: KinTransactionApi.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.TransientFailure)
            assertNull(it.transactions)
        }.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(StatusRuntimeException(Status.CANCELLED))
    }

    @Test
    fun getTransactionHistoryResponse_statusRuntimeException() {
        { it: KinTransactionApi.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.NotFound)
            assertNull(it.transactions)
        }.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(StatusRuntimeException(Status.NOT_FOUND))
    }

    @Test
    fun getTransactionHistoryResponse_undefined() {
        { it: KinTransactionApi.GetTransactionHistoryResponse ->
            assertTrue(it.result is KinTransactionApi.GetTransactionHistoryResponse.Result.UndefinedError)
            assertNull(it.transactions)
        }.getTransactionHistoryResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(GrpcApi.UnrecognizedResultException(Exception()))
    }

    @Test
    fun getTransactionResponse_success() {
        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Historical(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!,
                KinTransaction.PagingToken("12312312323123123")
            )
        )

        val onCompleted: (KinTransactionApi.GetTransactionResponse) -> Unit = {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transaction!!.bytesValue) }
            assertTrue { it.transaction?.recordType is KinTransaction.RecordType.Historical }
        }

        onCompleted.getTransactionResponse(NetworkEnvironment.KinStellarTestNetKin3).onSuccess(
            TransactionService.GetTransactionResponse.newBuilder()
                .setState(TransactionService.GetTransactionResponse.State.SUCCESS)
                .setItem(
                    TransactionService.HistoryItem.newBuilder()
                        .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")))
                        .setEnvelopeXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")))
                )
                .build()
        )
    }

    @Test
    fun getTransactionResponse_notFound() {
        { it: KinTransactionApi.GetTransactionResponse ->
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.NotFound)
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
        { it: KinTransactionApi.GetTransactionResponse ->
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.TransientFailure)
            assertNull(it.transaction)
        }.getTransactionResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(StatusRuntimeException(Status.CANCELLED))
    }

    @Test
    fun getTransactionResponse_undefined() {
        { it: KinTransactionApi.GetTransactionResponse ->
            assertTrue(it.result is KinTransactionApi.GetTransactionResponse.Result.UndefinedError)
            assertNull(it.transaction)
        }.getTransactionResponse(NetworkEnvironment.KinStellarTestNetKin3)
            .onError?.invoke(GrpcApi.UnrecognizedResultException(Exception()))
    }

    @Test
    fun submitTransactionResponse_success() {
        val expectedKinTransaction = TestUtils.kinTransactionFromXdr(
            "AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=",
            KinTransaction.RecordType.Acknowledged(
                System.currentTimeMillis(),
                Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!
            )
        )

        val onCompleted: (KinTransactionApi.SubmitTransactionResponse) -> Unit = {
            assertTrue { expectedKinTransaction.bytesValue.contentEquals(it.transaction!!.bytesValue) }
            assertTrue { it.transaction?.recordType is KinTransaction.RecordType.Acknowledged }
        }

        val request = KinTransactionApi.SubmitTransactionRequest(
            Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
        )

        onCompleted.submitTransactionResponse(request, NetworkEnvironment.KinStellarTestNetKin3)
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.OK)
                    .setResultXdr(ByteString.copyFrom(Base64.decodeBase64("AAAAAAAAAGQAAAAAAAAAAQAAAAAAAAABAAAAAAAAAAA=")!!))
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_insufficientBalance() {
        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.InsufficientBalance }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.FAILED)
                    .setResultXdr(ByteString.copyFrom(createTransactionResultXdr(TransactionResultCode.txINSUFFICIENT_BALANCE)))
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_internalError() {
        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.UndefinedError }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.FAILED)
                    .setResultXdr(ByteString.copyFrom(createTransactionResultXdr(TransactionResultCode.txINTERNAL_ERROR)))
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_unrecogniedError() {
        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.UndefinedError }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
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
        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR)
                    .addInvoiceErrors(InvoiceError.newBuilder().setReason(InvoiceError.Reason.ALREADY_PAID))
                    .build()
            );

            { it: KinTransactionApi.SubmitTransactionResponse ->
                assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors }
                assertNull(it.transaction)
            }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR)
                    .addInvoiceErrors(InvoiceError.newBuilder().setReason(InvoiceError.Reason.SKU_NOT_FOUND))
                    .build()
            );

        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR)
                    .addInvoiceErrors(InvoiceError.newBuilder().setReason(InvoiceError.Reason.WRONG_DESTINATION))
                    .build()
            );

        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.InvoiceErrors }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onSuccess(
                TransactionService.SubmitTransactionResponse.newBuilder()
                    .setResult(TransactionService.SubmitTransactionResponse.Result.INVOICE_ERROR)
                    .addInvoiceErrors(InvoiceError.newBuilder().setReason(InvoiceError.Reason.UNKNOWN))
                    .build()
            )
    }

    @Test
    fun submitTransactionResponse_webhook_rejected() {
        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.WebhookRejected }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
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
        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.TransientFailure }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onError?.invoke(
                StatusRuntimeException(Status.CANCELLED)
            )
    }

    @Test
    fun submitTransactionResponse_undefinedError() {
        { it: KinTransactionApi.SubmitTransactionResponse ->
            assertTrue { it.result is KinTransactionApi.SubmitTransactionResponse.Result.UndefinedError }
            assertNull(it.transaction)
        }
            .submitTransactionResponse(
                KinTransactionApi.SubmitTransactionRequest(
                    Base64.decodeBase64("AAAAAF3F+luUcf1MXVhQNVM5hmYFAGO8h2DL5wv4rCHCGO/7AAAAZAA65AMAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAACEHLqkO+hRTLAROj/XYWiX22Llwa7F/EN/FPca3iiAvAAAAAAAAAAAAu67gAAAAAAAAAAHCGO/7AAAAQBPhVdcWukxwTHvqvvCUB159IPIfT4DypiKWsXSeT92SNskltFanXy0fTF7kCtjGpOQ7uIKrdhK8ImYQdGSowgI=")!!
                ),
                NetworkEnvironment.KinStellarTestNetKin3
            )
            .onError?.invoke(
                Exception("unknown")
            )
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
