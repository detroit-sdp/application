package com.example.sdp_assistiverobot.map

import android.util.Log
import android.widget.TextView
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class NetworkTask {

    var inetAddress = InetAddress.getByName("100.67.203.12")
    private val robotPort = 20001
    private var mPort = 12345

    private lateinit var mSocket: DatagramSocket
    private var listenerRunnable: ListenerRunnable
    private lateinit var sendCommRunnable: SendCommandRunnable
    private lateinit var mReceiveThread: Thread
    private lateinit var mSendThread: Thread
    private val mNetworkManager: NetworkManager = NetworkManager.getInstance()
    lateinit var buffer: ByteArray

    constructor(ipAddress: String) {
        inetAddress = InetAddress.getByName(ipAddress)
        listenerRunnable = ListenerRunnable(this)
    }

    fun initializeSendTask(message: String) {
//        sendCommRunnable = SendCommandRunnable(this, message)
    }

    fun openSocket(): DatagramSocket {
        try {
            mSocket = DatagramSocket(mPort).also {
                it.broadcast = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mSocket
    }

    fun createPackage(message: String): DatagramPacket {
        val out = message.toByteArray()
        return  DatagramPacket(out, out.size, inetAddress, robotPort)
    }

    fun createPackage(): DatagramPacket {
        val buffer = ByteArray(1024)
        return DatagramPacket(buffer, buffer.size)
    }

    fun setInBuffer(buffer: ByteArray) {
        this.buffer = buffer
    }

    fun getSocket(): DatagramSocket {
        return mSocket
    }

    fun getSendCommRunnable(): SendCommandRunnable {
        return sendCommRunnable
    }

    fun getListenerRunnable(): ListenerRunnable {
        return listenerRunnable
    }

//    override fun setSendThread(currentThread: Thread) {
//        synchronized(mNetworkManager) {
//            mSendThread = currentThread
//        }
//    }
//
//    override fun setReceiveThread(currentThread: Thread) {
//        synchronized(mNetworkManager) {
//            mReceiveThread = currentThread
//        }
//    }
//
//    override fun getReceiveThread(): Thread {
//        synchronized(mNetworkManager) {
//            return mReceiveThread
//        }
//    }
//
//    override fun getSendThread(): Thread {
//        synchronized(mNetworkManager) {
//            return mSendThread
//        }
//    }
//
//    override fun handleSendState(state: Int) {
//
//    }
//
//    override fun handleReceiveState(state: Int) {
//
//    }

    private fun outState(state: Int) {

    }
}