package com.example.sdp_assistiverobot.map

import android.util.Log

class ReceiveMessageRunnable(private val mTask: NetworkTask): Runnable {

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

    override fun run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)

        mTask.setSendThread(Thread.currentThread())

        while (true) {
            try {
                if(Thread.interrupted()) {
                    return
                }
                mTask.handleReceiveState(LISTEN_START)
                val socket = mTask.openSocket()
                val buffer = mTask.createPackage()
                Log.d(TAG, "Listening...")

                if(Thread.interrupted()) {
                    socket.close()
                    return
                }
                socket.receive(buffer)
                if (buffer.data != null) {
                    mTask.handleReceiveState(DATA_RECEIVED)
                }
            } catch (e: Exception) {
                mTask.handleReceiveState(LISTEN_FAILURE)
            }
        }
    }
}