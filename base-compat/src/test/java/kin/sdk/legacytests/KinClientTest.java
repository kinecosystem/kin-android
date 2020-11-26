package kin.sdk.legacytests;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kin.sdk.base.KinAccountContext;
import org.kin.sdk.base.KinEnvironment;
import org.kin.sdk.base.models.AppId;
import org.kin.sdk.base.models.Key;
import org.kin.sdk.base.models.QuarkAmount;
import org.kin.sdk.base.network.services.KinService;
import org.kin.sdk.base.stellar.models.NetworkEnvironment;
import org.kin.sdk.base.storage.Storage;
import org.kin.sdk.base.tools.ExecutorServices;
import org.kin.sdk.base.tools.Optional;
import org.kin.sdk.base.tools.Promise;
import org.kin.stellarfork.KeyPair;
import org.mockito.MockitoAnnotations;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kin.sdk.BackupRestore;
import kin.sdk.Environment;
import kin.sdk.KeyStore;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;
import kin.sdk.exception.OperationFailedException;
import kin.sdk.internal.KinAccountImpl;
import kin.sdk.internal.UtilsKt;
import kin.utils.Request;
import kin.utils.ResultCallback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
public class KinClientTest {

    private static final String APP_ID = "1a2c";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private KinClient kinClient;
    private KeyStore fakeKeyStore;
    private Environment fakeEnvironment;
    private KinEnvironment kinEnvironment;
    private KinService mockKinService;

    private KeyPair account1 = createRandomAccount();
    private KeyPair account2 = createRandomAccount();
    private KeyPair account3 = createRandomAccount();

    private Context ctx;
    private Storage mockStorage;
    private BackupRestore mockBackupRestore;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Request.setCallbackExecutor(executorService);
        fakeEnvironment = new Environment("empty", Environment.TEST.getNetworkPassphrase());
        fakeKeyStore = new FakeKeyStore();
        kinClient = createNewKinClient();
    }

    @NonNull
    private KinClient createNewKinClient() {
        return createNewKinClient(null);
    }

    @NonNull
    private KinClient createNewKinClient(@Nullable KinService kinService) {

        ctx = mock(Context.class);
        mockStorage = mock(Storage.class);
        mockBackupRestore = mock(BackupRestore.class);
        if (kinService == null) {
            this.mockKinService = mock(KinService.class);
        } else {
            this.mockKinService = kinService;
        }
        ExecutorServices executors = new ExecutorServices();
        ILoggerFactory logger = LoggerFactory.getILoggerFactory();
        kinEnvironment = new KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3.INSTANCE)
                .setKinService(mockKinService)
                .setStorage(mockStorage)
                .build();

        org.kin.sdk.base.models.KinAccount.Id account1KinAccountId = new org.kin.sdk.base.models.KinAccount.Id(account1.getPublicKey());
        org.kin.sdk.base.models.KinAccount kinAccount1 = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(account1.getPublicKey()));

        when(mockStorage.getStoredAccount(eq(account1KinAccountId)))
                .thenReturn(Promise.Companion.of(Optional.of(kinAccount1)));

        org.kin.sdk.base.models.KinAccount.Id account2KinAccountId = new org.kin.sdk.base.models.KinAccount.Id(account2.getPublicKey());
        org.kin.sdk.base.models.KinAccount kinAccount2 = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(account2.getPublicKey()));

        when(mockStorage.getStoredAccount(eq(account2KinAccountId)))
                .thenReturn(Promise.Companion.of(Optional.of(kinAccount2)));

        org.kin.sdk.base.models.KinAccount.Id account3KinAccountId = new org.kin.sdk.base.models.KinAccount.Id(account3.getPublicKey());
        org.kin.sdk.base.models.KinAccount kinAccount3 = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(account3.getPublicKey()));

        when(mockStorage.getStoredAccount(eq(account3KinAccountId)))
                .thenReturn(Promise.Companion.of(Optional.of(kinAccount3)));

        when(mockStorage.deleteAllStorage(eq(account1KinAccountId)))
                .thenReturn(Promise.Companion.of(true));
        when(mockStorage.deleteAllStorage(eq(account2KinAccountId)))
                .thenReturn(Promise.Companion.of(true));
        when(mockStorage.deleteAllStorage(eq(account3KinAccountId)))
                .thenReturn(Promise.Companion.of(true));

        when(mockKinService.getAccount(eq(kinAccount1.getId())))
                .thenReturn(Promise.Companion.of(kinAccount1));
        when(mockKinService.getAccount(eq(kinAccount2.getId())))
                .thenReturn(Promise.Companion.of(kinAccount2));
        when(mockKinService.getAccount(eq(kinAccount3.getId())))
                .thenReturn(Promise.Companion.of(kinAccount3));

        return new KinClient(
                ctx,
                fakeEnvironment,
                APP_ID,
                "",
                mockBackupRestore,
                fakeKeyStore,
                mockStorage,
                kinEnvironment);
    }

    @Test
    public void testPublicCtr() {
        Context context = mock(Context.class);
        when(context.getApplicationContext())
                .thenReturn(context);
        SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        SharedPreferences.Editor mockEditor = mock(SharedPreferences.Editor.class);
        when(mockEditor.putString(any(), any())).thenReturn(mockEditor);
        when(mockEditor.remove(any())).thenReturn(mockEditor);

        when(context.getSharedPreferences(any(), eq(Context.MODE_PRIVATE)))
                .thenReturn(sharedPreferences);
        when(sharedPreferences.edit()).thenReturn(mockEditor);
        when(sharedPreferences.getString(any(), any())).thenReturn("");
        when(context.getFilesDir()).thenReturn(new File(""));

        new KinClient(context, fakeEnvironment, APP_ID, "");
    }

    @Test
    public void kinClientBuilder_missingEnvironment_NullPointerException() {
        expectedEx.expect(NullPointerException.class);

        Context ctx = mock(Context.class);

        new KinClient(
                ctx,
                null,
                APP_ID,
                "",
                mockBackupRestore,
                fakeKeyStore,
                mockStorage
        );

    }

    @Test
    public void kinClientBuilder_missingStoreKey_NullPointerException() {
        expectedEx.expect(NullPointerException.class);

        Context ctx = mock(Context.class);

        new KinClient(
                ctx,
                fakeEnvironment,
                APP_ID,
                null,
                mockBackupRestore,
                fakeKeyStore,
                mockStorage);
    }

    @Test
    public void kinClientBuilder_missingContext_NullPointerException() {
        expectedEx.expect(NullPointerException.class);

        new KinClient(null,
                fakeEnvironment,
                APP_ID,
                null,
                mockBackupRestore,
                fakeKeyStore,
                mockStorage
        );
    }

    @Test
    public void addAccount_NewAccount() throws Exception {
        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());

        KinAccount kinAccount = kinClient.addAccount();

        assertNotNull(kinAccount);
        assertNotNull(kinAccount.getPublicAddress());
        assertFalse(kinAccount.getPublicAddress().isEmpty());
    }

    @Test
    public void createAccount_AddAccount() throws Exception {
        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());

        KinAccount kinAccount = kinClient.addAccount();

        assertNotNull(kinAccount);
        assertNotNull(kinAccount.getPublicAddress());
        assertFalse(kinAccount.getPublicAddress().isEmpty());
    }

    @Test
    public void createAccount_AddMultipleAccount() throws Exception {

        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());

        KinAccount kinAccount = kinClient.addAccount();
        KinAccount kinAccount2 = kinClient.addAccount();

        assertNotNull(kinAccount);
        assertNotNull(kinAccount2);
        assertNotNull(kinAccount.getPublicAddress());
        assertFalse(kinAccount.getPublicAddress().isEmpty());
        assertNotNull(kinAccount2.getPublicAddress());
        assertFalse(kinAccount2.getPublicAddress().isEmpty());
        assertNotEquals(kinAccount, kinAccount2);
    }

//    @Test
//    public void getAccount_AddMultipleAccount() throws Exception {
//        KinAccount kinAccount = kinClient.addAccount();
//        KinAccount kinAccount2 = kinClient.addAccount();
//
//        assertNotNull(kinAccount);
//        assertNotNull(kinAccount2);
//        assertNotNull(account1);
//        assertNotNull(account2);
//        assertThat(kinAccount, equalTo(account1));
//        assertThat(kinAccount2, equalTo(account2));
//    }

    @Test
    public void getAccount_ExistingAccount_AddMultipleAccount() throws Exception {
        KeyPair account1 = createKeyStoreWithRandomAccount();

        kinClient = createNewKinClient();

        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());


        KinAccount kinAccount2 = kinClient.addAccount();
        KinAccount kinAccount3 = kinClient.addAccount();

        KinAccount expectedAccount3 = kinClient.getAccount(2);
        KinAccount expectedAccount2 = kinClient.getAccount(1);
        KinAccount expectedAccount1 = kinClient.getAccount(0);

        assertNotNull(expectedAccount1);
        assertNotNull(expectedAccount2);
        assertNotNull(expectedAccount3);
        assertEquals(account1.getAccountId(), expectedAccount1.getPublicAddress());
        assertEquals(kinAccount2, expectedAccount2);
        assertEquals(kinAccount3, expectedAccount3);
    }

    @Test
    public void getAccount_ExistingMultipleAccount() throws Exception {

        fakeKeyStore = new FakeKeyStore(Arrays.asList(account1, account2));
        kinClient = createNewKinClient();
        KinAccount expectedAccount2 = kinClient.getAccount(1);
        KinAccount expectedAccount1 = kinClient.getAccount(0);

        assertNotNull(expectedAccount1);
        assertNotNull(expectedAccount2);
        assertEquals(account1.getAccountId(), expectedAccount1.getPublicAddress());
        assertEquals(account2.getAccountId(), expectedAccount2.getPublicAddress());
    }

    @Test
    public void createMultipleKinClients_SameAccounts() throws Exception {
        KinClient kinClient1 = createNewKinClient();

        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());

        kinClient1.addAccount();
        kinClient1.addAccount();

        assertEquals(kinClient1.getAccountCount(), 2);


        KinService mockKinService = mock(KinService.class);
        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any(org.kin.sdk.base.models.KinAccount.Id.class));

        KinClient kinClient2 = createNewKinClient(mockKinService);

        assertTrue(kinClient2.hasAccount());
        assertEquals(kinClient2.getAccountCount(), 2);

        kinClient2.addAccount();
        kinClient2.addAccount();

        assertEquals(kinClient1.getAccountCount(), 4);
        assertEquals(kinClient2.getAccountCount(), 4);

        KinAccount expectedAccount1 = kinClient1.getAccount(0);
        KinAccount expectedAccount2 = kinClient1.getAccount(1);
        KinAccount expectedAccount3 = kinClient1.getAccount(2);
        KinAccount expectedAccount4 = kinClient1.getAccount(3);

        assertNotNull(expectedAccount1);
        assertNotNull(expectedAccount2);
        assertNotNull(expectedAccount3);
        assertNotNull(expectedAccount4);
        assertEquals(Objects.requireNonNull(kinClient2.getAccount(0)).getPublicAddress(), expectedAccount1.getPublicAddress());
        assertEquals(Objects.requireNonNull(kinClient2.getAccount(1)).getPublicAddress(), expectedAccount2.getPublicAddress());
        assertEquals(Objects.requireNonNull(kinClient2.getAccount(2)).getPublicAddress(), expectedAccount3.getPublicAddress());
        assertEquals(Objects.requireNonNull(kinClient2.getAccount(3)).getPublicAddress(), expectedAccount4.getPublicAddress());
    }

    @Test
    public void getAccount_NegativeIndex() throws Exception {
        createKeyStoreWithRandomAccount();

        assertNull(kinClient.getAccount(-1));
    }

    @NonNull
    private KeyPair createRandomAccount() {
        return KeyPair.random();
    }

    @Test
    public void getAccount_EmptyKeyStore_Null() throws Exception {
        KinAccount kinAccount = kinClient.getAccount(0);

        assertNull(kinAccount);
    }

    @Test
    public void getAccount_ExistingAccount_SameAccount() throws Exception {
        KeyPair account = createKeyStoreWithRandomAccount();

        KinAccount kinAccount = kinClient.getAccount(0);

        assertEquals(account.getAccountId(), kinAccount.getPublicAddress());
    }

    @NonNull
    private KeyPair createKeyStoreWithRandomAccount() {
        ArrayList<KeyPair> accounts = new ArrayList<>();
        accounts.add(account1);
        fakeKeyStore = new FakeKeyStore(accounts);
        kinClient = createNewKinClient();
        return account1;
    }

    @Test
    public void hasAccount_EmptyKeyStore_False() throws Exception {
        assertFalse(kinClient.hasAccount());
    }

    @Test
    public void hasAccount_ExistingAccount_True() throws Exception {
        createKeyStoreWithRandomAccount();

        assertTrue(kinClient.hasAccount());
    }

    @Test
    public void hasAccount_ExistingMultipleAccounts_True() throws Exception {

        fakeKeyStore = new FakeKeyStore(Arrays.asList(account1, account2));

        kinClient = createNewKinClient();
        assertTrue(kinClient.hasAccount());
    }

    @Test
    public void deleteAccount() throws Exception {
        createKeyStoreWithRandomAccount();

        assertTrue(kinClient.hasAccount());
        kinClient.deleteAccount(0);

        assertFalse(kinClient.hasAccount());
    }

    @Test
    public void deleteAccount_MultipleAccounts() throws Exception {
        fakeKeyStore = new FakeKeyStore(Arrays.asList(account1, account2));

        kinClient = createNewKinClient();
        kinClient.deleteAccount(0);

        assertTrue(kinClient.hasAccount());
        kinClient.deleteAccount(0);
        assertFalse(kinClient.hasAccount());
    }

    @Test
    public void deleteAccount_AtIndex() throws Exception {
        fakeKeyStore = new FakeKeyStore(Arrays.asList(account1, account2));

        kinClient = createNewKinClient();
        kinClient.deleteAccount(1);

        assertTrue(kinClient.hasAccount());
        assertEquals(account1.getAccountId(), Objects.requireNonNull(kinClient.getAccount(0)).getPublicAddress());
    }

    @Test
    public void deleteAccount_IndexOutOfBounds() throws Exception {
        fakeKeyStore = new FakeKeyStore(Arrays.asList(account1, account2));

        kinClient = createNewKinClient();
        kinClient.deleteAccount(3);

        assertNotNull(kinClient.getAccount(0));
        assertNotNull(kinClient.getAccount(1));
        assertEquals(kinClient.getAccountCount(), 2);
    }

    @Test
    public void deleteAccount_NegativeIndex() throws Exception {
        fakeKeyStore = new FakeKeyStore(Arrays.asList(account1, account2));

        kinClient = createNewKinClient();
        kinClient.deleteAccount(-1);

        assertNotNull(kinClient.getAccount(0));
        assertNotNull(kinClient.getAccount(1));
        assertEquals(kinClient.getAccountCount(), 2);
    }

    @Test
    public void getAccountCount() throws Exception {
        fakeKeyStore = new FakeKeyStore(Arrays.asList(account1, account2, account3));
        kinClient = createNewKinClient();

        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());

        assertEquals(kinClient.getAccountCount(), 3);
        kinClient.deleteAccount(2);
        kinClient.deleteAccount(1);
        assertEquals(kinClient.getAccountCount(), 1);

        KinAccount newAccount = kinClient.addAccount();
        org.kin.sdk.base.models.KinAccount.Id newAccountKinAccountId = new org.kin.sdk.base.models.KinAccount.Id(KeyPair.fromAccountId(Objects.requireNonNull(newAccount.getPublicAddress())).getPublicKey());
        when(mockStorage.deleteAllStorage(eq(newAccountKinAccountId)))
                .thenReturn(Promise.Companion.of(true));

        assertEquals(kinClient.getAccountCount(), 2);
        kinClient.deleteAccount(1);
        kinClient.deleteAccount(0);
        assertEquals(kinClient.getAccountCount(), 0);
    }

    @Test
    public void clearAllAccounts() {
        fakeKeyStore = new FakeKeyStore(Arrays.asList(account1, account2, account3));
        kinClient = createNewKinClient();

        kinClient.clearAllAccounts();

        assertEquals(kinClient.getAccountCount(), 0);
    }

    @Test
    public void getEnvironment() throws Exception {
        String url = "My awesome Horizon server";
        Environment environment = new Environment(url, Environment.TEST.getNetworkPassphrase());

        Context ctx = mock(Context.class);

        Storage mockStorage = mock(Storage.class);
        doAnswer( invocation -> Promise.Companion.of(Optional.Companion.empty())).when(mockStorage).getMinApiVersion();

        kinClient = new KinClient(
                ctx,
                environment,
                APP_ID,
                "",
                mock(BackupRestore.class),
                fakeKeyStore,
                mockStorage
                );
        Environment actualEnvironment = kinClient.getEnvironment();

        assertNotNull(actualEnvironment);
        assertFalse(actualEnvironment.isMainNet());
        assertEquals(url, actualEnvironment.getNetworkUrl());
        assertEquals(Environment.TEST.getNetworkPassphrase(), actualEnvironment.getNetworkPassphrase());
        assertEquals(Environment.TEST.getNetwork(), actualEnvironment.getNetwork());
    }

    @Test
    public void environment_MissingNetworkUrl_NullPointerException() throws Exception {
        expectedEx.expect(NullPointerException.class);


        new Environment(null, Environment.TEST.getNetworkPassphrase());
    }

    @Test
    public void environment_MissingNetworkPassphrase_IllegalArgumentException() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("networkPassphrase");

        new Environment(Environment.TEST.getNetworkUrl(), null);
    }

    @Test
    public void environment_EmptyNetworkUrl_IllegalArgumentException() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("networkUrl");

        new Environment("", Environment.TEST.getNetworkPassphrase());
    }

    @Test
    public void environment_EmptyNetworkPassphrase_IllegalArgumentException() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("networkPassphrase");

        new Environment(Environment.TEST.getNetworkUrl(), "");
    }

    @Test
    public void getMinimumFeeSync_from_storage() throws Exception {
        long expectedMinFee = 100;
        when(mockStorage.getMinFee())
                .thenReturn(Promise.Companion.of(Optional.of(new QuarkAmount(expectedMinFee))));

        long minFee = kinClient.getMinimumFeeSync();
        assertEquals(expectedMinFee, minFee);
    }

    @Test(expected = OperationFailedException.class)
    public void getMinimumFeeSync_error() throws Exception {
        when(mockStorage.getMinFee())
                .thenReturn(Promise.Companion.error(new RuntimeException()));

        kinClient.getMinimumFeeSync();
    }

    @Test(expected = KinService.FatalError.SDKUpgradeRequired.class)
    public void getMinimumFeeSync_upgrade_error() throws Exception {
        when(mockStorage.getMinFee())
                .thenReturn(Promise.Companion.error(KinService.FatalError.SDKUpgradeRequired.INSTANCE));

        kinClient.getMinimumFeeSync();
    }

    @Test
    public void getMinimumFeeAsync_from_storage() throws Exception {
        long expectedMinFee = 100;
        when(mockStorage.getMinFee())
                .thenReturn(Promise.Companion.of(Optional.of(new QuarkAmount(expectedMinFee))));

        final long[] minFee = new long[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinClient.getMinimumFee().run(new ResultCallback<Long>() {
                @Override
                public void onResult(Long result) {
                    minFee[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        assertEquals(expectedMinFee, minFee[0]);
    }

    @Test
    public void getMinimumFeeSync_from_network() throws Exception {
        long expectedMinFee = 100;
        when(mockStorage.getMinFee())
                .thenReturn(Promise.Companion.of(Optional.Companion.empty()));

        when(mockKinService.getMinFee())
                .thenReturn(Promise.Companion.of(new QuarkAmount(expectedMinFee)));

        when(mockStorage.setMinFee(eq(new QuarkAmount(100))))
                .thenReturn(Promise.Companion.of(Optional.of(new QuarkAmount(100))));

        long minFee = kinClient.getMinimumFeeSync();
        assertEquals(expectedMinFee, minFee);
    }

    @Test
    public void getMinimumFeeAsync_from_network() throws Exception {
        long expectedMinFee = 100;
        when(mockStorage.getMinFee())
                .thenReturn(Promise.Companion.of(Optional.Companion.empty()));

        when(mockKinService.getMinFee())
                .thenReturn(Promise.Companion.of(new QuarkAmount(expectedMinFee)));

        when(mockStorage.setMinFee(eq(new QuarkAmount(100))))
                .thenReturn(Promise.Companion.of(Optional.of(new QuarkAmount(100))));


        final long[] minFee = new long[1];

        UtilsKt.latchOperation(countDownLatch -> {
            kinClient.getMinimumFee().run(new ResultCallback<Long>() {
                @Override
                public void onResult(Long result) {
                    minFee[0] = result;
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Exception e) {
                    countDownLatch.countDown();
                }
            });
            return null;
        });

        assertEquals(expectedMinFee, minFee[0]);
    }

    @Test
    public void getAccountByPublicAddress() throws Exception {

        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());

        KinAccount kinAccount1 = kinClient.addAccount();
        KinAccount kinAccount2 = kinClient.addAccount();

        KinAccount account1 = kinClient.getAccountByPublicAddress(Objects.requireNonNull(kinAccount1.getPublicAddress()));
        KinAccount account2 = kinClient.getAccountByPublicAddress(Objects.requireNonNull(kinAccount2.getPublicAddress()));

        assertEquals(account1, kinAccount1);
        assertEquals(account2, kinAccount2);
    }

    @Test
    public void importAccount() throws Exception {
        ctx = mock(Context.class);
        mockStorage = mock(Storage.class);
        mockBackupRestore = mock(BackupRestore.class);
        mockKinService = mock(KinService.class);
        ExecutorServices executors = new ExecutorServices();
        kinEnvironment = new KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3.INSTANCE)
                .setKinService(mockKinService)
                .setStorage(mockStorage)
                .build();

        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());

        KeyStore mockKeyStore = mock(KeyStore.class);

        when(mockKeyStore.importAccount(any(), any()))
                .thenReturn(account1);

        kinClient = new KinClient(
                ctx,
                fakeEnvironment,
                APP_ID,
                "",
                mockBackupRestore,
                mockKeyStore,
                mockStorage,
                kinEnvironment);


        kinClient.importAccount("", "password");

        KinAccount kinAccount1 = kinClient.getAccountByPublicAddress(Objects.requireNonNull(account1.getAccountId()));

        assertEquals(
                new KinAccountImpl(
                        account1,
                        mockBackupRestore,
                        new KinAccountContext.Builder(kinEnvironment).useExistingAccount(new org.kin.sdk.base.models.KinAccount.Id(account1.getPublicKey())).build(),
                        mockKinService,
                        NetworkEnvironment.KinStellarTestNetKin3.INSTANCE,
                        new AppId(APP_ID)
                ),
                kinAccount1
        );
    }

    @Test
    public void testBadAppIds_empty() throws Exception {
        kinClient = new KinClient(
                ctx,
                fakeEnvironment,
                "",
                "",
                mockBackupRestore,
                fakeKeyStore,
                mockStorage,
                kinEnvironment);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadAppIds_bad_id() throws Exception {
        kinClient = new KinClient(
                ctx,
                fakeEnvironment,
                "not_valid",
                "",
                mockBackupRestore,
                fakeKeyStore,
                mockStorage,
                kinEnvironment);
    }
}
