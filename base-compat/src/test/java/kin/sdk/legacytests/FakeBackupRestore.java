package kin.sdk.legacytests;


import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.kin.stellarfork.KeyPair;

import kin.sdk.exception.CryptoException;
import kin.sdk.BackupRestore;

public class FakeBackupRestore implements BackupRestore {

    @NonNull
    @Override
    public String exportWallet(@NonNull KeyPair keyPair, @NonNull String passphrase) throws CryptoException {
        JSONObject json = new JSONObject();
        try {
            json.put("seed", new String(keyPair.getSecretSeed()));
            json.put("passphrase", passphrase);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @NonNull
    @Override
    public KeyPair importWallet(@NonNull String exportedJson, @NonNull String passphrase) throws CryptoException {
        JSONObject json = new JSONObject();
        try {
            String seed = json.getString("seed");
            String storedPassphrase = json.getString("passphrase");
            if (storedPassphrase.equals(passphrase)) {
                return KeyPair.fromSecretSeed(seed);
            } else {
                throw new CryptoException("incorrect passohrase");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new CryptoException(e);
        }
    }
}
