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
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Socket

class NetworkCommService : Service() {

    private val TAG = "NetworkCommService"

    private val LISTEN_PORT = 20002

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
            mSocket = DatagramSocket(LISTEN_PORT).also {
                it.broadcast = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun startListen() {
        Log.d("ListenerRunnable", "Start Listening...")

        val buffer = ByteArray(1024)
        val outPacket = DatagramPacket(buffer, buffer.size)

        while (true) {
            try {
                if(Thread.interrupted()) {
                    mSocket.close()
                    return
                }
                mSocket.receive(outPacket)
                if (outPacket.data != null) {
//                    mTask.setInBuffer(buffer.data)
                    Log.d(TAG, "${outPacket.data}")
//                    mTask.handleReceiveState(DATA_RECEIVED)
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                mTask.handleReceiveState(LISTEN_FAILURE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.close()
    }


}
