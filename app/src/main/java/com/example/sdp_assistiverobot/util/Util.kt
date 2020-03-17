package com.example.sdp_assistiverobot.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object Util {
    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }

    val calendar = Calendar.getInstance()
    fun getDayOfMonth(): Int {
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
    fun getMonth(): Int {
        return calendar.get(Calendar.MONTH)+1
    }
    fun getYear(): Int {
        return calendar.get(Calendar.YEAR)
    }

    fun todayToLong(): Long {
        val curDay = "${getDayOfMonth()}".padStart(2,'0')
        val curMonth = "${getMonth()}".padStart(2,'0')
        val curYear = "${getYear()}"
        return convertDateToLong("${curYear}.${curMonth}.${curDay}")
    }

    fun formatName(string: String): String {
        return string[0].toUpperCase()+string.substring(1).toLowerCase()
    }

    fun convertDateToLong(time: String): Long {
        val df = SimpleDateFormat("yyyy.MM.dd")
        return df.parse(time).time
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm")
        return format.format(date)
    }

    fun convertTimeToLong(time: String): Long {
        val df = SimpleDateFormat("HH:mm")
        return df.parse(time).time
    }

    fun generateEventId(time: String): String {
        val df = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return df.parse(time).time.toString()
    }
}