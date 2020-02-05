package com.example.sdp_assistiverobot.patients

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.Util
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_patient.*
import java.util.*


class AddPatientActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var state: String? = null
    private lateinit var db : FirebaseFirestore
    private var mGender: String = "male"

    private val TAG = "AddPatientActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)
        setSupportActionBar(findViewById(R.id.add_patient_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val states = this.resources.getStringArray(R.array.medicalStates)
        medicalState.adapter = SpinnerArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            states.toList()
        )
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        medicalState.onItemSelectedListener = this

        gender.setOnCheckedChangeListener { radioGroup, _ ->
            val id = radioGroup.checkedRadioButtonId
            val radio:RadioButton = findViewById(id)
            mGender = radio.text.toString()
        }

        birthday.inputType = InputType.TYPE_NULL
        val calendar = Calendar.getInstance()
        var curDay = calendar.get(Calendar.DAY_OF_MONTH)
        var curMonth = calendar.get(Calendar.MONTH)
        var curYear = calendar.get(Calendar.YEAR)
        birthday.setOnClickListener{
            DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val padMonth = "$month".padStart(2,'0')
                    val padDay = "$dayOfMonth".padStart(2,'0')
                    birthday.setText("$padDay/$padMonth/$year")
                    curDay = dayOfMonth
                    curMonth = month
                    curYear = year
                }, curYear, curMonth, curDay).apply {
                show()
                this.datePicker.maxDate = System.currentTimeMillis()-1000
                this.datePicker.updateDate(curYear, curMonth, curDay)
            }
        }

        button_save.setOnClickListener {
//            createsTestUsers()
            uploadNewPatient()
        }

        setSupportActionBar(findViewById(R.id.add_patient_toolbar))

        db = FirebaseFirestore.getInstance()
    }

    private fun validate(): Boolean {

        if (id.text.toString().length != 10) {
            id.error = "NHS is not correct"
            return false
        }  else {
            id.error = null
        }

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

        if (birthday.text.toString().isEmpty()) {
            birthday.error = "Empty block"
            return false
        }  else {
            birthday.error = null
        }

        if (birthday.text.toString().isEmpty()) {
            birthday.error = "Empty block"
            return false
        }  else {
            birthday.error = null
        }

        if (state == "Medical State") {
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
        for (x in 0..99) {
            // Generate random dob
            val rnd = Random()
            val ms = -946771200000L + (Math.abs(rnd.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000))
            val dob = Date(ms)
            val day = "${dob.day}".padStart(2,'0')
            val month = "${dob.month}".padStart(2,'0')
            val patient = Patient(
                "Test", "User$x",
                "$day/$month/19${dob.year}", "Male", "Stable", "None", "Bed 1 Room 315"
            )
            db.collection("Patients").document("$x".padStart(10, '0')).set(patient)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
//                    finish()
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

        val patient = Patient(
            firstText.text.toString(), lastText.text.toString(),
            birthday.text.toString(), mGender, state!!, notesText.text.toString()
        )

        db.collection("Patients").document(id.text.toString()).set(patient)
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
        id.isEnabled = enable
        birthday.isEnabled = enable
        firstText.isEnabled = enable
        lastText.isEnabled = enable
        gender.isEnabled = enable
        medicalState.isEnabled = enable
        notes.isEnabled = enable
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
