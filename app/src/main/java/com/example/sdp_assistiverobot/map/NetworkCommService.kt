package com.example.sdp_assistiverobot.map

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.sdp_assistiverobot.util.Constants
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class NetworkCommService : Service() {

    private val TAG = "NetworkCommService"

    private val LISTENER_PORT = 20002

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private lateinit var mSocket: DatagramSocket

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread(Runnable {
            if (openPort()) {
                startListen()
            }
            Thread.currentThread().interrupt()
        }).start()
        return START_STICKY
    }

    private fun openPort(): Boolean {
        Log.d(TAG, "Opening port...")
        try{
            mSocket = DatagramSocket(LISTENER_PORT).also {
                it.broadcast = true
            }
        } catch (e: Exception) {
            Log.d(TAG, "Port is in use")
            return false
        }

        return true
    }

    private fun startListen() {
        Log.d(TAG, "Start Listening...")

        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)
        while (true) {
            try {
                if(Thread.interrupted() && !mSocket.isClosed) {
                    Log.d(TAG, "Listener thread interrupted")
                    mSocket.close()
                    return
                }
                mSocket.receive(packet)

                if (packet.data != null) {
                    handleReceivedMessage(packet)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mSocket.close()
                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.close()
        Log.d(TAG, "Service destroyed")
    }

    private fun handleReceivedMessage(packet: DatagramPacket) {
        val inMessage = String(packet.data, 0, packet.length)

        sendBroadcast(Intent().apply {
            action = Constants.ACTION_NETWORK_RECEIVE
            putExtra("message", "$inMessage from ${packet.address}")
        })
    }
}
