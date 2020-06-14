package org.kin.sdk.demo.di.modern

import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.KinAccount
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.sdk.demo.di.Resolver
import org.kin.sdk.demo.viewmodel.HomeViewModel
import org.kin.sdk.demo.viewmodel.Navigator
import org.kin.sdk.demo.viewmodel.SendTransactionViewModel
import org.kin.sdk.demo.viewmodel.TransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.WalletViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernHomeViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernSendTransactionViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernTransactionLoadTestingViewModel
import org.kin.sdk.demo.viewmodel.modern.ModernWalletViewModel

class ModernResolver(val storagePath: String) : Resolver {

    private val testKinEnvironment: KinEnvironment by lazy {
        KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarTestNet)
            .setStorage(KinFileStorage.Builder(storagePath))
            .build()
    }

    private val mainNetKinEnvironment: KinEnvironment by lazy {
        KinEnvironment.Horizon.Builder(NetworkEnvironment.KinStellarMainNet)
// TODO - Need to implelment these to create accounts or whitelist transactions on KinStellarMainNet
//                    .setKinAccountCreationApi(object : KinAccountCreationApi {
//                        override fun createAccount(
//                            request: KinAccountCreationApi.CreateAccountRequest,
//                            onCompleted: (KinAccountCreationApi.CreateAccountResponse) -> Unit
//                        ) {
//                            TODO("Not yet implemented")
//                        }
//
//                    })
//                    .setKinTransactionWhitelistingApi(object : KinTransactionWhitelistingApi {
//                        override val isWhitelistingAvailable: Boolean
//                            get() = true
//
//                        override fun whitelistTransaction(
//                            request: KinTransactionWhitelistingApi.WhitelistTransactionRequest,
//                            onCompleted: (KinTransactionWhitelistingApi.WhitelistTransactionResponse) -> Unit
//                        ) {
//                            TODO("Not yet implemented")
//                        }
//
//                    })
            .setStorage(KinFileStorage.Builder(storagePath))
            .build()
    }

    private val accountContexts =
        mutableMapOf<KinAccount.Id, MutableMap<NetworkEnvironment, KinAccountContext>>()

    private fun getKinAccountContext(
        networkEnvironment: NetworkEnvironment,
        accountId: KinAccount.Id
    ): KinAccountContext {
        var networkEnvContext = accountContexts[accountId]
        if (networkEnvContext == null) {
            networkEnvContext = mutableMapOf()
            accountContexts[accountId] = networkEnvContext
        }
        var context = networkEnvContext[networkEnvironment]
        if (context == null) {
            context = KinAccountContext
                .Builder(testKinEnvironment)
                .useExistingAccount(accountId)
                .build()
            networkEnvContext[networkEnvironment] = context
        }
        return context
    }

    override fun resolve(args: HomeViewModel.NavigationArgs, navigator: Navigator) =
        ModernHomeViewModel(navigator, args, testKinEnvironment, mainNetKinEnvironment)

    override fun resolve(args: SendTransactionViewModel.NavigationArgs, navigator: Navigator) =
        ModernSendTransactionViewModel(
            navigator,
            args,
            getKinAccountContext(
                NetworkEnvironment.KinStellarTestNet,
                KinAccount.Id(args.publicAddress)
            )
        )

    override fun resolve(args: WalletViewModel.NavigationArgs, navigator: Navigator) =
        ModernWalletViewModel(
            navigator,
            args,
            getKinAccountContext(
                NetworkEnvironment.KinStellarTestNet,
                KinAccount.Id(args.publicAddress)
            )
        )

    override fun resolve(
        args: TransactionLoadTestingViewModel.NavigationArgs,
        navigator: Navigator
    ) = ModernTransactionLoadTestingViewModel(
        navigator,
        args,
        getKinAccountContext(
            NetworkEnvironment.KinStellarTestNet,
            KinAccount.Id(args.publicAddress)
        )
    )
}
