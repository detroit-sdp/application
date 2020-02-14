package com.example.sdp_assistiverobot.map

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class NetworkTask: SendMessageRunnable.SendMessageInterface, ReceiveMessageRunnable.ReceiveMessageInterface {

    var inetAddress = InetAddress.getByName("127.0.0.1")
    private val robotPort = 20001
    private var mPort = 12345

    private lateinit var sendMessageRunnable: SendMessageRunnable
    private lateinit var receiveMessageRunnable: ReceiveMessageRunnable
    private lateinit var mReceiveThread: Thread
    private lateinit var mSendThread: Thread
    private val mNetworkManager: NetworkManager

    constructor() {
        mNetworkManager = NetworkManager.getInstance()
        receiveMessageRunnable = ReceiveMessageRunnable(this)
    }

    constructor(message: String) {
        sendMessageRunnable = SendMessageRunnable(this, message)
        mNetworkManager = NetworkManager.getInstance()
    }

    fun openSocket(): DatagramSocket {
        return DatagramSocket(mPort).also {
            it.broadcast = true
        }
    }

    fun createPackage(message: String): DatagramPacket {
        val out = message.toByteArray()
        return  DatagramPacket(out, out.size, inetAddress, robotPort)
    }

    fun createPackage(): DatagramPacket {
        val buffer = ByteArray(1024)
        return DatagramPacket(buffer, buffer.size)
    }

    fun setIpAddress(ipAddress: String) {
        inetAddress = InetAddress.getByName(ipAddress)
    }

    fun setPort(port: Int) {
        mPort = port
    }

    fun getReceiveMessageRunnable(): Runnable {
        return receiveMessageRunnable
    }

    fun getSendMessageRunnable(): Runnable? {
        return sendMessageRunnable
    }

    override fun setSendThread(currentThread: Thread) {
        synchronized(mNetworkManager) {
            mSendThread = currentThread
        }
    }

    override fun getSendThread(): Thread {
        synchronized(mNetworkManager) {
            return mSendThread
        }
    }

    override fun handleSendState(state: Int) {

    }

    override fun setReceiveThread(currentThread: Thread) {
        synchronized(mNetworkManager) {
            mReceiveThread = currentThread
        }
    }

    override fun getReceiveThread(): Thread {
        synchronized(mNetworkManager) {
            return mReceiveThread
        }
    }

    override fun handleReceiveState(state: Int) {

    }

    private fun handleState(state: Int) {

    }
}