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
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    private val IP_ADDRESS = "172.20.97.106"
    private val PORT = 8080
    private lateinit var socket: Socket
    private lateinit var out: PrintWriter
    private lateinit var input: BufferedReader
    private var isConnected  = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        button_connect.setOnClickListener {
            Thread(Runnable {
                socket = Socket(IP_ADDRESS, PORT)
                out = PrintWriter(socket.getOutputStream())
                input = BufferedReader(InputStreamReader(socket.getInputStream()))
                isConnected = socket.isConnected
                Log.d(TAG, "Connected!")
            }).start()
        }

        send.setOnClickListener{
            val message: String = out_text.text.toString()
            if (message.isNotEmpty()) {
                Thread(Runnable {
                    out.write(message)
                    out.flush()
                    out_text.text = null
                    Log.d(TAG, "Message send: $message")
                })
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
