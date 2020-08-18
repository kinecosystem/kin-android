package org.kin.sdk.design.showcase

import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import com.github.mproberts.museum.showcase.MainActivity
import org.kin.sdk.design.view.tools.setupViewExtensions

class MainActivity : MainActivity() {

    override val backgroundColor: Int
        get() = 0xffffffff.toInt()

    override val primaryTextColor: Int
        get() = 0xff000000.toInt()

    override val secondaryTextColor: Int
        get() = 0xffc0c0c0.toInt()

    override fun onStart() {
        super.onStart()


    }

}
