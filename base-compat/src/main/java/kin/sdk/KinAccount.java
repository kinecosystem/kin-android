package kin.sdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;

import kin.sdk.exception.AccountNotFoundException;
import kin.sdk.exception.CryptoException;
import kin.sdk.exception.InsufficientKinException;
import kin.sdk.exception.OperationFailedException;
import kin.sdk.exception.TransactionFailedException;
import kin.utils.Request;

/**
 * Represents an account which holds Kin.
 */
public interface KinAccount {

    /**
     * @return String the public address of the account, or null if deleted
     */
    @Nullable
    String getPublicAddress();

    /**
     * *** DO NOT DISPLAY OR SHARE THIS ***
     * *** THIS PROVIDES FULL ACCESS TO THE FUNDS OF THIS ACCOUNT ***
     * *** USEFUL TO EXPORT THE KEY TO USE IN ANOTHER SYSTEM. e.g. for use in the ../base module***
     * @return the private key of the account, or null if deleted
     */
    @Nullable
    String getStringEncodedPrivateKey();

    /**
     * Build a Transaction object of the given amount in kin, to the specified public address.
     * <p> See {@link KinAccount#buildTransactionSync(String, BigDecimal, int)} for possibles errors</p>
     *
     * @param publicAddress the account address to send the specified kin amount.
     * @param amount        the amount of kin to transfer.
     * @param fee           the amount of fee(in stroops) for this transfer.
     * @return {@code Request<TransactionId>}, TransactionId - the transaction identifier.
     */
    Request<Transaction> buildTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount, int fee);

    /**
     * Build a Transaction object of the given amount in kin, to the specified public address and with a memo(that can be empty or null).
     * <p> See {@link KinAccount#buildTransactionSync(String, BigDecimal, int, String)} for possibles errors</p>
     *
     * @param publicAddress the account address to send the specified kin amount.
     * @param amount        the amount of kin to transfer.
     * @param fee           the amount of fee(in stroops) for this transfer.
     * @param memo          An optional string, can contain a utf-8 string up to 21 bytes in length, included on the transaction record.
     * @return {@code Request<TransactionId>}, TransactionId - the transaction identifier
     */
    Request<Transaction> buildTransaction(@NonNull String publicAddress, @NonNull BigDecimal amount, int fee, @Nullable String memo);

    /**
     * Create {@link Request} for signing and sending a transaction
     * <p> See {@link KinAccount#sendTransactionSync(Transaction)} for possibles errors</p>
     *
     * @param transaction is the transaction object to send.
     * @return {@code Request<TransactionId>}, TransactionId - the transaction identifier.
     */
    @NonNull
    Request<TransactionId> sendTransaction(Transaction transaction);

    /**
     * Create {@link Request} for signing and sending a transaction from a whitelist.
     * whitelist a transaction means that the user will not pay any fee(if your App is in the Kin whitelist)
     * <p> See {@link KinAccount#sendWhitelistTransactionSync(String)} for possibles errors</p>
     *
     * @param whitelist is the whitelist data (got from the server) which will be used to send the transaction.
     * @return {@code Request<TransactionId>}, TransactionId - the transaction identifier.
     */
    @NonNull
    Request<TransactionId> sendWhitelistTransaction(String whitelist);

    /**
     * Build a Transaction object of the given amount in kin, to the specified public address.
     * <p><b>Note:</b> This method accesses the network, and should not be called on the android main thread.</p>
     *
     * @param publicAddress the account address to send the specified kin amount.
     * @param amount        the amount of kin to transfer.
     * @param fee           the amount of fee(in stroops) for this transfer.
     * @return a Transaction object which also includes the transaction id.
     * @throws AccountNotFoundException if the sender or destination account was not created.
     * @throws OperationFailedException other error occurred.
     */
    Transaction buildTransactionSync(@NonNull String publicAddress, @NonNull BigDecimal amount, int fee) throws OperationFailedException;

    /**
     * Build a Transaction object of the given amount in kin, to the specified public address and with a memo(that can be empty or null).
     * <p><b>Note:</b> This method accesses the network, and should not be called on the android main thread.</p>
     *
     * @param publicAddress the account address to send the specified kin amount.
     * @param amount        the amount of kin to transfer.
     * @param fee           the amount of fee(in stroops) for this transfer.
     * @param memo          An optional string, can contain a utf-8 string up to 21 bytes in length, included on the transaction record.
     * @return a Transaction object which also includes the transaction id.
     * @throws AccountNotFoundException if the sender or destination account was not created.
     * @throws OperationFailedException other error occurred.
     */
    Transaction buildTransactionSync(@NonNull String publicAddress, @NonNull BigDecimal amount, int fee, @Nullable String memo) throws OperationFailedException;

    /**
     * send a transaction.
     * <p><b>Note:</b> This method accesses the network, and should not be called on the android main thread.</p>
     *
     * @param transaction is the transaction object to send.
     * @return TransactionId the transaction identifier.
     * @throws AccountNotFoundException   if the sender or destination account was not created.
     * @throws InsufficientKinException   if account balance has not enough kin.
     * @throws TransactionFailedException if transaction failed, contains blockchain failure details.
     * @throws OperationFailedException   other error occurred.
     */
    @NonNull
    TransactionId sendTransactionSync(Transaction transaction) throws OperationFailedException;

    /**
     * send a whitelist transaction.
     * whitelist a transaction means that the user will not pay any fee(if your App is in the Kin whitelist)
     * <p><b>Note:</b> This method accesses the network, and should not be called on the android main thread.</p>
     *
     * @param whitelist is the whitelist data (got from the server) which will be used to send the transaction.
     * @return TransactionId the transaction identifier.
     * @throws AccountNotFoundException   if the sender or destination account was not created.
     * @throws InsufficientKinException   if account balance has not enough kin.
     * @throws TransactionFailedException if transaction failed, contains blockchain failure details.
     * @throws OperationFailedException   other error occurred.
     */
    @NonNull
    TransactionId sendWhitelistTransactionSync(String whitelist) throws OperationFailedException;

    /**
     * Create {@link Request} for getting the current confirmed balance in kin
     * <p> See {@link KinAccount#getBalanceSync()} for possibles errors</p>
     *
     * @return {@code Request<Balance>} Balance - the balance in kin
     */
    @NonNull
    Request<Balance> getBalance();

    /**
     * Get the current confirmed balance in kin
     * <p><b>Note:</b> This method accesses the network, and should not be called on the android main thread.</p>
     *
     * @return the balance in kin
     * @throws AccountNotFoundException if account was not created
     * @throws OperationFailedException any other error
     */
    @NonNull
    Balance getBalanceSync() throws OperationFailedException;

    /**
     * Get current account status on blockchain network.
     *
     * @return account status, either {@link AccountStatus#NOT_CREATED}, or {@link
     * AccountStatus#CREATED}
     * @throws OperationFailedException any other error
     */
    @AccountStatus
    int getStatusSync() throws OperationFailedException;

    /**
     * Create {@link Request} for getting current account status on blockchain network.
     * <p> See {@link KinAccount#getStatusSync()} for possibles errors</p>
     *
     * @return account status, either {@link AccountStatus#NOT_CREATED}, or {@link
     * AccountStatus#CREATED}
     */
    Request<Integer> getStatus();

    /**
     * Creates and adds listener for balance changes of this account, use returned {@link ListenerRegistration} to
     * stop listening. <p><b>Note:</b> Events will be fired on background thread.</p>
     *
     * @param listener listener object for payment events
     */
    ListenerRegistration addBalanceListener(@NonNull final EventListener<Balance> listener);

    /**
     * Creates and adds listener for payments concerning this account, use returned {@link ListenerRegistration} to
     * stop listening. <p><b>Note:</b> Events will be fired on background thread.</p>
     *
     * @param listener listener object for payment events
     */
    ListenerRegistration addPaymentListener(@NonNull final EventListener<PaymentInfo> listener);

    /**
     * Creates and adds listener for account creation event, use returned {@link ListenerRegistration} to stop
     * listening. <p><b>Note:</b> Events will be fired on background thread.</p>
     *
     * @param listener listener object for payment events
     */
    ListenerRegistration addAccountCreationListener(final EventListener<Void> listener);

    /**
     * Export the account data as a JSON string. The seed is encrypted.
     *
     * @param passphrase The passphrase with which to encrypt the seed
     * @return A JSON representation of the data as a string
     */
    String export(@NonNull String passphrase) throws CryptoException;
}
