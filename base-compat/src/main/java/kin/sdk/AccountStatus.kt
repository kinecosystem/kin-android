package kin.sdk

import androidx.annotation.IntDef

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(AccountStatus.NOT_CREATED, AccountStatus.CREATED)
annotation class AccountStatus {
    companion object {
        /**
         * Account was not created on blockchain network, account should be created and funded by a different account on
         * the blockchain.
         */
        const val NOT_CREATED = 0

        /**
         * Account was created, account is ready to use with kin.
         */
        const val CREATED = 2
    }
}
