package com.mariyalozjkina.testappbnetkotlin

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class EntryActivity : AppCompatActivity() {

    private val tvEntryText: TextView by lazy { findViewById(R.id.tvEntryText) }
    private val tvCreatDate: TextView by lazy { findViewById(R.id.tvCreateDate) }
    private val tvModificationDate: TextView by lazy { findViewById(R.id.tvModificationDate) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        val intent = intent
        val text: String? = intent.getStringExtra(Constants.BODY)
        val da: Long = intent.getLongExtra(Constants.DA, 0)
        val dm: Long = intent.getLongExtra(Constants.DM, 0)
        tvEntryText.text = text
        val dateFormat = SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault())
        tvCreatDate.text = dateFormat.format(da * 1000)
        tvModificationDate.text = dateFormat.format(dm * 1000)
    }
}