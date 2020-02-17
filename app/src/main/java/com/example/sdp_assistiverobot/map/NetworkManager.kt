package com.example.sdp_assistiverobot.map

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.TextView
import org.w3c.dom.Text
import java.net.InetAddress
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object NetworkManager {
    private val senderWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
    private val listenerWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
    private val networkTaskQueue: BlockingQueue<NetworkTask> = LinkedBlockingQueue<NetworkTask>()

    // Check how many processors on the machine
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    // Sets the amount of time an idle thread waits before terminating
    private const val KEEP_ALIVE_TIME = 1L
    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    // Creates a thread pool manager
    private val senderThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,       // Initial pool size
        NUMBER_OF_CORES,       // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        senderWorkQueue
    )
    private val listenerThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,       // Initial pool size
        NUMBER_OF_CORES,       // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        listenerWorkQueue
    )

    private lateinit var text: TextView

    private val handler: Handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(inputmsg: Message?) {

        }
    }

    fun setTextView(text: TextView) {
        this.text = text
    }

    private val sInstance: NetworkManager = NetworkManager

    fun handleState(networkTask: NetworkTask, state: Int) {

    }

    fun getInstance(): NetworkManager {
        return sInstance
    }

    fun startComm(ipaddress: String): NetworkTask {
        var listenTask = sInstance.networkTaskQueue.poll()

        if (listenTask == null) {
            listenTask = NetworkTask(ipaddress)
        }
        Log.d("Manager", "Connecting")
        sInstance.listenerThreadPool.execute(listenTask.getListenerRunnable())

        return listenTask
    }

    fun sendCommand(network: NetworkTask, message: String) {
        network.initializeSendTask(message)
        Log.d("Manager", "Sending")
        sInstance.senderThreadPool.execute(network.getSendCommRunnable())
    }

    fun removeListening(netTask: NetworkTask, hostName: String) {
        if (netTask.inetAddress == InetAddress.getByName(hostName)) {
            synchronized(sInstance) {
                netTask.getReceiveThread().interrupt()
            }
        }

        sInstance.listenerThreadPool.remove(netTask.getListenerRunnable())
    }

    fun cancelAll() {
        /*
         * Creates and populates an array of Runnables with the Runnables in the queue
         */
        val runnableArray: Array<Runnable> = listenerWorkQueue.toTypedArray()
        /*
         * Iterates over the array of Runnables and interrupts each one's Thread.
         */
        synchronized(this) {
            // Iterates over the array of tasks
            runnableArray.map { (it as? ListenerRunnable)?.mThread }
                .forEach { thread ->
                    thread?.interrupt()
                }
        }
    }

}