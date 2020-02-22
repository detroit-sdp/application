package com.example.sdp_assistiverobot.patients

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.Util.Resident
import kotlinx.android.synthetic.main.activity_resident_view.*

class ResidentViewActivity : AppCompatActivity() {

    lateinit var resident: Resident

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_resident_view)

        resident = intent.getSerializableExtra("resident") as Resident
        toolbar_title.text = "${resident.first} ${resident.last}"
        setSupportActionBar(findViewById(R.id.patient_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setPatientInfo()

        button_sendTadashi.setOnClickListener{
            sendTadashi()
        }
    }

    private fun setPatientInfo() {
        location.text = "Location: ${resident.location}"
        priority.text = "Priority: ${resident.priority}"
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
