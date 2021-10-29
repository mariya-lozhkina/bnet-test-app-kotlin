package com.mariyalozjkina.testappbnetkotlin.activity

import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mariyalozjkina.testappbnetkotlin.Constants
import com.mariyalozjkina.testappbnetkotlin.R
import com.mariyalozjkina.testappbnetkotlin.data.AddEntryResponse
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder


class CreateEntryActivity : AppCompatActivity() {

    private val etTextField: EditText by lazy { findViewById(R.id.etTextField) }
    private val tvSave: TextView by lazy { findViewById(R.id.tvSave) }
    private val tvCancel: TextView by lazy { findViewById(R.id.tvCancel) }
    private val handler = Handler()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_entry)
        tvSave.setOnClickListener {
            addEntry()
        }
        tvCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun addEntry() {
        val intent = intent
        val session = intent.getStringExtra(Constants.SESSION)
        val body = etTextField.text.toString()
        Thread {
            val requestAddEntryJson = requestAddEntry(session, body)
            handler.post {
                if (requestAddEntryJson == null) {
                    showAddEntryAlert()
                    return@post
                }
                val response = gson.fromJson(requestAddEntryJson, AddEntryResponse::class.java)
                if (response.status == 1) {
                    setResult(RESULT_OK)
                    finish()
                }else {
                    showAddEntryAlert()
                }
            }
        }.start()
    }

    private fun showAddEntryAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.error)
        builder.setMessage(R.string.check_connection)
        builder.setPositiveButton(R.string.update_data) { dialog, which ->
            addEntry()
        }
        builder.show()
    }

    private fun requestAddEntry(session: String?, body: String) : String? {
        if (session == null) {
            return null
        }
        val url = Constants.URL
        val timeout = 5000
        val params = hashMapOf<String, String>()
        params["a"] = "add_entry"
        params["session"] = session
        params["body"] = body
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
            c.connectTimeout = timeout
            c.readTimeout = timeout
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