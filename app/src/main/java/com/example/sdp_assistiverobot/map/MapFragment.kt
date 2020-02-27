package com.example.sdp_assistiverobot.map

import android.app.Activity
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
import com.example.sdp_assistiverobot.residents.AddResidentActivity
import com.example.sdp_assistiverobot.util.Constants
import com.example.sdp_assistiverobot.util.Constants.CHANNEL_ID
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.util.Resident
import kotlinx.android.synthetic.main.fragment_map.*


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

    private lateinit var residentDialogFragment: DialogFragment
    private fun showResidentDialog(resident: Resident) {
        residentDialogFragment = ResidentDialogFragment()
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

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        // Do nothing.
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
}
