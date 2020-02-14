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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Listen the port forever
        Thread(Runnable {
            while (true) {

            }
        }).start()

        return START_STICKY
    }

}
