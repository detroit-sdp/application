package com.example.sdp_assistiverobot.map

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.example.sdp_assistiverobot.util.Constants
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class NetworkCommService : Service() {

    private val TAG = "NetworkCommService"

    private val LISTENER_PORT = 20002

    private val IN_ROOM = 0
    private val MOVING = 1
    private val DELIVERED = 2
    private val STUCK = 3
    private val LOW_BATTERY = 4

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

    private fun handleReceivedMessage(packet: DatagramPacket) {
        val inMessage = String(packet.data, 0, packet.length)

        sendBroadcast(Intent().apply {
            action = Constants.ACTION_NETWORK_RECEIVE
            putExtra("message", "$inMessage from ${packet.address}")
        })
    }

    override fun onDestroy() {
        onTaskRemoved(null)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartIntent = Intent(applicationContext, this.javaClass)
        val restartServicePI = PendingIntent.getService(applicationContext, 1
            , restartIntent, PendingIntent.FLAG_ONE_SHOT)

        val alarmService = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), restartServicePI)
        super.onTaskRemoved(rootIntent)
    }
}
