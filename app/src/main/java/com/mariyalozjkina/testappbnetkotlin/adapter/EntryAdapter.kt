package com.mariyalozjkina.testappbnetkotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariyalozjkina.testappbnetkotlin.R
import com.mariyalozjkina.testappbnetkotlin.data.Entry

class EntryAdapter(
    private val selectEntryListener: SelectEntryListener,
) : RecyclerView.Adapter<EntryListHolder>() {

    private var items = listOf<Entry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryListHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_entry_list, parent, false)
        return EntryListHolder(view)
    }

    override fun onBindViewHolder(holder: EntryListHolder, position: Int) {
        holder.bind(items[position], selectEntryListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<Entry>) {
        this.items = items
    }
}