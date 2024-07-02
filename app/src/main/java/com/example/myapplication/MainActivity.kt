package com.example.myapplication

import android.content.ClipboardManager
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream
import java.net.Socket
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var ipAddressEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start the receiver service
        val intent = Intent(this, ReceiverService::class.java)
        startService(intent)

        ipAddressEditText = findViewById(R.id.ipAddressEditText)
        val sendButton: Button = findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val ipAddress = ipAddressEditText.text.toString()
            if (ipAddress.isNotEmpty()) {
                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipboardText = clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()

                if (clipboardText != null && clipboardText.isNotEmpty()) {
                    SendTextTask(ipAddress, clipboardText).execute()
                } else {
                    Toast.makeText(this, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid IP address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class SendTextTask(private val ipAddress: String, private val text: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                val socket = Socket(ipAddress, 12345)
                val outputStream: OutputStream = socket.getOutputStream()
                outputStream.write(text.toByteArray())
                outputStream.flush()
                outputStream.close()
                socket.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result) {
                Toast.makeText(MyApp.instance, "Text sent successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(MyApp.instance, "Failed to send text", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
