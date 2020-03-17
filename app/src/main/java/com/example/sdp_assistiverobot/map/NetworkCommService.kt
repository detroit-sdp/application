package com.example.sdp_assistiverobot.map

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.sdp_assistiverobot.MainActivity
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.calendar.Delivery
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.util.DatabaseManager.authUser
import com.example.sdp_assistiverobot.util.Util.convertTimeToLong
import com.example.sdp_assistiverobot.util.Util.todayToLong
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.*


class NetworkCommService : Service() {

    private val TAG = "NetworkCommService"

    private val LISTENER_PORT = 20002

    private val LOW_BATTERY = "STATUS LOW_BATTERY"
    private val ASSISTANCE = "STATUS ASSISTANCE"
    private val BASE = "STATUS BASE"
    private val MOVING = "STATUS MOVING"
    private val ARRIVED = "ARRIVED"

    private val CHANNEL_ID = "TadashiNetworkService"

    private lateinit var notification: Notification

//    private val connection = object: ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val iGuardServices = IGuardServices.Stub.asInterface(service)
//            try {
//                Log.i(TAG, "connected with " + iGuardServices.getServiceName())
//            } catch (e: RemoteException) {
//                e.printStackTrace()
//            }
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            startService(Intent(this@NetworkCommService, GuardService::class.java))
//            bindService(Intent(this@NetworkCommService, GuardService::class.java), this, Context.BIND_IMPORTANT)
//        }
//    }

    override fun onBind(intent: Intent?): IBinder? {
//        return object: IGuardServices.Stub() {
//            override fun getServiceName(): String {
//                return "NetworkCommService"
//            }
//        }

        return null
    }

    private lateinit var mSocket: DatagramSocket

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
        val restartIntent = Intent(applicationContext, this.javaClass)
        val restartServicePI = PendingIntent.getService(applicationContext, 0, restartIntent, 0)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }

        val alarmService = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 1000 * 60 * 20, restartServicePI)
//        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), restartServicePI)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Tadashi Network Service", NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun buildNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_black_24)
            .setContentTitle("Tadashi Service Running")
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext, "Service started", Toast.LENGTH_SHORT).show()
//        bindService(Intent(this@NetworkCommService, GuardService::class.java), connection, Context.BIND_IMPORTANT)
        createNotificationChannel()
        buildNotification()
        startForeground(1, notification)

        // Incoming message monitor
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
        handleReceivedMessage(DatagramPacket(MOVING.toByteArray(), MOVING.toByteArray().size))
        while (true) {
            try {
                if(Thread.interrupted() && !mSocket.isClosed) {
                    Log.d(TAG, "Listener thread interrupted")
                    mSocket.close()
                    notification
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

        Log.d(TAG,  inMessage)

        // Update realtime database
        if (inMessage.substring(0,5) == "STATUS") {
            DatabaseManager.robotStatusRef.setValue(inMessage.substring(7))
        }

        sendBroadcast(Intent().apply {
            action = "com.example.sdp_assistiverobot.getMessage"
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
        Toast.makeText(applicationContext, "Tadashi connection lost", Toast.LENGTH_LONG).show()
        // Remove notification
        super.onTaskRemoved(rootIntent)
    }
}
