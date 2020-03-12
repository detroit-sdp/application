package com.example.sdp_assistiverobot.map

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.sdp_assistiverobot.MainActivity
import com.example.sdp_assistiverobot.util.Constants
import java.net.DatagramPacket
import java.net.DatagramSocket


class NetworkCommService : Service() {

    private val TAG = "NetworkCommService"

    private val LISTENER_PORT = 20002

    private val IN_ROOM = 0
    private val MOVING = 1
    private val DELIVERED = 2
    private val STUCK = 3
    private val LOW_BATTERY = 4

    private val CHANNEL_ID = "TadashiNetworkService"

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val iGuardServices = IGuardServices.Stub.asInterface(service)
            try {
                Log.i(TAG, "connected with " + iGuardServices.getServiceName())
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            startService(Intent(this@NetworkCommService, GuardService::class.java))
            bindService(Intent(this@NetworkCommService, GuardService::class.java), this, Context.BIND_IMPORTANT)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return object: IGuardServices.Stub() {
            override fun getServiceName(): String {
                return "NetworkCommService"
            }
        }
    }

    private lateinit var mSocket: DatagramSocket

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
//        val restartIntent = Intent(applicationContext, this.javaClass)
//        val restartServicePI = PendingIntent.getService(applicationContext, 1
//            , restartIntent, PendingIntent.FLAG_ONE_SHOT)
//
//        val alarmService = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val now = System.currentTimeMillis()
//        alarmService.setInexactRepeating(AlarmManager.RTC_WAKEUP, now, 5000, restartServicePI)
//        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), restartServicePI)

//        buildNotification()
    }

    private fun buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Tadashi Network Service", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tadashi Robot Status")
            .setContentText("Running")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext, "Service started", Toast.LENGTH_SHORT).show()
        bindService(Intent(this@NetworkCommService, GuardService::class.java), connection, Context.BIND_IMPORTANT)
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
        Log.d(TAG, "Service onDestroy")
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "Service onTaskRemoved")
        Toast.makeText(applicationContext, "Service killed", Toast.LENGTH_SHORT).show()
        super.onTaskRemoved(rootIntent)
    }
}
