package com.example.sdp_assistiverobot.map

import android.content.Context
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import android.widget.Toast
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.coroutines.coroutineContext

class SendCommandRunnable(private val message: String): Runnable {

    private val TAG = "SendMessageRunnable"
    lateinit var mThread: Thread
    private val IP_ADDRESS = "192.168.105.172"
    private val PORT = 20001

    override fun run() {
        android.os.Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND)

        mThread = Thread.currentThread()
        val socket: DatagramSocket
        try {
            Log.d(TAG, "Sending $message to $IP_ADDRESS:$PORT")

            if(Thread.interrupted()) {
                return
            }
            socket = DatagramSocket().also {
                it.broadcast = true
            }

            val out = message.toByteArray()
            val outPackage = DatagramPacket(out, out.size, InetAddress.getByName(IP_ADDRESS), PORT)
            if(Thread.interrupted()){
                return
            }
            socket.send(outPackage)
            socket.close()
            Log.d(TAG, "Send success")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}