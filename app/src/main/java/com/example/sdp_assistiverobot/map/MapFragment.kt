package com.example.sdp_assistiverobot.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.R
import kotlinx.android.synthetic.main.fragment_map.*
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.*
import java.nio.charset.Charset


class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    private val IP_ADDRESS = "192.168.137.1"
    private val PORT = 20001
    private lateinit var out: PrintWriter
    private lateinit var input: BufferedReader
    private lateinit var socket: DatagramSocket

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        btnConnect.setOnClickListener {
//            Thread(Thread1()).start()
//        }
        socket = DatagramSocket(PORT)
        socket.broadcast = true

        Thread(Thread2()).start()

        btnSend.setOnClickListener{
            Thread(Runnable {
//                    out.write(message)
//                    out.flush()
//                    etMessage.text = null
//                    Log.d(TAG, "Message send: $message")
                try {
                    val inetAddress: InetAddress = InetAddress.getByName(IP_ADDRESS)
                    val sendData = ("Hello world!").toByteArray()
                    val sendPackage = DatagramPacket(sendData, sendData.size, inetAddress, PORT)
                    socket.send(sendPackage)
                    Log.d(TAG, "Message sent to ${inetAddress.hostName}: ${sendData.toString(Charset.defaultCharset()).subSequence(0,sendData.toString(Charset.defaultCharset()).lastIndex)}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }).start()
        }
    }

//    inner class Thread1: Runnable{
//        override fun run() {
//            val socket: Socket?
//            try{
//                socket = Socket(etIP.text.toString().trim(), Integer.parseInt(etPort.text.toString().trim()))
//                out = PrintWriter(socket.getOutputStream())
//                input = BufferedReader(InputStreamReader(socket.getInputStream()))
//                Thread(Thread2()).start()
//            } catch (e: UnknownHostException) {
//                e.printStackTrace()
//            }
//        }
//    }

    inner class Thread2: Runnable {
        override fun run() {
            while (true) {
//                try {
//                    val message = input.readLine()
//                    if (message != null) {
////                        tvMessages.append("server: $message\n")
//                        Log.d(TAG, message)
//                    } else {
////                        Thread(Thread1()).start()
//                        return
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
                val buffer = ByteArray(1024)

                try {
                    Log.d(TAG, "Listening...")
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    Log.d(TAG, "${packet.address}")
                    Log.d(TAG, "Message received from ${packet.address}:${packet.port}: ${packet.data.toString(Charset.defaultCharset())}")
                    Thread(Thread2()).start()
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private class NetworkBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "connection" -> Log.d("TAG", "Connected!")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        socket.close()
    }
}
