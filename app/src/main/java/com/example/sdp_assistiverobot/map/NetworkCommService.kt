package com.example.sdp_assistiverobot.map

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class NetworkCommService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val IP_ADDRESS = "127.0.0.1"
    private val PORT = 8080
    private lateinit var socket: Socket
    private lateinit var out: PrintWriter
    private lateinit var input: BufferedReader
    private var isConnected  = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            "Connect" -> connect()
        }

        // Listen the port forever
        Thread(Runnable {
            while (true) {

            }
        }).start()

        return START_STICKY
    }

    private fun connect() {
    }
}
