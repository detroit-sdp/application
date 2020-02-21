package com.example.sdp_assistiverobot.map

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import kotlinx.android.synthetic.main.fragment_map.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.StringBuilder
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Socket

class NetworkCommService : Service() {

    private val TAG = "NetworkCommService"

    private val LISTENER_PORT = 20002

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private lateinit var mSocket: DatagramSocket

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "Connecting")

        Thread {
            openPort()
            startListen()
        }.start()

        return START_STICKY
    }

    private fun openPort() {
        try{
            mSocket = DatagramSocket(LISTENER_PORT).also {
                it.broadcast = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun startListen() {
        Log.d("ListenerRunnable", "Start Listening...")

        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)
        while (true) {
            try {
                if(Thread.interrupted()) {
                    mSocket.close()
                    return
                }
                mSocket.receive(packet)
                if (packet.data != null) {
//                    mTask.setInBuffer(buffer.data)
                    val inMessage = String(packet.data, 0, packet.length)
                    Log.d(TAG, inMessage)
                    sendBroadcast(Intent().apply {
                        action = "com.example.sdp_assistiverobot.getMessage"
                        putExtra("message", inMessage)
                    })
//                    mTask.handleReceiveState(DATA_RECEIVED)
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                mTask.handleReceiveState(LISTEN_FAILURE)
            }
        }
    }

}
