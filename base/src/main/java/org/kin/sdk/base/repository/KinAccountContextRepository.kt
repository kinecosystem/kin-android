package org.kin.sdk.base.repository

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.KinAccount

interface KinAccountContextRepository {
    fun getKinAccountContext(accountId: KinAccount.Id): KinAccountContext?
}

class InMemoryKinAccountContextRepositoryImpl(
    private val kinEnvironment: KinEnvironment,
    private val storage: MutableMap<KinAccount.Id, KinAccountContext> = mutableMapOf<KinAccount.Id, KinAccountContext>()
) : KinAccountContextRepository {

    override fun getKinAccountContext(accountId: KinAccount.Id): KinAccountContext? {
        if (storage[accountId] == null) {

            if (!kinEnvironment.storage.getAllAccountIds().contains(accountId)) {
                // We can't issue a write context...
                return null
            }

            storage[accountId] =
                KinAccountContext.Builder(kinEnvironment)
                    .useExistingAccount(accountId)
                    .build()
        }
        return storage[accountId]!!
    }
}
