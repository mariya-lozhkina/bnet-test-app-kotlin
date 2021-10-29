package com.mariyalozjkina.testappbnetkotlin.adapter

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mariyalozjkina.testappbnetkotlin.R
import com.mariyalozjkina.testappbnetkotlin.data.Entry
import java.text.SimpleDateFormat

class EntryListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val cvRoot: CardView = itemView.findViewById(R.id.cvRoot)
    private val tvCreateDate: TextView = itemView.findViewById(R.id.tvCreateDate)
    private val tvModificationDate: TextView = itemView.findViewById(R.id.tvModificationDate)
    private val tvEntryText: TextView = itemView.findViewById(R.id.tvEntryText)

    fun bind(entry: Entry, selectEntryListener: SelectEntryListener) {
        val dateFormat = SimpleDateFormat("dd.MM.yy HH:mm:ss")
        tvCreateDate.text = dateFormat.format(entry.da * 1000)
        tvModificationDate.text = dateFormat.format(entry.da * 1000)
        tvEntryText.text = entry.body
        cvRoot.setOnClickListener {
            selectEntryListener.selectEntry(entry)
        }
    }

}