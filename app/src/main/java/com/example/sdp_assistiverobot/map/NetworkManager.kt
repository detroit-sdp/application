package com.example.sdp_assistiverobot.map

import android.app.Service
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object NetworkManager {
    private val outWordQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()

    fun startSendMessage() {

    }
    // Check how many processors on the machine
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    // Sets the amount of time an idle thread waits before terminating
    private const val KEEP_ALIVE_TIME = 1L
    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    // Creates a thread pool manager
    private val outThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,       // Initial pool size
        NUMBER_OF_CORES,       // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        outWordQueue
    )
}