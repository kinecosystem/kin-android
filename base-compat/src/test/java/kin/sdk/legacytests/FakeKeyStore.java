package kin.sdk.legacytests;

import androidx.annotation.NonNull;

import org.kin.stellarfork.KeyPair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kin.sdk.exception.CreateAccountException;
import kin.sdk.exception.CryptoException;
import kin.sdk.KeyStore;

/**
 * Fake KeyStore for testing, implementing naive in memory store
 */
class FakeKeyStore implements KeyStore {

    private List<KeyPair> accounts;

    FakeKeyStore(List<KeyPair> preloadedAccounts) {
        accounts = new ArrayList<>(preloadedAccounts);
    }

    FakeKeyStore() {
        accounts = new ArrayList<>();
    }

    @Override
    public void deleteAccount(String publicAddress) {
        Iterator<KeyPair> iterator = accounts.iterator();
        while (iterator.hasNext()) {
            KeyPair keyPair = iterator.next();
            if (keyPair.getAccountId().equals(publicAddress)) {
                iterator.remove();
                break;
            }
        }
    }

    @NonNull
    @Override
    public List<KeyPair> loadAccounts() {
        return accounts;
    }

    @Override
    public KeyPair newAccount() {
        KeyPair account = KeyPair.random();
        accounts.add(account);
        return account;
    }

    @Override
    public KeyPair importAccount(@NonNull String json, @NonNull String passphrase)
            throws CryptoException, CreateAccountException {
        return null;
    }

    @Override
    public void clearAllAccounts() {
        accounts.clear();
    }
}
