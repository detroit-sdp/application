package com.example.sdp_assistiverobot.calendar

import com.example.sdp_assistiverobot.residents.Resident
import java.io.Serializable

data class Delivery(val userId: String,
                    val date: Long,
                    val time: Long,
                    val residentId: String,
                    val category: String,
                    val weightBefore: Double,
                    val weightAfter: Double,
                    val deliveryState: String,
                    val note: String = "") : Serializable
