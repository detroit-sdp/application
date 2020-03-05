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

    private val SEND_START = 0
    private val SEND_SUCCESS = 1
    private val SEND_FAIL = -1

    private val handler: Handler = object: Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                SEND_START -> {
                    mProgressBar.visibility = ProgressBar.VISIBLE
                }
                SEND_SUCCESS -> {
                    mProgressBar.visibility = ProgressBar.GONE
                }
                SEND_FAIL -> {
                    mButton?.imageTintList = ContextCompat.getColorStateList(mContext, R.color.colorPrimary)
                    mButton?.setImageResource(R.drawable.baseline_person_pin_circle_black_36)
                    mButton?.tag = 1
                }
            }
        }

    }

    private val locationToComms: HashMap<String, String> = hashMapOf(
        "Room 1" to "GOTO 3",
        "Room 2" to "GOTO 3",
        "Room 3" to "GOTO 3")

    private lateinit var mProgressBar: ProgressBar
    private var mButton: ImageButton? = null
    private lateinit var mContext: Context

    private val mInstance = NetworkManager

    fun getInstance(): NetworkManager {
        return mInstance
    }

    fun sendCommand(location: String, progressBar: ProgressBar, button: ImageButton?) {
//        networkThreadPool.execute(SendCommandRunnable(locationToComms[location]!!))
        mProgressBar = progressBar
        mButton = button
    }

    fun setContext(context: Context) {
        mContext = context
    }


    fun handleState(state: Int) {
        handler.obtainMessage(state)?.apply {
            sendToTarget()
        }
    }
}