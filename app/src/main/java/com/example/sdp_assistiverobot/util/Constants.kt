package com.example.sdp_assistiverobot.util

import com.example.sdp_assistiverobot.R
import com.google.firebase.auth.FirebaseAuth

object Constants {

    val ACTION_NETWORK_RECEIVE = "com.example.sdp_assistiverobot.getMessage"
    val CHANNEL_ID = "0315"
    var NOTIFICATION_ID = -1
    var HAS_MAIN_FOCUSED = false
    val currentUser = FirebaseAuth.getInstance().currentUser
}