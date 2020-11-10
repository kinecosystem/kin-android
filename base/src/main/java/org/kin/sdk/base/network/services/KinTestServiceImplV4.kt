package org.kin.sdk.base.network.services

import org.kin.agora.gen.airdrop.v4.AirdropGrpc
import org.kin.agora.gen.airdrop.v4.AirdropService
import org.kin.agora.gen.common.v4.Model
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.QuarkAmount
import org.kin.sdk.base.models.asKinAccountId
import org.kin.sdk.base.models.toKin
import org.kin.sdk.base.models.toQuarks
import org.kin.sdk.base.network.api.agora.GrpcApi
import org.kin.sdk.base.network.api.agora.OkHttpChannelBuilderForcedTls12
import org.kin.sdk.base.network.api.agora.toProtoSolanaAccountId
import org.kin.sdk.base.stellar.models.ApiConfig
import org.kin.sdk.base.tools.KinLoggerFactory
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.PromisedCallback
import org.kin.sdk.base.tools.onErrorResumeNext

class KinTestServiceImplV4(
    val logger: KinLoggerFactory,
    val service: KinService
) : KinTestService {

    private val log = logger.getLogger(javaClass.simpleName)

    private fun <T> T.requestPrint(): T {
        log.log { "[Request][V4] ===============" }
        log.log { "${this}" }
        log.log { "=============== [V4][Request]" }
        return this
    }

    private fun <T> T.responsePrint(): T {
        log.log { "[Response][V4] ===============" }
        log.log { "${this}" }
        log.log { "=============== [V4][Response]" }
        return this
    }

    override fun fundAccount(accountId: KinAccount.Id): Promise<KinAccount> {
        var attempts = 3
        return Promise.create<KinAmount> { resolve, reject ->
            object : GrpcApi(with(ApiConfig.TestNetAgora) {
                OkHttpChannelBuilderForcedTls12.forAddress(networkEndpoint, tlsPort)
                    .build()
            }) {
                init {
                    with(AirdropGrpc.newStub(managedChannel)) {
                        val request = AirdropService.RequestAirdropRequest.newBuilder()
                            .setAccountId(accountId.toProtoSolanaAccountId())
                            .setQuarks(KinAmount(10).toQuarks().value)
                            .setCommitment(Model.Commitment.SINGLE)
                            .build()
                        this::requestAirdrop.callAsPromisedCallback(
                            request.requestPrint(),
                            PromisedCallback({
                                it.responsePrint()
                                when (it.result) {
                                    AirdropService.RequestAirdropResponse.Result.OK ->
                                        resolve(QuarkAmount(request.quarks).toKin())
                                    AirdropService.RequestAirdropResponse.Result.NOT_FOUND ->
                                        reject(KinService.FatalError.ItemNotFound)
                                    AirdropService.RequestAirdropResponse.Result.INSUFFICIENT_KIN,
                                    AirdropService.RequestAirdropResponse.Result.UNRECOGNIZED ->
                                        reject(
                                            KinService.FatalError.UnexpectedServiceError(
                                                Exception(it.result.name)
                                            )
                                        )
                                }
                            }, {
                                reject(it)
                            })
                        )
                    }
                }
            }
        }.onErrorResumeNext(KinService.FatalError.ItemNotFound.javaClass) { error ->
            if (attempts-- != 0) {
                service.resolveTokenAccounts(accountId)
                    .flatMap {
                        fundAccount(
                            it.firstOrNull()?.asKinAccountId() ?: accountId
                        )
                    }
            } else Promise.error(error)
        }.flatMap { service.getAccount(accountId) }
    }

}
