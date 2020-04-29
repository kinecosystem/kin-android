package kin.sdk.legacytests;

import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kin.stellarfork.KeyPair;

import java.util.List;

import kin.sdk.BackupRestore;
import kin.sdk.exception.CreateAccountException;
import kin.sdk.exception.DeleteAccountException;
import kin.sdk.exception.LoadAccountException;
import kin.sdk.internal.KeyStoreImpl;
import kin.sdk.internal.Store;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class KeyStoreImplTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void newAccount() throws Exception {
        KeyStoreImpl keyStore = new KeyStoreImpl(new FakeStore(), new FakeBackupRestore());
        KeyPair account = keyStore.newAccount();
        assertNotNull(account);
        assertNotNull(account.getPublicKey());
        assertNotNull(account.getSecretSeed());
    }

    @Test
    public void newAccount_JsonException_CreateAccountException() throws Exception {
        Store mockStore = mock(Store.class);
        when(mockStore.getString(anyString()))
                .thenReturn("")
                .thenReturn(KeyStoreImpl.ENCRYPTION_VERSION_NAME)
                .thenReturn("not a real json");
        KeyStoreImpl keyStore = new KeyStoreImpl(mockStore, new FakeBackupRestore());

        expectedEx.expect(CreateAccountException.class);
        expectedEx.expectCause(isA(JSONException.class));
        keyStore.newAccount();
    }

    @Test
    public void loadAccounts_OldVersionData_DropOldData() throws Exception {
        FakeStore fakeStore = new FakeStore();
        fakeStore.saveString(KeyStoreImpl.VERSION_KEY, "some_version");
        fakeStore.saveString(KeyStoreImpl.STORE_KEY_ACCOUNTS,
                "{&quot;accounts&quot;:[{&quot;seed&quot;:&quot;{\\&quot;iv\\&quot;:\\&quot;nVGsoEHgjW4xw2gx\\\\n\\&quot;,\\&quot;cipher\\&quot;:\\&quot;kEC64vaQu\\\\\\/erpFvvnrY+sWm\\\\\\/o4GjmjPfgG31zQTwvp0taxo\\\\\\/04PoaisjfEQxrydRwBGFvG\\\\\\/nG345\\\\ntXMn+x2H0jnaPWWCznPA\\\\n\\&quot;}&quot;,&quot;public_key&quot;:&quot;GBYPGYWPWHWSVTQGTUCH2IICIP2PRLN3QYSUX5NOHHMNDQW26A4WK2IK&quot;}]}");
        KeyStoreImpl keyStore = new KeyStoreImpl(fakeStore, new FakeBackupRestore());

        keyStore.loadAccounts();
        assertEquals(fakeStore.getString(KeyStoreImpl.VERSION_KEY), KeyStoreImpl.ENCRYPTION_VERSION_NAME);
        assertNull(fakeStore.getString(KeyStoreImpl.STORE_KEY_ACCOUNTS));
    }

    @Test
    public void loadAccounts() throws Exception {
        KeyStoreImpl keyStore = new KeyStoreImpl(new FakeStore(), new FakeBackupRestore());
        KeyPair account1 = keyStore.newAccount();
        KeyPair account2 = keyStore.newAccount();
        List<KeyPair> accounts = keyStore.loadAccounts();
        KeyPair actualAccount1 = accounts.get(0);
        KeyPair actualAccount2 = accounts.get(1);
        assertEquals(String.valueOf(account1.getSecretSeed()), String.valueOf(actualAccount1.getSecretSeed()));
        assertEquals(String.valueOf(account2.getSecretSeed()), String.valueOf(actualAccount2.getSecretSeed()));
    }

    @Test
    public void loadAccounts_JsonException_LoadAccountException() throws Exception {
        Store mockStore = mock(Store.class);
        when(mockStore.getString(anyString()))
                .thenReturn(KeyStoreImpl.ENCRYPTION_VERSION_NAME)
                .thenReturn("not a real json");
        KeyStoreImpl keyStore = new KeyStoreImpl(mockStore, new FakeBackupRestore());

        expectedEx.expect(LoadAccountException.class);
        expectedEx.expectCause(isA(JSONException.class));
        keyStore.loadAccounts();
    }

    @Test
    public void deleteAccount() throws Exception {
        KeyStoreImpl keyStore = new KeyStoreImpl(new FakeStore(), new FakeBackupRestore());
        KeyPair account1 = keyStore.newAccount();
        KeyPair keyPair = keyStore.newAccount();
        keyStore.deleteAccount(keyPair.getAccountId());

        List<KeyPair> accounts = keyStore.loadAccounts();
        assertEquals(1, accounts.size());
        assertEquals(String.valueOf(account1.getSecretSeed()), String.valueOf(accounts.get(0).getSecretSeed()));
    }

    @Test
    public void deleteAccount_JsonException_DeleteAccountException() throws Exception {
        Store stubStore = spy(FakeStore.class);
        when(stubStore.getString(anyString()))
                .thenCallRealMethod()
                .thenCallRealMethod()
                .thenCallRealMethod()
                .thenReturn("not a real json");
        KeyStoreImpl keyStore = new KeyStoreImpl(stubStore, new FakeBackupRestore());

        KeyPair keyPair = keyStore.newAccount();
        expectedEx.expect(DeleteAccountException.class);
        expectedEx.expectCause(isA(JSONException.class));
        keyStore.deleteAccount(keyPair.getAccountId());
    }

    @Test
    public void clearAllAccounts() throws Exception {
        KeyStoreImpl keyStore = new KeyStoreImpl(new FakeStore(), new FakeBackupRestore());
        keyStore.newAccount();
        keyStore.newAccount();
        keyStore.clearAllAccounts();
        assertTrue(keyStore.loadAccounts().isEmpty());
    }

    @Test
    public void importAccount() throws Exception {

        BackupRestore mockBackupRestore = mock(BackupRestore.class);

        KeyPair newKeyPair = KeyPair.random();
        when(mockBackupRestore.importWallet(any(), any()))
                .thenReturn(newKeyPair);

        KeyStoreImpl keyStore = new KeyStoreImpl(new FakeStore(), mockBackupRestore);

        KeyPair importedKeyPair = keyStore.importAccount("", "password");

        assertEquals(newKeyPair, importedKeyPair);
    }

}
