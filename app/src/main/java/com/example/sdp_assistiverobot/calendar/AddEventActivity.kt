package com.example.sdp_assistiverobot.calendar

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.residents.Resident
import com.example.sdp_assistiverobot.util.Util.convertDateToLong
import com.example.sdp_assistiverobot.util.Util.generateEventId
import kotlinx.android.synthetic.main.activity_add_event.*
import java.util.*
import kotlin.collections.ArrayList

class AddEventActivity : AppCompatActivity() {

    private val db = DatabaseManager.eventsRef
    private val residents = DatabaseManager.getResidents()
    private lateinit var resident: Resident
    private lateinit var date: String
    private val calendar = Calendar.getInstance()
    private var mHour = calendar.get(Calendar.HOUR_OF_DAY)
    private var mMinute = calendar.get(Calendar.MINUTE)

    private val TAG = "AddEventActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        setSupportActionBar(add_event_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        isEnable(true)

        date = intent.getStringExtra("date")
        val hourString = "$mHour".padStart(2,'0')
        timeText.text = "Start Time: $hourString:$mMinute"

        initialiseSpinner()

        timeText.setOnClickListener {
            showTimePicker()
        }

        button_save.setOnClickListener {
            isEnable(false)
            addNewEvent()
        }
    }

    private fun initialiseSpinner() {
        val spinnerList = generateList()

        residentSpinner.adapter = SpinnerArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinnerList
        ). apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        residentSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position >= 1) {
                    resident = residents[position-1]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(this, object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                mHour = hourOfDay
                mMinute = minute
                val hourString = "$hourOfDay".padStart(2, '0')
                timeText.text = "Start Time: $hourString:$minute"
            }
        }, mHour, mMinute, true)

        timePickerDialog.setTitle("Select Time")
        timePickerDialog.show()
    }

    private fun isEnable(enable: Boolean) {
        timeText.isEnabled = enable
        residentSpinner.isEnabled = enable
        noteText.isEnabled = enable
    }

    private fun generateList(): List<String> {
        val spinnerList = ArrayList<String>()
        spinnerList.add("Resident")

        for (resident in residents) {
            spinnerList.add("${resident.first} ${resident.last} (${resident.location})")
        }

        return spinnerList
    }

    private fun validate(): Boolean {
        if (residentSpinner.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select a destination (resident)", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun addNewEvent() {
        if (!validate()) {
            isEnable(true)
            return
        }

        val event = Event(convertDateToLong(date),
            mHour,
            mMinute,
            resident.location,
            noteText.text.toString())

        db.document(generateEventId("$date $mHour:$mMinute"))
            .set(event)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                isEnable(true)
            }
    }

    private class SpinnerArrayAdapter<String>(context: Context, resource: Int,
                                              objects : List<String>) : ArrayAdapter<String>(context, resource, objects) {
        override fun isEnabled(position: Int): Boolean {
            return position != 0
        }
        override fun getDropDownView(
            position: Int, convertView: View?,
            parent: ViewGroup?
        ): View? {
            val view = super.getDropDownView(position, convertView, parent!!)
            val tv = view as TextView
            if (position == 0) { // Set the hint text color gray
                tv.setTextColor(Color.GRAY)
            } else {
                tv.setTextColor(Color.BLACK)
            }
            return view
        }
    }
}
