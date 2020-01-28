package com.example.sdp_assistiverobot.patients

import java.util.*

data class Patient(val first: String,
                   val last: String,
                   val dob: String,
                   val gender: String,
                   val medicalState: String,
                   var note: String = "",
                   val location: String = "") {

    fun getAge(): Int {
        val parts= dob.split("/")
        val cldr = Calendar.getInstance()
        var age = cldr.get(Calendar.YEAR) - parts[2].toInt()

        if (cldr.get(Calendar.MONTH) < parts[1].toInt()
            || (cldr.get(Calendar.MONTH) == parts[1].toInt() && (cldr.get(Calendar.DAY_OF_MONTH) < parts[0].toInt()))) {
            age--
        }

        return age
    }

}

