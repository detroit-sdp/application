package com.example.sdp_assistiverobot.calendar

import com.example.sdp_assistiverobot.residents.Resident
import java.io.Serializable

data class Event(val date: Long,
                 val hour: Int,
                 val minute: Int,
                 val location: String,
                 val note: String = "") : Serializable
