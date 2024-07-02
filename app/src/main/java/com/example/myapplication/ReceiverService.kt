package com.example.myapplication

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket

class ReceiverService : Service() {

    private val PORT = 12345

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread {
            try {
                val serverSocket = ServerSocket(PORT)
                while (true) {
                    val clientSocket = serverSocket.accept()
                    val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                    val receivedText = reader.readLine()
                    writeTextToClipboard(receivedText)
                    clientSocket.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
        return START_STICKY
    }

    private fun writeTextToClipboard(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("receivedText", text)
        clipboardManager.setPrimaryClip(clip)
        runOnUiThread {
            Toast.makeText(this, "Text received and copied to clipboard: $text", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun runOnUiThread(action: Runnable) {
        val handler = android.os.Handler(mainLooper)
        handler.post(action)
    }
}
