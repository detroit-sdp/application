package com.example.sdp_assistiverobot.calendar

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.sdp_assistiverobot.util.NetworkManager
import com.example.sdp_assistiverobot.util.Constants.Delivery_Pending
import com.example.sdp_assistiverobot.util.Constants.Delivery_Send
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.util.DatabaseManager.DATABASE
import com.example.sdp_assistiverobot.util.Util
import java.util.*

class DeliveryMonitorService : Service() {

    private val TAG = "DeliveryMonitorService"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val restartIntent = Intent(applicationContext, this.javaClass)
        val restartServicePI = PendingIntent.getService(applicationContext, 0, restartIntent, 0)

        val calendar: Calendar = Calendar.getInstance()

        val alarmService = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 1000 * 60, restartServicePI)
        monitorDelivery()

        return START_STICKY
    }

    private fun monitorDelivery() {
        val calendar = Calendar.getInstance()
        val hour = "${calendar.get(Calendar.HOUR_OF_DAY)}".padStart(2,'0')
        val minute = "${calendar.get(Calendar.MINUTE)}".padStart(2,'0')
        val now = Util.convertTimeToLong("$hour:$minute")
        val residents = DatabaseManager.getResidents()
        DatabaseManager.eventsRef
            .whereEqualTo("userId", DatabaseManager.authUser?.email)
            .whereEqualTo("date", Util.todayToLong())
            .whereEqualTo("time", now)
            .whereEqualTo("deliveryState", Delivery_Pending)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    val resident = document.get("residentId").toString()
                    NetworkManager.sendCommand((residents[resident] ?: error("")).location)
                    DATABASE.runTransaction { transaction ->
                            transaction.update(
                                DatabaseManager.eventsRef.document(document.id),
                                "deliveryState",
                                Delivery_Send
                            )
                        }.addOnSuccessListener {
                            Log.d(TAG, "Transaction success!")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Transaction failure.", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}
