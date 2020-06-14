package org.kin.sdk.demo

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import org.kin.sdk.demo.di.Resolver
//import org.kin.sdk.demo.di.modern.ModernResolver
import org.kin.sdk.demo.di.compat.CompatResolver
import org.kin.sdk.demo.di.modern.ModernResolver
import org.kin.sdk.demo.view.tools.setupViewExtensions
import kotlin.properties.ReadWriteProperty

interface ApplicationResolver {

    val compatResolver: Resolver
    val modernResolver: Resolver

    var resolver: Resolver
}

class DemoApplication : MultiDexApplication(), ApplicationResolver {
    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate() {
        super.onCreate()
        setupViewExtensions(applicationContext)
    }

    override val compatResolver: Resolver by lazy { CompatResolver(applicationContext) }
    override val modernResolver: Resolver by lazy { ModernResolver("${applicationContext.filesDir}/kin") }

    override lateinit var resolver: Resolver
}
