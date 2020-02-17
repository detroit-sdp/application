package com.example.sdp_assistiverobot.map

import android.util.Log
import java.net.DatagramSocket
import java.nio.charset.Charset

class ListenerRunnable(private val mTask: NetworkTask): Runnable {

    companion object {
        const val LISTEN_FAILURE = -1
        const val LISTEN_START = 0
        const val DATA_RECEIVED = 1
    }

    interface ReceiveMessageInterface{
        fun setReceiveThread(currentThread: Thread)
        fun getReceiveThread(): Thread
        fun handleReceiveState(state: Int)
    }

    private val TAG = "ReceiveMessageRunnable"
    lateinit var mThread: Thread

    override fun run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
        Log.d("ListenerRunnable", "connecting")
        mThread = Thread.currentThread()
        mTask.setReceiveThread(mThread)
        if(Thread.interrupted()) {
            return
        }
        mTask.handleReceiveState(LISTEN_START)
        val socket = mTask.openSocket()
//        val socket = DatagramSocket(12345).also {
//            it.broadcast = true
//        }
        val buffer = mTask.createPackage()

        while (true) {
            try {
                if(Thread.interrupted()) {
                    socket.close()
                    return
                }
                socket.receive(buffer)
                if (buffer.data != null) {
                    mTask.setInBuffer(buffer.data)
                    Log.d(TAG, "${buffer.data}")
                    mTask.handleReceiveState(DATA_RECEIVED)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mTask.handleReceiveState(LISTEN_FAILURE)
            }
        }
    }
}