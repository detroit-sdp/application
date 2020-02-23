package com.example.sdp_assistiverobot.util

import com.example.sdp_assistiverobot.R

object Constants {

    val ACTION_NETWORK_RECEIVE = "com.example.sdp_assistiverobot.getMessage"
    val CHANNEL_ID = "0315"
    var NOTIFICATION_ID = -1
    val DEMO_MAP: HashMap<Int, String> = hashMapOf(R.id.room_1 to "GOTO 1"
        , R.id.room_2 to "GOTO 2", R.id.room_3 to "GOTO 3", R.id.room_4 to "GOTO 4"
        , R.id.room_5 to "GOTO 5", R.id.charge_station to "Charging Station")
    var HAS_MAIN_FOCUSED = false
}
