package com.example.sdp_assistiverobot.patients

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sdp_assistiverobot.R
import kotlinx.android.synthetic.main.activity_patient_view.*

class PatientViewActivity : AppCompatActivity() {

    lateinit var patient: Patient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_patient_view)

        patient = intent.getSerializableExtra("patient") as Patient
        toolbar_title.text = "${patient.first} ${patient.last}"
        setSupportActionBar(findViewById(R.id.patient_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setPatientInfo()
    }

    private fun setPatientInfo() {
        gender.text = "Gender: ${patient.gender}"
        dob.text = "Date of Birth: ${patient.dob}"
        age.text = "Age: ${patient.getAge()}"
        location.text = "Location: ${patient.location}"
        medicalState.text = "Medical state: ${patient.medicalState}"
        notes.text = "Notes: ${patient.note}"
    }

    /**
     * Search bar on top: filter to specify the attribute for search / use special format (attribute:query) as filter
     * Update User Info
     */
}