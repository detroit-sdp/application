package com.example.sdp_assistiverobot.map

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.MainActivity
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.patients.AddResidentActivity
import com.example.sdp_assistiverobot.util.Constants
import com.example.sdp_assistiverobot.util.Constants.CHANNEL_ID
import com.example.sdp_assistiverobot.util.DatabaseManager
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MapFragment : Fragment(), ClickDialogFragment.NoticeDialogListener {

    private val TAG = "MapFragment"

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

    private val LOCATION_TO_COMM: HashMap<Int, String> = hashMapOf(R.id.room_1 to "GOTO 1"
        , R.id.room_2 to "GOTO 2", R.id.room_3 to "GOTO 3", R.id.room_4 to "GOTO 4"
        , R.id.room_5 to "GOTO 5", R.id.charge_station to "Charging Station")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        br = NetworkBroadcastReceiver()

        initialiseButtons()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(Constants.ACTION_NETWORK_RECEIVE)
        }
        Log.d(TAG, "br registered")
        activity?.registerReceiver(br, filter)
    }

    private fun initialiseButtons() {
        setOnClick(room_1)
        setOnClick(room_2)
        setOnClick(room_3)
        setOnClick(room_4)
        setOnClick(room_5)
        setOnClick(charge_station)
    }

    private fun setOnClick(button: ImageButton) {

        if (button.id != charge_station.id) {
            val residents = DatabaseManager.getResidents()
            for (resident in residents) {
                if (button.id.toString() == resident.location) {
                    button.setImageResource(R.drawable.baseline_person_pin_circle_black_36)
                    button.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimary)
                    button.setOnClickListener {
                        if (button.imageTintList == ContextCompat.getColorStateList(context!!, R.color.colorPrimary)) {
                            button.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimaryYellow)
                            button.setImageResource(R.drawable.baseline_person_pin_circle_black_48)
                            showDialog(LOCATION_TO_COMM[button.id]!!)
                        } else {
                            button.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimary)
                            button.setImageResource(R.drawable.baseline_person_pin_circle_black_36)
                        }
                    }
                    return
                }
            }

            button.setOnClickListener {
                button.setImageResource(R.drawable.baseline_add_location_black_48)
                startActivity(Intent(context, AddResidentActivity::class.java).apply {
                    putExtra("location", button.id)
                })
            }
        } else {
            button.setOnClickListener {
                if (button.imageTintList == ContextCompat.getColorStateList(context!!, R.color.colorPrimaryGreen)) {
                    button.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimaryYellow)
                    showDialog(LOCATION_TO_COMM[button.id]!!)
                } else {
                    button.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimaryGreen)
                }
            }

        }
    }

    private fun showDialog(command: String) {
        val dialogFragment = ClickDialogFragment()
        dialogFragment.setTargetFragment(this, 0)
        val bundle = Bundle()
        bundle.putString("command", command)
        dialogFragment.arguments = bundle
        dialogFragment.show(fragmentManager?.beginTransaction(), "dialog")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        senderThreadPool.execute(SendCommandRunnable(dialog.arguments!!.getString("command")!!))
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "br unregistered")
        activity?.unregisterReceiver(br)
    }

    private fun buildNotification(message: String): NotificationCompat.Builder {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this.context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0)

        return NotificationCompat.Builder(this.context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_black_24)
            .setContentTitle("Tadashi")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    inner class NetworkBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_NETWORK_RECEIVE -> {
                    // Push message to chat frame
                    Log.d(TAG, "Income Message: ${intent.getStringExtra("message")}")

                    val builder = buildNotification(intent.getStringExtra("message"))
                    with(NotificationManagerCompat.from(context)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(Constants.NOTIFICATION_ID, builder.build())
                    }

                }
            }
        }
    }
}
