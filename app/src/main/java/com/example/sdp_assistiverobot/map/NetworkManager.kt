package com.example.sdp_assistiverobot.map

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.sdp_assistiverobot.R
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object NetworkManager {

    private val networkWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
    // Check how many processors on the machine
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    // Sets the amount of time an idle thread waits before terminating
    private val KEEP_ALIVE_TIME = 1L
    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    // Creates a thread pool manager
    private val networkThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,       // Initial pool size
        NUMBER_OF_CORES,       // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        networkWorkQueue
    )

    private val locationToComms: HashMap<String, String> = hashMapOf(
        "Room 1" to "GOTO 1",
        "Room 2" to "GOTO 2",
        "Room 3" to "GOTO 3",
        "Base" to "GOTO Base")

    private val mInstance = NetworkManager

    fun getInstance(): NetworkManager {
        return mInstance
    }

    fun sendCommand(location: String) {
        networkThreadPool.execute(SendCommandRunnable(locationToComms[location]!!))
    }
}