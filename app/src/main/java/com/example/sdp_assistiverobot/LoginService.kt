package com.example.sdp_assistiverobot

import android.app.IntentService
import android.app.Service
import android.content.Intent


class LoginService : IntentService("LoginService") {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_REDELIVER_INTENT
    }

    override fun onHandleIntent(intent: Intent?) {

        Thread {
            val username = intent?.getStringExtra("username")
            val password = intent?.getStringExtra("password")

        }.start()
    }

}
