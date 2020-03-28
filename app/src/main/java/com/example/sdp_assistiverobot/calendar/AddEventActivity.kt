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
import androidx.core.content.ContextCompat
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.residents.Resident
import com.example.sdp_assistiverobot.util.Constants.Delivery_Pending
import com.example.sdp_assistiverobot.util.DatabaseManager.authUser
import com.example.sdp_assistiverobot.util.Util.convertDateToLong
import com.example.sdp_assistiverobot.util.Util.convertTimeToLong
import com.example.sdp_assistiverobot.util.Util.generateEventId
import kotlinx.android.synthetic.main.activity_add_event.*
import java.util.*
import kotlin.collections.ArrayList

class AddEventActivity : AppCompatActivity() {

    private val db = DatabaseManager.eventsRef
    private val residents = DatabaseManager.getResidents()
    private lateinit var resident: String
    private lateinit var category: String
    private lateinit var date: String
    private val calendar = Calendar.getInstance()
    private var mHour = "${calendar.get(Calendar.HOUR_OF_DAY)}".padStart(2,'0')
    private var mMinute = "${calendar.get(Calendar.MINUTE)}".padStart(2,'0')

    private val TAG = "AddEventActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        setSupportActionBar(add_event_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        isEnable(true)

        date = intent.getStringExtra("date")
        timeText.text = "$mHour:$mMinute"

        initialiseSpinner()

        timeText.setOnClickListener {
            showTimePicker()
        }

        button_save.setOnClickListener {
            addNewEvent()
        }
    }

    private fun initialiseSpinner() {
        val residentList = generateResidentList()

        residentSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            residentList
        ). apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        residentSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                resident = residents.keys.toList()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val categories = resources.getStringArray(R.array.categories)
        categorySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories.toList()
        ). apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        categorySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category = parent?.getItemAtPosition(position) as String
                Log.d(TAG, "$category selected")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(this, object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                mHour = "$hourOfDay".padStart(2, '0')
                mMinute = "$minute".padStart(2, '0')
                timeText.text = "$mHour:$mMinute"
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.setTitle("Select Time")
        timePickerDialog.show()
    }

    private fun isEnable(enable: Boolean) {
        timeText.isEnabled = enable
        residentSpinner.isEnabled = enable
        categorySpinner.isEnabled = enable
        noteText.isEnabled = enable
        button_save.isEnabled = enable
        if (enable) {
            progressBar2.visibility = ProgressBar.GONE
            button_save.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        } else {
            progressBar2.visibility = ProgressBar.VISIBLE
            button_save.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorAccent)
        }
    }

    private fun generateResidentList(): List<String> {
        val spinnerList = ArrayList<String>()
        for (resident in residents.values) {
            spinnerList.add("${resident.location} (${resident.first} ${resident.last})")
        }

        return spinnerList
    }

    private fun addNewEvent() {
        isEnable(false)

        val event = Delivery(authUser?.email!!,
            convertDateToLong(date),
            convertTimeToLong("$mHour:$mMinute"),
            resident,
            category,
            0.0,
            0.0,
            Delivery_Pending,
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
}
