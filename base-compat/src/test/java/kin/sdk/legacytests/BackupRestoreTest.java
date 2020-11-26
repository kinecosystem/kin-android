package kin.sdk.legacytests;


import android.content.Context;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kin.sdk.base.KinEnvironment;
import org.kin.sdk.base.models.Key;
import org.kin.sdk.base.network.services.KinService;
import org.kin.sdk.base.stellar.models.NetworkEnvironment;
import org.kin.sdk.base.storage.Storage;
import org.kin.sdk.base.tools.Promise;
import org.kin.stellarfork.KeyPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import kin.sdk.BackupRestore;
import kin.sdk.Environment;
import kin.sdk.KeyStore;
import kin.sdk.KinAccount;
import kin.sdk.KinClient;
import kin.sdk.exception.CorruptedDataException;
import kin.sdk.exception.CryptoException;
import kin.sdk.internal.BackupRestoreImpl;
import kin.sdk.internal.KeyStoreImpl;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BackupRestoreTest {

    static final class IntegConsts {
        static final String TEST_NETWORK_URL = Environment.TEST.getNetworkUrl();
        static final String TEST_NETWORK_ID = Environment.TEST.getNetworkPassphrase();
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private static final String APP_ID = "1a2c";
    private Environment environment;
    private KinClient kinClient1;
    private KinClient kinClient2;
    private KeyStore keystore1;
    private KeyStore keystore2;
    private BackupRestore backupRestore;

    Context ctx;
    Storage mockStorage;
    KinService mockKinService;

    @Before
    public void setup() {

        ctx = mock(Context.class);
        mockStorage = mock(Storage.class);
        mockKinService = mock(KinService.class);

        environment = new Environment(IntegConsts.TEST_NETWORK_URL, IntegConsts.TEST_NETWORK_ID);
        backupRestore = new BackupRestoreImpl();
        keystore1 = new KeyStoreImpl(new FakeStore(), backupRestore);
        keystore2 = new KeyStoreImpl(new FakeStore(), backupRestore);
        kinClient1 = createNewKinClient(keystore1);
        kinClient2 = createNewKinClient(keystore2);
        kinClient1.clearAllAccounts();
        kinClient2.clearAllAccounts();

        when(mockStorage.deleteAllStorage(any())).thenReturn(Promise.Companion.of(true));
    }

    private KinClient createNewKinClient(KeyStore keyStore) {
        KinEnvironment kinEnvironment = new KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNetKin3.INSTANCE)
                .setKinService(mockKinService)
                .setStorage(mockStorage)
                .build();

        doAnswer(invocation -> {
            org.kin.sdk.base.models.KinAccount.Id accountId = invocation.getArgument(0);
            org.kin.sdk.base.models.KinAccount kinAccount = new org.kin.sdk.base.models.KinAccount(new Key.PublicKey(accountId.getValue()));
            return Promise.Companion.of(kinAccount);
        }).when(mockKinService).getAccount(any());

        return new KinClient(ctx,
                environment,
                APP_ID,
                "",
                backupRestore,
                keyStore,
                mockStorage,
                kinEnvironment);
    }

    @Test
    public void backupAndRestore_Success() throws Throwable {
        Random random = new Random(System.currentTimeMillis());

        List<KeyPair> kinAccounts = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            KeyPair keyPair = KeyPair.random();
            kinAccounts.add(keyPair);
        }

        List<Throwable> errors = new ArrayList<>();
        kinAccounts.parallelStream()
                .forEach((keyPair) -> {
                    try {
                        String passphrase = String.valueOf(random.nextLong());
                        String exportedJson = backupRestore.exportWallet(keyPair, passphrase);
                        KeyPair importKeyPair = backupRestore.importWallet(exportedJson, passphrase);
                        assertEquals(importKeyPair.getAccountId(), keyPair.getAccountId());
                        assertArrayEquals(importKeyPair.getSecretSeed(), keyPair.getSecretSeed());
                    } catch (Throwable t) {
                        t.printStackTrace();
                        errors.add(t);
                    }
                });
        if (errors.size() > 0) {
            throw errors.get(0);
        }
    }

    @Test
    public void backupAndRestore_WrongPassphrase_CryptoException() throws CryptoException, CorruptedDataException {
        expectedEx.expect(CryptoException.class);

        KeyPair keyPair = KeyPair.random();
        String exportedJson = backupRestore.exportWallet(keyPair, "1234567890abcefghijkl");
        backupRestore.importWallet(exportedJson, "1234567890abcefghijklX");
    }

    @Test
    public void import_BadJson_CryptoException() throws CryptoException, CorruptedDataException {
        expectedEx.expect(CorruptedDataException.class);
        expectedEx.expectCause(isA(JSONException.class));

        backupRestore.importWallet("not a real json!!", "123456");
    }

    @Test
    public void import_TamperedSaltJson_CryptoException() throws CryptoException, CorruptedDataException {
        expectedEx.expect(CryptoException.class);

        testImportBackup("{\n"
                        + "  \"pkey\" : \"GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S\",\n"
                        + "  \"seed\" : \"f5b162bf9bfa93922b709b00a89e5bf7f61eef38717b35dabbabc73a68be77e2b498c5697f99c3f70882a8a11cc5e34f88b6f069f47443dbfa031fadd12e8b6af1cc142c902cfef9\",\n"
                        // change last bit in seed (first ones are salt)
                        + "  \"salt\" : \"d00564d4887b4ccade9f2b63211c37c3\"\n"
                        + "}",
                "123456",
                "GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S");
    }

    @Test
    public void import_TamperedSeedJson_CryptoException() throws CryptoException, CorruptedDataException {
        expectedEx.expect(CryptoException.class);

        testImportBackup("{\n"
                        + "  \"pkey\" : \"GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S\",\n"
                        // change last bit in seed (first ones are nonce)
                        + "  \"seed\" : \"f5b162bf9bfa93922b709b00a89e5bf7f61eef38717b35dabbabc73a68be77e2b498c5697f99c3f70882a8a11cc5e34f88b6f069f47443dbfa031fadd12e8b6af1cc142c902cfef8\",\n"
                        + "  \"salt\" : \"d00564d4887b4ccade9f2b63211c37c4\"\n"
                        + "}",
                "123456",
                "GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S");
    }

    @Test
    public void importFromIOS() throws CryptoException, CorruptedDataException {
        testImportBackup("{\n"
                        + "  \"pkey\" : \"GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S\",\n"
                        + "  \"seed\" : \"f5b162bf9bfa93922b709b00a89e5bf7f61eef38717b35dabbabc73a68be77e2b498c5697f99c3f70882a8a11cc5e34f88b6f069f47443dbfa031fadd12e8b6af1cc142c902cfef9\",\n"
                        + "  \"salt\" : \"d00564d4887b4ccade9f2b63211c37c4\"\n"
                        + "}",
                "123456",
                "GAQJH2KSSWOTX3LCEESPIJPY73QRCH55OBCCM3SYZ3EEWSTMJ4NYNT6S");
        testImportBackup("{\n"
                        + "  \"pkey\" : \"GDNLGOSLTZMZX5GNZ41538663709111FY26DPXLGXEIFJJ7VWDJVN3L5DOG7TFLZXHNCU\",\n"
                        + "  \"seed\" : \"0d2a1bab487f5591277692987bd66c6f07199120f3e8fab6d013ac81bc3bd648fa357d90b39178d105da130705ee6e8beaea4a5cf900b53978d8c8fecd72fee7bd5aa7295a619b9d\",\n"
                        + "  \"salt\" : \"7fb38499e44f084958e954b73f1c2cf0\"\n"
                        + "}",
                "123456",
                "GDNLGOSLTZMZX5GNZ4FY26DPXLGXEIFJJ7VWDJVN3L5DOG7TFLZXHNCU");
    }

    private void testImportBackup(String exportedJson, String passphrase, String publicKey)
            throws CryptoException, CorruptedDataException {
        KeyPair importKeyPair = backupRestore.importWallet(exportedJson, passphrase);
        assertEquals(importKeyPair.getAccountId(), publicKey);
    }


    @Test
    public void importAccount_AddOnlyIfNotExists() throws Exception {
        kinClient1.addAccount();
        kinClient1.addAccount();
        kinClient1.addAccount();
        KinAccount kinAccount = kinClient1.addAccount();
        String passphrase = UUID.randomUUID().toString();
        String exported = kinAccount.export(passphrase);
        kinClient1.importAccount(exported, passphrase);
        assertEquals(4, kinClient1.getAccountCount());
    }

    @Test
    public void backupRestore() throws Throwable {

        List<KinAccount> kinAccounts = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            KinAccount kinAccount = kinClient1.addAccount();
            kinAccounts.add(kinAccount);
        }

        List<Throwable> errors = new ArrayList<>();
        kinAccounts.parallelStream()
                .forEach((kinAccount) -> {
                    try {
                        String uuid = UUID.randomUUID().toString();
                        String exported = kinAccount.export(uuid);
                        KinAccount kinAccount2 = kinClient2.importAccount(exported, uuid);
                        assertEquals(kinAccount2.getPublicAddress(), kinAccount.getPublicAddress());
                    } catch (Throwable t) {
                        t.printStackTrace();
                        errors.add(t);
                    }
                });
        if (errors.size() > 0) {
            throw errors.get(0);
        }
    }

    @Test
    public void importAccount_AddNewAccount() throws Exception {
        KinAccount kinAccount = kinClient1.addAccount();
        String passphrase = UUID.randomUUID().toString();
        String exported = kinAccount.export(passphrase);
        kinClient1.importAccount(exported, passphrase);
        assertEquals(1, kinClient1.getAccountCount());

        kinClient1.clearAllAccounts();
        assertEquals(0, kinClient1.getAccountCount());

        kinClient1.importAccount(exported, passphrase);
        assertEquals(1, kinClient1.getAccountCount());
    }

    @Test
    public void importAccount_CreateKinClientAgain_AddOnlyIfNotExists() throws Exception {
        kinClient1.addAccount();
        kinClient1.addAccount();
        kinClient1.addAccount();
        KinAccount kinAccount = kinClient1.addAccount();
        String passphrase = UUID.randomUUID().toString();
        String exported = kinAccount.export(passphrase);
        kinClient1.importAccount(exported, passphrase);
        KinClient anotherKinClient = createNewKinClient(keystore1);
        assertEquals(4, anotherKinClient.getAccountCount());
        assertEquals(4, kinClient1.getAccountCount());
    }

    @Test(expected = NullPointerException.class)
    public void initKinClient_NullId_Exception() {
        kinClient1 = createNewKinClient(null);
    }

}
