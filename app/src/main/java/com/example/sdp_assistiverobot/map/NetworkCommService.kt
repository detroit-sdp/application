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
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
            openPort()
            startListen()
        }).start()
        return START_STICKY
    }

    private fun openPort() {
        Log.d(TAG, "Port opened")
        try{
            mSocket = DatagramSocket(LISTENER_PORT, InetAddress.getByName("192.168.105.106")).also {
                it.broadcast = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    val inMessage = String(packet.data, 0, packet.length)
                    Log.d(TAG, inMessage)

                    handleReceivedMessage(inMessage)
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

    private fun handleReceivedMessage(message: String) {
        sendBroadcast(Intent().apply {
            action = Constants.ACTION_NETWORK_RECEIVE
            putExtra("message", message)
        })
    }
}
