package org.kin.sdk.demo

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import org.kin.sdk.base.KinAccountContext
import org.kin.sdk.base.KinEnvironment
import org.kin.sdk.base.models.AppInfo
import org.kin.sdk.base.models.AppUserCreds
import org.kin.sdk.base.models.Invoice
import org.kin.sdk.base.models.Key
import org.kin.sdk.base.models.KinAmount
import org.kin.sdk.base.models.LineItem
import org.kin.sdk.base.models.SKU
import org.kin.sdk.base.network.services.AppInfoProvider
import org.kin.sdk.base.repository.InvoiceRepository
import org.kin.sdk.base.stellar.models.NetworkEnvironment
import org.kin.sdk.base.storage.KinFileStorage
import org.kin.sdk.base.tools.Promise
import org.kin.sdk.base.tools.toByteArray
import org.kin.sdk.base.viewmodel.di.MetaResolver
import org.kin.sdk.base.viewmodel.di.SpendResolver
import org.kin.sdk.base.viewmodel.di.SpendResolverImpl
import org.kin.sdk.demo.di.DemoResolver
import org.kin.sdk.demo.di.compat.CompatResolver
import org.kin.sdk.demo.di.modern.DemoAppConfig
import org.kin.sdk.demo.di.modern.ModernResolver
import org.kin.sdk.design.di.ActivityResultSink
import org.kin.sdk.design.view.tools.setupViewExtensions
import java.util.UUID

interface ResolverProvider : MetaResolver {

    val compatResolver: DemoResolver
    val modernResolver: DemoResolver

    var resolver: DemoResolver
}

class DemoApplication : MultiDexApplication(), ResolverProvider, AppInfoProvider {
    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate() {
        super.onCreate()
        applicationContext.setupViewExtensions()

        setupTestAccounts()
    }

    private fun setupTestAccounts() {
        KinAccountContext.Builder(testKinEnvironment)
            .importExistingPrivateKey(Key.PrivateKey(DemoAppConfig.DEMO_APP_SECRET_SEED))
            .build()
            .getAccount()
            .then {
                println("Setup KinAccount: $it")
            }
    }

    override val compatResolver: DemoResolver by lazy { CompatResolver(applicationContext) }
    override val modernResolver: DemoResolver by lazy {
        ModernResolver(
            testKinEnvironment,
            mainNetKinEnvironment,
            testKinEnvironment.invoiceRepository
        )
    }

    private val testKinEnvironment: KinEnvironment.Agora by lazy {
        KinEnvironment.Agora.Builder(NetworkEnvironment.TestNet)
            .setAppInfoProvider(object : AppInfoProvider {
                override val appInfo: AppInfo =
                    AppInfo(
                        DemoAppConfig.DEMO_APP_IDX,
                        DemoAppConfig.DEMO_APP_ACCOUNT_ID,
                        "Kin Demo App",
                        R.drawable.app_icon
                    )

                override fun getPassthroughAppUserCredentials(): AppUserCreds {
                    return AppUserCreds("demo_app_uid", "demo_app_user_passkey")
                }
            })
            .setEnableLogging()
            .setStorage(KinFileStorage.Builder("${applicationContext.filesDir}/kin"))
            .build()
            .apply {
                addDefaultInvoices(invoiceRepository)
            }
    }

    private val mainNetKinEnvironment: KinEnvironment.Agora by lazy {
        KinEnvironment.Agora.Builder(NetworkEnvironment.MainNet)
            .setAppInfoProvider(this)
            .setEnableLogging()
            .setStorage(KinFileStorage.Builder("${applicationContext.filesDir}/kin"))
            .build()
    }


    override lateinit var resolver: DemoResolver

    override val spendResolver: SpendResolver by lazy {
        SpendResolverImpl(testKinEnvironment)
    }

    override val activityResultSink: ActivityResultSink? = object : ActivityResultSink {
        private val lookup =
            mutableMapOf<Int, (requestCode: Int, intent: Any?) -> Unit>()

        override fun register(
            requestCode: Int,
            onResult: (requestCode: Int, intent: Any?) -> Unit
        ) {
            lookup[requestCode] = onResult
        }

        override fun onNewResult(requestCode: Int, intent: Any?) {
            lookup[requestCode]?.invoke(requestCode, intent)
            lookup.remove(requestCode)
        }
    }
    override val appInfo: AppInfo =
        AppInfo(
            DemoAppConfig.DEMO_APP_IDX,
            DemoAppConfig.DEMO_APP_ACCOUNT_ID,
            "Kin Demo App",
            R.drawable.app_icon
        )

    override fun getPassthroughAppUserCredentials(): AppUserCreds {
        return AppUserCreds("demo_app_uid", "demo_app_user_passkey")
    }

    private fun addDefaultInvoices(invoiceRepository: InvoiceRepository) {
        val invoice1 = Invoice.Builder().apply {
            addLineItem(
                LineItem.Builder("Boombox Badger Sticker", KinAmount(25))
                    .setDescription("Let's Jam!")
                    .setSKU(SKU(UUID.fromString("8b154ad6-dab8-11ea-87d0-0242ac130003").toByteArray()))
                    .build()
            )
            addLineItem(
                LineItem.Builder("Relaxer Badger Sticker", KinAmount(25))
                    .setDescription("#HammockLife")
                    .setSKU(SKU(UUID.fromString("964d1730-dab8-11ea-87d0-0242ac130003").toByteArray()))
                    .build()
            )
            addLineItem(
                LineItem.Builder("Classic Badger Sticker", KinAmount(25))
                    .setDescription("Nothing beats the original")
                    .setSKU(SKU(UUID.fromString("cc081bd6-dab8-11ea-87d0-0242ac130003").toByteArray()))
                    .build()
            )
        }.build()

        val invoice2 = Invoice.Builder().apply {
            addLineItem(
                LineItem.Builder("Fancy Tunic of Defence", KinAmount(42))
                    .setDescription("+40 Defence, -9000 Style")
                    .setSKU(SKU(UUID.fromString("a1b4a796-dab8-11ea-87d0-0242ac130003").toByteArray()))
                    .build()
            )
            addLineItem(
                LineItem.Builder("Wizard Hat", KinAmount(99)).setDescription("+999 Mana")
                    .setSKU(SKU(UUID.fromString("a911cae6-dab8-11ea-87d0-0242ac130003").toByteArray()))
                    .build()
            )
        }.build()

        val invoice3 = Invoice.Builder().apply {
            addLineItem(
                LineItem.Builder("Start a Chat", KinAmount(50))
                    .setSKU(SKU(UUID.fromString("cfe1f0b0-dab8-11ea-87d0-0242ac130003").toByteArray()))
                    .build()
            )
        }.build()

        val invoice4 = Invoice.Builder().apply {
            addLineItem(
                LineItem.Builder("Thing", KinAmount(1))
                    .setDescription("That does stuff")
                    .setSKU(SKU(UUID.fromString("dac0b678-a936-44ef-abc8-365f4cae2ed1").toByteArray()))
                    .build()
            )
        }.build()

        Promise.allAny(
            invoiceRepository.addInvoice(invoice1),
            invoiceRepository.addInvoice(invoice2),
            invoiceRepository.addInvoice(invoice3),
            invoiceRepository.addInvoice(invoice4)
        ).resolve()
    }
}
