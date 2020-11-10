package org.kin.sdk.base.models

/**
 * A spec for how to interpret a set of accounts.
 */
sealed class AccountSpec(val value: Int) {

    /**
     * Use the EXACT account address specified and only that. Fail otherwise.
     */
    object Exact : AccountSpec(0)

    /**
     * PREFER to use the account address I specify, but if that does not exist,
     * resolve the tokenAccounts for this account and use the first one.
     */
    object Preferred : AccountSpec(1)
}
