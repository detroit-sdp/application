package com.example.sdp_assistiverobot.map

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.MainActivity
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.residents.AddResidentActivity
import com.example.sdp_assistiverobot.util.Constants
import com.example.sdp_assistiverobot.util.Constants.CHANNEL_ID
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.residents.Resident
import kotlinx.android.synthetic.main.fragment_map.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MapFragment : Fragment(), ConfirmDialogFragment.ConfirmDialogListener,
    ResidentDialogFragment.ResidentDialogListener {

    private val TAG = "MapFragment"

    private lateinit var locationsToButtons: HashMap<String, Int>
    private lateinit var buttonsToLocations: HashMap<Int, String>

    private val AVAILABLE = 0
    private val OCCUPIED = 1
    private val SELECTED = 2

    private val SHOW_RESIDENT_PROFILE = 0

    private lateinit var br: NetworkBroadcastReceiver

    private var clickedButton: Int = 0

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

        locationsToButtons = hashMapOf(
            "Room 1" to room_1.id,
            "Room 2" to room_2.id,
            "Room 3" to room_3.id)

        buttonsToLocations = hashMapOf(
            room_1.id to "Room 1",
            room_2.id to "Room 2",
            room_3.id to "Room 3")

        initialiseButtons()

        if (savedInstanceState != null) {
            val button = view?.findViewById<ImageButton>(savedInstanceState.getInt("clicked"))
            onOccupiedSelected(button)
        }

        progressBar.visibility = ProgressBar.GONE
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
        val residents = DatabaseManager.getResidents()
        for (resident in residents) {
            val button = findButtonByLocation(resident.location)
            onOccupiedNormal(button)
            if (button?.tag == SELECTED) {
                onOccupiedSelected(button)
            }

            button!!.setOnClickListener {
                if (button.tag == OCCUPIED) {
                    onOccupiedSelected(button)
                    showResidentDialog(resident)
                } else {
                    onOccupiedNormal(button)
                }
                clickedButton = button.id
            }
        }

        setOnClick(room_1)
        setOnClick(room_2)
        setOnClick(room_3)
//        setOnClick(room_4)
//        setOnClick(room_5)
//        setOnClick(charge_station)
    }

    private fun setOnClick(button: ImageButton) {
        if (button.tag == OCCUPIED) {
            return
        }

        if (button.id != charge_station.id) {
            button.setOnClickListener {
                button.setImageResource(R.drawable.baseline_add_location_black_48)
                startActivityForResult(Intent(context, AddResidentActivity::class.java).apply {
                    putExtra("location", buttonsToLocations[button.id])
                },SHOW_RESIDENT_PROFILE)
                clickedButton = button.id
            }
        } else {
            button.setOnClickListener {
                if (button.tag == AVAILABLE) {
                    button.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimaryYellow)
                    showAlertDialog("Charging Station", "Charging Station")
                } else {
                    button.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimaryGreen)
                }
                clickedButton = button.id
            }
        }
    }

    private fun showResidentDialog(resident: Resident) {
        val residentDialogFragment = ResidentDialogFragment()
        residentDialogFragment.setTargetFragment(this, 0)
        val bundle = Bundle()
        bundle.putSerializable("resident", resident)
        residentDialogFragment.arguments = bundle
        residentDialogFragment.show(fragmentManager?.beginTransaction(), "dialog")
    }

    fun showAlertDialog(name: String, location: String) {
        val dialogFragment = ConfirmDialogFragment()
        dialogFragment.setTargetFragment(this, 0)
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("location", location)
        dialogFragment.arguments = bundle
        dialogFragment.show(fragmentManager?.beginTransaction(), "dialog")
    }

    override fun onDialogPositiveClick(location: String) {
//        NetworkManager.sendCommand(location, progressBar, findButtonByLocation(location))
        val task = NetworkAsyncTask()
        task.executeOnExecutor(networkThreadPool, location)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        val name = dialog.arguments?.getString("name") as String

        if (name == "Charging Station") {
            charge_station.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimaryGreen)
        } else {
            val location = dialog.arguments?.get("location") as String
            val button = findButtonByLocation(location)
            button?.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimary)
            button?.performClick()
        }
    }

    override fun onCloseClicked(dialog: DialogFragment) {
        val resident = dialog.arguments?.get("resident") as Resident
        val button = findButtonByLocation(resident.location)
        onOccupiedNormal(button)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "br unregistered")
        activity?.unregisterReceiver(br)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("Clicked", clickedButton)
    }

    private fun onOccupiedSelected(button: ImageButton?) {
        button?.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimaryYellow)
        button?.setImageResource(R.drawable.baseline_person_pin_circle_black_48)
        button?.tag = SELECTED
    }

    private fun onOccupiedNormal(button: ImageButton?) {
        button?.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimary)
        button?.setImageResource(R.drawable.baseline_person_pin_circle_black_36)
        button?.tag = OCCUPIED
    }

    private fun findButtonByLocation(location: String?): ImageButton? {
        return activity?.findViewById(locationsToButtons[location]!!)
    }

    /**
     * Notifications Functions
     */
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
                Constants.ACTION_NETWORK_SEND_SUCCESS -> {
                    // Push message to chat frame
                    val builder = buildNotification(intent.getStringExtra("message"))
                    with(NotificationManagerCompat.from(context)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(Constants.NOTIFICATION_ID, builder.build())
                    }
                }
            }
        }
    }


    /**
     * AsyncTask class
     */
    private val networkWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()
    // Check how many processors on the machine
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    // Sets the amount of time an idle thread waits before terminating
    private val KEEP_ALIVE_TIME = 1L
    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    // Creates a thread pool manager
    private val networkThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,       // Initial pool size
        NUMBER_OF_CORES,       // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        networkWorkQueue
    )

    inner class NetworkAsyncTask: AsyncTask<String, Void, Int>() {
        private val TAG = "SendMessageRunnable"
        private val IP_ADDRESS = "172.21.3.60"
        private val PORT = 20001

        private val SEND_SUCCESS = 0
        private val SEND_FAIL = -1
        private lateinit var location: String

        private val locationToComms: HashMap<String, String> = hashMapOf(
            "Room 1" to "GOTO 3",
            "Room 2" to "GOTO 3",
            "Room 3" to "GOTO 3")

        private val LIFT_UP = 0
        private val LIFT_DOWN = 1

        override fun doInBackground(vararg params: String): Int {
            val socket: DatagramSocket
            location = params[0]
            val message = locationToComms[location]
            try {
                Log.d(TAG, "Sending $message to $IP_ADDRESS:$PORT")

                if(Thread.interrupted()) {
                    return SEND_FAIL
                }
                socket = DatagramSocket().also {
                    it.broadcast = true
                }

                val out = message!!.toByteArray()
                val outPackage = DatagramPacket(out, out.size, InetAddress.getByName(IP_ADDRESS), PORT)
                if(Thread.interrupted()){
                    return SEND_FAIL
                }
                socket.send(outPackage)
                socket.close()
                Log.d(TAG, "Send success")
            } catch (e: Exception) {
                e.printStackTrace()
                return SEND_FAIL
            }
            return SEND_SUCCESS
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = ProgressBar.VISIBLE
            activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

        override fun onPostExecute(result: Int) {
            super.onPostExecute(result)
            progressBar.visibility = ProgressBar.GONE
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            when (result) {
                SEND_FAIL -> {
                    val button = findButtonByLocation(location)
                    onOccupiedNormal(button)
                    Toast.makeText(context, "Send failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
