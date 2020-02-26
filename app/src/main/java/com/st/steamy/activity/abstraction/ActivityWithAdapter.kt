package com.st.steamy.activity.abstraction

import androidx.recyclerview.widget.RecyclerView
import com.st.steamy.data.ADAPTERS

interface ActivityWithAdapter {
    fun addAdapters(adapters : Iterable<RecyclerView.Adapter<RecyclerView.ViewHolder>>) {
        ADAPTERS.addAll(adapters)
    }
    fun delAdapters(adapters: Iterable<RecyclerView.Adapter<RecyclerView.ViewHolder>>) {
        ADAPTERS.removeAll(adapters)
    }
}
