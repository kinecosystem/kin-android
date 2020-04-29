package org.kin.stellarfork

import org.kin.stellarfork.xdr.AccountFlags

/**
 * AccountFlag is the `enum` that can be used in [SetOptionsOperation].
 *
 * @see [Account Flags](https://www.stellar.org/developers/guides/concepts/accounts.html.flags)
 */
enum class AccountFlag(val value: Int) {
    /**
     * Authorization required (0x1): Requires the issuing account to give other accounts permission before they can hold the issuing account’s credit.
     */
    AUTH_REQUIRED_FLAG(AccountFlags.AUTH_REQUIRED_FLAG.value),
    /**
     * Authorization revocable (0x2): Allows the issuing account to revoke its credit held by other accounts.
     */
    AUTH_REVOCABLE_FLAG(AccountFlags.AUTH_REVOCABLE_FLAG.value),
    /**
     * Authorization immutable (0x4): If this is set then none of the authorization flags can be set and the account can never be deleted.
     */
    AUTH_IMMUTABLE_FLAG(AccountFlags.AUTH_IMMUTABLE_FLAG.value);
}
