package kin.sdk;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import org.kin.sdk.base.KinEnvironment;
import org.kin.sdk.base.storage.Storage;

import kin.sdk.exception.CorruptedDataException;
import kin.sdk.exception.CreateAccountException;
import kin.sdk.exception.CryptoException;
import kin.sdk.exception.DeleteAccountException;
import kin.sdk.exception.OperationFailedException;
import kin.utils.Request;

/**
 * An account manager for a [KinAccount].
 */
public class KinClient {
    private KinClientInternal delegate;

    /**
     * For more details please look at {@link #KinClient(Context context, Environment environment, String appId, String storeKey)}
     */
    public KinClient(@NonNull Context context, @NonNull Environment environment, String appId) {
        this(context, environment, appId, "");

        delegate = new KinClientInternal(context, environment, appId);
    }

    /**
     * Build KinClient object.
     *
     * @param context     android context
     * @param environment the blockchain network details.
     * @param appId       a 4 character string which represent the application id which will be added to each transaction.
     *                    <br><b>Note:</b> appId must contain only upper and/or lower case letters and/or digits and that the total string length is between 3 to 4.
     *                    For example 1234 or 2ab3 or bcda, etc.</br>
     * @param storeKey    an optional param which is the key for storing this KinClient data, different keys will store a different accounts.
     */
    public KinClient(@NonNull Context context, @NonNull Environment environment, @NonNull String appId, @NonNull String storeKey) {
        delegate = new KinClientInternal(context, environment, appId, storeKey);
    }

    @VisibleForTesting
    public KinClient(Context context, Environment environment, String appId, String storeKey, BackupRestore backupRestore, KeyStore keyStore, Storage storage, KinEnvironment kinEnvironment) {
        delegate = new KinClientInternal(context, environment, appId, storeKey, backupRestore, keyStore, storage, kinEnvironment);
    }

    @VisibleForTesting
    public KinClient(Context context, Environment environment, String appId, String storeKey, BackupRestore backupRestore, KeyStore keyStore, Storage storage) {
        delegate = new KinClientInternal(context, environment, appId, storeKey, backupRestore, keyStore, storage);
    }

    /**
     * Creates and adds an account.
     * <p>Once created, the account information will be stored securely on the device and can
     * be accessed again via the {@link #getAccount(int)} method.</p>
     *
     * @return {@link KinAccount} the account created store the key.
     */
    public @NonNull
    KinAccount addAccount() throws CreateAccountException {
        return delegate.addAccount();
    }

    /**
     * Import an account from a JSON-formatted string.
     *
     * @param exportedJson The exported JSON-formatted string.
     * @param passphrase   The passphrase to decrypt the secret key.
     * @return The imported account
     */
    @NonNull
    public KinAccount importAccount(@NonNull String exportedJson, @NonNull String passphrase)
            throws CryptoException, CreateAccountException, CorruptedDataException {
        return delegate.importAccount(exportedJson, passphrase);
    }

    /**
     * Returns an account at input index.
     *
     * @return the account at the input index or null if there is no such account
     */
    public KinAccount getAccount(int index) {
        return delegate.getAccount(index);
    }

    /**
     * Returns an account corresponding to the supplied public address.
     *
     * @return the account with the public address or null if there is no such account
     */
    public KinAccount getAccountByPublicAddress(String accountId) {
        return delegate.getAccountByPublicAddress(accountId);
    }

    /**
     * @return true if there is an existing account
     */
    public boolean hasAccount() {
        return delegate.hasAccount();
    }

    /**
     * Returns the number of existing accounts
     */
    public int getAccountCount() {
        return delegate.getAccountCount();
    }

    /**
     * Deletes the account at input index (if it exists)
     *
     * @return true if the delete was successful or false otherwise
     * @throws DeleteAccountException in case of a delete account exception while trying to delete the account
     */
    public boolean deleteAccount(int index) throws DeleteAccountException {
        return delegate.deleteAccount(index);
    }

    /**
     * Deletes all accounts.
     */
    public void clearAllAccounts() {
        delegate.clearAllAccounts();
    }

    public Environment getEnvironment() {
        return delegate.getEnvironment();
    }

    /**
     * Get the current minimum fee that the network charges per operation.
     * This value is expressed in stroops.
     *
     * @return {@code Request<Integer>} - the minimum fee.
     */
    public Request<Long> getMinimumFee() {
        return delegate.getMinimumFee();
    }

    /**
     * Get the current minimum fee that the network charges per operation.
     * This value is expressed in stroops.
     * <p><b>Note:</b> This method accesses the network, and should not be called on the android main thread.</p>
     *
     * @return the minimum fee.
     */
    public long getMinimumFeeSync() throws OperationFailedException {
        return delegate.getMinimumFeeSync();
    }

    public String getAppId() {
        return delegate.getAppId();
    }

    public String getStoreKey() {
        return delegate.getStoreKey();
    }
}
