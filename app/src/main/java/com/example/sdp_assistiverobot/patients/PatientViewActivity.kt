package com.example.sdp_assistiverobot.patients

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.dashboard.DashboardFragment
import kotlinx.android.synthetic.main.activity_patient_view.*
import java.security.AccessController.getContext

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

        button_sendTadashi.setOnClickListener{
            sendTadashi()
        }
    }

    private fun setPatientInfo() {
        gender.text = "Gender: ${patient.gender}"
        dob.text = "Date of Birth: ${patient.dob}"
        age.text = "Age: ${patient.getAge()}"
        location.text = "Location: ${patient.location}"
        medicalState.text = "Medical state: ${patient.medicalState}"
        notes.text = "Notes: ${patient.note}"
    }

    private fun sendTadashi(){
        val destination = location.text
        //TODO: send request to Tadashi
        Toast.makeText(getApplicationContext(), "Tadashi is on the way!", Toast.LENGTH_SHORT).show()
        finish()
//        val dashboardFragment= DashboardFragment()
//        val fragmentManager: FragmentManager? = fragmentManager
//        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
//        fragmentTransaction.replace(R.id.container, patientsFragment)
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()
    }

    /**
     * Search bar on top: filter to specify the attribute for search / use special format (attribute:query) as filter
     * Update User Info
     */
}
