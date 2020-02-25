package com.example.sdp_assistiverobot.residents

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
import com.example.sdp_assistiverobot.util.Resident
import com.example.sdp_assistiverobot.util.Util
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_resident.*


class AddResidentActivity : AppCompatActivity() {

    private var state: String? = null
    private var location: String? = null
    private lateinit var db : FirebaseFirestore

    private val TAG = "AddPatientActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resident)
        setSupportActionBar(findViewById(R.id.add_patient_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        location = intent.getStringExtra("location")

        val states = resources.getStringArray(R.array.priorities)
        priorityText.adapter = SpinnerArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            states.toList()
        )
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        priorityText.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                state = parent?.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        button_save.setOnClickListener {
            uploadNewPatient()
//            createsTestUsers()
        }

        db = FirebaseFirestore.getInstance()
    }

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
            formatName(firstText.text.toString()),
            formatName(lastText.text.toString()),
            state!!,
            location!!
//            locationText.text.toString()
        )

        db.collection("Residents").document().set(resident)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                isEnable(true)
            }
    }

    private fun formatName(string: String): String {
        return string[0].toUpperCase()+string.substring(1).toLowerCase()
    }

    private fun isEnable(enable: Boolean) {
        firstText.isEnabled = enable
        lastText.isEnabled = enable
//        location.isEnabled = enable
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