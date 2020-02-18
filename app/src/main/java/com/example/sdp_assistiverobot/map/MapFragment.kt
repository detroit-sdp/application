package com.example.sdp_assistiverobot.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.R
import kotlinx.android.synthetic.main.fragment_map.*
import java.lang.Exception
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.Charset
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    private lateinit var outIP: String

    private val senderWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
    // Check how many processors on the machine
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    // Sets the amount of time an idle thread waits before terminating
    private val KEEP_ALIVE_TIME = 1L
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        NetworkManager.setTextView(tvMessages)

        btnConnect.setOnClickListener {
            outIP = etIP.text.toString()
            Log.d(TAG, "Connecting")
            activity?.startService(Intent(this.context, NetworkCommService::class.java).apply {
                putExtra("IP_ADDRESS", outIP)
            })
        }

        btnSend.setOnClickListener {
            senderThreadPool.execute(SendCommandRunnable("192.168.105.111", 20001, etMessage.text.toString()))
        }
    }

    inner class NetworkBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "com.example.sdp_assistiverobot.getMessage" -> {
                    // Push message to chat frame
                    tvMessages.append(intent.getCharSequenceExtra("message"))
                }
            }
        }
    }
}
