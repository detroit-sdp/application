package com.example.sdp_assistiverobot.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.Constants.Delivery_Pending
import com.example.sdp_assistiverobot.util.DatabaseManager.eventsRef
import com.example.sdp_assistiverobot.util.DatabaseManager.getResidents
import com.example.sdp_assistiverobot.util.Util.convertLongToDate
import com.example.sdp_assistiverobot.util.Util.convertLongToTime
import com.example.sdp_assistiverobot.util.Util.generateEventId
import kotlinx.android.synthetic.main.activity_event_view.*

class EventViewActivity : AppCompatActivity(), DeleteDeliveryDialogFragment.DeleteDeliveryDialogListener {

    private val TAG = "EventViewActivity"

    private lateinit var event: Delivery
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_view)
        setSupportActionBar(findViewById(R.id.event_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        event = intent.getSerializableExtra("event") as Delivery

        state.text = event.deliveryState
        time.text = "${convertLongToDate(event.date)} ${convertLongToTime(event.time)}"
        category.text = event.category
        note.text = event.note

        val residents = getResidents()
        resident.text = "${residents[event.residentId]?.first} ${residents[event.residentId]?.last}"
        location.text = "${residents[event.residentId]?.location}"

        id = generateEventId(time.text.toString())

        if (event.deliveryState == Delivery_Pending) {
            button_delete.visibility = Button.VISIBLE
            button_delete.setOnClickListener {
                val dialogFragment = DeleteDeliveryDialogFragment()
                dialogFragment.show(supportFragmentManager.beginTransaction(), "dialog")
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        eventsRef.document(id).delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                finish()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {

    }

}
