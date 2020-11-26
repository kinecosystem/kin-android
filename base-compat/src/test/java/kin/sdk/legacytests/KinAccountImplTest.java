package kin.sdk.legacytests;

import org.junit.Before;
import org.junit.Test;
import org.kin.sdk.base.KinAccountContext;
import org.kin.sdk.base.ObservationMode;
import org.kin.sdk.base.models.AppId;
import org.kin.sdk.base.models.Key;
import org.kin.sdk.base.models.KinAccount;
import org.kin.sdk.base.models.KinAmount;
import org.kin.sdk.base.models.KinBalance;
import org.kin.sdk.base.models.KinMemo;
import org.kin.sdk.base.models.KinPayment;
import org.kin.sdk.base.models.StellarBaseTypeConversionsKt;
import org.kin.sdk.base.models.TransactionHash;
import org.kin.sdk.base.network.services.KinService;
import org.kin.sdk.base.stellar.models.KinTransaction;
import org.kin.sdk.base.stellar.models.NetworkEnvironment;
import org.kin.sdk.base.tools.ListSubject;
import org.kin.sdk.base.tools.Promise;
import org.kin.sdk.base.tools.ValueSubject;
import org.kin.stellarfork.KeyPair;
import org.kin.stellarfork.Network;
import org.kin.stellarfork.codec.Hex;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kin.sdk.AccountStatus;
import kin.sdk.BackupRestore;
import kin.sdk.Balance;
import kin.sdk.ListenerRegistration;
import kin.sdk.PaymentInfo;
import kin.sdk.Transaction;
import kin.sdk.TransactionId;
import kin.sdk.WhitelistableTransaction;
import kin.sdk.exception.AccountDeletedException;
import kin.sdk.exception.CryptoException;
import kin.sdk.exception.OperationFailedException;
import kin.sdk.internal.BalanceImpl;
import kin.sdk.internal.KinAccountImpl;
import kin.sdk.internal.TransactionIdImpl;
import kin.sdk.internal.UtilsKt;
import kin.utils.Request;
import kin.utils.ResultCallback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class KinAccountImplTest {

    private static final String APP_ID = "1a2c";

    private KinAccountImpl kinAccount;
    private KeyPair expectedRandomAccount;
    private KinAccount expectedRandomKinAccount;

    @Mock
    private KinAccountContext mockAccountContext;
    @Mock
    private KinService mockKinService;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Request.setCallbackExecutor(executorService);
        initWithRandomAccount();
    }

    private void initWithRandomAccount() {
        expectedRandomAccount = KeyPair.random();

        expectedRandomKinAccount = StellarBaseTypeConversionsKt.asKinAccount(expectedRandomAccount);

        when(mockAccountContext.getAccountId())
                .thenReturn(new KinAccount.Id(expectedRandomAccount.getPublicKey()));

        when(mockAccountContext.getAccount())
                .thenReturn(
                        Promise.Companion.of(
                                new KinAccount(new Key.PrivateKey(expectedRandomAccount.getRawSecretSeed()),
                                        new KinAccount.Id(expectedRandomAccount.getPublicKey()),
                                        new ArrayList<>(),
                                        new KinBalance(new KinAmount(11.0)),
                                        new KinAccount.Status.Registered(0))
                        )
                );

        when(mockAccountContext.getAccount(true))
                .thenReturn(
                        Promise.Companion.of(
                                new KinAccount(new Key.PrivateKey(expectedRandomAccount.getRawSecretSeed()),
                                        new KinAccount.Id(expectedRandomAccount.getPublicKey()),
                                        new ArrayList<>(),
                                        new KinBalance(new KinAmount(11.0)),
                                        new KinAccount.Status.Registered(0))
                        )
                );

        when(mockAccountContext.clearStorage())
                .thenReturn(Promise.Companion.of(true));

        kinAccount = new KinAccountImpl(
                expectedRandomAccount,
                new FakeBackupRestore(),
                mockAccountContext,
                mockKinService,
                NetworkEnvironment.KinStellarTestNetKin3.INSTANCE,
                new AppId(APP_ID)
        );
    }

    @Test
    public void getPublicAddress_ExistingAccount() throws Exception {
        assertEquals(expectedRandomAccount.getAccountId(), kinAccount.getPublicAddress());
    }

    @Test
    public void getEncodedPrivateKey() {
        assertEquals(new String(expectedRandomAccount.getSecretSeed()), kinAccount.getStringEncodedPrivateKey());
    }

    @Test
    public void sendTransactionSync() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0).getAmount().getValue();

        when(mockKinService.buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(kinTransaction));
        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(Collections.singletonList(kinPayment)));

        Transaction transaction = kinAccount.buildTransactionSync(expectedAccountId, expectedAmount, 100);

        assertEquals(transaction.getAmount(), expectedAmount);
        assertEquals(transaction.getDestination(), KeyPair.fromAccountId("GAZGCY6DQN7ESXHCYHL2P3CFZMBPUOYXI5UWYJMO26UXQV55FDSQ4LUY"));
        assertEquals(transaction.getSource(), KeyPair.fromAccountId("GDS3RPC4HBRWJXXS6HPRV4K56SOGSTLP6YX2RZ4YI6RQ57PJIYUVXEIL"));
        assertEquals(transaction.getFee(), 100);
        assertEquals(transaction.getId(), new TransactionIdImpl(new TransactionHash(Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b"))));
        assertEquals(transaction.getMemo(), "");
        assertEquals(transaction.getStellarTransaction(), UtilsKt.asTransaction(kinTransaction, Network.getTestNetwork()).getStellarTransaction());
        assertEquals(transaction.getStellarTransaction().hashCode(), UtilsKt.asTransaction(kinTransaction, Network.getTestNetwork()).getStellarTransaction().hashCode());
        assertEquals(transaction.getWhitelistableTransaction(), new WhitelistableTransaction("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=", "Kin Testnet ; December 2018"));

        TransactionId transactionId = kinAccount.sendTransactionSync(transaction);

        verify(mockKinService).buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId);
        assertEquals(expectedTransactionId.id(), transactionId.id());
    }

    @Test
    public void sendWhitelistedTransactionSync() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOsas8dY1Z3wb918WHGIPznz+qYdxDgCILjMvkNfoccdAAAAAACvhksAAAACAAAAAAAAAAEAAAAHMS1sODNoLQAAAAABAAAAAQAAAADrGrPHWNWd8G/dfFhxiD858/qmHcQ4AiC4zL5DX6HHHQAAAAEAAAAANWTpaxPindylRungCZ28VOxyXIBGDpwEA9ST5LEC/bEAAAAAAAAAAAX14QAAAAAAAAAAAl+hxx0AAABAhB7rhuYfyeFVcFkgvxKO/gkjE5RkRPglNCF2+m1Ws4oOxNuw2jjpz+gZhhpQm0yzhGNIR//IvAACQbW2ttiyBbEC/bEAAABALKCiOluhSzhzEP1g4l6ZnjEpzIS5o8PJLpWyC/pRCxHTFzuzjnBEdMbIW3wzLZnsC4a3/pM/ePoRBy9otl/sDQ==");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0).getAmount().getValue();

        when(mockKinService.buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(kinTransaction));
        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(Collections.singletonList(kinPayment)));

        Transaction transaction = kinAccount.buildTransactionSync(expectedAccountId, expectedAmount, 100);

        assertEquals(transaction.getAmount(), expectedAmount);
       // assertEquals(transaction.getDestination(), KeyPair.fromAccountId("GAZGCY6DQN7ESXHCYHL2P3CFZMBPUOYXI5UWYJMO26UXQV55FDSQ4LUY"));
        //assertEquals(transaction.getSource(), KeyPair.fromAccountId("GDS3RPC4HBRWJXXS6HPRV4K56SOGSTLP6YX2RZ4YI6RQ57PJIYUVXEIL"));
        assertEquals(transaction.getFee(), 0);
        //assertEquals(transaction.getId(), new TransactionIdImpl(new TransactionHash(Hex.decodeHex("91484c283c10bdc3fe45e3295e2df3993e03343d250d54b98039fb614139657b"))));
        assertEquals(transaction.getMemo(), "1-l83h-");
        assertEquals(transaction.getStellarTransaction(), UtilsKt.asTransaction(kinTransaction, Network.getTestNetwork()).getStellarTransaction());
        assertEquals(transaction.getStellarTransaction().hashCode(), UtilsKt.asTransaction(kinTransaction, Network.getTestNetwork()).getStellarTransaction().hashCode());
        assertEquals(transaction.getWhitelistableTransaction(), new WhitelistableTransaction("AAAAAOsas8dY1Z3wb918WHGIPznz+qYdxDgCILjMvkNfoccdAAAAAACvhksAAAACAAAAAAAAAAEAAAAHMS1sODNoLQAAAAABAAAAAQAAAADrGrPHWNWd8G/dfFhxiD858/qmHcQ4AiC4zL5DX6HHHQAAAAEAAAAANWTpaxPindylRungCZ28VOxyXIBGDpwEA9ST5LEC/bEAAAAAAAAAAAX14QAAAAAAAAAAAl+hxx0AAABAhB7rhuYfyeFVcFkgvxKO/gkjE5RkRPglNCF2+m1Ws4oOxNuw2jjpz+gZhhpQm0yzhGNIR//IvAACQbW2ttiyBbEC/bEAAABALKCiOluhSzhzEP1g4l6ZnjEpzIS5o8PJLpWyC/pRCxHTFzuzjnBEdMbIW3wzLZnsC4a3/pM/ePoRBy9otl/sDQ==", "Kin Testnet ; December 2018"));

        TransactionId transactionId = kinAccount.sendTransactionSync(transaction);

        verify(mockKinService).buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId);
        assertEquals(expectedTransactionId.id(), transactionId.id());
    }


    @Test(expected = OperationFailedException.class)
    public void sendTransactionSync_build_error() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0).getAmount().getValue();

        when(mockKinService.buildAndSignTransaction(eq((Key.PrivateKey) expectedRandomKinAccount.getKey()), any(), anyLong(), any(), any(), any()))
                .thenReturn(Promise.Companion.error(new KinService.FatalError.UnexpectedServiceError(new Exception())));
        when(mockAccountContext.sendKinPayment(any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(kinPayment));

        Transaction transaction = kinAccount.buildTransactionSync(expectedAccountId, expectedAmount, 100);
        TransactionId transactionId = kinAccount.sendTransactionSync(transaction);

        verify(mockKinService).buildAndSignTransaction(eq((Key.PrivateKey) expectedRandomKinAccount.getKey()), any(), anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayment(any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId);
    }

    @Test(expected = OperationFailedException.class)
    public void sendTransactionSync_send_error() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0).getAmount().getValue();

        when(mockKinService.buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(kinTransaction));
        when(mockAccountContext.sendKinPayment(any(), any(), any(), any()))
                .thenReturn(Promise.Companion.error(new KinService.FatalError.UnexpectedServiceError(new Exception())));


        Transaction transaction = kinAccount.buildTransactionSync(expectedAccountId, expectedAmount, 100);
        TransactionId transactionId = kinAccount.sendTransactionSync(transaction);

        verify(mockKinService).buildAndSignTransaction(any(), any(),anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayment(any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId);
    }

    @Test
    public void buildAndSendTransactionAsync() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0).getAmount().getValue();

        when(mockKinService.buildAndSignTransaction(any(),any(), anyLong(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(kinTransaction));
        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(Collections.singletonList(kinPayment)));

        final Transaction[] transaction = new Transaction[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.buildTransaction(expectedAccountId, expectedAmount, 100).run(new ResultCallback<Transaction>() {
                @Override
                public void onResult(Transaction result) {
                    transaction[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        final TransactionId[] transactionId = new TransactionId[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.sendTransaction(transaction[0]).run(new ResultCallback<TransactionId>() {
                @Override
                public void onResult(TransactionId result) {
                    transactionId[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        verify(mockKinService).buildAndSignTransaction(any(),any(),anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId[0]);
    }

    @Test
    public void buildAndSendTransactionAsync_withMemo() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAANjf/bJmcEcUJlHQ9wterZa4+GNRUXuNzzk80U3de+EYAAAAZABwt4MAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAABAAAADzEtMWEyYy1zb21lTWVtbwAAAAABAAAAAAAAAAEAAAAA2N/9smZwRxQmUdD3C16tlrj4Y1FRe43POTzRTd174RgAAAAAAAAAAABT7GAAAAAAAAAAAd174RgAAABAayE9lgf1HOE6GzRfl0p1WF53iIDCfJ7kScfMOLJE6hMcRJYBfxEoh60+E1fPoDXkPo9XtMMz/mdn3TwnzUslAQ==");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = new BigDecimal("55.0");

        when(mockKinService.buildAndSignTransaction(any(),any(), anyLong(), any(), eq(new KinMemo("1-1a2c-someMemo")), any()))
                .thenReturn(Promise.Companion.of(kinTransaction));
        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(Collections.singletonList(kinPayment)));

        final Transaction[] transaction = new Transaction[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.buildTransaction(expectedAccountId, expectedAmount, 100, "someMemo").run(new ResultCallback<Transaction>() {
                @Override
                public void onResult(Transaction result) {
                    transaction[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        final TransactionId[] transactionId = new TransactionId[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.sendTransaction(transaction[0]).run(new ResultCallback<TransactionId>() {
                @Override
                public void onResult(TransactionId result) {
                    transactionId[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        verify(mockKinService).buildAndSignTransaction(any(),any(),anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId[0]);
        assertEquals("1-1a2c-someMemo", transaction[0].getMemo());
    }

    @Test
    public void buildAndSendTransactionAsync_noMemo() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAANjf/bJmcEcUJlHQ9wterZa4+GNRUXuNzzk80U3de+EYAAAAZABwt4MAAAABAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAABAAAABzEtMWEyYy0AAAAAAQAAAAAAAAABAAAAANjf/bJmcEcUJlHQ9wterZa4+GNRUXuNzzk80U3de+EYAAAAAAAAAAAAU+xgAAAAAAAAAAHde+EYAAAAQBn/cX7q5P0wREQtwH5De74Rufr9GSz7yLeQyRPDb13wEEkVVzfd6ArC1enu0kERFjlz1t+i3jOZRDmfuApoKw4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = new BigDecimal("55.0");

        when(mockKinService.buildAndSignTransaction(any(),any(), anyLong(), any(), eq(new KinMemo("1-1a2c-")), any()))
                .thenReturn(Promise.Companion.of(kinTransaction));
        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(Collections.singletonList(kinPayment)));

        final Transaction[] transaction = new Transaction[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.buildTransaction(expectedAccountId, expectedAmount, 100).run(new ResultCallback<Transaction>() {
                @Override
                public void onResult(Transaction result) {
                    transaction[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        final TransactionId[] transactionId = new TransactionId[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.sendTransaction(transaction[0]).run(new ResultCallback<TransactionId>() {
                @Override
                public void onResult(TransactionId result) {
                    transactionId[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        verify(mockKinService).buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId[0]);
        assertEquals("1-1a2c-", transaction[0].getMemo());
    }

    @Test
    public void sendWhitelistTransactionSync() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0).getAmount().getValue();

        when(mockKinService.buildAndSignTransaction(any(),any(), anyLong(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(kinTransaction));
        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(Collections.singletonList(kinPayment)));

        Transaction transaction = kinAccount.buildTransactionSync(expectedAccountId, expectedAmount, 100);

        assertEquals(transaction.getWhitelistableTransaction().getNetworkPassphrase(), "Kin Testnet ; December 2018");
        assertEquals(transaction.getWhitelistableTransaction().getTransactionPayload(), "AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");

        TransactionId transactionId = kinAccount.sendWhitelistTransactionSync("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");

        verify(mockKinService).buildAndSignTransaction(any(),any(), anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId);
    }

    @Test(expected = OperationFailedException.class)
    public void sendWhitelistTransactionSync_invalid_error() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0).getAmount().getValue();

        when(mockKinService.buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any()))
                .thenReturn(Promise.Companion.error(new IndexOutOfBoundsException()));
        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(Collections.singletonList(kinPayment)));

        Transaction transaction = kinAccount.buildTransactionSync(expectedAccountId, expectedAmount, 100);
        TransactionId transactionId = kinAccount.sendWhitelistTransactionSync("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");

        verify(mockKinService).buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId);
    }

    @Test(expected = OperationFailedException.class)
    public void sendWhitelistTransactionSync_send_error() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        String expectedAccountId = kinTransaction.getSigningSource().stellarBase32Encode();
        BigDecimal expectedAmount = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0).getAmount().getValue();

        when(mockKinService.buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(kinTransaction));
        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.error(new KinService.FatalError.UnexpectedServiceError(new Exception())));

        Transaction transaction = kinAccount.buildTransactionSync(expectedAccountId, expectedAmount, 100);
        TransactionId transactionId = kinAccount.sendWhitelistTransactionSync("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");

        verify(mockKinService).buildAndSignTransaction(any(), any(), anyLong(), any(), any(), any());
        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());
        verify(mockAccountContext).getAccount();

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId);
    }

    @Test
    public void buildAndSendWhitelistTransactionAsync() throws Exception {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment kinPayment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);
        TransactionId expectedTransactionId = new TransactionIdImpl(kinTransaction.getTransactionHash());

        when(mockAccountContext.sendKinPayments(any(), any(), any(), any(), any(), any()))
                .thenReturn(Promise.Companion.of(Collections.singletonList(kinPayment)));

        final TransactionId[] transactionId = new TransactionId[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.sendWhitelistTransaction("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=").run(new ResultCallback<TransactionId>() {
                @Override
                public void onResult(TransactionId result) {
                    transactionId[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        verify(mockAccountContext).sendKinPayments(any(), any(), any(), any(), any(), any());

        verifyNoMoreInteractions(mockKinService);
        verifyNoMoreInteractions(mockAccountContext);

        assertEquals(expectedTransactionId, transactionId[0]);
    }


    @Test
    public void getBalanceSync() throws Exception {
        KinAmount amount = new KinAmount(11.0);

        ValueSubject<KinBalance> subject = new ValueSubject<>();
        subject.onNext(new KinBalance(amount));

        Balance expectedBalance = new BalanceImpl(amount);

        Balance balance = kinAccount.getBalanceSync();

        assertEquals(expectedBalance, balance);
        assertEquals(expectedBalance.value(), balance.value());
        assertEquals(expectedBalance.value(2), balance.value(2));
    }

    @Test(expected = OperationFailedException.class)
    public void getBalanceSync_error() throws Exception {
        when(mockAccountContext.getAccount(eq(true)))
                .thenReturn(Promise.Companion.error(new RuntimeException()));

        KinAmount amount = new KinAmount(11.0);

        ValueSubject<KinBalance> subject = new ValueSubject<>();
        subject.onNext(new KinBalance(amount));

        Balance expectedBalance = new BalanceImpl(amount);

        Balance balance = kinAccount.getBalanceSync();

        assertEquals(expectedBalance, balance);
    }


    @Test
    public void getBalanceAsync() throws Exception {
        KinAmount amount = new KinAmount(11.0);

        ValueSubject<KinBalance> subject = new ValueSubject<>();
        subject.onNext(new KinBalance(amount));

        Balance expectedBalance = new BalanceImpl(amount);

        final Balance[] balance = new Balance[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.getBalance().run(new ResultCallback<Balance>() {
                @Override
                public void onResult(Balance result) {
                    balance[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        assertEquals(expectedBalance, balance[0]);
    }

    @Test
    public void getStatusSync() throws Exception {
        int status = kinAccount.getStatusSync();
        assertEquals(AccountStatus.CREATED, status);
    }

    @Test(expected = OperationFailedException.class)
    public void getStatusSync_error() throws Exception {
        when(mockAccountContext.getAccount())
                .thenReturn(Promise.Companion.error(new RuntimeException()));

        int status = kinAccount.getStatusSync();

        assertEquals(AccountStatus.CREATED, status);
    }

    @Test
    public void getStatusAsync() throws Exception {
        final int[] status = new int[1];
        UtilsKt.latchOperation(countDownLatch -> {
            kinAccount.getStatus().run(new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    status[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        assertEquals(AccountStatus.CREATED, status[0]);
    }

    @Test(expected = AccountDeletedException.class)
    public void sendTransactionSync_DeletedAccount_Exception() throws Exception {
        kinAccount.markAsDeleted();

        Transaction transaction = kinAccount.buildTransactionSync("GDKJAMCTGZGD6KM7RBEII6QUYAHQQUGERXKM3ESHBX2UUNTNAVNB3OGX", new BigDecimal("12.2"), 100);
        kinAccount.sendTransactionSync(transaction);
    }

    @Test(expected = AccountDeletedException.class)
    public void sendWhitelistTransaction_DeletedAccount_Exception() throws Exception {
        kinAccount.markAsDeleted();

        String whitelist = "whitelist test string";
        kinAccount.sendWhitelistTransactionSync(whitelist);
    }

    @Test(expected = AccountDeletedException.class)
    public void getBalanceSync_DeletedAccount_Exception() throws Exception {
        kinAccount.markAsDeleted();
        kinAccount.getBalanceSync();
    }

    @Test(expected = AccountDeletedException.class)
    public void getStatusSync_DeletedAccount_Exception() throws Exception {
        kinAccount.markAsDeleted();
        kinAccount.getStatusSync();
    }

    @Test
    public void getPublicAddress_DeletedAccount_Empty() throws Exception {
        kinAccount.markAsDeleted();

        assertNull(kinAccount.getPublicAddress());
    }

    @Test
    public void addBalanceListener() {
        ValueSubject<KinBalance> subject = new ValueSubject<>();

        subject.onNext(new KinBalance(new KinAmount(22L)));

        when(mockAccountContext.observeBalance(any()))
                .thenReturn(subject);

        ArrayList<Balance> updates = new ArrayList<>();

        UtilsKt.latchOperation((latch) -> {
            kinAccount.addBalanceListener(data -> {
                updates.add(data);
                latch.countDown();
            });
            return null;
        });

        assertEquals(UtilsKt.toBalance(new KinBalance(new KinAmount(22L))), updates.get(0));
    }

    @Test
    public void addPaymentListener() {
        KinTransaction kinTransaction = TestUtils.kinTransactionFromXDR("AAAAAOW4vFw4Y2Te8vHfGvFd9JxpTW/2L6jnmEejDv3pRilbAAAAZABZ2/gAAAADAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAABAAAAADJhY8ODfklc4sHXp+xFywL6OxdHaWwljtepeFe9KOUOAAAAAAAAAAAAIAsgAAAAAAAAAAHpRilbAAAAQN1YkzWbQdhatwAHZW4dlfVo61cbHfFFY5I6UOcnrwgMZ5bN+iaCMi6V8tEjxnKjP9BjLGJnbvg7d9iYCcQiWg4=");
        KinPayment payment = StellarBaseTypeConversionsKt.asKinPayments(kinTransaction).get(0);

        ListSubject<KinPayment> subject = new ListSubject<>();
        subject.onNext(StellarBaseTypeConversionsKt.asKinPayments(kinTransaction));

        when(mockAccountContext.observePayments(eq(ObservationMode.ActiveNewOnly.INSTANCE)))
                .thenReturn(subject);

        ArrayList<PaymentInfo> updates = new ArrayList<>();

        UtilsKt.latchOperation((latch) -> {
            kinAccount.addPaymentListener(data -> {
                updates.add(data);
                latch.countDown();
            });
            return null;
        });

        PaymentInfo paymentInfo = UtilsKt.toPaymentInfo(payment);
        assertEquals(paymentInfo.amount(), updates.get(0).amount());
        assertEquals(paymentInfo.createdAt(), updates.get(0).createdAt());
        assertEquals(paymentInfo.destinationPublicKey(), updates.get(0).destinationPublicKey());
        assertEquals(paymentInfo.memo(), updates.get(0).memo());
        assertEquals(paymentInfo.fee(), updates.get(0).fee());
        assertEquals(paymentInfo.sourcePublicKey(), updates.get(0).sourcePublicKey());
        assertEquals(paymentInfo.hash(), updates.get(0).hash());
    }

    @Test
    public void addAccountCreationListener() {
        ArrayList<Boolean> updates = new ArrayList<>();

        final ListenerRegistration[] registration = new ListenerRegistration[1];
        UtilsKt.latchOperation((latch) -> {
            registration[0] = kinAccount.addAccountCreationListener(data -> {
                updates.add(true);
                latch.countDown();
            });
            return null;
        });
        registration[0].remove();

        assertEquals(1, updates.size());
    }

    @Test
    public void export() throws CryptoException {

        BackupRestore mockBackupRestore = mock(BackupRestore.class);

        when(mockBackupRestore.exportWallet(any(), any()))
                .thenReturn("exported_string");

        kinAccount = new KinAccountImpl(
                expectedRandomAccount,
                mockBackupRestore,
                mockAccountContext,
                mockKinService,
                NetworkEnvironment.KinStellarTestNetKin3.INSTANCE,
                new AppId(APP_ID)
        );

        String exported = kinAccount.export("passw0rd");

        assertEquals("exported_string", exported);
    }
}
