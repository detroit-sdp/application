package com.example.sdp_assistiverobot.map

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.calendar.Delivery
import com.example.sdp_assistiverobot.residents.AddResidentActivity
import com.example.sdp_assistiverobot.util.Constants
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.residents.Resident
import com.example.sdp_assistiverobot.util.Constants.Delivery_Send
import com.example.sdp_assistiverobot.util.DatabaseManager.authUser
import com.example.sdp_assistiverobot.util.DatabaseManager.eventsRef
import com.example.sdp_assistiverobot.util.DatabaseManager.getResidents
import com.example.sdp_assistiverobot.util.DatabaseManager.residentsRef
import com.example.sdp_assistiverobot.util.DatabaseManager.updatePriority
import com.example.sdp_assistiverobot.util.Util.nowToId
import com.example.sdp_assistiverobot.util.Util.nowToLong
import com.example.sdp_assistiverobot.util.Util.todayToLong
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
        for ((id, resident) in residents) {
            val button = findButtonByLocation(resident.location)
            onOccupiedNormal(button)
            if (button?.tag == SELECTED) {
                onOccupiedSelected(button)
            }

            button!!.setOnClickListener {
                if (button.tag == OCCUPIED) {
                    onOccupiedSelected(button)
                    showResidentDialog(id, resident)
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
                    showAlertDialog("Base", "Charging", "High", "")
                } else {
                    button.imageTintList = ContextCompat.getColorStateList(context!!, R.color.colorPrimaryGreen)
                }
                clickedButton = button.id
            }
        }
    }

    private fun showResidentDialog(id: String, resident: Resident) {
        val residentDialogFragment = ResidentDialogFragment()
        residentDialogFragment.setTargetFragment(this, 0)
        val bundle = Bundle()
        bundle.putString("residentId", id)
        bundle.putSerializable("resident", resident)
        residentDialogFragment.arguments = bundle
        residentDialogFragment.show(fragmentManager?.beginTransaction(), "dialog")
    }

    fun showAlertDialog(id: String, category: String, priority: String, note: String) {
        val dialogFragment = ConfirmDialogFragment()
        dialogFragment.setTargetFragment(this, 0)
        val bundle = Bundle()
        bundle.putString("id", id)
        bundle.putString("category", category)
        bundle.putString("priority", priority)
        bundle.putString("note", note)
        dialogFragment.arguments = bundle
        dialogFragment.show(fragmentManager?.beginTransaction(), "dialog")
    }

    override fun onDialogPositiveClick(id: String, category: String, priority: String, note: String) {
        if (id == "Base") {
            val now = nowToId()
            val date = todayToLong()
            val time = nowToLong()
            val delivery = Delivery(
                authUser.email!!,
                date,
                time,
                id,
                category,
                0.0,
                0.0,
                Delivery_Send,
                note
            )

            // Create new delivery
            eventsRef.document(now).set(delivery)
                .addOnSuccessListener {Log.d(TAG, "DocumentSnapshot successfully written!")}
                .addOnFailureListener {e -> Log.w(TAG, "Error writing document", e)}

            // Update priority of resident
            val residents = getResidents()
            val resident = residents[id]

            updatePriority(id, priority)

            // Send command
            NetworkManager.sendCommand(resident!!.location)
        } else {
            NetworkManager.sendCommand(id)
        }
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
}
