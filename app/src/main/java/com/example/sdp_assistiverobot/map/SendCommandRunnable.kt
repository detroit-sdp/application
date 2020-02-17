package com.example.sdp_assistiverobot.map

import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class SendCommandRunnable(private val ip: String, private val port: Int, private val message: String): Runnable {

    companion object {
        const val SEND_FAILURE = -1
        const val SEND_START = 0
        const val SEND_SUCCEED = 1
    }

    interface SendCommandInterface{
        fun setSendThread(currentThread: Thread)
        fun getSendThread(): Thread
        fun handleSendState(state: Int)
    }

    private val TAG = "SendMessageRunnable"
    lateinit var mThread: Thread

    override fun run() {
        android.os.Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND)

        mThread = Thread.currentThread()

        try {
            Log.d(TAG, "Sending $message")
            if(Thread.interrupted()) {
                return
            }
            val socket = DatagramSocket().also {
                it.broadcast = true
            }

            val out = message.toByteArray()
            val outPackage = DatagramPacket(out, out.size, InetAddress.getByName(ip), port)
            if(Thread.interrupted()){
                return
            }
            socket.send(outPackage)
            socket.close()
            Log.d(TAG, "Send success")
//            mTask.handleSendState(SEND_SUCCEED)
        } catch (e: Exception) {
            e.printStackTrace()
//            mTask.handleSendState(SEND_FAILURE)
        }
    }
}