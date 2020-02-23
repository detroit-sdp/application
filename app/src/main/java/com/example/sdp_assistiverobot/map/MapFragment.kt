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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.Constants
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MapFragment : Fragment() {

    private val TAG = "MapFragment"

    private val outIP: String = "192.168.105.111"
    private val outPort = 20001

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

    private lateinit var br: NetworkBroadcastReceiver
    private lateinit var serviceIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val builder = NotificationCompat.Builder(this.context!!, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.calendar_icon)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this.context!!)) {
            // notificationId is a unique int for each notification that you must define
            notify(Constants.NOTIFICATION_ID++, builder.build())
        }

        br = NetworkBroadcastReceiver()
        val filter = IntentFilter().apply {
            addAction(Constants.ACTION_NETWORK_RECEIVE)
        }
        activity?.registerReceiver(br, filter)

        serviceIntent = Intent(this.context, NetworkCommService::class.java)

        room_1.setOnClickListener {
            if (room_1.imageTintList == ContextCompat.getColorStateList(context!!, R.color.colorOrange)) {
                room_1.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorLightGreen)
            } else {
                room_1.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorOrange)
            }
        }
//        btnConnect.setOnClickListener {
//            activity?.startService(serviceIntent)
//        }

//        btnSend.setOnClickListener {
//            senderThreadPool.execute(SendCommandRunnable(outIP, outPort, etMessage.text.toString()))
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(br)
    }

    inner class NetworkBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_NETWORK_RECEIVE -> {
                    Log.d(TAG, "Message received")
                    // Push message to chat frame
//                    if (tvMessages != null) {
//                        tvMessages.append("Tadashi: ${intent.getCharSequenceExtra("message")}\n")
//                    }
                }
            }
        }
    }
}
