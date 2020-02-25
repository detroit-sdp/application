package com.example.sdp_assistiverobot.util

import java.io.Serializable

data class Resident(val carer: String,
                    val first: String,
                    val last: String,
                    val priority: String,
                    val location: String = ""): Serializable {

//    fun getAge(): Int {
//        val parts= dob.split("/")
//        val cldr = Calendar.getInstance()
//        var age = cldr.get(Calendar.YEAR) - parts[2].toInt()
//
//        if (cldr.get(Calendar.MONTH) < parts[1].toInt()
//            || (cldr.get(Calendar.MONTH) == parts[1].toInt() && (cldr.get(Calendar.DAY_OF_MONTH) < parts[0].toInt()))) {
//            age--
//        }
//
//        return age
//    }
}

