package org.kin.sdk.design.view.widget.internal

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.kin.sdk.design.view.tools.setupViewExtensions

class VerticalRecyclerView(context: Context) : RecyclerView(context) {

    init {
        context.setupViewExtensions()
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
    }
}
