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
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException

class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    private val IP_ADDRESS = "172.20.97.106"
    private val PORT = 8080
    private lateinit var out: PrintWriter
    private lateinit var input: BufferedReader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnConnect.setOnClickListener {
            Thread(Thread1()).start()
        }

        btnSend.setOnClickListener{
            val message: String = etMessage.text.toString()
            if (message.isNotEmpty()) {
                Thread(Runnable {
                    out.write(message)
                    out.flush()
                    etMessage.text = null
                    Log.d(TAG, "Message send: $message")
                })
            }
        }
    }

    inner class Thread1: Runnable{
        override fun run() {
            var socket: Socket?
            try{
                socket = Socket(etIP.text.toString().trim(), Integer.parseInt(etPort.text.toString().trim()))
                out = PrintWriter(socket.getOutputStream())
                input = BufferedReader(InputStreamReader(socket.getInputStream()))
                tvMessages.text = "Connected\n"
                Thread(Thread2()).start()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }
        }
    }

    inner class Thread2: Runnable {
        override fun run() {
            while (true) {
                try {
                    val message = input.readLine()
                    if (message != null) {
                        tvMessages.append("server: $message\n")
                    } else {
                        Thread(Thread1()).start()
                        return
                    }
                } catch (e: IOException) {
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
}
