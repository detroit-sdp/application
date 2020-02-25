package com.example.sdp_assistiverobot.patients

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.Constants.currentUser
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.util.Resident
import com.example.sdp_assistiverobot.util.Util
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_resident.*


class AddResidentActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var state: String? = null
    private lateinit var db : FirebaseFirestore
    private lateinit var mLocation: String

    private val TAG = "AddPatientActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resident)
        setSupportActionBar(findViewById(R.id.add_patient_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set functionalities to each components
        val states = this.resources.getStringArray(R.array.priorities)
        priorityText.adapter = SpinnerArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            states.toList()
        )
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        priorityText.onItemSelectedListener = this

        mLocation = intent.getIntExtra("location", 0).toString()
//        birthday.setOnClickListener{
//            createDatePickerDialog()
//        }

        button_save.setOnClickListener {
            uploadNewPatient()
//            createsTestUsers()
        }

        db = FirebaseFirestore.getInstance()
    }

//    private fun createDatePickerDialog() {
//        birthday.inputType = InputType.TYPE_NULL
//        val calendar = Calendar.getInstance()
//        var curDay = calendar.get(Calendar.DAY_OF_MONTH)
//        var curMonth = calendar.get(Calendar.MONTH)
//        var curYear = calendar.get(Calendar.YEAR)
//        DatePickerDialog(this,
//            android.R.style.Theme_Holo_Light_Dialog,
//            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
//                val padMonth = "$month".padStart(2,'0')
//                val padDay = "$dayOfMonth".padStart(2,'0')
//                birthday.text = "$padDay/$padMonth/$year"
//                curDay = dayOfMonth
//                curMonth = month
//                curYear = year
//            }, curYear, curMonth, curDay).apply {
//            show()
//            this.datePicker.maxDate = System.currentTimeMillis()-1000
//            this.datePicker.updateDate(curYear, curMonth, curDay)
//        }
//    }

    private fun validate(): Boolean {
        if (firstText.text.toString().isEmpty()) {
            firstText.error = "Empty block"
            return false
        }  else {
            firstText.error = null
        }

        if (lastText.text.toString().isEmpty()) {
            lastText.error = "Empty block"
            return false
        }  else {
            lastText.error = null
        }

        if (state == "Priority") {
            Toast.makeText(this, "Select a medical state", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Util.isInternetAvailable(baseContext)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    /**
     * For Test
     */
    private fun createsTestUsers() {
        for (x in 0..4) {
            val patient = Resident(
                "${currentUser!!.email}","Test", "User$x", "Medium", "Room ${x+1}"
            )
            db.collection("Residents").document("$x").set(patient)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    isEnable(true)
                }
        }
    }

    private fun uploadNewPatient() {
        isEnable(false)

        if (!validate()) {
            isEnable(true)
            return
        }

        val resident = Resident(
            currentUser!!.email!!,
            firstText.text.toString(),
            lastText.text.toString(),
            state!!,
            mLocation
//            locationText.text.toString()
        )

        db.collection("Residents").document("${DatabaseManager.getResidents().size}").set(resident)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                isEnable(true)
            }
    }

    private fun isEnable(enable: Boolean) {
        firstText.isEnabled = enable
        lastText.isEnabled = enable
//        location.isEnabled = enable
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        state = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    // Customized spinner adapter for medical states
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