package com.example.sdp_assistiverobot.residents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.sdp_assistiverobot.R
import com.example.sdp_assistiverobot.util.DatabaseManager
import com.example.sdp_assistiverobot.util.DatabaseManager.DATABASE
import com.example.sdp_assistiverobot.util.DatabaseManager.authUser
import com.example.sdp_assistiverobot.util.DatabaseManager.eventsRef
import com.example.sdp_assistiverobot.util.DatabaseManager.residentsRef
import com.example.sdp_assistiverobot.util.SpinnerArrayAdapter
import com.example.sdp_assistiverobot.util.Util
import com.example.sdp_assistiverobot.util.Util.formatName
import kotlinx.android.synthetic.main.activity_edit_resident.*

class EditResidentActivity : AppCompatActivity() {

    private val TAG = "EditResidentActivity"

    private lateinit var priority: String
    private lateinit var location: String
    private lateinit var resident: Resident
    private lateinit var id: String

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_single, menu)
        menu?.getItem(0)?.setIcon(R.drawable.baseline_check_black_24)
        menu?.getItem(0)?.iconTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_resident)
        setSupportActionBar(findViewById(R.id.edit_resident_toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        id = intent.getStringExtra("id")
        resident = intent.getSerializableExtra("resident") as Resident

        firstText.setText(resident.first)
        lastText.setText(resident.last)

        val priorities = resources.getStringArray(R.array.priorities)
        priorityText.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            priorities.toList()
        )
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        priorityText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                priority = parent?.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        priorityText.setSelection(priorities.indexOf(resident.priority))

        val locations = resources.getStringArray(R.array.locations)
        val residents = DatabaseManager.getResidents()
        val occupiedLocations = ArrayList<String>()

        residents.forEach {occupiedLocations.add(it.value.location)}

        val freeLocations = locations.filterNot { occupiedLocations.contains(it) } as ArrayList
        freeLocations.add(0, resident.location)
        locationText.adapter = SpinnerArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            freeLocations.toList()
        )
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        locationText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                location = parent?.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        button_delete.setOnClickListener {
            isEnable(false)
            residentsRef.document(id)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    setResult(1)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

            eventsRef
                .whereEqualTo("residentId", id)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        eventsRef.document(document.id).delete()
                    }
                }
        }

        isEnable(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action0 -> {
                updateResident()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

        if (!Util.isInternetAvailable(baseContext)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun isEnable(enable: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enable)
        firstText.isEnabled = enable
        lastText.isEnabled = enable
        priorityText.isEnabled = enable
        locationText.isEnabled = enable
        if (enable) {
            progressBar.visibility = ProgressBar.GONE
            button_delete.foregroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        } else {
            progressBar.visibility = ProgressBar.VISIBLE
            button_delete.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorAccent)
        }
    }

    private fun updateResident() {
        isEnable(false)

        if (!validate()) {
            isEnable(true)
            return
        }

        val newResident = Resident(
            authUser?.email!!,
            formatName(firstText.text.toString()),
            formatName(lastText.text.toString()),
            priority,
            location
        )

        val docRef = DATABASE.collection("Residents").document(id)
        DATABASE.runBatch {batch ->
            if (newResident.first != resident.first) {
                batch.update(docRef, "first", newResident.first)
            }
            if (newResident.last != resident.last) {
                batch.update(docRef, "last", newResident.last)
            }
            if (newResident.priority != resident.priority) {
                batch.update(docRef, "priority", newResident.priority)
            }
            if (newResident.location != resident.location) {
                batch.update(docRef, "location", newResident.location)
            }
        }.addOnCompleteListener {
            Log.d(TAG, "Database update complete")
            setResult(Activity.RESULT_OK, Intent(applicationContext, ResidentViewActivity::class.java).apply {
                putExtra("id", id)
                putExtra("resident", newResident)
            })
            finish()
        }
    }
}
