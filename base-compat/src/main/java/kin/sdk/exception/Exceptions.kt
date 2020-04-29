package kin.sdk.exception

/**
 * Account was deleted using [KinClient.deleteAccount], and cannot be used any more.
 */
class AccountDeletedException :
    OperationFailedException("Account deleted, Create new account")

/**
 * Account was not created on the blockchain
 */
class AccountNotFoundException(val accountId: String) :
    OperationFailedException("Account $accountId was not found")

/**
 * Input exported account data is corrupted and cannot be imported.
 */
class CorruptedDataException : Exception {
    constructor(e: Throwable?) : super(e) {}
    constructor(msg: String?) : super(msg) {}
    constructor(msg: String?, e: Throwable?) : super(msg, e) {}
}

class CreateAccountException(cause: Throwable?) : Exception(cause)

/**
 * Decryption/Encryption error when importing - [KinClient.importAccount] or
 * exporting [KinAccount.export] an account.
 */
class CryptoException : Exception {
    constructor(e: Throwable?) : super(e) {}
    constructor(msg: String?) : super(msg) {}
    constructor(msg: String?, e: Throwable?) : super(msg, e) {}
}

class DeleteAccountException(cause: Throwable?) : Exception(cause)

/**
 * amount was not legal
 */
class IllegalAmountException(errorReason: String) :
    OperationFailedException("Illegal amount - $errorReason")

class InsufficientFeeException :
    OperationFailedException("Not enough fee to perform the transaction.")

/**
 * Transaction failed due to insufficient kin.
 */
class InsufficientKinException :
    OperationFailedException("Not enough kin to perform the transaction.")

class LoadAccountException(msg: String?, cause: Throwable?) :
    Exception(msg, cause)

open class OperationFailedException : Exception {
    constructor(cause: Throwable?) : super(cause) {}
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Exception?) : super(message, cause) {}
}

/**
 * Blockchain transaction failure has happened, contains blockchain specific error details
 */
class TransactionFailedException(
    val transactionResultCode: String?,
    val operationsResultCodes: List<String>?
) : OperationFailedException(getMessage(operationsResultCodes)) {

    companion object {
        private fun getMessage(opResultCode: List<String>?): String {
            return if (opResultCode != null && !opResultCode.isEmpty()) "Transaction failed with the error = " + opResultCode[0] else "Transaction failed"
        }
    }

}
