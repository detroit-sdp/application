package com.example.sdp_assistiverobot.map

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object NetworkManager {
    private val networkWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()

    fun getInstance(): NetworkManager {
        return this
    }

    fun startListening() {

    }

    fun removeListening(netTask: NetworkTask, hostName: String) {

    }

    fun sendMessage(message: String) {

    }

    fun cancelAll() {
        /*
         * Creates and populates an array of Runnables with the Runnables in the queue
         */
        val taskArray: Array<NetworkTask?> = arrayOfNulls(this.networkWorkQueue.size)

        /*
         * Iterates over the array of Runnables and interrupts each one's Thread.
         */
        synchronized(this) {
            // Iterates over the array of tasks

        }
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
        networkWorkQueue
    )
}