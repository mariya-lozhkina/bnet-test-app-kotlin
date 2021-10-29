package com.mariyalozjkina.testappbnetkotlin.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mariyalozjkina.testappbnetkotlin.Constants
import com.mariyalozjkina.testappbnetkotlin.R
import com.mariyalozjkina.testappbnetkotlin.adapter.EntryAdapter
import com.mariyalozjkina.testappbnetkotlin.adapter.SelectEntryListener
import com.mariyalozjkina.testappbnetkotlin.data.Entry
import com.mariyalozjkina.testappbnetkotlin.data.GetEntriesResponse
import com.mariyalozjkina.testappbnetkotlin.data.NewSessionResponse
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

class EntryListActivity : AppCompatActivity() {

    private val rvEntrys: RecyclerView by lazy { findViewById(R.id.rvEntrys) }
    private val tvCreateEntry: TextView by lazy { findViewById(R.id.tvCreateEntry) }
    private lateinit var entryAdapter: EntryAdapter
    private val handler = Handler()
    private var session: String? = null
    private val gson = Gson()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            loadEntries()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_list)
        startNewSession()
        initList()
        tvCreateEntry.setOnClickListener {
            openCreateEntry()
        }
    }

    private fun openCreateEntry() {
        val intent = Intent(this, CreateEntryActivity::class.java)
        intent.putExtra(Constants.SESSION, session)
        startActivityForResult(intent, 1)
    }

    private fun initList() {
        entryAdapter = EntryAdapter(object : SelectEntryListener {
            override fun selectEntry(entry: Entry) {
                openEntry(entry)
            }
        })
        val layoutManager = LinearLayoutManager(this)
        rvEntrys.adapter = entryAdapter
        rvEntrys.layoutManager = layoutManager
    }

    private fun openEntry(entry: Entry) {
        val intent = Intent(this, EntryActivity::class.java)
        intent.putExtra(Constants.BODY, entry.body)
        intent.putExtra(Constants.DA, entry.da)
        intent.putExtra(Constants.DM, entry.dm)
        startActivity(intent)
    }

    private fun setSession(session: String) {
        this.session = session
    }

    private fun startNewSession() {
        Thread {
            val newSessionJson = getNewSession()
            handler.post {
                if (newSessionJson == null) {
                    showSessionAlert()
                    return@post
                }
                val response = gson.fromJson(newSessionJson, NewSessionResponse::class.java)
                if (response.status == 0) {
                    showSessionAlert()
                } else {
                    val session = response.data?.session
                    if (session != null) {
                        setSession(session)
                    }
                }
            }
        }.start()
    }

    private fun showSessionAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.error)
        builder.setMessage(R.string.check_connection)
        builder.setPositiveButton(R.string.update_data) { dialog, which ->
            startNewSession()
        }
        builder.show()
    }

    private fun getNewSession(): String? {
        val url = Constants.URL
        val params = hashMapOf<String, String>()
        params["a"] = "new_session"
        var c: HttpURLConnection? = null
        try {
            val u = URL(url)
            c = u.openConnection() as HttpURLConnection
            c.requestMethod = "POST"
            c.setRequestProperty("token", Constants.TOKEN)
            val postData = StringBuilder()
            for ((key, value) in params) {
                if (postData.length != 0) postData.append('&')
                postData.append(URLEncoder.encode(key, "UTF-8"))
                postData.append('=')
                postData.append(URLEncoder.encode(value, "UTF-8"))
            }
            val postDataBytes = postData.toString().toByteArray(charset("UTF-8"))
            c.doOutput = true
            c.outputStream.write(postDataBytes)
            c.connectTimeout = Constants.TIMEOUT
            c.readTimeout = Constants.TIMEOUT
            c.connect()
            val status = c.responseCode
            when (status) {
                200, 201 -> {
                    val br = BufferedReader(InputStreamReader(c.inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        sb.append(line + "\n")
                    }
                    br.close()
                    val result = sb.toString()
                    println(result)
                    return result
                }
            }
        } catch (ex: MalformedURLException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        } finally {
            if (c != null) {
                try {
                    c.disconnect()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        return null
    }

    private fun loadEntries() {
        Thread {
            val getEntriesJson = requestGetEntries(session)
            handler.post {
                if (getEntriesJson == null) {
                    showGetEntriesAlert()
                    return@post
                }
                val response = gson.fromJson(getEntriesJson, GetEntriesResponse::class.java)
                if (response.status == 0) {
                    showGetEntriesAlert()
                } else {
                    val entries = response.data?.get(0) ?: listOf()
                    entryAdapter.setItems(entries)
                    entryAdapter.notifyDataSetChanged()
                }
            }
        }.start()
    }

    private fun showGetEntriesAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.error)
        builder.setMessage(R.string.check_connection)
        builder.setPositiveButton(R.string.update_data) { dialog, which ->
        }
        builder.show()
    }

    private fun requestGetEntries(session: String?): String?{
        if (session == null) {
            return null
        }
        val url = Constants.URL
        val params = hashMapOf<String, String>()
        params["a"] = "get_entries"
        params["session"] = session
        var c: HttpURLConnection? = null
        try {
            val u = URL(url)
            c = u.openConnection() as HttpURLConnection
            c.requestMethod = "POST"
            c.setRequestProperty("token", Constants.TOKEN)
            val postData = StringBuilder()
            for ((key, value) in params) {
                if (postData.length != 0) postData.append('&')
                postData.append(URLEncoder.encode(key, "UTF-8"))
                postData.append('=')
                postData.append(URLEncoder.encode(value, "UTF-8"))
            }
            val postDataBytes = postData.toString().toByteArray(charset("UTF-8"))
            c.doOutput = true
            c.outputStream.write(postDataBytes)
            c.connectTimeout = Constants.TIMEOUT
            c.readTimeout = Constants.TIMEOUT
            c.connect()
            val status = c.responseCode
            when (status) {
                200, 201 -> {
                    val br = BufferedReader(InputStreamReader(c.inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        sb.append(line + "\n")
                    }
                    br.close()
                    val result = sb.toString()
                    println(result)
                    return result
                }
            }
        } catch (ex: MalformedURLException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        } finally {
            if (c != null) {
                try {
                    c.disconnect()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        return null
    }
}