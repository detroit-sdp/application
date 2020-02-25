package com.example.sdp_assistiverobot.residents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.Resident
import kotlinx.android.synthetic.main.activity_resident_view.*

class ResidentViewActivity : AppCompatActivity() {

    lateinit var resident: Resident

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_resident_view)

        resident = intent.getSerializableExtra("resident") as Resident
        name.text = "${resident.first} ${resident.last}"
        setSupportActionBar(findViewById(R.id.resident_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setPatientInfo()

        edit_name.setOnClickListener {

        }

        edit_location.setOnClickListener {

        }

        edit_priority.setOnClickListener {

        }

        send_comm.setOnClickListener {

        }

        delete.setOnClickListener {

        }
    }

    private fun setPatientInfo() {
        location.text = "Location: ${resident.location}"
        priority.text = "Priority: ${resident.priority}"
    }

    private fun sendTadashi(){

    }
}
