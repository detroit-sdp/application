package com.example.sdp_assistiverobot.calendar

import com.example.sdp_assistiverobot.residents.Resident

data class Event(val date: String,
                 val hour: Int,
                 val minute: Int,
                 val resident: Resident,
                 val note: String = "")
