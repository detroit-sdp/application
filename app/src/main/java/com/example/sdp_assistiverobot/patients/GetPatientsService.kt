package com.example.sdp_assistiverobot.patients

import android.app.Service
import android.content.Intent
import android.os.IBinder

class GetPatientsService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
