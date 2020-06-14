package org.kin.sdk.demo.view.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VerticalRecyclerView(context: Context) : RecyclerView(context) {

    init {
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
    }
}
