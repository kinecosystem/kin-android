package org.kin.sdk.design.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.kin.sdk.design.di.ActivityResultSink
import org.kin.sdk.design.di.Resolver
import org.kin.sdk.design.viewmodel.Navigator
import java.util.Random
import kotlin.math.absoluteValue

open class ActivityNavigatorBase(private val launchActivity: Activity) : Navigator {

    private val resultSink: ActivityResultSink? by lazy {
        (launchActivity.application as Resolver).activityResultSink
    }

    fun launchActivity(activityClass: Class<*>, bundleBuilder: Bundle.() -> Unit) =
        launchActivity(activityClass, { }, bundleBuilder)

    fun launchActivity(
        activityClass: Class<*>,
        modifyIntent: (Intent) -> Unit,
        bundleBuilder: Bundle.() -> Unit
    ) {
        val intent = Intent(launchActivity, activityClass)
        val b = Bundle()

        b.bundleBuilder()

        intent.putExtras(b)

        modifyIntent(intent)

        launchActivity.startActivity(intent)
    }

    @Suppress("UNCHECKED_CAST")
    fun launchActivityForResult(
        activityClass: Class<*>,
        modifyIntent: (Intent) -> Unit,
        bundleBuilder: Bundle.() -> Unit,
        onResult: (resultCode: Int, data: Intent?) -> Unit,
        requestCode: Int = Random(System.currentTimeMillis()).nextInt().absoluteValue % 65535
    ) {
        val intent = Intent(launchActivity, activityClass)
        val b = Bundle()

        b.bundleBuilder()

        intent.putExtras(b)

        modifyIntent(intent)

        resultSink?.register(requestCode, onResult as (Int, Any?) -> Unit)

        launchActivity.startActivityForResult(intent, requestCode)
    }

    override fun close() {
        launchActivity.finish()
    }
}
