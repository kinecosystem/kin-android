package kin.sdk

/**
 * This class wraps a transaction envelope xdr in base 64(transaction payload)
 * and a network passphrase(the network id as string). *
 * Those fields are necessary for the whitelist server in order to sign this transaction to be a whitelist transaction.
 */
data class WhitelistableTransaction(
    val transactionPayload: String,
    val networkPassphrase: String
)
